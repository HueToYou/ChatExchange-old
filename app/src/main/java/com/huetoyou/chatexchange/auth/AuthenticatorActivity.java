package com.huetoyou.chatexchange.auth;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;

import com.huetoyou.chatexchange.R;

/**
 * Activity shown when the account needs to be authenticated
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
    }
}
