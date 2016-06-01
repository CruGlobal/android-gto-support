package org.ccci.gto.android.common.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

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
    public static void removeAccountExplicitly(@NonNull final AccountManager manager, @NonNull final Account account) {
        COMPAT.removeAccountExplicitly(manager, account);
    }

    interface Compat {
        void removeAccountExplicitly(@NonNull AccountManager manager, @NonNull Account account);
    }

    static class GingerbreadCompat implements Compat {
        @Override
        @RequiresPermission(value = "android.permission.MANAGE_ACCOUNTS")
        public void removeAccountExplicitly(@NonNull final AccountManager manager, @NonNull final Account account) {
            manager.removeAccount(account, null, null);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    static class LollipopMR1Compat extends GingerbreadCompat {
        @Override
        @RequiresPermission(value = "android.permission.AUTHENTICATE_ACCOUNTS", conditional = true)
        public void removeAccountExplicitly(@NonNull final AccountManager manager, @NonNull final Account account) {
            manager.removeAccountExplicitly(account);
        }
    }
}
