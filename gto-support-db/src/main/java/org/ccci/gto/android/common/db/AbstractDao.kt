package org.ccci.gto.android.common.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteTransactionListener
import androidx.annotation.WorkerThread
import androidx.collection.SimpleArrayMap
import java.util.Date
import java.util.Locale
import org.ccci.gto.android.common.db.CommonTables.LastSyncTable
import org.ccci.gto.android.common.util.ArrayUtils
import org.ccci.gto.android.common.util.database.getLong
import org.ccci.gto.android.common.util.database.map
import org.ccci.gto.android.common.util.kotlin.threadLocal

abstract class AbstractDao(private val helper: SQLiteOpenHelper) : Dao {
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
                is Locale -> it.toLanguageTag()
                else -> it.toString()
            }
        }.toTypedArray()
    }

    final override val services = mutableMapOf<Class<*>, Any>()
    @get:WorkerThread
    protected val readableDatabase: SQLiteDatabase get() = helper.readableDatabase
    @get:WorkerThread
    protected val writableDatabase: SQLiteDatabase get() = helper.writableDatabase

    // region Registered Types
    private val tableTypes = SimpleArrayMap<Class<*>, TableType>()

    protected inline fun <reified T : Any> registerType(
        table: String,
        projection: Array<String>? = null,
        mapper: Mapper<T>? = null,
        pkWhere: Expression? = null,
    ) = registerType(T::class.java, table, projection, mapper, pkWhere)

    protected fun <T : Any> registerType(
        clazz: Class<T>,
        table: String,
        projection: Array<String>? = null,
        mapper: Mapper<T>? = null,
        pkWhere: Expression? = null,
    ) {
        tableTypes.put(clazz, TableType(table, projection, mapper, pkWhere))
    }

    internal open fun tableName(clazz: Class<*>) =
        tableTypes.get(clazz)?.table ?: throw IllegalArgumentException("invalid class specified: ${clazz.name}")
    protected fun getTable(clazz: Class<*>) = tableName(clazz)

    fun getFullProjection(table: Table<*>) = getFullProjection(table.type)
    override fun getFullProjection(clazz: Class<*>) =
        tableTypes.get(clazz)?.projection ?: throw IllegalArgumentException("invalid class specified: ${clazz.name}")

    protected fun getPrimaryKeyWhere(clazz: Class<*>, vararg key: Any) = getPrimaryKeyWhere(clazz).args(*key)
    protected fun getPrimaryKeyWhere(clazz: Class<*>) =
        tableTypes.get(clazz)?.primaryWhere ?: throw IllegalArgumentException("invalid class specified: ${clazz.name}")

    protected open fun getPrimaryKeyWhere(obj: Any): Expression =
        throw IllegalArgumentException("unsupported object: ${obj.javaClass.name}")

    @Suppress("UNCHECKED_CAST")
    protected fun <T> getMapper(clazz: Class<T>) = tableTypes.get(clazz)?.mapper as? Mapper<T>
        ?: throw IllegalArgumentException("invalid class specified: ${clazz.name}")
    // endregion Registered Types

    // region Queries
    // region Read-Only
    @WorkerThread
    fun <T : Any> refresh(obj: T): T? = find(obj.javaClass, getPrimaryKeyWhere(obj))

    @WorkerThread
    final override fun <T : Any> find(clazz: Class<T>, vararg key: Any): T? =
        find(clazz, getPrimaryKeyWhere(clazz, *key))

    @WorkerThread
    private fun <T : Any> find(clazz: Class<T>, where: Expression): T? {
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
    fun <T : Any> get(clazz: Class<T>): List<T> = get(Query.select(clazz))

    @WorkerThread
    final override fun <T : Any> get(query: Query<T>) = getCursor(query.projection()).use { c ->
        val mapper = getMapper(query.table.type)
        c.map { mapper.toObject(it) }
    }

    @WorkerThread
    final override fun getCursor(query: Query<*>): Cursor {
        var projection = query.projection?.toTypedArray() ?: getFullProjection(query.table.type)
        var orderBy = query.orderBy

        // prefix projection and orderBy when we have joins
        if (query.joins.isNotEmpty()) {
            val prefix = query.table.sqlPrefix(this)
            projection = projection.map { if (it.contains(".")) it else prefix + it }.toTypedArray()
            orderBy = orderBy?.prefixOrderByFieldsWith(prefix)
        }

        // generate "FROM {}" SQL
        val from = query.buildSqlFrom(this)
        var args = from.args

        // generate "WHERE {}" SQL
        val where = query.buildSqlWhere(this)
        args = ArrayUtils.merge(String::class.java, args, where?.args)

        // handle GROUP BY {} HAVING {}
        var groupBy: String? = null
        var having: String? = null
        if (query.groupBy.isNotEmpty()) {
            // generate "GROUP BY {}" SQL
            groupBy = query.groupBy.joinToString(",") { it.buildSql(this).sql }

            // generate "HAVING {}" SQL
            val havingRaw = query.buildSqlHaving(this)
            having = havingRaw?.sql
            args = ArrayUtils.merge(String::class.java, args, havingRaw?.args)
        }

        // execute actual query
        val c = transaction(exclusive = false, readOnly = true) {
            it.query(query.isDistinct, from.sql, projection, where?.sql, args, groupBy, having, orderBy, query.sqlLimit)
        }
        c.moveToPosition(-1)
        return c
    }

    // endregion Read-Only

    // region Read-Write
    @WorkerThread
    final override fun <T : Any> insert(obj: T, conflictAlgorithm: Int): Long {
        val clazz = obj.javaClass
        val table = tableName(clazz)
        val values = getMapper(clazz).toContentValues(obj, getFullProjection(clazz))
        return transaction(exclusive = false) { db ->
            invalidateClass(clazz)
            db.insertWithOnConflict(table, null, values, conflictAlgorithm)
        }
    }

    @WorkerThread
    fun replace(obj: Any) {
        transaction { _ ->
            delete(obj)
            insert(obj)
        }
    }

    @WorkerThread
    fun update(obj: Any) = update(obj, projection = getFullProjection(obj.javaClass))

    @WorkerThread
    final override fun <T : Any> update(obj: T, conflictAlgorithm: Int, vararg projection: String): Int {
        val type = obj.javaClass
        val values = getMapper(type).toContentValues(obj, projection)
        return update(type, values, getPrimaryKeyWhere(obj), conflictAlgorithm)
    }

    @WorkerThread
    final override fun <T : Any> update(
        obj: T,
        where: Expression?,
        conflictAlgorithm: Int,
        vararg projection: String,
    ): Int {
        val type = obj.javaClass
        return update(type, getMapper(type).toContentValues(obj, projection), where, conflictAlgorithm)
    }

    /**
     * Update the specified `values` for objects of type `type` that match the specified `where` clause.
     * If `where` is null, all objects of type `type` will be updated
     *
     * @param type the type of Object to update
     * @param values the new values for the specified object
     * @param where an optional [Expression] to narrow the scope of which objects are updated
     * @param conflictAlgorithm the conflict algorithm to use when updating the database
     * @return the number of rows affected
     */
    @JvmOverloads
    @WorkerThread
    protected fun update(
        type: Class<*>,
        values: ContentValues,
        where: Expression?,
        conflictAlgorithm: Int = SQLiteDatabase.CONFLICT_NONE,
    ): Int {
        val table = tableName(type)
        val w = where?.buildSql(this)
        return transaction(exclusive = false) { db ->
            invalidateClass(type)
            db.updateWithOnConflict(table, values, w?.sql, w?.args, conflictAlgorithm)
        }
    }

    @WorkerThread
    fun updateOrInsert(obj: Any) = updateOrInsert(obj, projection = getFullProjection(obj.javaClass))

    @WorkerThread
    @Suppress("IMPLICIT_CAST_TO_ANY")
    final override fun updateOrInsert(obj: Any, conflictAlgorithm: Int, vararg projection: String) {
        transaction { _ ->
            when {
                refresh(obj) != null -> update(obj, conflictAlgorithm, *projection)
                else -> insert(obj, conflictAlgorithm)
            }
        }
    }

    @WorkerThread
    final override fun delete(obj: Any) = delete(obj.javaClass, getPrimaryKeyWhere(obj))

    @WorkerThread
    final override fun delete(clazz: Class<*>, where: Expression?) {
        val w = where?.buildSql(this)
        transaction(exclusive = false) { db ->
            db.delete(tableName(clazz), w?.sql, w?.args)
            invalidateClass(clazz)
        }
    }
    // endregion Read-Write
    // endregion Queries

    // region Transaction Management
    @WorkerThread
    private fun newTransaction(db: SQLiteDatabase) = Transaction.newTransaction(db).apply {
        transactionListener = InvalidationListener(this)
    }

    @WorkerThread
    fun <T, X : Throwable?> inTransaction(closure: Closure<T, X>): T = inTransaction(writableDatabase, true, closure)

    @WorkerThread
    fun <T, X : Throwable?> inNonExclusiveTransaction(closure: Closure<T, X>): T =
        inTransaction(writableDatabase, false, closure)

    @WorkerThread
    protected fun <T, X : Throwable?> inTransaction(
        db: SQLiteDatabase,
        exclusive: Boolean = true,
        closure: Closure<T, X>,
    ): T = db.transaction(exclusive) { closure.run() }

    @WorkerThread
    override fun <T> transaction(exclusive: Boolean, body: () -> T): T =
        writableDatabase.transaction(exclusive) { body() }

    @WorkerThread
    protected fun <T> transaction(
        exclusive: Boolean = true,
        readOnly: Boolean = false,
        body: (SQLiteDatabase) -> T,
    ): T = (if (readOnly) readableDatabase else writableDatabase).transaction(exclusive, body)

    @WorkerThread
    private inline fun <T> SQLiteDatabase.transaction(
        exclusive: Boolean = true,
        body: (SQLiteDatabase) -> T,
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
            .use { if (it.moveToFirst()) it.getLong(LastSyncTable.COLUMN_LAST_SYNCED, 0) else 0 }

    fun updateLastSyncTime(vararg key: Any) {
        val values = ContentValues().apply {
            put(LastSyncTable.COLUMN_KEY, key.joinToString(":"))
            put(LastSyncTable.COLUMN_LAST_SYNCED, System.currentTimeMillis())
        }
        // update the last sync time, we can use replace since this is just a keyed timestamp
        transaction(exclusive = false) { db ->
            db.replace(tableName(LastSyncTable::class.java), null, values)
            invalidateClass(LastSyncTable::class.java)
        }
    }
    // endregion LastSync tracking

    // region Data Invalidation
    private var currentTransaction by threadLocal<Transaction>()

    private inner class InvalidationListener(
        private val transaction: Transaction,
    ) : SQLiteTransactionListener, Transaction.Listener {
        private var commited = false

        override fun onBegin() {
            transaction.parent = currentTransaction
            currentTransaction = transaction
        }

        override fun onCommit() {
            currentTransaction = transaction.parent
            commited = true
        }

        override fun onRollback() {
            currentTransaction = transaction.parent
        }

        override fun onFinished() {
            if (commited) transaction.invalidatedClasses.forEach { invalidateClass(it) }
        }
    }

    private val invalidationCallbacks = mutableListOf<Dao.InvalidationCallback>()

    final override fun registerInvalidationCallback(callback: Dao.InvalidationCallback) =
        synchronized(invalidationCallbacks) { invalidationCallbacks += callback }

    final override fun unregisterInvalidationCallback(callback: Dao.InvalidationCallback) =
        synchronized(invalidationCallbacks) { invalidationCallbacks -= callback }

    @WorkerThread
    protected fun invalidateClass(clazz: Class<*>) {
        currentTransaction?.let {
            it.invalidatedClasses.add(clazz)
            return
        }

        synchronized(invalidationCallbacks) { invalidationCallbacks.toTypedArray() }.forEach { it.onInvalidate(clazz) }
    }
    // endregion Data Invalidation

    protected fun compileExpression(expression: Expression) = expression.buildSql(this)
}

internal fun String.prefixOrderByFieldsWith(prefix: String): String = when {
    contains(",") -> split(",").joinToString(",") { it.prefixOrderByFieldsWith(prefix) }
    !contains(".") -> "$prefix${trimStart()}"
    else -> this
}
