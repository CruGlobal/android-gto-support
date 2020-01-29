package org.ccci.gto.android.common.db

import android.os.Parcelable
import androidx.annotation.RestrictTo
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.ccci.gto.android.common.db.Table.Companion.forClass

@Parcelize
class Join<S, T> private constructor(
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) val target: Table<T>,
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) val base: Join<S, *>? = null,
    private val type: String? = null,
    private val on: Expression? = null
) : Parcelable {
    companion object {
        @JvmField
        val NO_JOINS = emptyArray<Join<*, *>>()

        @Deprecated("Since v3.3.0, use source.join() instead", ReplaceWith("source.join(target)"))
        fun <S, T> create(source: Table<S>, target: Table<T>) = source.join(target)

        fun <S, T> create(target: Table<T>) = Join<S, T>(target = target)
    }

    private constructor(
        join: Join<S, T>,
        target: Table<T> = join.target,
        base: Join<S, *>? = join.base,
        type: String? = join.type,
        on: Expression? = join.on
    ) : this(target, base, type, on)
    fun type(type: String?) = Join(this, type = type)
    fun on(on: Expression?) = Join(this, on = on)
    fun andOn(on: Expression) = Join(this, on = this.on?.and(on) ?: on)

    fun <T2> join(target: Class<T2>) = join(forClass(target))
    fun <T2> join(target: Table<T2>) = Join(target = target, base = this)

    @Transient
    @IgnoredOnParcel
    private var sqlJoin: QueryComponent? = null

    internal fun buildSql(dao: AbstractDao): QueryComponent {
        return sqlJoin ?: (base?.buildSql(dao) + buildString {
            if (type != null) append(' ').append(type)
            append(" JOIN ").append(target.sqlTable(dao))
        } + on?.buildSql(dao)?.toQueryComponent()?.prepend(" ON ")).also { sqlJoin = it }
    }
}
