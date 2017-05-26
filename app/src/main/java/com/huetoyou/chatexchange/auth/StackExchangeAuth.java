package com.huetoyou.chatexchange.auth;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Perform Stack Exchange login via email and password
 */
public class StackExchangeAuth {

    private static final String TAG = "StackExchangeAuth";

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    private static final String SE_DOMAIN = "stackexchange.com";
    private static final String CHAT_DOMAIN = "chat.stackexchange.com";

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
         * Authentication failed
         * @param message descriptive error
         */
        void authFailed(String message);
    }

    /**
     * Perform a network request in a secondary thread and invoke a callback with the response
     */
    private class NetworkRequest extends AsyncTask<NetworkRequest.Request, Void, NetworkRequest.Response> {

        class Request {
            String method = METHOD_POST;
            String domain = CHAT_DOMAIN;
            String path;
            Map<String, String> params = new HashMap<>();
        }

        class Response {
            boolean succeeded = false;
            String data;
        }

        /**
         * Generate an error response
         * @param message descriptive message
         * @return newly created response
         */
        private Response error(String message) {
            Response response = new Response();
            response.data = message;
            return response;
        }

        /**
         * Format request parameters as an application/x-www-form-urlencoded string
         * @param params encode these parameters
         * @return properly formatted string
         */
        private String formatParameters(Map<String, String> params) throws UnsupportedEncodingException {
            List<String> encodedParams = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.add(
                        String.format(
                                "%s=%s",
                                URLEncoder.encode(entry.getKey(), "UTF-8"),
                                URLEncoder.encode(entry.getValue(), "UTF-8")
                        )
                );
            }
            return TextUtils.join("&", encodedParams);
        }

        @Override
        protected Response doInBackground(Request... requests) {
            Request request = requests[0];
            HttpURLConnection connection;
            try {
                URL url = new URL(String.format("https://%s%s", request.domain, request.path));
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(request.method);
            } catch (IOException e) {
                return error(e.getMessage());
            };
            connection.setDoInput(true);
            if (!request.method.equals(METHOD_GET)) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                try {
                    OutputStream stream = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
                    writer.write(formatParameters(request.params));
                    writer.flush();
                    writer.close();
                    stream.close();
                } catch (IOException e) {
                    return error(e.getMessage());
                }
            }
            int responseCode;
            String responseMessage;
            String responseData;
            try {
                responseCode = connection.getResponseCode();
                responseMessage = connection.getResponseMessage();
                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                String body = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    body += line;
                }
                responseData = body;
            } catch (IOException e) {
                return error(e.getMessage());
            }
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                return error(responseMessage);
            }
            Response response = new Response();
            response.succeeded = true;
            response.data = responseData;
            return response;
        }
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
