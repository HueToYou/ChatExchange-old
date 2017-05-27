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
class Request extends AsyncTask<Request.Params, Void, Request.Response> {

    interface Listener {
        void onResponse(Response response);
    }

    /**
     * Parameters for the request
     */
    static class Params {
        String method;
        String url;
        String cookies;
        Map<String, String> form;
    }

    /**
     * Response data for the request
     */
    static class Response {
        boolean succeeded = false;
        String cookies;
        String data;
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
            URL url = new URL(params.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(params.method);
        } catch (IOException e) {
            return error(e.getMessage());
        }
        connection.setRequestProperty("Cookie", params.cookies);
        connection.setDoInput(true);
        if (!params.method.equals("GET")) {
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
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return error(String.format("HTTP response: %s", responseMessage));
            }
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
        Response response = new Response();
        response.succeeded = true;
        response.cookies = connection.getHeaderField("Set-Cookie");
        response.data = responseData;
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