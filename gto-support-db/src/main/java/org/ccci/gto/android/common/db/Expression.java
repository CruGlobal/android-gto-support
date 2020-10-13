package org.ccci.gto.android.common.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ccci.gto.android.common.util.ArrayUtils;

import java.util.Arrays;

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
    protected Binary binaryExpr(@NonNull final String op, @NonNull final Expression expression) {
        return new Binary(op, this, expression);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
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
        public KotlinExpression not() {
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
}
