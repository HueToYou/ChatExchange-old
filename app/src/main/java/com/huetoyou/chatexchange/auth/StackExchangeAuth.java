package com.huetoyou.chatexchange.auth;

import android.content.Context;
import android.util.Log;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.net.RequestFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jodd.jerry.Jerry;

import static jodd.jerry.Jerry.jerry;

/**
 * Perform Stack Exchange login via email and password
 */
class StackExchangeAuth
{
    private static final String TAG = "StackExchangeAuth";

    /**
     * Callbacks for activity during the login process
     */
    interface Listener
    {

        /**
         * Authentication succeeded
         *
         * @param authToken token containing cookies
         */
        void authSucceeded(String authToken);

        /**
         * Indicate progress of authentication
         *
         * @param progress value between 0 and 100 inclusive
         */
        void authProgress(int progress);

        /**
         * Authentication failed
         *
         * @param message descriptive error
         */
        void authFailed(String message);
    }

    private final RequestFactory mRequestFactory = new RequestFactory();

    private final String mEmail;
    private final String mPassword;
    private final Listener mListener;

    private String mLoginUrl;
    private String mNetworkFkey;
    private String mAuthUrl;
    private String mSessionId;
    private String mSessionFkey;

    private final Context mContext;

    /**
     * Implement the onFailed() method since it is always handled the same way
     */
    private abstract class RequestListener implements RequestFactory.Listener
    {
        @Override
        public void onFailed(String message)
        {
            Log.e(TAG, message);
            mListener.authFailed(message);
        }
    }

    /**
     * Confirm the OpenID transaction
     * <p>
     * Occasionally, a second page is shown after POSTing the credentials
     * requiring the user to confirm the OpenID transaction.
     */
    private void confirmOpenId()
    {
        Log.d(TAG, "confirming OpenID...");
        Map<String, String> form = new HashMap<>();
        form.put("session", mSessionId);
        form.put("fkey", mSessionFkey);
        mRequestFactory.post(
                "https://openid.stackexchange.com/account/prompt/submit",
                form,
                new RequestListener()
                {
                    @Override
                    public void onSucceeded(URL url, String data)
                    {
                        mListener.authProgress(90);
                        if (!url.getPath().equals("/"))
                        {
                            mListener.authFailed(mContext.getResources().getText(R.string.se_auth_unable_to_confirm_openid).toString());
                        }
                        else
                        {
                            mListener.authSucceeded(mRequestFactory.cookies());
                        }
                    }
                }
        );
    }

    /**
     * Attempt to complete the login process
     */
    private void completeLogin()
    {
        Log.d(TAG, "completing login...");
        mRequestFactory.get(mAuthUrl, true, new RequestListener()
        {
            @Override
            public void onSucceeded(URL url, String data)
            {
                mListener.authProgress(80);
                if (url.getPath().equals("/account/prompt"))
                {
                    Jerry doc = jerry(data);
                    mSessionId = doc.$("input[name=session]").attr("value");
                    mSessionFkey = doc.$("input[name=fkey]").attr("value");
                    if (mSessionId == null || mSessionId.isEmpty() ||
                            mSessionFkey == null || mSessionId.isEmpty())
                    {
                        mListener.authFailed(mContext.getResources().getText(R.string.se_auth_unable_to_read_session).toString());
                    }
                    else
                    {
                        confirmOpenId();
                    }
                }
                else if (url.getPath().equals("/"))
                {
                    mListener.authSucceeded(mRequestFactory.cookies());
                }
            }
        });
    }

    /**
     * Submit the credentials the user supplied
     */
    private void fetchAuthUrl()
    {
        Log.d(TAG, "fetching auth URL...");
        Map<String, String> form = new HashMap<>();
        form.put("email", mEmail);
        form.put("password", mPassword);
        form.put("affId", "11");
        form.put("fkey", mNetworkFkey);
        mRequestFactory.post(
                "https://openid.stackexchange.com/affiliate/form/login/submit",
                form,
                new RequestListener()
                {
                    @Override
                    public void onSucceeded(URL url, String data)
                    {
                        mListener.authProgress(60);
                        mAuthUrl = jerry(data).$("noscript a").attr("href");
                        if (mAuthUrl == null || mAuthUrl.isEmpty())
                        {
                            mListener.authFailed(mContext.getResources().getText(R.string.se_auth_unable_to_read_url).toString());
                        }
                        else
                        {
                            completeLogin();
                        }
                    }
                }
        );
    }

    /**
     * Retrieve the network fkey from the login form
     */
    private void fetchNetworkFkey()
    {
        Log.d(TAG, "fetching network fkey...");
        mRequestFactory.get(mLoginUrl, true, new RequestListener()
        {
            @Override
            public void onSucceeded(URL url, String data)
            {
                mListener.authProgress(40);
                mNetworkFkey = jerry(data).$("#fkey").attr("value");
                if (mNetworkFkey == null || mNetworkFkey.isEmpty())
                {
                    mListener.authFailed(mContext.getResources().getText(R.string.se_auth_unable_to_read_fkey).toString());
                }
                else
                {
                    fetchAuthUrl();
                }
            }
        });
    }

    /**
     * Retrieve the URL of the page that contains the login form
     */
    private void fetchLoginUrl()
    {
        Log.d(TAG, "fetching login URL...");
        mRequestFactory.get(
                "https://stackexchange.com/users/signin",
                true,
                new RequestListener()
                {
                    @Override
                    public void onSucceeded(URL url, String data)
                    {
                        mListener.authProgress(20);
                        mLoginUrl = data;
                        fetchNetworkFkey();
                    }
                }
        );
    }

    /**
     * Create a new Stack Exchange authenticator with the provided credentials
     *
     * @param email    account email obtained from the user
     * @param password account password obtained from the user
     * @param listener callback listener
     * @param context  app context for string resources
     */
    StackExchangeAuth(String email, String password, Listener listener, Context context)
    {
        mEmail = email;
        mPassword = password;
        mListener = listener;
        mContext = context;

        // Start the authentication process
        fetchLoginUrl();
    }
}
