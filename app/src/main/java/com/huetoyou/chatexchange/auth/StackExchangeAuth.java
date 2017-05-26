package com.huetoyou.chatexchange.auth;

import android.util.Log;

import com.huetoyou.chatexchange.net.Request;

/**
 * Perform Stack Exchange login via email and password
 */
public class StackExchangeAuth {

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

    private enum State {
        FetchLoginUrl,
        FetchNetworkFkey,
        FetchAuthUrl,
        ConfirmOpenId,
        FetchChatFkey,
        Finished,
    }

    private String mEmail;
    private String mPassword;
    private Listener mListener;

    private State mState = State.FetchLoginUrl;

    /**
     * Implement the onFailed() method since it is always handled the same way
     */
    private abstract class RequestListener implements Request.Listener {
        @Override
        public void onFailed(String message) {
            Log.e(TAG, message);
            mListener.authFailed(message);
        }
    }

    /**
     * Retrieve the URL of the page that contains the login form
     */
    private void fetchLoginUrl() {
        Log.d(TAG, "fetching login URL...");
        Request.create(Request.METHOD_GET, Request.DOMAIN_STACKEXCHANGE, "/users/signin", null, new RequestListener() {
            @Override
            public void onSucceeded(String data) {
                Log.i(TAG, "request succeeded!");
            }
        });
    }

    private void fetchNetworkFkey() {
        Log.d(TAG, "fetching network fkey...");
        //...
    }

    private void fetchAuthUrl() {
        Log.d(TAG, "fetching auth URL...");
        //...
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
     * Perform the next step in the authentication process
     */
    private void nextStep() {
        switch (mState) {
            case FetchLoginUrl:
                fetchLoginUrl();
                break;
            case FetchNetworkFkey:
                fetchNetworkFkey();
                break;
            case FetchAuthUrl:
                fetchAuthUrl();
                break;
            case ConfirmOpenId:
                confirmOpenId();
                break;
            case FetchChatFkey:
                fetchChatFkey();
                break;
            case Finished:

                // TODO: invoke the callback

                break;
        }
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
        nextStep();
    }
}
