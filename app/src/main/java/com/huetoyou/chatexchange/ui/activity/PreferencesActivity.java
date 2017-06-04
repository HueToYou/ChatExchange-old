package com.huetoyou.chatexchange.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.auth.AuthenticatorActivity;
import com.huetoyou.chatexchange.backend.BackendService;
import com.huetoyou.chatexchange.ui.misc.HueUtils;
import com.jrummyapps.android.colorpicker.ColorPanelView;
import com.jrummyapps.android.colorpicker.ColorPickerView;
import com.jrummyapps.android.colorpicker.ColorPreference;

import java.util.ArrayList;

public class PreferencesActivity extends AppCompatPreferenceActivity {
    private SharedPreferences mSharedPrefs;
    private ColorPickerView colorPickerView;
    private ColorPanelView newColorPanelView;
    private AccountManager mAccountManager;
    private Account[] mAccounts;
    private ArrayList<CharSequence> mAccountNames = new ArrayList<>();

    static HueUtils hueUtils = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        hueUtils = new HueUtils();

        hueUtils.setActionBarColorDefault(this);

        mAccountManager = AccountManager.get(this);
        if (mAccountManager.getAccounts().length > 0) {
            mAccounts = mAccountManager.getAccounts();

            for (Account a : mAccounts) {
                mAccountNames.add(a.name);
            }
        }
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

            ListPreference backend = (ListPreference) findPreference("backend_type");
            setBackendMethod(backend);

            ListPreference account = (ListPreference) findPreference("account_select");
            setAccount(account);

            Preference addAcc = findPreference("add_account");
            addAcc.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), AuthenticatorActivity.class));
                    return false;
                }
            });

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

        private void setAccount(final ListPreference listPrefernece) {
            ArrayList<CharSequence> accnames = ((PreferencesActivity)getActivity()).mAccountNames;

            CharSequence[] names = new CharSequence[accnames.size()];
            names = accnames.toArray(names);

            listPrefernece.setEntries(names);
            listPrefernece.setEntryValues(names);

            String currentSelected = mSharedPreferences.getString("account_selected", "None");
            int index = listPrefernece.findIndexOfValue(currentSelected);
            listPrefernece.setValueIndex(index != -1 ? index : 0);

            listPrefernece.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    mSharedPreferences.edit().putString("account_selected", newValue.toString()).apply();
                    int index = listPrefernece.findIndexOfValue(newValue.toString());
                    listPrefernece.setValueIndex(index != -1 ? index : 0);
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