package com.huetoyou.chatexchange.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.huetoyou.chatexchange.R;
import com.jrummyapps.android.colorpicker.ColorPanelView;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerView;
import com.jrummyapps.android.colorpicker.ColorPreference;

public class PreferencesActivity extends AppCompatPreferenceActivity {
    private SharedPreferences mSharedPrefs;
    private ColorPickerView colorPickerView;
    private ColorPanelView newColorPanelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        setActionBarColor();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            ColorPreference colorPreference = (ColorPreference) findPreference("default_color");
            colorPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ((PreferencesActivity)getActivity()).setActionBarColor();
                    return true;
                }
            });

            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("dynamic_bar_color");
            setAppBarColorChange(checkBoxPreference);
        }

        private void setAppBarColorChange(CheckBoxPreference checkBoxPreference) {
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean pref = Boolean.parseBoolean(newValue.toString());

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sharedPreferences.edit().putBoolean("dynamicallyColorBar", pref).apply();

                    return true;
                }
            });
        }
    }

    private void setActionBarColor()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int initialColor = prefs.getInt("default_color", 0xFF000000);
        System.out.println(initialColor);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(initialColor);
        bar.setBackgroundDrawable(cd);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(manipulateColor(initialColor, 0.7f));
        }
    }

    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }

//    @Override
//    public void onBackPressed() {
//        Intent i = getBaseContext().getPackageManager()
//                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);
//        super.onBackPressed();
//    }



    @Override
    protected void onDestroy() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onDestroy();
    }

}