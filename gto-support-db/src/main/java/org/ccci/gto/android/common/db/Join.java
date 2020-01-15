package org.ccci.gto.android.common.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import org.ccci.gto.android.common.util.ArrayUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

public final class Join<S, T> implements Parcelable {
    public static final Join[] NO_JOINS = new Join[0];

    @Nullable
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    final Join<S, ?> mBase;

    @NonNull
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    final Table<T> mTarget;

    @NonNull
    private final String mType;

    @Nullable
    private final Expression mOn;

    @Nullable
    private transient Pair<String, String[]> mSqlJoin;

    private Join(@NonNull final Table<T> target) {
        this(null, target, null, null);
    }

    private Join(@Nullable final Join<S, ?> base, @NonNull final Table<T> target, @Nullable final String type,
                 @Nullable final Expression on) {
        mBase = base;
        mTarget = target;
        mType = type != null ? type : "";
        mOn = on;
    }

    @SuppressWarnings("unchecked")
    Join(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
        mBase = in.readParcelable(loader);
        mTarget = in.readParcelable(loader);
        mType = in.readString();
        mOn = in.readParcelable(loader);
    }

    @NonNull
    public static <S, T> Join<S, T> create(@NonNull final Table<S> source, @NonNull final Table<T> target) {
        return create(target);
    }

    @NonNull
    public static <S, T> Join<S, T> create(@NonNull final Table<T> target) {
        return new Join<>(target);
    }

    @NonNull
    public Join<S, T> type(@Nullable final String type) {
        return new Join<>(mBase, mTarget, type, mOn);
    }

    @NonNull
    public Join<S, T> on(@Nullable final Expression on) {
        return new Join<>(mBase, mTarget, mType, on);
    }

    @NonNull
    public Join<S, T> andOn(@NonNull final Expression on) {
        return new Join<>(mBase, mTarget, mType, mOn != null ? mOn.and(on) : on);
    }

    @NonNull
    public <T2> Join<S, T2> join(final Class<T2> target) {
        return join(Table.forClass(target));
    }

    @NonNull
    public <T2> Join<S, T2> join(final Table<T2> target) {
        return new Join<>(this, target, null, null);
    }

    @NonNull
    Pair<String, String[]> buildSql(@NonNull final AbstractDao2 dao) {
        // build join if we haven't built it already
        if (mSqlJoin == null) {
            final Pair<String, String[]> base = mBase != null ? mBase.buildSql(dao) : Pair.create("", (String[]) null);
            String[] args = base.second;

            // build SQL
            final StringBuilder sql = new StringBuilder(base.first.length() + 32 + mType.length());
            sql.append(' ').append(base.first);
            sql.append(' ').append(mType);
            sql.append(" JOIN ").append(mTarget.sqlTable(dao));
            if (mOn != null) {
                final Pair<String, String[]> on = mOn.buildSql(dao);
                sql.append(" ON ").append(on.first);
                args = ArrayUtils.merge(String.class, args, on.second);
            }

            // save built JOIN
            mSqlJoin = Pair.create(sql.toString(), args);
        }

        // return the cached join
        return mSqlJoin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel out, final int flags) {
        out.writeParcelable(mBase, flags);
        out.writeParcelable(mTarget, flags);
        out.writeString(mType);
        out.writeParcelable(mOn, flags);
    }

    public static final Creator<Join> CREATOR = new JoinCreator();

    private static class JoinCreator implements ClassLoaderCreator<Join> {
        @Override
        public Join createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            return new Join(in, loader);
        }

        @Override
        public Join createFromParcel(@NonNull final Parcel in) {
            return new Join(in, null);
        }

        @Override
        public Join[] newArray(final int size) {
            return new Join[size];
        }
    }
}
