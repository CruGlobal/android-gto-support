package org.ccci.gto.android.common.db;

import static org.ccci.gto.android.common.db.AbstractDao.bindValues;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import org.ccci.gto.android.common.util.ArrayUtils;

public final class Join<S, T> implements Parcelable {
    public static final Join[] NO_JOINS = new Join[0];

    @Nullable
    private final Join<S, ?> mBase;

    @NonNull
    private final Class<T> mTarget;

    @NonNull
    private final String mType;

    @Nullable
    private final String mOn;

    @NonNull
    private final String[] mArgs;

    private Join(@NonNull final Class<T> target) {
        this(null, target, null, null, null);
    }

    private Join(@Nullable final Join<S, ?> base, @NonNull final Class<T> target, @Nullable final String type,
                 @Nullable final String on, @Nullable final String[] args) {
        mBase = base;
        mTarget = target;
        mType = type != null ? type : "";
        mOn = on;
        mArgs = args != null ? args : new String[0];
    }

    @SuppressWarnings("unchecked")
    Join(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
        mBase = in.readParcelable(loader);
        try {
            mTarget = (Class<T>) Class.forName(in.readString(), true, loader);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        mType = in.readString();
        mOn = in.readString();
        mArgs = in.createStringArray();
    }

    @NonNull
    public static <S, T> Join<S, T> create(@NonNull final Class<S> source, @NonNull final Class<T> target) {
        return new Join<>(target);
    }

    @NonNull
    public final Join<S, T> type(@Nullable final String type) {
        return new Join<>(mBase, mTarget, type, mOn, mArgs);
    }

    @NonNull
    public final Join<S, T> on(@Nullable final String on, @NonNull final Object... args) {
        return on(on, bindValues(args));
    }

    @NonNull
    public final Join<S, T> on(@Nullable final String on, @Nullable final String... args) {
        return new Join<>(mBase, mTarget, mType, on, null).args(args);
    }

    @NonNull
    public final Join<S, T> andOn(@NonNull final String on, @NonNull final Object... args) {
        return andOn(on, bindValues(args));
    }

    @NonNull
    public final Join<S, T> andOn(@NonNull final String on, @Nullable final String... args) {
        return new Join<>(mBase, mTarget, mType, !TextUtils.isEmpty(mOn) ? mOn + " AND " + on : on,
                          ArrayUtils.merge(String.class, mArgs, args));
    }

    @NonNull
    public final Join<S, T> args(@NonNull final Object... args) {
        return args(bindValues(args));
    }

    @NonNull
    public final Join<S, T> args(@Nullable final String... args) {
        return new Join<>(mBase, mTarget, mType, mOn, args);
    }

    @NonNull
    public final <T2> Join<S, T2> join(final Class<T2> target) {
        return new Join<>(this, target, null, null, null);
    }

    @NonNull
    final Pair<String, String[]> build(@NonNull final AbstractDao dao) {
        final Pair<String, String[]> base = mBase != null ? mBase.build(dao) : Pair.create("", new String[0]);

        // build SQL
        final StringBuilder sql = new StringBuilder(base.first.length() + 32 + mType.length());
        sql.append(" ").append(base.first);
        sql.append(" ").append(mType);
        sql.append(" JOIN ").append(dao.getTable(mTarget));
        if (mOn != null && mOn.length() > 0) {
            sql.append(" ON ").append(mOn);
        }

        // return built JOIN
        return Pair.create(sql.toString(), ArrayUtils.merge(String.class, base.second, mArgs));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel out, int flags) {
        out.writeParcelable(mBase, flags);
        out.writeString(mTarget.getName());
        out.writeString(mType);
        out.writeString(mOn);
        out.writeStringArray(mArgs);
    }

    public static final Creator<Join> CREATOR;
    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            CREATOR = new HoneycombMR1JoinCreator();
        } else {
            CREATOR = new JoinCreator();
        }
    }

    private static class HoneycombMR1JoinCreator implements Creator<Join> {
        @Override
        public Join createFromParcel(@NonNull final Parcel in) {
            return new Join(in, null);
        }

        @Override
        public Join[] newArray(final int size) {
            return new Join[size];
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private static class JoinCreator extends HoneycombMR1JoinCreator implements ClassLoaderCreator<Join> {
        @Override
        public Join createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            return new Join(in, loader);
        }
    }
}
