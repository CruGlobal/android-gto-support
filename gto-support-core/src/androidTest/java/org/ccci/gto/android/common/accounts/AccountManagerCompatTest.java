package org.ccci.gto.android.common.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import java.util.Arrays;

import static org.ccci.gto.android.common.accounts.TestConstants.ACCOUNT_TYPE;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@SuppressWarnings("MissingPermission")
public class AccountManagerCompatTest {
    private static final Account ACCOUNT_1 = new Account("Account 1", ACCOUNT_TYPE);
    private static final Account ACCOUNT_2 = new Account("Account 2", ACCOUNT_TYPE);

    @Test
    public void verifyRemoveAccountExplicitly() throws Exception {
        final AccountManager accountManager = AccountManager.get(InstrumentationRegistry.getContext());

        // check for no current accounts
        assertThat(Arrays.asList(accountManager.getAccountsByType(ACCOUNT_TYPE)), is(empty()));

        // create 2 sample accounts
        accountManager.addAccountExplicitly(ACCOUNT_1, null, null);
        accountManager.addAccountExplicitly(ACCOUNT_2, null, null);
        assertThat(Arrays.asList(accountManager.getAccountsByType(ACCOUNT_TYPE)), hasItems(ACCOUNT_1, ACCOUNT_2));

        // delete 1 of the accounts
        AccountManagerCompat.removeAccountExplicitly(accountManager, ACCOUNT_1).getResult();
        assertThat(Arrays.asList(accountManager.getAccountsByType(ACCOUNT_TYPE)),
                   allOf(not(hasItem(ACCOUNT_1)), hasItem(ACCOUNT_2)));

        // delete remaining account
        AccountManagerCompat.removeAccountExplicitly(accountManager, ACCOUNT_2).getResult();
        assertThat(Arrays.asList(accountManager.getAccountsByType(ACCOUNT_TYPE)), is(empty()));
    }
}
