package com.huetoyou.chatexchange;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.huetoyou.chatexchange.auth.AuthenticatorActivity;

public class AccountsFragment extends Fragment {

    private AccountManager mAccountManager;

    public AccountsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        mAccountManager = AccountManager.get(getActivity());

        Account[] accounts = mAccountManager.getAccounts();

        LinearLayout accountLayout = (LinearLayout) view.findViewById(R.id.select_account_lin);

        for (final Account account : accounts) {
            final Button acc = new AppCompatButton(getActivity());
            acc.setText(account.name);
            acc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            accountLayout.addView(acc);
        }

        Button newAccount = new AppCompatButton(getActivity());
        newAccount.setText(getResources().getText(R.string.activity_authenticator_add_account));
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), AuthenticatorActivity.class));
                getActivity().finish();
            }
        });

        accountLayout.addView(newAccount);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
