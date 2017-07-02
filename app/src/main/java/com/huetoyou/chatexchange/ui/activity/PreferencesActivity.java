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
import android.view.MenuItem;
import android.widget.Toast;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.auth.AuthenticatorActivity;
import com.huetoyou.chatexchange.backend.BackendService;
import com.huetoyou.chatexchange.ui.misc.AppCompatPreferenceActivity;
import com.huetoyou.chatexchange.ui.misc.hue.ActionBarHue;
import com.huetoyou.chatexchange.ui.misc.hue.ThemeHue;
import com.jrummyapps.android.colorpicker.ColorPreference;

import java.util.ArrayList;

public class PreferencesActivity extends AppCompatPreferenceActivity
{
    private static SharedPreferences mSharedPrefs;
    private final ArrayList<CharSequence> mAccountNames = new ArrayList<>();
    private static ActionBarHue actionBarHue = null;
    private static ThemeHue themeHue = null;
    private static boolean darkThemePrevState;
    private static ColorPreference fabColorPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        themeHue = new ThemeHue();
        actionBarHue = new ActionBarHue();
        ThemeHue.setTheme(PreferencesActivity.this);

        super.onCreate(null);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        ActionBarHue.setActionBarColorToSharedPrefsValue(this);

        AccountManager mAccountManager = AccountManager.get(this);
        if (mAccountManager.getAccounts().length > 0)
        {
            Account[] mAccounts = mAccountManager.getAccounts();

            for (Account a : mAccounts)
            {
                mAccountNames.add(a.name);
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            /*
             * Default color preference
             */
            ColorPreference colorPreference = (ColorPreference) findPreference("default_color");
            colorPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    ActionBarHue.setActionBarColorToSharedPrefsValue(((PreferencesActivity) getActivity()));
                    return true;
                }
            });

            /*
             * Dynamically change color based on chat room theme preference
             */
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("dynamic_bar_color");
            setAppBarColorChange(checkBoxPreference);

            /*
             * FAB color preference
             */
            fabColorPreference = (ColorPreference) findPreference("fab_color");
            colorPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    ActionBarHue.setActionBarColorToSharedPrefsValue(((PreferencesActivity) getActivity()));
                    return true;
                }
            });

            /*
             * Dark theme preference
             */
            CheckBoxPreference darkThemePref = (CheckBoxPreference) findPreference("dark_theme");
            darkThemePrevState = darkThemePref.isChecked();
            setDarkTheme(darkThemePref);

            /*
             * Backend preference
             */
            ListPreference backend = (ListPreference) findPreference("backend_type");
            setBackendMethod(backend);

            /*
             * Account preference
             */
            ListPreference account = (ListPreference) findPreference("account_select");
            setAccount(account);

            Preference addAcc = findPreference("add_account");
            addAcc.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    startActivity(new Intent(getActivity(), AuthenticatorActivity.class));
                    return false;
                }
            });

        }

        private void setAppBarColorChange(CheckBoxPreference checkBoxPreference)
        {
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    boolean pref = Boolean.parseBoolean(newValue.toString());

                    mSharedPrefs.edit().putBoolean("dynamicallyColorBar", pref).apply();

                    return true;
                }
            });
        }

        private void setBackendMethod(final ListPreference listPreference)
        {
            String currentSelected = mSharedPrefs.getString("backend_selected", "None");
            int index = listPreference.findIndexOfValue(currentSelected);
            listPreference.setValueIndex(index != -1 ? index : 0);

            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    Toast.makeText(getActivity(), newValue.toString(), Toast.LENGTH_LONG).show();

                    String prefVal = "";

                    switch (newValue.toString().toLowerCase())
                    {
                        case "websocket":
                            prefVal = BackendService.BACKEND_WEBSOCKET;
                            break;
                        case "none":
                            prefVal = BackendService.BACKEND_NONE;
                            break;
                    }
                    mSharedPrefs.edit().putString("backend_type", prefVal).apply();
                    mSharedPrefs.edit().putString("backend_selected", newValue.toString()).apply();

                    int index = listPreference.findIndexOfValue(newValue.toString());
                    listPreference.setValueIndex(index != -1 ? index : 0);
                    return false;
                }
            });
        }

        private void setAccount(final ListPreference listPrefernece)
        {
            ArrayList<CharSequence> accnames = ((PreferencesActivity) getActivity()).mAccountNames;

            CharSequence[] names = new CharSequence[accnames.size()];
            names = accnames.toArray(names);

            listPrefernece.setEntries(names);
            listPrefernece.setEntryValues(names);

            String currentSelected = mSharedPrefs.getString("account_selected", "None");
            int index = listPrefernece.findIndexOfValue(currentSelected);
            listPrefernece.setValueIndex(index != -1 ? index : 0);

            listPrefernece.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    mSharedPrefs.edit().putString("account_selected", newValue.toString()).apply();
                    int index = listPrefernece.findIndexOfValue(newValue.toString());
                    listPrefernece.setValueIndex(index != -1 ? index : 0);
                    return false;
                }
            });
        }

        private void setDarkTheme(CheckBoxPreference pref)
        {
            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    boolean pref = Boolean.parseBoolean(newValue.toString());

                    mSharedPrefs.edit().putBoolean("darkTheme", pref).apply();

                    if (pref != darkThemePrevState)
                    {
                        mSharedPrefs.edit().putBoolean("FLAG_restartMain", true).apply();
                    } else
                    {
                        mSharedPrefs.edit().putBoolean("FLAG_restartMain", false).apply();
                    }

                    getActivity().recreate();

                    return true;
                }
            });
        }
    }


    /*@Override
    public void onBackPressed() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        super.onBackPressed();
    }*/


    @Override
    protected void onDestroy()
    {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onDestroy();
    }

}