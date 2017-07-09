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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.huetoyou.chatexchange.ui.activity.main.MainActivity;
import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;

import android.text.Html;

/**
 * Activity shown when the account needs to be authenticated
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity
        implements StackExchangeAuth.Listener
{
    private static final String TAG = "AuthenticatorActivity";

    private ProgressDialog mProgressDialog;

    private EditText mEmail;
    private EditText mPassword;
    private AccountManager mAccountManager;
    private Button mSubmit;

    /**
     * Start the auth procedure (use StackExchangeAuth for now)
     */
    private void startAuth()
    {
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
     * Set the hint for an EditText to the specified string on error
     *
     * @param editText edit widget
     * @param resId    string resource
     */
    private void setErrorHint(EditText editText, @StringRes int resId)
    {
        String html = "<font color='#ff0000'>" + getResources().getText(resId) + "</font>";
        CharSequence hint;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N)
        {
            hint = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        }
        else
        {
            //noinspection deprecation
            hint = Html.fromHtml(html);
        }
        editText.setHint(hint);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ThemeHue.setTheme(this);
        setContentView(R.layout.activity_authenticator);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mEmail = findViewById(R.id.auth_email);
        mPassword = findViewById(R.id.auth_password);
        mSubmit = findViewById(R.id.auth_submit);
        mSubmit.setVisibility(View.GONE);

        mEmail.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (!mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty())
                {
                    mSubmit.setVisibility(View.VISIBLE);
                }
                else
                {
                    mSubmit.setVisibility(View.GONE);
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (!mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty())
                {
                    mSubmit.setVisibility(View.VISIBLE);
                }
                else
                {
                    mSubmit.setVisibility(View.GONE);
                }
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startAuth();
            }
        });

        mAccountManager = AccountManager.get(this);

        CheckBox showPassword = findViewById(R.id.show_password);
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                mPassword.setInputType(isChecked ? InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
    }

    @Override
    public void authSucceeded(String authToken)
    {
        Log.d(TAG, String.format("auth token: %s", authToken));

        mProgressDialog.cancel();

        String accountName = mEmail.getText().toString();

        // Create a new account of the specified type and add it
        Account account = new Account(accountName, Authenticator.ACCOUNT_TYPE);
        mAccountManager.addAccountExplicitly(
                account,
                mPassword.getText().toString(),
                null
        );
        mAccountManager.setAuthToken(account, Authenticator.ACCOUNT_TYPE, authToken);

        onAuthFinish(accountName, authToken);
    }

    private void onAuthFinish(String accountName, String authToken)
    {
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
    public void authProgress(int progress)
    {
        mProgressDialog.setProgress(progress);
    }

    @Override
    public void authFailed(String message)
    {
        mProgressDialog.cancel();
        new AlertDialog.Builder(this)
                .setTitle(R.string.activity_authenticator_error_title)
                .setMessage(getString(R.string.activity_authenticator_error_message, message))
                .setPositiveButton(getText(R.string.activity_authenticator_error_ok), null)
                .create()
                .show();
    }
}
