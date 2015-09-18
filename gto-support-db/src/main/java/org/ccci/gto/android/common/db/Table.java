package org.ccci.gto.android.common.db;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Table<T> implements Parcelable {
    @NonNull
    final Class<T> mType;
    @Nullable
    final String mAlias;

    @Nullable
    private transient String mSqlName;

    private Table(@NonNull final Class<T> type, @Nullable final String alias) {
        mType = type;
        mAlias = alias;
    }

    @NonNull
    public static <T> Table<T> forClass(@NonNull final Class<T> type) {
        return new Table<>(type, null);
    }

    @NonNull
    public final Table<T> as(@Nullable final String alias) {
        return new Table<>(mType, alias);
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
    final String buildSqlName(@NonNull final AbstractDao dao) {
        // build the name if we haven't built it already
        if (mSqlName == null) {
            final StringBuilder sql = new StringBuilder(dao.getTable(mType));
            if (mAlias != null) {
                sql.append(" AS ").append(mAlias);
            }
            mSqlName = sql.toString();
        }

        return mSqlName;
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

    public static final Creator<Table> CREATOR;

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            CREATOR = new HoneycombMR1TableCreator();
        } else {
            CREATOR = new TableCreator();
        }
    }

    private static class HoneycombMR1TableCreator implements Creator<Table> {
        @Override
        public Table createFromParcel(@NonNull final Parcel in) {
            return new Table(in, null);
        }

        @Override
        public Table[] newArray(final int size) {
            return new Table[size];
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private static class TableCreator extends HoneycombMR1TableCreator implements ClassLoaderCreator<Table> {
        @Override
        public Table createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            return new Table(in, loader);
        }
    }
}
