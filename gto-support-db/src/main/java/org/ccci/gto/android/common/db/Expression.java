package org.ccci.gto.android.common.db;

import static org.ccci.gto.android.common.db.AbstractDao.bindValues;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import org.ccci.gto.android.common.compat.ArraysCompat;
import org.ccci.gto.android.common.compat.os.ParcelCompat;
import org.ccci.gto.android.common.util.ArrayUtils;

public abstract class Expression implements Parcelable {
    public static final Literal NULL = new Literal((String) null, true);
    static final String[] NO_ARGS = new String[0];

    Expression() {
    }

    @NonNull
    protected abstract Pair<String, String[]> buildSql(@NonNull AbstractDao dao);

    @NonNull
    public Expression args(@NonNull final Object... args) {
        return args(bindValues(args));
    }

    /**
     * returns the number of "dynamic" arguments in this expression. This may not be the same as the actual number of
     * arguments returned from buildSql
     *
     * @return
     */
    protected int numOfArgs() {
        return 0;
    }

    @NonNull
    public Expression args(@NonNull final String... args) {
        if (args.length > 0) {
            throw new IllegalArgumentException("invalid number of arguments specified");
        }
        return this;
    }

    @NonNull
    public final Binary and(@NonNull final Expression expression) {
        return binaryExpr(Binary.AND, expression);
    }

    @NonNull
    public final Binary eq(@NonNull final Number constant) {
        return eq(constant(constant));
    }

    @NonNull
    public final Binary eq(@NonNull final Expression expression) {
        return binaryExpr(Binary.EQ, expression);
    }

    @NonNull
    public final Binary is(@NonNull final Expression expression) {
        return binaryExpr(Binary.IS, expression);
    }

    @NonNull
    public final Binary or(@NonNull final Expression expression) {
        return binaryExpr(Binary.OR, expression);
    }

    @NonNull
    public final Binary ne(@NonNull final Number constant) {
        return ne(constant(constant));
    }

    @NonNull
    public final Binary ne(@NonNull final Expression expression) {
        return binaryExpr(Binary.NE, expression);
    }

    @NonNull
    public Expression not() {
        return new Unary(Unary.NOT, this);
    }

    @NonNull
    public static Expression not(@NonNull final Expression expression) {
        return expression.not();
    }

    @NonNull
    protected Binary binaryExpr(@NonNull final String op, @NonNull final Expression expression) {
        return new Binary(op, this, expression);
    }

