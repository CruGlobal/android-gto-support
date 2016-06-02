package org.ccci.gto.android.common.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import java.util.concurrent.TimeUnit;

public class AccountManagerCompat {
    private static final Compat COMPAT;
    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            COMPAT = new LollipopMR1Compat();
        } else {
            COMPAT = new GingerbreadCompat();
        }
    }

    /**
     * MANAGE_ACCOUNTS permission is needed for API levels 9-21.
     * AUTHENTICATE_ACCOUNTS permission is needed for API level 22.
     *
     * @param manager The AccountManager
     * @param account the account being removed
     */
    @RequiresPermission(allOf = {
            "android.permission.AUTHENTICATE_ACCOUNTS",
            "android.permission.MANAGE_ACCOUNTS"
    }, conditional = true)
    public static AccountManagerFuture<Boolean> removeAccountExplicitly(@NonNull final AccountManager manager,
                                                                        @NonNull final Account account) {
        return COMPAT.removeAccountExplicitly(manager, account);
    }

    interface Compat {
        AccountManagerFuture<Boolean> removeAccountExplicitly(@NonNull AccountManager manager,
                                                              @NonNull Account account);
    }

    static class GingerbreadCompat implements Compat {
        @Override
        @RequiresPermission(value = "android.permission.MANAGE_ACCOUNTS")
        public AccountManagerFuture<Boolean> removeAccountExplicitly(@NonNull final AccountManager manager,
                                                                     @NonNull final Account account) {
            return manager.removeAccount(account, null, null);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    static class LollipopMR1Compat extends GingerbreadCompat {
        @Override
        @RequiresPermission(value = "android.permission.AUTHENTICATE_ACCOUNTS", conditional = true)
        public AccountManagerFuture<Boolean> removeAccountExplicitly(@NonNull final AccountManager manager,
                                                                     @NonNull final Account account) {
            return new ImmediateAccountManagerFuture<>(manager.removeAccountExplicitly(account));
        }
    }

    static class ImmediateAccountManagerFuture<V> implements AccountManagerFuture<V> {
        private final V mValue;

        public ImmediateAccountManagerFuture(V value) {
            mValue = value;
        }

        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public V getResult() {
            return mValue;
        }

        @Override
        public V getResult(long timeout, TimeUnit unit) {
            return mValue;
        }
    }
}
