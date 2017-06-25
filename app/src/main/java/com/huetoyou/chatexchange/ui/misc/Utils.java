package com.huetoyou.chatexchange.ui.misc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils
{
    private SharedPreferences mSharedPreferences = null;

    public int getColorInt(Activity activity, String url) {

        if(mSharedPreferences == null)
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        }

        try {
            Document doc = Jsoup.connect(url).get();

            Elements styles = doc.select("link");
            Element element = new Element("hue");

            for (int i = 0; i < styles.size(); i++) {
                Element current = styles.get(i);

                if (current.hasAttr("href") && current.attr("rel").equals("stylesheet")) {
                    element = current;
                    break;
                }
            }

            String link = "";
            if (element.hasAttr("href")) {
                link = element.attr("href");
                if (!(link.contains("http://") || link.contains("https://")))
                    link = "https:".concat(link);
            }


//                Log.e("UR", link);
            URL url1 = new URL(link);

            InputStream inStr = url1.openStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStr));
            String line;
            String css = "";

            while ((line = bufferedReader.readLine()) != null) {
                css = css.concat(line);
            }


            Pattern p = Pattern.compile("\\.msparea\\{(.+?)\\}");
            Matcher m = p.matcher(css);
            String a = "";

            if (m.find()) {
                a = m.group();
            }

            p = Pattern.compile("color:(.*?);");
            m = p.matcher(a);

            String colorHex = "#000000";

            if (m.find()) {
                colorHex = m.group().replace("color", "").replace(":", "").replace(";", "").replaceAll(" ", "");
            }

            mSharedPreferences.edit().putInt(url + "Color", Color.parseColor(colorHex)).apply();
            return Color.parseColor(colorHex);
        } catch (Exception e) {
            e.printStackTrace();
            return Color.parseColor("#000000");
        }
    }
}
