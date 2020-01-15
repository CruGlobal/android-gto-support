package org.ccci.gto.android.common.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.ccci.gto.android.common.db.Expression.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class Table<T> implements Parcelable {
    @NonNull
    final Class<T> mType;
    @Nullable
    private final String mAlias;

    @Nullable
    private transient String mSqlTable;
    @Nullable
    private transient String mSqlPrefix;

    private Table(@NonNull final Class<T> type, @Nullable final String alias) {
        mType = type;
        mAlias = alias;
    }

    @NonNull
    public static <T> Table<T> forClass(@NonNull final Class<T> type) {
        return new Table<>(type, null);
    }

    @NonNull
    public Table<T> as(@Nullable final String alias) {
        return new Table<>(mType, alias);
    }

    @NonNull
    public Field field(@NonNull final String field) {
        return new Field(this, field);
    }

    @SuppressWarnings("unchecked")
    Table(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
        try {
            mType = (Class<T>) Class.forName(in.readString(), true, loader);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        mAlias = in.readString();
    }

    @NonNull
    String sqlTable(@NonNull final AbstractDao2 dao) {
        // build the name if we haven't built it already
        if (mSqlTable == null) {
            final StringBuilder sql = new StringBuilder(dao.getTable(mType));
            if (mAlias != null) {
                sql.append(" AS ").append(mAlias);
            }
            mSqlTable = sql.toString();
        }

        return mSqlTable;
    }

    @NonNull
    String sqlPrefix(@NonNull final AbstractDao2 dao) {
        if (mSqlPrefix == null) {
            mSqlPrefix = (mAlias != null ? mAlias : dao.getTable(mType)) + ".";
        }

        return mSqlPrefix;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel out, int flags) {
        out.writeString(mType.getName());
        out.writeString(mAlias);
    }

    public static final Creator<Table> CREATOR = new ClassLoaderCreator<Table>() {
        @Override
        public Table createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            return new Table(in, loader);
        }

        @Override
        public Table createFromParcel(@NonNull final Parcel in) {
            return new Table(in, null);
        }

        @Override
        public Table[] newArray(final int size) {
            return new Table[size];
        }
    };
}
