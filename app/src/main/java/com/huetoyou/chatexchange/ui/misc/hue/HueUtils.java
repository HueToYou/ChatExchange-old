package com.huetoyou.chatexchange.ui.misc.hue;

import android.graphics.Color;

/*
 * This class contains common utils which the other Hue classes use
 */

class HueUtils
{
    /*
     * This method darkens a color by the factor specified in the second parameter
     */
    public static  int darkenColor(int color, @SuppressWarnings("SameParameterValue") float factor)
    {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }
}
