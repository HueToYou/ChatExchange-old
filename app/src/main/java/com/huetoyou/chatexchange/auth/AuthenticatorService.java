package com.huetoyou.chatexchange.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Service to facilitate login
 */
public class AuthenticatorService extends Service
{

    private Authenticator mAuthenticator;

    @Override
    public void onCreate()
    {
        super.onCreate();

        mAuthenticator = new Authenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return mAuthenticator.getIBinder();
    }
}
