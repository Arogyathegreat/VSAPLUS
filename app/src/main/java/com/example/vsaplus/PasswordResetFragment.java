package com.example.vsaplus;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordResetFragment extends Fragment {
    FirebaseAuth mAuth;
    EditText editText = (EditText)getActivity().findViewById(R.id.email);

    public static PasswordResetFragment newInstance() {
        return new PasswordResetFragment();
    }
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    public void buttonclicked(View v){
        String emailAddress;
        if ( editText.getText().toString().length() == 0 ) {
            Toast.makeText(getActivity(),"put your email above the button",Toast.LENGTH_LONG);
            return;
        } else {
            emailAddress = editText.getText().toString();
        }

        mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"password reset email has sent. check your email!",Toast.LENGTH_LONG);
                }
                else{
                    Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_reset, container, false);
    }

}
