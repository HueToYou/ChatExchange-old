package com.huetoyou.chatexchange.auth;

import android.util.Log;

import com.huetoyou.chatexchange.net.RequestFactory;

import java.util.HashMap;
import java.util.Map;

import static jodd.jerry.Jerry.jerry;

/**
 * Perform Stack Exchange login via email and password
 */
class StackExchangeAuth {

    private static final String TAG = "StackExchangeAuth";

    /**
     * Callbacks for activity during the login process
     */
    interface Listener {

        /**
         * Authentication succeeded
         * @param authToken token containing cookies
         */
        void authSucceeded(String authToken);

        /**
         * Indicate progress of authentication
         * @param progress value between 0 and 100 inclusive
         */
        void authProgress(int progress);

        /**
         * Authentication failed
         * @param message descriptive error
         */
        void authFailed(String message);
    }

    private RequestFactory mRequestFactory = new RequestFactory();

    private String mEmail;
    private String mPassword;
    private Listener mListener;

    private String mLoginUrl;
    private String mNetworkFkey;
    private String mAuthUrl;

    /**
     * Implement the onFailed() method since it is always handled the same way
     */
    private abstract class RequestListener implements RequestFactory.Listener {
        @Override
        public void onFailed(String message) {
            Log.e(TAG, message);
            mListener.authFailed(message);
        }
    }

    private void confirmOpenId() {
        Log.d(TAG, "confirming OpenID...");
        //...
    }

    private void fetchChatFkey() {
        Log.d(TAG, "fetching chat fkey...");
        //...
    }

    /**
     * Submit the credentials the user supplied
     */
    private void fetchAuthUrl() {
        Log.d(TAG, "fetching auth URL...");
        Map<String, String> form = new HashMap<>();
        form.put("email", mEmail);
        form.put("password", mPassword);
        form.put("affId", "11");
        form.put("fkey", mNetworkFkey);
        mRequestFactory.post(
                "https://openid.stackexchange.com/affiliate/form/login/submit",
                form,
                new RequestListener() {
                    @Override
                    public void onSucceeded(String data) {
                        mListener.authProgress(60);
                        mAuthUrl = jerry(data).$("noscript a").attr("href");
                        if (mAuthUrl == null || mAuthUrl.isEmpty()) {
                            mListener.authFailed("unable to read auth URL");
                        } else {
                            fetchChatFkey();
                        }
                    }
                }
        );
    }

    /**
     * Retrieve the network fkey from the login form
     */
    private void fetchNetworkFkey() {
        Log.d(TAG, "fetching network fkey...");
        mRequestFactory.get(mLoginUrl, new RequestListener() {
            @Override
            public void onSucceeded(String data) {
                mListener.authProgress(40);
                mNetworkFkey = jerry(data).$("#fkey").attr("value");
                if (mNetworkFkey == null || mNetworkFkey.isEmpty()) {
                    mListener.authFailed("unable to read network fkey");
                } else {
                    fetchAuthUrl();
                }
            }
        });
    }

    /**
     * Retrieve the URL of the page that contains the login form
     */
    private void fetchLoginUrl() {
        Log.d(TAG, "fetching login URL...");
        mRequestFactory.get("https://stackexchange.com/users/signin", new RequestListener() {
            @Override
            public void onSucceeded(String data) {
                mListener.authProgress(20);
                mLoginUrl = data;
                fetchNetworkFkey();
            }
        });
    }

    /**
     * Create a new Stack Exchange authenticator with the provided credentials
     * @param email account email obtained from the user
     * @param password account password obtained from the user
     * @param listener callback listener
     */
    StackExchangeAuth(String email, String password, Listener listener) {
        mEmail = email;
        mPassword = password;
        mListener = listener;

        // Start the authentication process
        fetchLoginUrl();
    }
}
