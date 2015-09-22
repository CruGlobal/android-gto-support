package org.ccci.gto.android.common.db;

import static org.ccci.gto.android.common.db.AbstractDao.bindValues;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import org.ccci.gto.android.common.util.ArrayUtils;

public abstract class Expression implements Parcelable {
    Expression() {
    }

    @NonNull
    protected abstract Pair<String, String[]> buildSql(@NonNull AbstractDao dao);

    @NonNull
    public final Binary and(@NonNull final Expression expression) {
        return binaryExpr(Binary.AND, expression);
    }

    @NonNull
    public final Binary eq(@NonNull final Expression expression) {
        return binaryExpr(Binary.EQ, expression);
    }

    @NonNull
    public final Binary or(@NonNull final Expression expression) {
        return binaryExpr(Binary.OR, expression);
    }

    @NonNull
    protected Binary binaryExpr(@NonNull final String op, @NonNull final Expression expression) {
        return new Binary(op, this, expression);
    }

    @NonNull
    public static Field field(@NonNull final String name) {
        return new Field(null, name);
    }

    @NonNull
    public static Field field(@NonNull final Table<?> table, @NonNull final String name) {
        return new Field(table, name);
    }

    @NonNull
    public static Field field(@NonNull final Class<?> type, @NonNull final String name) {
        return field(Table.forClass(type), name);
    }

    @NonNull
    public static Literal literal(@NonNull final Object value) {
        return new Literal(bindValues(value)[0]);
    }

    @NonNull
    public static Literal literal(@NonNull final String value) {
        return new Literal(value);
    }

    @NonNull
    public static Raw raw(@NonNull final String expr, @NonNull final Object... args) {
        return new Raw(expr, bindValues(args));
    }

    @NonNull
    public static Raw raw(@NonNull final String expr, @NonNull final String... args) {
        return new Raw(expr, args);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
    }

    public static class Literal extends Expression {
        @Nullable
        private final String mValue;

        Literal(@Nullable final String value) {
            mValue = value;
        }

        Literal(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            mValue = in.readString();
        }

        @NonNull
        @Override
        protected Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
            return Pair.create("?", new String[] {mValue});
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeString(mValue);
        }

        public static final Creator<Literal> CREATOR =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2 ? new HoneycombMR1LiteralCreator() :
                        new LiteralCreator();

        private static class HoneycombMR1LiteralCreator implements Creator<Literal> {
            @Override
            public Literal createFromParcel(@NonNull final Parcel in) {
                return new Literal(in, null);
            }

            @Override
            public Literal[] newArray(final int size) {
                return new Literal[size];
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        private static class LiteralCreator extends HoneycombMR1LiteralCreator implements ClassLoaderCreator<Literal> {
            @Override
            public Literal createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
                return new Literal(in, loader);
            }
        }
    }

    public static class Field extends Expression {
        @Nullable
        private final Table<?> mTable;
        @NonNull
        private final String mName;

        @Nullable
        private transient Pair<String, String[]> mSql;

        Field(@Nullable final Table<?> table, @NonNull final String name) {
            mTable = table;
            mName = name;
        }

        Field(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            mTable = in.readParcelable(loader);
            mName = in.readString();
        }

        @NonNull
        @Override
        protected Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
            // generate SQL for this field
            if (mSql == null) {
                final StringBuilder sql = new StringBuilder();
                if (mTable != null) {
                    sql.append(mTable.sqlPrefix(dao));
                }
                sql.append(mName);
                mSql = Pair.create(sql.toString(), null);
            }

            return mSql;
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(mTable, 0);
            out.writeString(mName);
        }

        public static final Creator<Field> CREATOR =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2 ? new HoneycombMR1FieldCreator() :
                        new FieldCreator();

        private static class HoneycombMR1FieldCreator implements Creator<Field> {
            @Override
            public Field createFromParcel(@NonNull final Parcel in) {
                return new Field(in, null);
            }

