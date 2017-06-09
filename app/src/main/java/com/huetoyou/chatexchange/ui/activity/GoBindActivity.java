package com.huetoyou.chatexchange.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.backend.NewMessageListenerService;

import hello.Hello;

public class GoBindActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_bind);
        mTextView = (TextView) findViewById(R.id.mytextview);

        // Call Go function.
        //String greetings = Hello.greetings("Android and Gopher");
        //mTextView.setText(greetings);

        //mTextView.setText(Hello.helloWorld());

        //mTextView.setText(Hello.testNathansAPI());
    }

    public void hue(View v)
    {
        AccountManager accountManager = AccountManager.get(GoBindActivity.this);

        final TextView username = (TextView) findViewById(R.id.username);
        final TextView password = (TextView) findViewById(R.id.password);
        final TextView message = (TextView) findViewById(R.id.message);


        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Hue!");
        progress.setMessage("Hueing, please wait...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        System.out.println("Email: " + accountManager.getAccounts()[0].name);
        System.out.println("Pass: " + accountManager.getPassword(accountManager.getAccounts()[0]));

        Hello.initConnection(message.getText().toString(), accountManager.getAccounts()[0].name, accountManager.getPassword(accountManager.getAccounts()[0]));

        Intent mServiceIntent = new Intent(GoBindActivity.this, NewMessageListenerService.class);
        mServiceIntent.setData(Uri.parse("test"));
        this.startService(mServiceIntent);


        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter("org.golang.example.bind.BROADCAST");

        // Instantiates a new DownloadStateReceiver
        NewMessageReceiver messageReceiver = new NewMessageReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, statusIntentFilter);

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(3000);

                    GoBindActivity.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            // To dismiss the dialog
                            progress.dismiss();
                        }
                    });

                }

                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    private class NewMessageReceiver extends BroadcastReceiver
    {
        // Prevents instantiation
        private NewMessageReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String messg = intent.getStringExtra("org.golang.example.bind.STATUS");

            //System.out.println("Broadcast: " + intent.getStringExtra("org.golang.example.bind.STATUS"));

            final TextView chat = (TextView) findViewById(R.id.chatBox);
            final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.chime);
            mediaPlayer.start();

            try
            {
                GoBindActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {

                        chat.setText(chat.getText() + "\n" + messg);
                    }
                });

            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