    @NonNull
    public Raw toRaw(@NonNull final AbstractDao dao) {
        final Pair<String, String[]> sql = buildSql(dao);
        return raw(sql.first, sql.second);
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
    @Deprecated
    public static Literal literal(@NonNull final Object value) {
        return bind(value);
    }

    @NonNull
    @Deprecated
    public static Literal literal(@NonNull final Number value) {
        return bind(value);
    }

    @NonNull
    @Deprecated
    public static Literal literal(@NonNull final String value) {
        return bind(value);
    }

    @NonNull
    public static Literal bind() {
        return new Literal((String) null, false);
    }

    @NonNull
    public static Literal bind(@NonNull final Object value) {
        return new Literal(bindValues(value)[0], false);
    }

    @NonNull
    public static Literal bind(@NonNull final Number value) {
        return new Literal(value, false);
    }

    @NonNull
    public static Literal bind(@NonNull final String value) {
        return new Literal(value, false);
    }

    @NonNull
    public static Literal constant(@NonNull final Number value) {
        return new Literal(value, true);
    }

    @NonNull
    public static Raw raw(@NonNull final String expr, @NonNull final Object... args) {
        return new Raw(expr, bindValues(args));
    }

    @NonNull
    public static Raw raw(@NonNull final String expr, @Nullable final String... args) {
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
        private final String mStrValue;
        @Nullable
        private final Number mNumValue;
        private final boolean mConstant;

        Literal(@Nullable final Number value, final boolean constant) {
            this(null, value, constant);
        }

        Literal(@Nullable final String value, final boolean constant) {
            this(value, null, constant);
        }

        private Literal(@Nullable final String strValue, @Nullable final Number numValue, final boolean constant) {
            mStrValue = strValue;
            mNumValue = numValue;
            mConstant = constant;
        }

        Literal(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            mStrValue = in.readString();
            mNumValue = (Number) in.readValue(loader);
            mConstant = ParcelCompat.readBoolean(in);
        }

        @Override
        protected int numOfArgs() {
            return mConstant ? 0 : 1;
        }

        @NonNull
        @Override
        public Literal args(@NonNull final String... args) {
            if (args.length != (mConstant ? 0 : 1)) {
                throw new IllegalArgumentException("incorrect number of args specified");
            }
            return mConstant ? this : new Literal(args[0], false);
        }

        @NonNull
        @Override
        protected Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
            if (mConstant) {
                if (mNumValue != null) {
                    return Pair.create(mNumValue.toString(), NO_ARGS);
                } else if (mStrValue != null) {
                    //TODO: how should we handle non-null constant string values?
                } else {
                    return Pair.create("NULL", NO_ARGS);
                }
            }

            return Pair.create("?", new String[] {mNumValue != null ? mNumValue.toString() : mStrValue});
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeString(mStrValue);
            out.writeValue(mNumValue);
            ParcelCompat.writeBoolean(out, mConstant);
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
                mSql = Pair.create(sql.toString(), NO_ARGS);
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

        Raw(@NonNull final String expr, @Nullable final String... args) {
            mExpr = expr;
            mArgs = args != null ? args : NO_ARGS;
        }

        Raw(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            mExpr = in.readString();
            mArgs = in.createStringArray();
        }

        @Override
        protected int numOfArgs() {
            return mArgs.length;
        }

        @NonNull
        @Override
        public Raw args(@NonNull Object... args) {
            return args(bindValues(args));
        }

        @NonNull
        @Override
        public Raw args(@NonNull final String... args) {
            return new Raw(mExpr, args);
        }

        @NonNull
        @Override
        protected Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
            return Pair.create(mExpr, mArgs);
        }

        @NonNull
        @Override
        public Raw toRaw(@NonNull AbstractDao dao) {
            return this;
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
        static final String IS = "IS";
        static final String ISNOT = "IS NOT";
        static final String EQ = "==";
        static final String NE = "!=";

        @NonNull
        private final String mOp;
        @NonNull
        private final Expression[] mExprs;
        private final int mNumOfArgs;

        @Nullable
        private transient Pair<String, String[]> mSql;

        Binary(@NonNull final String op, @NonNull final Expression... exprs) {
            mOp = op;
            mExprs = exprs;
            mNumOfArgs = calcNumOfArgs();
        }

        Binary(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            mOp = in.readString();
            final Parcelable[] exprs = in.readParcelableArray(loader);
            mExprs = new Expression[exprs.length];
            for (int i = 0; i < exprs.length; i++) {
                mExprs[i] = (Expression) exprs[i];
            }
            mNumOfArgs = calcNumOfArgs();
        }

        private int calcNumOfArgs() {
            int sum = 0;
            for (final Expression expr : mExprs) {
                sum += expr.numOfArgs();
            }
            return sum;
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
        public Expression not() {
            // sometimes we can just change our own op for not()
            switch (mOp) {
                case EQ:
                    return new Binary(NE, mExprs);
                case NE:
                    return new Binary(EQ, mExprs);
                case IS:
                    return new Binary(ISNOT, mExprs);
                case ISNOT:
                    return new Binary(IS, mExprs);
                default:
                    return super.not();
            }
        }

        @Override
        protected int numOfArgs() {
            return mNumOfArgs;
        }

        @NonNull
        @Override
        public Binary args(@NonNull final String... args) {
            if (args.length != mNumOfArgs) {
                throw new IllegalArgumentException("incorrect number of args specified");
            }

            // short-circuit if there are no args
            if (args.length == 0) {
                return this;
            }

            int pos = 0;
            final Expression[] exprs = new Expression[mExprs.length];
            for (int i = 0; i < mExprs.length; i++) {
                final int num = mExprs[i].numOfArgs();
                exprs[i] = num > 0 ? mExprs[i].args(ArraysCompat.copyOfRange(args, pos, pos + num)) : mExprs[i];
                pos += num;
            }
            return new Binary(mOp, exprs);
        }

        @NonNull
        @Override
        protected Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
            // generate SQL if it hasn't been generated yet
            if (mSql == null) {
                boolean first = true;
                final StringBuilder sql = new StringBuilder();
                String[] args = NO_ARGS;
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

    public static class Unary extends Expression {
        static final String NOT = "NOT";

        @NonNull
        private final String mOp;
        @NonNull
        private final Expression mExpr;

        @Nullable
        private transient Pair<String, String[]> mSql;

        Unary(@NonNull final String op, @NonNull final Expression expr) {
            mOp = op;
            mExpr = expr;
        }

        Unary(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            mOp = in.readString();
            mExpr = in.readParcelable(loader);
        }

        @Override
        protected int numOfArgs() {
            return mExpr.numOfArgs();
        }

        @NonNull
        @Override
        public Expression args(@NonNull final String... args) {
            return mExpr.args(args);
        }

        @NonNull
        @Override
        public Expression not() {
            switch (mOp) {
                case NOT:
                    return mExpr;
                default:
                    return super.not();
            }
        }

        @NonNull
        @Override
        protected Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
            // generate SQL if it hasn't been generated yet
            if (mSql == null) {
                final StringBuilder sql = new StringBuilder(mOp).append(" (");
                String[] args = NO_ARGS;
                final Pair<String, String[]> resp = mExpr.buildSql(dao);
                sql.append(resp.first);
                args = ArrayUtils.merge(String.class, args, resp.second);
                sql.append(')');
                mSql = Pair.create(sql.toString(), args);
            }

            return mSql;
        }

        @Override
        public void writeToParcel(final Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeString(mOp);
            out.writeParcelable(mExpr, 0);
        }

        public static final Creator<Unary> CREATOR =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2 ? new HoneycombMR1UnaryExpressionCreator() :
                        new UnaryExpressionCreator();

        private static class HoneycombMR1UnaryExpressionCreator implements Creator<Unary> {
            @Override
            public Unary createFromParcel(@NonNull final Parcel in) {
                return new Unary(in, null);
            }

            @Override
            public Unary[] newArray(final int size) {
                return new Unary[size];
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        private static class UnaryExpressionCreator extends HoneycombMR1UnaryExpressionCreator
                implements ClassLoaderCreator<Unary> {
            @Override
            public Unary createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
                return new Unary(in, loader);
            }
        }
    }
}
