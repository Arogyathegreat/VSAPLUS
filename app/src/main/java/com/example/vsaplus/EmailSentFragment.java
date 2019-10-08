package com.example.vsaplus;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;


public class EmailSentFragment extends Fragment {
    public static EmailSentFragment newInstance() {
        return new EmailSentFragment();
    }
    private Button btp;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verification_sent, container, false);
        btp = (Button) view.findViewById(R.id.back_to_profile);
        btp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                ((MainActivity)getActivity()).loadFragment(SigninFragment.newInstance());
            }
        });
        return view;
    }

}
