package com.huetoyou.chatexchange.auth;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huetoyou.chatexchange.R;

/**
 * Activity shown when the account needs to be authenticated
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity
        implements StackExchangeAuth.Listener {

    /**
     * Start the auth procedure (use StackExchangeAuth for now)
     */
    private void startAuth() {
        String email = ((EditText) findViewById(R.id.auth_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.auth_password)).getText().toString();

        // Begin the auth process
        new StackExchangeAuth(email, password, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authenticator);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAuth();
            }
        });
    }

    @Override
    public void authSucceeded(String authToken) {

    }

    @Override
    public void authProgress(int progress) {

    }

    @Override
    public void authFailed(String message) {

    }
}