            @Override
            public Field[] newArray(final int size) {
                return new Field[size];
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        private static class FieldCreator extends HoneycombMR1FieldCreator implements ClassLoaderCreator<Field> {
            @Override
            public Field createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
                return new Field(in, loader);
            }
        }
    }

    public static class Raw extends Expression {
        @NonNull
        private final String mExpr;
        @NonNull
        private final String[] mArgs;

        Raw(@NonNull final String expr, @NonNull final String... args) {
            mExpr = expr;
            mArgs = args;
        }

        Raw(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            mExpr = in.readString();
            mArgs = in.createStringArray();
        }

        @NonNull
        Raw args(@NonNull final String... args) {
            return new Raw(mExpr, args);
        }

        @NonNull
        @Override
        protected Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
            return Pair.create(mExpr, mArgs);
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeString(mExpr);
            out.writeStringArray(mArgs);
        }

        public static final Creator<Raw> CREATOR =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2 ? new HoneycombMR1RawCreator() :
                        new RawCreator();

        private static class HoneycombMR1RawCreator implements Creator<Raw> {
            @Override
            public Raw createFromParcel(@NonNull final Parcel in) {
                return new Raw(in, null);
            }

            @Override
            public Raw[] newArray(final int size) {
                return new Raw[size];
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        private static class RawCreator extends HoneycombMR1RawCreator
                implements ClassLoaderCreator<Raw> {
            @Override
            public Raw createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
                return new Raw(in, loader);
            }
        }
    }

    public static class Binary extends Expression {
        static final String AND = "AND";
        static final String OR = "OR";
        static final String EQ = "==";

        @NonNull
        private final String mOp;
        @NonNull
        private final Expression[] mExprs;

        @Nullable
        private transient Pair<String, String[]> mSql;

        Binary(@NonNull final String op, @NonNull final Expression... exprs) {
            mOp = op;
            mExprs = exprs;
        }

        Binary(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            mOp = in.readString();
            final Parcelable[] exprs = in.readParcelableArray(loader);
            mExprs = new Expression[exprs.length];
            for (int i = 0; i < exprs.length; i++) {
                mExprs[i] = (Expression) exprs[i];
            }
        }

        @NonNull
        @Override
        protected Binary binaryExpr(@NonNull final String op, @NonNull final Expression expression) {
            // chain binary expressions together when possible
            switch (mOp) {
                case AND:
                case OR:
                    if (mOp.equals(op)) {
                        return new Binary(mOp,
                                          ArrayUtils.merge(Expression.class, mExprs, new Expression[] {expression}));
                    }
            }

            return super.binaryExpr(op, expression);
        }

        @NonNull
        @Override
        protected Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
            // generate SQL if it hasn't been generated yet
            if (mSql == null) {
                boolean first = true;
                final StringBuilder sql = new StringBuilder();
                String[] args = null;
                sql.append('(');
                for (final Expression expr : mExprs) {
                    if (!first) {
                        sql.append(' ').append(mOp).append(' ');
                    }
                    final Pair<String, String[]> resp = expr.buildSql(dao);
                    sql.append(resp.first);
                    args = ArrayUtils.merge(String.class, args, resp.second);
                    first = false;
                }
                sql.append(')');
                mSql = Pair.create(sql.toString(), args);
            }

            return mSql;
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeString(mOp);
            out.writeParcelableArray(mExprs, 0);
        }

        public static final Creator<Binary> CREATOR =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2 ? new HoneycombMR1BinaryExpressionCreator() :
                        new BinaryExpressionCreator();

        private static class HoneycombMR1BinaryExpressionCreator implements Creator<Binary> {
            @Override
            public Binary createFromParcel(@NonNull final Parcel in) {
                return new Binary(in, null);
            }

            @Override
            public Binary[] newArray(final int size) {
                return new Binary[size];
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        private static class BinaryExpressionCreator extends HoneycombMR1BinaryExpressionCreator
                implements ClassLoaderCreator<Binary> {
            @Override
            public Binary createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
                return new Binary(in, loader);
            }
        }
    }
}
