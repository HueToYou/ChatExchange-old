package com.huetoyou.chatexchange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;

import com.huetoyou.chatexchange.auth.AuthenticatorActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPrefs = getSharedPreferences(getResources().getText(R.string.app_name).toString(), MODE_PRIVATE);

        startActivity(new Intent(this, AuthenticatorActivity.class));
    }
}
