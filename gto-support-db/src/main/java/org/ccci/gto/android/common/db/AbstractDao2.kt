package org.ccci.gto.android.common.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.AsyncTask
import androidx.annotation.WorkerThread
import androidx.collection.SimpleArrayMap
import org.ccci.gto.android.common.compat.util.LocaleCompat
import org.ccci.gto.android.common.db.CommonTables.LastSyncTable
import org.ccci.gto.android.common.util.database.getLong
import org.ccci.gto.android.common.util.database.map
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor

abstract class AbstractDao2(private val helper: SQLiteOpenHelper) : Dao {
    companion object {
        private const val ARG_PREFIX = "org.ccci.gto.android.common.db.AbstractDao"
        const val ARG_DISTINCT = "$ARG_PREFIX.ARG_DISTINCT"
        const val ARG_JOINS = "$ARG_PREFIX.ARG_JOINS"
        const val ARG_PROJECTION = "$ARG_PREFIX.ARG_PROJECTION"
        const val ARG_WHERE = "$ARG_PREFIX.ARG_WHERE"
        const val ARG_ORDER_BY = "$ARG_PREFIX.ARG_ORDER_BY"

        @JvmStatic
        fun bindValues(vararg raw: Any) = raw.map {
            when (it) {
                is String -> it
                is Boolean -> if (it) "1" else "0"
                is Date -> it.time.toString()
                is Locale -> LocaleCompat.toLanguageTag(it)
                else -> it.toString()
            }
        }.toTypedArray()
    }

    override val backgroundExecutor: Executor get() = AsyncTask.THREAD_POOL_EXECUTOR
    @get:WorkerThread
    protected val readableDatabase: SQLiteDatabase
        get() = helper.readableDatabase
    @get:WorkerThread
    protected val writableDatabase: SQLiteDatabase
        get() = helper.writableDatabase

    // region Registered Types
    private val tableTypes = SimpleArrayMap<Class<*>, TableType>()

    protected fun <T> registerType(
        clazz: Class<T>,
        table: String,
        projection: Array<String>? = null,
        mapper: Mapper<T>? = null,
        pkWhere: Expression? = null
    ) {
        tableTypes.put(clazz, TableType(table, projection, mapper, pkWhere))
    }

    protected open fun getTable(clazz: Class<*>) =
        tableTypes.get(clazz)?.table ?: throw IllegalArgumentException("invalid class specified: ${clazz.name}")

    fun getFullProjection(table: Table<*>) = getFullProjection(table.mType)
    open fun getFullProjection(clazz: Class<*>) =
        tableTypes.get(clazz)?.projection ?: throw IllegalArgumentException("invalid class specified: ${clazz.name}")

    protected open fun getPrimaryKeyWhere(clazz: Class<*>, vararg key: Any) = getPrimaryKeyWhere(clazz).args(*key)
    protected open fun getPrimaryKeyWhere(clazz: Class<*>) =
        tableTypes.get(clazz)?.primaryWhere ?: throw IllegalArgumentException("invalid class specified: ${clazz.name}")

    protected open fun getPrimaryKeyWhere(obj: Any): Expression =
        throw IllegalArgumentException("unsupported object: ${obj.javaClass.name}")

    @Suppress("UNCHECKED_CAST")
    protected open fun <T> getMapper(clazz: Class<T>) = tableTypes.get(clazz)?.mapper as? Mapper<T>
        ?: throw IllegalArgumentException("invalid class specified: ${clazz.name}")
    // endregion Registered Types

    // region Queries
    // region Read-Only
    @WorkerThread
    fun <T : Any> refresh(obj: T): T? = find(obj.javaClass, getPrimaryKeyWhere(obj))

    @WorkerThread
    override fun <T> find(clazz: Class<T>, vararg key: Any): T? = find(clazz, getPrimaryKeyWhere(clazz, *key))

    @WorkerThread
    private fun <T> find(clazz: Class<T>, where: Expression): T? {
        Query.select(clazz).where(where).getCursor(this).use { c ->
            if (c.count > 0) {
                c.moveToFirst()
                return getMapper(clazz).toObject(c)
            }
        }

        // default to null
        return null
    }

    /**
     * retrieve all objects of the specified type
     *
     * @param clazz the type of object to retrieve
     * @return
     */
    @WorkerThread
    fun <T> get(clazz: Class<T>): List<T> = get(Query.select(clazz))

    @WorkerThread
    override fun <T> get(query: Query<T>) = getCursor(query.projection()).use { c ->
        val mapper = getMapper(query.mTable.mType)
        c.map { mapper.toObject(it) }
    }
    // endregion Read-Only
    // endregion Queries

    // region Transaction Management
    @WorkerThread
    fun newTransaction() = newTransaction(writableDatabase)

    @WorkerThread
    protected fun newTransaction(db: SQLiteDatabase) = Transaction.newTransaction(db)

    @WorkerThread
    fun beginTransaction() = newTransaction().beginTransaction()

    @WorkerThread
    fun <T, X : Throwable?> inTransaction(closure: Closure<T, X>): T = inTransaction(writableDatabase, true, closure)

    @WorkerThread
    fun <T, X : Throwable?> inNonExclusiveTransaction(closure: Closure<T, X>): T =
        inTransaction(writableDatabase, false, closure)

    @WorkerThread
    fun <T, X : Throwable?> inNonExclusiveTransaction(db: SQLiteDatabase, closure: Closure<T, X>): T =
        inTransaction(db, false, closure)

    @WorkerThread
    protected fun <T, X : Throwable?> inTransaction(
        db: SQLiteDatabase,
        exclusive: Boolean = true,
        closure: Closure<T, X>
    ): T = db.transaction(exclusive) { closure.run() }

    @WorkerThread
    fun <T> transaction(exclusive: Boolean = true, body: SQLiteDatabase.() -> T): T =
        writableDatabase.transaction(exclusive, body)

    @WorkerThread
    protected inline fun <T> SQLiteDatabase.transaction(
        exclusive: Boolean = true,
        body: SQLiteDatabase.() -> T
    ): T = with(newTransaction(this)) {
        try {
            beginTransaction(exclusive)
            val result = body(this@transaction)
            setTransactionSuccessful()
            return result
        } finally {
            endTransaction().recycle()
        }
    }
    // endregion Transaction Management

    // region LastSync tracking
    init {
        registerType(
            LastSyncTable::class.java,
            LastSyncTable.TABLE_NAME,
            null,
            null,
            LastSyncTable.SQL_WHERE_PRIMARY_KEY
        )
    }

    fun getLastSyncTime(vararg key: Any) =
        Query.select(LastSyncTable::class.java).projection(LastSyncTable.COLUMN_LAST_SYNCED)
            .where(LastSyncTable.SQL_WHERE_PRIMARY_KEY.args(key.joinToString(":")))
            .getCursor(this)
            .use { if (it.moveToFirst()) it.getLong(LastSyncTable.COLUMN_LAST_SYNCED, 0L)!! else 0 }

    fun updateLastSyncTime(vararg key: Any) {
        val values = ContentValues().apply {
            put(LastSyncTable.COLUMN_KEY, key.joinToString(":"))
            put(LastSyncTable.COLUMN_LAST_SYNCED, System.currentTimeMillis())
        }
        // update the last sync time, we can use replace since this is just a keyed timestamp
        writableDatabase.transaction(false) { replace(getTable(LastSyncTable::class.java), null, values) }
    }
    // endregion LastSync tracking
}
