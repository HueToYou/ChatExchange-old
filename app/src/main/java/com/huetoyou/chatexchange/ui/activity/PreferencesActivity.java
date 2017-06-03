package com.huetoyou.chatexchange.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
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
import android.widget.Toast;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.backend.BackendService;
import com.huetoyou.chatexchange.ui.misc.HueUtils;
import com.jrummyapps.android.colorpicker.ColorPanelView;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerView;
import com.jrummyapps.android.colorpicker.ColorPreference;

public class PreferencesActivity extends AppCompatPreferenceActivity {
    private SharedPreferences mSharedPrefs;
    private ColorPickerView colorPickerView;
    private ColorPanelView newColorPanelView;

    static HueUtils hueUtils = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        hueUtils = new HueUtils();

        hueUtils.setActionBarColorDefault(this);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        private SharedPreferences mSharedPreferences;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            ColorPreference colorPreference = (ColorPreference) findPreference("default_color");
            colorPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    hueUtils.setActionBarColorDefault(((PreferencesActivity)getActivity()));
                    return true;
                }
            });

            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("dynamic_bar_color");
            setAppBarColorChange(checkBoxPreference);

            ListPreference listPreference = (ListPreference) findPreference("backend_type");
            setBackendMethod(listPreference);
        }

        private void setAppBarColorChange(CheckBoxPreference checkBoxPreference) {
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean pref = Boolean.parseBoolean(newValue.toString());

                    mSharedPreferences.edit().putBoolean("dynamicallyColorBar", pref).apply();

                    return true;
                }
            });
        }

        private void setBackendMethod(final ListPreference listPreference) {
            String currentSelected = mSharedPreferences.getString("backend_selected", "None");
            int index = listPreference.findIndexOfValue(currentSelected);
            listPreference.setValueIndex(index != -1 ? index : 0);

            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getActivity(), newValue.toString(), Toast.LENGTH_LONG).show();

                    String prefVal = "";

                    switch (newValue.toString().toLowerCase()) {
                        case "websocket":
                            prefVal = BackendService.BACKEND_WEBSOCKET;
                            break;
                        case "none":
                            prefVal = BackendService.BACKEND_NONE;
                            break;
                    }
                    mSharedPreferences.edit().putString("backend_type", prefVal).apply();
                    mSharedPreferences.edit().putString("backend_selected", newValue.toString()).apply();

                    int index = listPreference.findIndexOfValue(newValue.toString());
                    listPreference.setValueIndex(index != -1 ? index : 0);
                    return false;
                }
            });
        }
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