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

    private EditText mEmail;
    private EditText mPassword;

    /**
     * Start the auth procedure (use StackExchangeAuth for now)
     */
    private void startAuth() {
        new StackExchangeAuth(
                mEmail.getText().toString(),
                mPassword.getText().toString(),
                this
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authenticator);

        mEmail = (EditText) findViewById(R.id.auth_email);
        mPassword = (EditText) findViewById(R.id.auth_password);

        Button button = (Button) findViewById(R.id.auth_submit);
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
