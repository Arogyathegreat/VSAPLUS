package com.teamdefault.vsaplus;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;



public class EmailSignupFragment extends Fragment {
    private FirebaseAuth mAuth;
    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mconfirmView;
    private Button signup;
    private Button signin;
    FirebaseUser user;
    DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;



    public static EmailSignupFragment newInstance() {
        return new EmailSignupFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_email_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mNameView = (EditText) v.findViewById(R.id.signup_name) ;
        mEmailView = (EditText) v.findViewById(R.id.email_entry);
        mPasswordView = (EditText) v.findViewById(R.id.password_entry);
        mconfirmView = (EditText) v.findViewById(R.id.password_confirm_entry);
        signup = (Button) v.findViewById(R.id.continue_);
        signin = (Button) v.findViewById(R.id.already_button);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(SigninFragment.newInstance());
            }
        });


        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String userName = mNameView.getText().toString();
                final String userID = mEmailView.getText().toString();
                final String userPassword = mPasswordView.getText().toString();
                final String confirmpass = mconfirmView.getText().toString();
                if(userID.equals("")||userPassword.equals("")){
                    Toast.makeText(getContext(),"put valid info!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!userPassword.equals(confirmpass)){
                    Toast.makeText(getApplicationContext(),"nope",Toast.LENGTH_LONG).show();
                    return;
                }
                final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userName).build();
                mAuth.createUserWithEmailAndPassword(userID, userPassword)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                user = mAuth.getCurrentUser();
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "User profile updated."+userName);
                                                        updateUI(user);
                                                    }
                                                }
                                            });



                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }


                                // ...
                            }
                        });
            }
        });

        return v;
    }
    private void updateUI(FirebaseUser User){
        if(User != null){
           /* Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();*/
            ((MainActivity)getActivity()).loadFragment(ProfileFragment.newInstance());
        }

    }

}