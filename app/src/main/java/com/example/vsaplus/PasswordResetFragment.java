package com.example.vsaplus;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordResetFragment extends Fragment {
    FirebaseAuth mAuth;
    EditText resetEmail;

    public static PasswordResetFragment newInstance() {
        return new PasswordResetFragment();
    }
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_reset, container, false);
        resetEmail = (EditText)view.findViewById(R.id.email);

        resetEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    String email = resetEmail.getText().toString();
                    buttonclicked(email);
                    return true;
                }
                return false;
            }
        });


        return view;
    }

    public void buttonclicked(String emailAddress){
        if ( emailAddress.length() == 0 ) {
            Toast.makeText(getActivity(),"Please type your email above",Toast.LENGTH_LONG).show();
            return;

        } else {

            mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "An email has been sent to your account!", Toast.LENGTH_LONG).show();
                        thread.start();
                    } else {
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        resetEmail.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(resetEmail.getWindowToken(), 0);

    }

    Thread thread = new Thread(){
        @Override
        public void run() {
            try {
                Thread.sleep(Toast.LENGTH_LONG);
                ((MainActivity) getActivity()).loadFragment(SignupFragment.newInstance());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


}
