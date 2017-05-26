package com.huetoyou.chatexchange.auth;

/**
 * Perform Stack Exchange login via email and password
 */
public class StackExchangeAuth {

    private static final String TAG = "StackExchangeAuth";

    /**
     * Callbacks for activity during the login process
     */
    interface AuthenticationListener {

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
    private AuthenticationListener mListener;

    private State mState = State.FetchLoginUrl;

    private void fetchLoginUrl() {
        //...
    }

    private void fetchNetworkFkey() {
        //...
    }

    private void fetchAuthUrl() {
        //...
    }

    private void confirmOpenId() {
        //...
    }

    private void fetchChatFkey() {
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
    StackExchangeAuth(String email, String password, AuthenticationListener listener) {
        mEmail = email;
        mPassword = password;
        mListener = listener;

        // Start the authentication process
        nextStep();
    }
}
