package com.huetoyou.chatexchange.ui.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.ui.activity.AppCompatPreferenceActivity;

public class PreferencesActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}