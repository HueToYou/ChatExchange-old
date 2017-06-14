package com.huetoyou.chatexchange.backend;

import com.huetoyou.chatexchange.net.RequestFactory;

import java.net.URL;

import jodd.jerry.Jerry;

import static jodd.jerry.Jerry.jerry;

/**
 * Retrieve data for an individual page
 */
public class PageRetriever {

    /**
     * Information about an individual page
     */
    public class Page {

        private String mName;
        private String mIcon;
        private int mColor;

        Page(String name, String icon, int color) {
            mName = name;
            mIcon = icon;
            mColor = color;
        }

        /**
         * Retrieve the page name
         */
        public String getName() {
            return mName;
        }

        /**
         * Retrieve the page icon
         */
        public String getIcon() {
            return mIcon;
        }

        /**
         * Retrieve the page color
         */
        public int getColor() {
            return mColor;
        }
    }

    public interface Listener {
        void onSucceeded(Page page);
        void onFailed(String message);
    }

    private Page parse(String data) {

        // Begin by parsing the page
        Jerry doc = jerry(data);

        // Extract the room name and icon
        String name = doc.$("#roomname").text();
        String icon = doc.$("link[rel=shortcut icon]").attr("href");

        // TODO: Extract a suitable color for the page
        int color = 1;

        return new Page(name, icon, color);
    }

    public PageRetriever(RequestFactory requestFactory, final String pageUrl, final Listener listener) {
        requestFactory.get(pageUrl, true, new RequestFactory.Listener() {
            @Override
            public void onSucceeded(URL url, String data) {
                try {
                    listener.onSucceeded(parse(data));
                } catch (Exception e) {
                    listener.onFailed(e.getMessage());
                }
            }

            @Override
            public void onFailed(String message) {
                listener.onFailed(message);
            }
        });
    }
}
