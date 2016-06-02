package org.ccci.gto.android.common.accounts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class SimpleAuthenticatorService extends Service {
    private SimpleAccountAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new SimpleAccountAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
