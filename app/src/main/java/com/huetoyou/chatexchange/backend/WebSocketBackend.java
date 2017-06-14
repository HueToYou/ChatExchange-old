package com.huetoyou.chatexchange.backend;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.huetoyou.chatexchange.net.RequestFactory;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Backend using a persistent WebSocket to maintain a connection to the chat server
 */
class WebSocketBackend {

    private static final String TAG = "WebSocketBackend";
    private static final String WEBSOCKET_URL = "https://chat.stackexchange.com/ws-auth";

    /**
     * Response returned by the /ws-auth call
     */
    private class WsAuthResponse {
        String url;
    }

    /**
     * List of events for an individual room
     */
    private class WsRoom {
        @SerializedName("e") List<Event> events;
    }

    /**
     * List of rooms with events
     */
    private class WsMessage {
        List<Event> events = new ArrayList<>();
    }

    /**
     * Custom deserializer for room events
     *
     * The custom deserializer works around the fact that the JSON returned by
     * the chat server contains dynamic keys and duplicated events. Both are
     * dealt with by this class.
     */
    private class WsMessageDeserializer implements JsonDeserializer<WsMessage> {
        @Override
        public WsMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            WsMessage message = new WsMessage();
            Set<Integer> ids = new HashSet<>();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                WsRoom room = mGson.fromJson(entry.getValue(), WsRoom.class);
                for (Event event : room.events) {
                    if (!ids.contains(event.getId())) {
                        message.events.add(event);
                        ids.add(event.getId());
                    }
                }
            }
            return message;
        }
    }


    private final Gson mGson = new GsonBuilder()
            .registerTypeAdapter(WsMessage.class, new WsMessageDeserializer())
            .create();


    private RequestFactory mRequestFactory;
    private int mRoomId;
    private BackendService.Broadcaster mBroadcaster;

    private WebSocketClient mWebSocketClient;

    /**
     * Creates the WebSocket using the provided URI
     * @param uri WebSocket URI
     */
    private void createWebSocket(URI uri) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", mRequestFactory.cookies());
        headers.put("Origin", "https://chat.stackexchange.com");
        mWebSocketClient = new WebSocketClient(uri, new Draft_17(), headers, 0) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i(TAG, "WebSocket connection opened");
            }

            @Override
            public void onMessage(String s) {
                for (Event event : mGson.fromJson(s, WsMessage.class).events) {
                    mBroadcaster.broadcastEvent(event);
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                // TODO
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, e.getMessage());
                // TODO
            }
        };
    }

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
                try {
                    WsAuthResponse authResponse = mGson.fromJson(data, WsAuthResponse.class);
                    createWebSocket(new URI(authResponse.url));
                } catch (JsonSyntaxException|URISyntaxException e) {
                    Log.e(TAG, e.getMessage());
                    retry();
                }
            }

            @Override
            public void onFailed(String message) {
                Log.e(TAG, message);
                retry();
            }
        });
    }

    /**
     * Set a timer to retry the connection in the future
     */
    private void retry() {
        // TODO
    }

    /**
     * Create a new WebSocket backend
     * @param requestFactory factory for creating the WebSocket requests
     * @param roomId ID of the initial room to join
     * @param broadcaster event broadcaster
     */
    WebSocketBackend(RequestFactory requestFactory, int roomId, BackendService.Broadcaster broadcaster) {
        mRequestFactory = requestFactory;
        mRoomId = roomId;
        mBroadcaster = broadcaster;

        // Connect immediately
        connect();
    }

    /**
     * Close the WebSocket
     */
    public void close() {
        mWebSocketClient.close();
    }
}
