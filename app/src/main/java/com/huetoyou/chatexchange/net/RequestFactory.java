package com.huetoyou.chatexchange.net;

import android.text.TextUtils;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URL;
import java.util.Map;

/**
 * Factory for new requests sharing a jar of cookies
 *
 * By using a factory to instantiate all network requests, cookies that are
 * obtained from a request can be preserved for future requests.
 */
public class RequestFactory {

    /**
     * Listener for request events
     */
    public interface Listener {

        /**
         * Indicate the request succeeded
         *
         * @param url  final request URL
         * @param data response body
         */
        void onSucceeded(URL url, String data);

        /**
         * Indicate the request failed
         *
         * @param message descriptive error message
         */
        void onFailed(String message);
    }

    private final CookieManager mManager = new CookieManager();

    /**
     * Add the provided cookies to the cookie manager
     */
    private void addCookies(String cookies) {
        for (HttpCookie cookie : HttpCookie.parse(cookies)) {
            mManager.getCookieStore().add(URI.create("stackexchange.com"), cookie);
        }
    }

    /**
     * Retrieve a string containing all of the cookies
     *
     * @return cookie string
     */
    public String cookies() {
        return TextUtils.join(",", mManager.getCookieStore().getCookies());
    }

    /**
     * Create an empty request factory
     */
    public RequestFactory() {
    }

    /**
     * Create a new request factory from the provided cookies
     *
     * @param cookies string containing cookies
     */
    public RequestFactory(String cookies) {
        if (!cookies.isEmpty()) {
            addCookies(cookies);
        }
    }

    /**
     * Create a request from the supplied parameters and start it
     */
    private void newRequest(String method, String url, Map<String, String> form, boolean followRedirects, final Listener listener) {
        Request.Params params = new Request.Params();
        params.method = method;
        params.url = url;
        params.cookieStore = mManager.getCookieStore();
        params.form = form;
        params.followRedirects = followRedirects;
        new Request(new Request.Listener() {
            @Override
            public void onResponse(Request.Response response) {
                if (response.succeeded) {
                    listener.onSucceeded(response.finalUrl, response.data);
                } else {
                    listener.onFailed(response.data);
                }
            }
        }).execute(params);
    }

    /**
     * Create a new GET request
     *
     * @param url             request URL
     * @param followRedirects true to follow HTTP redirects
     * @param listener        listener for request completion
     */
    public void get(String url, @SuppressWarnings("SameParameterValue") boolean followRedirects, Listener listener) {
        newRequest("GET", url, null, followRedirects, listener);
    }

    /**
     * Create a new POST request
     *
     * @param url      request URL
     * @param form     form data
     * @param listener listener for request completion
     */
    public void post(String url, Map<String, String> form, Listener listener) {
        newRequest("POST", url, form, false, listener);
    }
}
