package com.huetoyou.chatexchange.ui.frags;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.huetoyou.chatexchange.R;
import com.huetoyou.chatexchange.auth.AuthenticatorActivity;
import com.huetoyou.chatexchange.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class AccountsFragment extends Fragment {

    private AccountManager mAccountManager;
    private View view;

    public AccountsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_accounts, container, false);

        getActivity().setTitle(getResources().getText(R.string.app_name));

        mAccountManager = AccountManager.get(getActivity());

        Account[] accounts = mAccountManager.getAccounts();

        LinearLayout accountLayout = (LinearLayout) view.findViewById(R.id.select_account_lin);

        List<String> spinnerArray =  new ArrayList<String>();
        for (final Account account : accounts) {
            spinnerArray.add(account.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) accountLayout.findViewById(R.id.accountsSpinner);
        sItems.setAdapter(adapter);

        Button newAccount = new AppCompatButton(getActivity());
        newAccount.setText(getResources().getText(R.string.activity_main_add_account));
        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), AuthenticatorActivity.class));
            }
        });

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 20, 0, 0);
        newAccount.setLayoutParams(params);

        accountLayout.addView(newAccount);

        View v = new View(getActivity());
        v.setMinimumHeight(2);
        v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        ViewGroup.MarginLayoutParams vparams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        vparams.setMargins(40, 40, 40, 40);
        v.setLayoutParams(vparams);
        accountLayout.addView(v);

        Button newChat = new AppCompatButton(getActivity());
        newChat.setText(getResources().getText(R.string.activity_main_add_chat));
        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity)getActivity()).showAddTabDialog();
                }
            }
        });

        accountLayout.addView(newChat);

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
