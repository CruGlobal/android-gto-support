package org.ccci.gto.android.common.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread

interface Dao {
    // region Table Metadata
    fun getFullProjection(clazz: Class<*>): Array<String>
    // endregion Table Metadata

    // region Queries
    // region Read-Only
    @WorkerThread
    fun <T : Any> find(clazz: Class<T>, vararg key: Any): T?

    @WorkerThread
    fun <T : Any> get(query: Query<T>): List<T>

    @WorkerThread
    fun getCursor(clazz: Class<*>) = getCursor(Query.select(clazz))

    @WorkerThread
    fun getCursor(clazz: Class<*>, whereClause: String?, whereBindValues: Array<String>?, orderBy: String?) =
        getCursor(Query.select(clazz).where(whereClause, *whereBindValues.orEmpty()).orderBy(orderBy))

    @WorkerThread
    fun getCursor(query: Query<*>): Cursor
    // endregion Read-Only

    // region Read-Write
    @WorkerThread
    fun <T : Any> insert(obj: T) = insert(obj, SQLiteDatabase.CONFLICT_NONE)

    @WorkerThread
    fun <T : Any> insert(obj: T, conflictAlgorithm: Int): Long

    @WorkerThread
    fun <T : Any> update(obj: T, where: Expression?, vararg projection: String): Int =
        update(obj, where, SQLiteDatabase.CONFLICT_NONE, *projection)

    @WorkerThread
    fun <T : Any> update(obj: T, vararg projection: String) = update(obj, SQLiteDatabase.CONFLICT_NONE, *projection)

    @WorkerThread
    fun <T : Any> update(obj: T, conflictAlgorithm: Int, vararg projection: String): Int

    /**
     * This method updates all objects that match the where Expression based on the provided sample object and projection.
     *
     * @param obj a sample object that is used to find the type and generate the values being set on other objects.
     * @param where a where clause that restricts which objects get updated. If this is null all objects are updated.
     * @param conflictAlgorithm the conflict algorithm to use when updating the database
     * @param projection the fields to update in this call
     * @return the number of rows affected
     */
    @WorkerThread
    fun <T : Any> update(obj: T, where: Expression?, conflictAlgorithm: Int, vararg projection: String): Int

    @WorkerThread
    fun updateOrInsert(obj: Any, vararg projection: String) =
        updateOrInsert(obj, SQLiteDatabase.CONFLICT_NONE, *projection)

    @WorkerThread
    fun updateOrInsert(obj: Any, conflictAlgorithm: Int, vararg projection: String)

    @WorkerThread
    fun delete(obj: Any)

    /**
     * Delete all objects that match the provided where clause. Sending a null where clause will delete all objects.
     *
     * @param clazz The Class of the objects to be deleted
     * @param where An expression describing which objects to delete. Null indicates all objects should be deleted.
     */
    @WorkerThread
    fun delete(clazz: Class<*>, where: Expression?)
    // endregion Read-Write
    // endregion Queries

    @WorkerThread
    fun <T> transaction(exclusive: Boolean = true, body: () -> T): T

    // region Data Invalidation
    fun interface InvalidationCallback {
        @WorkerThread
        fun onInvalidate(clazz: Class<*>)
    }

    fun registerInvalidationCallback(callback: InvalidationCallback)
    fun unregisterInvalidationCallback(callback: InvalidationCallback)
    // endregion Data Invalidation

    // region Services
    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val services: MutableMap<Class<*>, Any>

    @Suppress("UNCHECKED_CAST")
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP, RestrictTo.Scope.SUBCLASSES)
    fun <T : Any> getService(clazz: Class<T>) = synchronized(services) { services[clazz] } as T?

    @Suppress("UNCHECKED_CAST")
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP, RestrictTo.Scope.SUBCLASSES)
    fun <T : Any> getService(clazz: Class<T>, defaultValue: () -> T) =
        synchronized(services) { services.getOrPut(clazz, defaultValue) } as T
    // endregion Services
}

inline fun <reified T : Any> Dao.find(vararg key: Any) = find(T::class.java, *key)
inline fun <T : Any> Query<T>.get(dao: Dao) = dao.get(this)
inline fun Query<*>.getCursor(dao: Dao) = dao.getCursor(this)

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
inline fun <reified T : Any> Dao.getService() = getService(T::class.java)
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
inline fun <reified T : Any> Dao.getService(noinline defaultValue: () -> T) = getService(T::class.java, defaultValue)
