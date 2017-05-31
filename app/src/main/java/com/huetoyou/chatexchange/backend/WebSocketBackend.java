package com.huetoyou.chatexchange.backend;

import com.huetoyou.chatexchange.net.RequestFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Backend using a persistent WebSocket to maintain a connection to the chat server
 */
public class WebSocketBackend {

    private static final String WEBSOCKET_URL = "https://chat.stackexchange.com/ws-auth";

    /**
     * Listener for chat events as they occur as well as errors
     */
    public interface Listener {

        /**
         * Indicate an error has occurred
         * @param message useful description of the error
         */
        void onError(String message);
    }

    private RequestFactory mRequestFactory;
    private int mRoomId;
    private Listener mListener;

    /**
     * Establish a connection to the WebSocket
     *
     * This is done by first obtaining the magic URL for the socket and then
     * attempting to connect with it. The response from the chat server is in
     * JSON format and must be decoded.
     */
    private void connect() {
        Map<String, String> form = new HashMap<>();
        form.put("roomid", Integer.toString(mRoomId));
        mRequestFactory.post(WEBSOCKET_URL, form, new RequestFactory.Listener() {
            @Override
            public void onSucceeded(URL url, String data) {
                // TODO
            }

            @Override
            public void onFailed(String message) {
                mListener.onError(message);
            }
        });
    }

    /**
     * Create a new WebSocket backend
     * @param requestFactory factory for creating the WebSocket requests
     * @param roomId ID of the initial room to join
     */
    public WebSocketBackend(RequestFactory requestFactory, int roomId, Listener listener) {
        mRequestFactory = requestFactory;
        mRoomId = roomId;
        mListener = listener;
    }
}
