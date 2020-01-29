package org.ccci.gto.android.common.db

import android.os.Parcelable
import android.util.Pair
import androidx.annotation.RestrictTo
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.ccci.gto.android.common.db.Table.Companion.forClass
import org.ccci.gto.android.common.util.ArrayUtils

@Parcelize
class Join<S, T> private constructor(
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) val target: Table<T>,
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) val base: Join<S, *>? = null,
    private val type: String = "",
    private val on: Expression? = null
) : Parcelable {
    private constructor(
        join: Join<S, T>,
        target: Table<T> = join.target,
        base: Join<S, *>? = join.base,
        type: String? = join.type,
        on: Expression? = join.on
    ) : this(target, base, type ?: "", on)

    fun type(type: String?) = Join(this, type = type)
    fun on(on: Expression?) = Join(this, on = on)
    fun andOn(on: Expression) = Join(this, on = this.on?.and(on) ?: on)

    fun <T2> join(target: Class<T2>) = join(forClass(target))
    fun <T2> join(target: Table<T2>) = Join(target = target, base = this)

    @Transient
    @IgnoredOnParcel
    private var sqlJoin: Pair<String, Array<String>>? = null

    fun buildSql(dao: AbstractDao): Pair<String, Array<String>> {
        // build join if we haven't built it already
        if (sqlJoin == null) {
            val base = base?.buildSql(dao) ?: Pair.create("", emptyArray())
            var args = base.second
            // build SQL
            val sql = StringBuilder(base.first.length + 32 + type.length)
            sql.append(' ').append(base.first)
            sql.append(' ').append(type)
            sql.append(" JOIN ").append(target.sqlTable(dao))
            if (on != null) {
                val on = on.buildSql(dao)
                sql.append(" ON ").append(on.first)
                args = ArrayUtils.merge(
                    String::class.java,
                    args,
                    on.second
                )
            }
            // save built JOIN
            sqlJoin = Pair.create(sql.toString(), args)
        }
        // return the cached join
        return sqlJoin!!
    }

    companion object {
        @JvmField
        val NO_JOINS: Array<Join<*, *>> = emptyArray()

        fun <S, T> create(source: Table<S>, target: Table<T>): Join<S, T> {
            return create(target)
        }

        fun <S, T> create(target: Table<T>): Join<S, T> {
            return Join(target = target)
        }
    }
}
