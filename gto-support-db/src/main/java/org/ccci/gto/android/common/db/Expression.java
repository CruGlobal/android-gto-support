package org.ccci.gto.android.common.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ccci.gto.android.common.util.ArrayUtils;

import java.util.Arrays;

import static org.ccci.gto.android.common.db.AbstractDao.bindValues;

public abstract class Expression extends ShimExpression {
    static final String[] NO_ARGS = new String[0];

    Expression() {
    }

    protected int numOfArgs() {
        return getNumOfArgs();
    }

    @NonNull
    public  Expression args(@NonNull final String... args) {
        super.args(args);
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
    public final Binary eq(@NonNull final String constant) {
        return eq(constant(constant));
    }

    @NonNull
    public final Binary eq(@NonNull final Object constant) {
        return eq(constant(constant));
    }

    @NonNull
    public final Binary eq(@NonNull final Expression expression) {
        return binaryExpr(Binary.EQ, expression);
    }

    @NonNull
    public final Binary lt(@NonNull final Number constant) {
        return lt(constant(constant));
    }

    @NonNull
    public final Binary lt(@NonNull final Object constant) {
        return lt(constant(constant));
    }

    @NonNull
    public final Binary lt(@NonNull final Expression expression) {
        return binaryExpr(Binary.LT, expression);
    }

    @NonNull
    public final Binary lte(@NonNull final Number constant) {
        return lte(constant(constant));
    }

    @NonNull
    public final Binary lte(@NonNull final Object constant) {
        return lte(constant(constant));
    }

    @NonNull
    public final Binary lte(@NonNull final Expression expression) {
        return binaryExpr(Binary.LTE, expression);
    }

    @NonNull
    public final Binary gt(@NonNull final Number constant) {
        return gt(constant(constant));
    }

    @NonNull
    public final Binary gt(@NonNull final Object constant) {
        return gt(constant(constant));
    }

    @NonNull
    public final Binary gt(@NonNull final Expression expression) {
        return binaryExpr(Binary.GT, expression);
    }

    @NonNull
    public final Binary gte(@NonNull final Number constant) {
        return gte(constant(constant));
    }

    @NonNull
    public final Binary gte(@NonNull final Object constant) {
        return gte(constant(constant));
    }

    @NonNull
    public final Binary gte(@NonNull final Expression expression) {
        return binaryExpr(Binary.GTE, expression);
    }

    @NonNull
    public final Binary in(@NonNull final Expression... expressions) {
        return new Binary(Binary.IN, ArrayUtils.merge(Expression.class, new Expression[] {this}, expressions));
    }

    @NonNull
    public final Binary notIn(@NonNull final Expression... expressions) {
        return new Binary(Binary.NOTIN, ArrayUtils.merge(Expression.class, new Expression[] {this}, expressions));
    }

    @NonNull
    public final Binary is(@NonNull final Expression expression) {
        return binaryExpr(Binary.IS, expression);
    }

    @NonNull
    public final Binary isNot(@NonNull final Expression expression) {
        return binaryExpr(Binary.ISNOT, expression);
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
    public final Binary ne(@NonNull final String constant) {
        return ne(constant(constant));
    }

    @NonNull
    public final Binary ne(@NonNull final Object constant) {
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

    public static final class Field extends Expression {
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
        public Aggregate count() {
            return new Aggregate(Aggregate.COUNT, false, this);
        }

        @NonNull
        public Aggregate count(final boolean distinct) {
            return new Aggregate(Aggregate.COUNT, distinct, this);
        }

        @NonNull
        public Aggregate max() {
            return new Aggregate(Aggregate.MAX, false, this);
        }

        @NonNull
        public Aggregate max(final boolean distinct) {
            return new Aggregate(Aggregate.MAX, distinct, this);
        }

        @NonNull
        public Aggregate min() {
            return new Aggregate(Aggregate.MIN, false, this);
        }

        @NonNull
        public Aggregate min(final boolean distinct) {
            return new Aggregate(Aggregate.MIN, distinct, this);
        }

        @NonNull
        public Aggregate sum() {
            return new Aggregate(Aggregate.SUM, false, this);
        }

        @NonNull
        public Aggregate sum(final boolean distinct) {
            return new Aggregate(Aggregate.SUM, distinct, this);
        }

        @NonNull
        @Override
        public Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
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

        public static final Creator<Field> CREATOR = new FieldCreator();

        private static class FieldCreator implements ClassLoaderCreator<Field> {
            @Override
            public Field createFromParcel(@NonNull final Parcel in) {
                return new Field(in, null);
            }

            @Override
            public Field[] newArray(final int size) {
                return new Field[size];
            }

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
        public Raw args(@NonNull final Object... args) {
            return args(bindValues(args));
        }

        @NonNull
        @Override
        public Raw args(@NonNull final String... args) {
            return new Raw(mExpr, args);
        }

        @NonNull
        @Override
        public Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
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

        public static final Creator<Raw> CREATOR = new RawCreator();

        private static class RawCreator implements ClassLoaderCreator<Raw> {
            @Override
            public Raw createFromParcel(@NonNull final Parcel in) {
                return new Raw(in, null);
            }

            @Override
            public Raw[] newArray(final int size) {
                return new Raw[size];
            }

            @Override
            public Raw createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
                return new Raw(in, loader);
            }
        }
    }

    public static class Binary extends Expression {
        static final String LT = "<";
        static final String LTE = "<=";
        static final String GT = ">";
        static final String GTE = ">=";
        static final String EQ = "==";
        static final String NE = "!=";
        static final String IS = "IS";
        static final String ISNOT = "IS NOT";
        static final String IN = "IN";
        static final String NOTIN = "NOT IN";
        static final String AND = "AND";
        static final String OR = "OR";

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
        @SuppressWarnings("checkstyle:MissingSwitchDefault")
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
                case IN:
                    return new Binary(NOTIN, mExprs);
                case NOTIN:
                    return new Binary(IN, mExprs);
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
                exprs[i] = num > 0 ? (Expression) mExprs[i].args(Arrays.copyOfRange(args, pos, pos + num)) : mExprs[i];
                pos += num;
            }
            return new Binary(mOp, exprs);
        }

        @NonNull
        @Override
        public Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
            // generate SQL if it hasn't been generated yet
            if (mSql == null) {
                int i = 0;
                final StringBuilder sql = new StringBuilder();
                String[] args = NO_ARGS;
                sql.append('(');

                final boolean isIn = IN.equals(mOp) || NOTIN.equals(mOp);
                if (isIn) {
                    // "{mExpr[0]} IN ("
                    final Pair<String, String[]> resp = mExprs[0].buildSql(dao);
                    sql.append(resp.first);
                    args = ArrayUtils.merge(String.class, args, resp.second);
                    sql.append(' ').append(mOp).append(" (");
                    i++;
                }

                // "{mExpr[i]} {mOp} {mExpr[i+1]} ..."
                boolean first = true;
                for (; i < mExprs.length; i++) {
                    final Expression expr = mExprs[i];
                    if (!first) {
                        sql.append(' ').append(isIn ? ',' : mOp).append(' ');
                    }
                    final Pair<String, String[]> resp = expr.buildSql(dao);
                    sql.append(resp.first);
                    args = ArrayUtils.merge(String.class, args, resp.second);
                    first = false;
                }

                if (isIn) {
                    sql.append(')');
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

        public static final Creator<Binary> CREATOR = new BinaryExpressionCreator();

        private static class BinaryExpressionCreator implements ClassLoaderCreator<Binary> {
            @Override
            public Binary createFromParcel(@NonNull final Parcel in) {
                return new Binary(in, null);
            }

            @Override
            public Binary[] newArray(final int size) {
                return new Binary[size];
            }

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
            return (Expression) mExpr.args(args);
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
        public Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
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

        public static final Creator<Unary> CREATOR = new UnaryExpressionCreator();

        private static class UnaryExpressionCreator implements ClassLoaderCreator<Unary> {
            @Override
            public Unary createFromParcel(@NonNull final Parcel in) {
                return new Unary(in, null);
            }

            @Override
            public Unary[] newArray(final int size) {
                return new Unary[size];
            }

            @Override
            public Unary createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
                return new Unary(in, loader);
            }
        }
    }

    public static class Aggregate extends Expression {
        static final String COUNT = "COUNT";
        static final String MAX = "MAX";
        static final String MIN = "MIN";
        static final String SUM = "SUM";

        @NonNull
        private final String mOp;
        @NonNull
        private final Field mField;
        private final boolean mDistinct;

        @Nullable
        private transient Pair<String, String[]> mSql;

        Aggregate(@NonNull final String op, final boolean distinct,
                  @NonNull final Field field) {
            mOp = op;
            mField = field;
            mDistinct = distinct;
        }

        Aggregate(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
            mOp = in.readString();
            mField = in.readParcelable(loader);
            mDistinct = false;
        }

        @NonNull
        public Aggregate distinct(final boolean distinct) {
            return new Aggregate(mOp, distinct, mField);
        }

        @Override
        protected int numOfArgs() {
            return mField.numOfArgs();
        }

        @NonNull
        @Override
        public Expression args(@NonNull final String... args) {
            return (Expression) mField.args(args);
        }

        @NonNull
        @Override
        public Pair<String, String[]> buildSql(@NonNull final AbstractDao dao) {
            // generate SQL if it hasn't been generated yet
            if (mSql == null) {
                final StringBuilder sql = new StringBuilder(mOp).append(" (");
                String[] args = NO_ARGS;
                final Pair<String, String[]> resp = mField.buildSql(dao);

                // {mOp} (DISTINCT {mExpr})
                if (mDistinct) {
                    sql.append("DISTINCT ");
                }
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
            out.writeParcelable(mField, 0);
        }

        public static final Creator<Aggregate> CREATOR = new AggregateExpressionCreator();

        private static class AggregateExpressionCreator implements ClassLoaderCreator<Aggregate> {
            @Override
            public Aggregate createFromParcel(@NonNull final Parcel in) {
                return new Aggregate(in, null);
            }

            @Override
            public Aggregate[] newArray(final int size) {
                return new Aggregate[size];
            }

            @Override
            public Aggregate createFromParcel(@NonNull final Parcel in, @Nullable final ClassLoader loader) {
                return new Aggregate(in, loader);
            }
        }
    }
}
