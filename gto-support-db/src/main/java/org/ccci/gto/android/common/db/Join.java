package org.ccci.gto.android.common.db;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Join<S, T> implements Parcelable {
    public static final Join[] NO_JOINS = new Join[0];

    @Nullable
    private final Join<S, ?> mBase;

    @NonNull
    private final Class<T> mTarget;

    @NonNull
    private final String mType;

    @NonNull
    private final String mOn;

    private Join(@NonNull final Class<T> target) {
        this(null, target, null, null);
    }

    private Join(@Nullable final Join<S, ?> base, @NonNull final Class<T> target, @Nullable final String type,
                 @Nullable final String on) {
        mBase = base;
        mTarget = target;
        mType = type != null ? type : "";
        mOn = on != null ? on : "";
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
    }

    @NonNull
    public static <S, T> Join<S, T> create(@NonNull final Class<S> source, @NonNull final Class<T> target) {
        return new Join<>(target);
    }

    @NonNull
    public final Join<S, T> type(final String type) {
        return new Join<>(mBase, mTarget, type, mOn);
    }

    @NonNull
    public final Join<S, T> on(final String on) {
        return new Join<>(mBase, mTarget, mType, on);
    }

    @NonNull
    public final <T2> Join<S, T2> join(final Class<T2> target) {
        return new Join<>(this, target, null, null);
    }

    @NonNull
    public final String build(@NonNull final AbstractDao dao) {
        final String base = mBase != null ? mBase.build(dao) : "";
        final StringBuilder sb = new StringBuilder(base.length() + 32 + mType.length() + mOn.length());
        sb.append(" ").append(base);
        sb.append(" ").append(mType);
        sb.append(" JOIN ").append(dao.getTable(mTarget));
        if (mOn.length() > 0) {
            sb.append(" ON ").append(mOn);
        }
        return sb.toString();
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
    }

    public static final Creator<Join> CREATOR;
    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            CREATOR = new Creator<Join>() {
                @Override
                public Join createFromParcel(@NonNull final Parcel in) {
                    return new Join(in, null);
                }

                @Override
                public Join[] newArray(final int size) {
                    return new Join[size];
                }
            };
        } else {
            CREATOR = new ClassLoaderCreator<Join>() {
                @Override
                public Join createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
                    return new Join(in, loader);
                }

                @Override
                public Join createFromParcel(@NonNull final Parcel in) {
                    return createFromParcel(in, null);
                }

                @Override
                public Join[] newArray(final int size) {
                    return new Join[size];
                }
            };
        }
    }
}
