package com.huetoyou.chatexchange.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Authenticator for Stack Exchange credentials
 * <p>
 * The "auth token" in our case is the set of cookies that are created as a
 * result of the login process. These cookies will enable the requests to
 * succeed and must be stored for later use.
 */
public class Authenticator extends AbstractAccountAuthenticator
{

    public static final String ACCOUNT_TYPE = "com.huetoyou.chatexchange";

    private final Context mContext;

    /**
     * Create a new authenticator with the provided context
     */
    public Authenticator(Context context)
    {
        super(context);

        mContext = context;
    }

    /**
     * Return a bundle with the information necessary to create a new account
     */
    private Bundle newAccount(AccountAuthenticatorResponse response)
    {
        Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException
    {
        return newAccount(response);
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException
    {
        AccountManager accountManager = AccountManager.get(mContext);
        String authToken = accountManager.peekAuthToken(account, authTokenType);
        if (!authToken.isEmpty())
        {
            Bundle bundle = new Bundle();
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            bundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return bundle;
        }
        return newAccount(response);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException
    {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType)
    {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException
    {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException
    {
        return null;
    }
}
