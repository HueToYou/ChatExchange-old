package com.huetoyou.chatexchange.net;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Perform a network request in a secondary thread and invoke a callback with the response
 */
class Request extends AsyncTask<Request.Params, Void, Request.Response> {

    private static final String TAG = "Request";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT x.y; rv:10.0) Gecko/20100101 Firefox/10.0";

    interface Listener {
        void onResponse(Response response);
    }

    // TODO: not sure if CookieStore is thread-safe

    /**
     * Parameters for the request
     */
    static class Params {
        String method;
        String url;
        CookieStore cookieStore;
        Map<String, String> form;
        boolean followRedirects;
    }

    /**
     * Response data for the request
     */
    static class Response {
        boolean succeeded = false;
        URL finalUrl;
        String data;
    }

    private final Listener mListener;

    /**
     * Generate an error response
     *
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
     *
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

    /**
     * Perform a single request
     * @param params request parameters
     * @param url request URL (overrides params)
     * @return resulting HTTP connection
     */
    private HttpURLConnection doRequest(Params params, String url) throws IOException {
        HttpURLConnection connection;
        URL requestUrl = new URL(url);

        // Build the cookie string to be passed in the header
        List<String> cookieList = new ArrayList<>();
        for (HttpCookie cookie : params.cookieStore.getCookies()) {
            cookieList.add(String.format("%s=%s", cookie.getName(), cookie.getValue()));
        }

        // Prepare the connection
        Log.i(TAG, String.format("Opening connection to %s", requestUrl.toString()));
        connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod(params.method);
        connection.setRequestProperty("Cookie", TextUtils.join("; ", cookieList));
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setDoInput(true);

        // For everything but GET requests, supply the provided input
        if (!params.method.equals("GET")) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] paramBytes = formatParameters(params.form).getBytes();
            connection.setRequestProperty("Content-Length", Integer.toString(paramBytes.length));
            OutputStream stream = connection.getOutputStream();
            BufferedOutputStream writer = new BufferedOutputStream(stream);
            writer.write(paramBytes);
            writer.flush();
            writer.close();
            stream.close();
        }

        // Return the resulting connection for reading
        return connection;
    }

    @Override
    protected Response doInBackground(Params... paramList) {
        Response response = new Response();
        Params params = paramList[0];
        String url = params.url;

        // Continue to make requests until no redirects are encountered
        HttpURLConnection connection;
        while (true) {
            // Initiate the request
            try {
                connection = doRequest(params, url);
            } catch (IOException e) {
                return error(e.getMessage());
            }

            // Ensure the cookies are added to the cookie store
            List<String> cookieStrs = connection.getHeaderFields().get("Set-Cookie");
            if (cookieStrs != null) {
                for (String cookieStr : cookieStrs) {
                    for (HttpCookie cookie : HttpCookie.parse(cookieStr)) {
                        params.cookieStore.add(URI.create("stackexchange.com"), cookie);
                    }
                }
            }

            // If an HTTP redirect is encountered (and followRedirects is set), redo the request
            try {
                switch (connection.getResponseCode()) {
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                    case HttpURLConnection.HTTP_MOVED_PERM:
                        if (params.followRedirects) {
                            url = connection.getHeaderField("Location");
                            if (url == null) {
                                throw new IOException("redirect without a location header");
                            }
                            continue;
                        }
                    case HttpURLConnection.HTTP_OK:
                        break;
                    default:
                        throw new IOException(
                                String.format("HTTP response: %s", connection.getResponseMessage())
                        );
                }
            } catch (IOException e) {
                return error(e.getMessage());
            }

            // If code reaches this point, quit the loop
            break;
        }

        // Read the response data
        String responseData;
        try {
            InputStream in = new BufferedInputStream(connection.getInputStream());
            responseData = IOUtils.toString(in, "UTF-8");
            in.close();
        } catch (IOException e) {
            return error(e.getMessage());
        }

        // Build the response and return it
        response.succeeded = true;
        response.finalUrl = connection.getURL();
        response.data = responseData;
        connection.disconnect();
        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
        mListener.onResponse(response);
    }

    Request(Listener listener) {
        mListener = listener;
    }
}