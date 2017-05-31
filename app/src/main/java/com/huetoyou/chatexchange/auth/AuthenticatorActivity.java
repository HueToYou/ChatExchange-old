package com.huetoyou.chatexchange.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.huetoyou.chatexchange.MainActivity;
import com.huetoyou.chatexchange.R;

import android.text.Html;

/**
 * Activity shown when the account needs to be authenticated
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity
        implements StackExchangeAuth.Listener {

    private ProgressDialog mProgressDialog;

    private EditText mEmail;
    private EditText mPassword;
    private AccountManager mAccountManager;

    /**
     * Start the auth procedure (use StackExchangeAuth for now)
     */
    private void startAuth() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getText(R.string.activity_authenticator_progress_title));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgressNumberFormat(null);
        mProgressDialog.setProgressPercentFormat(null);
        mProgressDialog.show();
        new StackExchangeAuth(
                mEmail.getText().toString(),
                mPassword.getText().toString(),
                this,
                this
        );
    }

    /**
     * Set the hint for an EditText to the specified string
     * @param editText edit widget
     * @param resId string resource
     */
    private void setHint(EditText editText, @StringRes int resId) {
        String html = "<font color='#ff0000'>" + getResources().getText(resId) + "</font>";
        CharSequence hint;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            hint = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            //noinspection deprecation
            hint = Html.fromHtml(html);
        }
        editText.setHint(hint);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authenticator);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mEmail = (EditText) findViewById(R.id.auth_email);
        mPassword = (EditText) findViewById(R.id.auth_password);

        mAccountManager = AccountManager.get(this);

        CheckBox showPassword = (CheckBox) findViewById(R.id.show_password);
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPassword.setInputType(isChecked ? InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        Button submit = (Button) findViewById(R.id.auth_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmail.getText().toString().isEmpty()) {
                    setHint(mEmail, R.string.activity_authenticator_email_required);
                } else if (mPassword.getText().toString().isEmpty()) {
                    setHint(mPassword, R.string.activity_authenticator_password_required);
                } else {
                    startAuth();
                }
            }
        });
    }

    @Override
    public void authSucceeded(String authToken) {
        mProgressDialog.cancel();

        String accountName = mEmail.getText().toString();

        // Create a new account of the specified type and add it
        Account account = new Account(accountName, Authenticator.ACCOUNT_TYPE);
        mAccountManager.addAccountExplicitly(
                account,
                mPassword.getText().toString(),
                null
        );

        onAuthFinish(accountName, authToken);
    }

    private void onAuthFinish(String accountName, String authToken) {
        // Create the intent for returning to the caller
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Authenticator.ACCOUNT_TYPE);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void authProgress(int progress) {
        mProgressDialog.setProgress(progress);
    }

    @Override
    public void authFailed(String message) {
        mProgressDialog.cancel();
        new AlertDialog.Builder(this)
                .setTitle(R.string.activity_authenticator_error_title)
                .setMessage(getString(R.string.activity_authenticator_error_message, message))
                .setPositiveButton(getText(R.string.activity_authenticator_error_ok), null)
                .create()
                .show();
    }
}
