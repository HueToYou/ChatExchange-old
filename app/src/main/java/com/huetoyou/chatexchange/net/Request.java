package com.huetoyou.chatexchange.net;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Perform a network request in a secondary thread and invoke a callback with the response
 */
public class Request extends AsyncTask<Request.Params, Void, Request.Response> {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    public static final String DOMAIN_STACKEXCHANGE = "stackexchange.com";
    public static final String DOMAIN_CHAT_SE = "chat.stackexchange.com";

    /**
     * Listener for request events
     */
    public interface Listener {

        /**
         * Indicate the request succeeded
         * @param data response body
         */
        void onSucceeded(String data);

        /**
         * Indicate the request failed
         * @param message descriptive error message
         */
        void onFailed(String message);
    }

    /**
     * Parameters for the request
     */
    static class Params {
        public String method;
        public String domain;
        public String path;
        public Map<String, String> form;
    }

    /**
     * Response data for the request
     */
    static class Response {
        public boolean succeeded = false;
        public String data;
    }

    private Listener mListener;

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
    protected Response doInBackground(Params... paramList) {
        Params params = paramList[0];
        HttpURLConnection connection;
        try {
            URL url = new URL(String.format("https://%s%s", params.domain, params.path));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(params.method);
        } catch (IOException e) {
            return error(e.getMessage());
        }
        connection.setDoInput(true);
        if (!params.method.equals(METHOD_GET)) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            try {
                byte[] paramBytes = formatParameters(params.form).getBytes();
                connection.setRequestProperty("Content-Length", Integer.toString(paramBytes.length));
                OutputStream stream = connection.getOutputStream();
                BufferedOutputStream writer = new BufferedOutputStream(stream);
                writer.write(paramBytes);
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
        if (responseCode != HttpURLConnection.HTTP_OK) {
            return error(responseMessage);
        }
        Response response = new Response();
        response.succeeded = true;
        response.data = responseData;
        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
        if (response.succeeded) {
            mListener.onSucceeded(response.data);
        } else {
            mListener.onFailed(response.data);
        }
    }

    private Request(Listener listener) {
        mListener = listener;
    }

    /**
     * Create a new request with the specified parameters
     * @param method request method (such as METHOD_GET)
     * @param domain request domain (such as DOMAIN_SE_CHAT)
     * @param path request path
     * @param form request form data (for non-GET requests)
     * @param listener listener for request events
     */
    public static void create(String method, String domain, String path, Map<String, String> form, Listener listener) {
        Params params = new Params();
        params.method = method;
        params.domain = domain;
        params.path = path;
        params.form = form;
        new Request(listener).execute(params);
    }
}