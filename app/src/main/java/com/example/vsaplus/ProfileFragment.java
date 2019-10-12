package com.example.vsaplus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileFragment extends Fragment {
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    private ProfilePictureView profileImage;
    private TextView profileName;
    private ImageButton settingsButton;
    private EditText statusBox;
    private FirebaseAuth mAuth;
    private TextView score;
    String TAG = getClass().getSimpleName();
    ImageView ivUser;
    private StorageReference mStorageRef;
    private SwipeRefreshLayout mSwipeRefreshWidget;
    Bitmap bitmap;
    String stUid;
    String stEmail;
    FirebaseUser user;
    DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    SharedPreferences sharedPreferences;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        mSwipeRefreshWidget = (SwipeRefreshLayout) v.findViewById(R.id.layout);
        String username = mAuth.getCurrentUser().getDisplayName();
        if(username==null||username.equals("")){
            username = "UNKNOWN";
        }

        profileName = v.findViewById(R.id.userName);
        profileName.setText(username);
        profileImage = (ProfilePictureView) v.findViewById(R.id.profileImage);//facebook user
        ivUser = (ImageView) v.findViewById(R.id.fbuserprofile);//firebase user
        user = mAuth.getCurrentUser();
        statusBox = (EditText) v.findViewById(R.id.statusBox);
        mSwipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity)getActivity()).loadFragment(ProfileFragment.newInstance());
                mSwipeRefreshWidget.setRefreshing(false);
            }
        });
       // statusBox.setText(myRef.child(user.getUid()).child("status").);
        if (user != null) {
            // User is signed in

            Log.d("tag", "onAuthStateChanged:signed_in:" + user.getUid());

            sharedPreferences = getContext().getSharedPreferences("email", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("uid", user.getUid());
            editor.putString("email", user.getEmail());

            editor.apply();
            statusBox.setText(sharedPreferences.getString("status",""));
        }

        statusBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    user = mAuth.getCurrentUser();

                    sharedPreferences = getContext().getSharedPreferences("email", Context.MODE_PRIVATE);
                    stUid = sharedPreferences.getString("uid", "");
                    stEmail = sharedPreferences.getString("email", "");

                    String userphoto = String.valueOf(user.getPhotoUrl());
                    String statusdata = statusBox.getText().toString();
                    HashMap<String, String> profile = new HashMap<String, String>();
                    profile.put("email",stEmail);
                    profile.put("photo",userphoto);
                    profile.put("status",statusdata);
                    Map<String, Object> inputprofile = new HashMap<String, Object>(profile);

                    myRef.child(stUid).updateChildren(inputprofile);

                    sharedPreferences = getContext().getSharedPreferences("email", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("status",statusdata);
                    editor.putString("uid", user.getUid());
                    editor.putString("email", user.getEmail());
                    editor.apply();

                    }

                return false; }
        });



        String userID;
        if(Profile.getCurrentProfile()==null||Profile.getCurrentProfile().getId().equals("")){//firebase user logic
            userID = null;
            profileImage.setVisibility(View.GONE);
            ivUser.setVisibility(View.VISIBLE);
            if(mAuth.getCurrentUser().getPhotoUrl()==null||mAuth.getCurrentUser().getPhotoUrl().equals("")){
                ivUser.setImageResource(R.drawable.defaultprofile);
            }
            else{
                String stPhoto = mAuth.getCurrentUser().getPhotoUrl().toString();
                Picasso.get()
                        .load(stPhoto)
                        .fit()
                        .centerInside()
                        .into(ivUser, new Callback.EmptyCallback() {
                            @Override
                            public void onSuccess() {
                                // Index 0 is the image view.
                                Log.d("tag", "SUCCESS");

                            }
                        });
            }

        }
        else{//facebook user
            userID = Profile.getCurrentProfile().getId();
            profileImage.setVisibility(View.VISIBLE);
            ivUser.setVisibility(View.GONE);
            profileImage.setProfileId(userID);
            String photouri = "https://graph.facebook.com/" + userID+ "/picture?type=large";
            Hashtable<String,Object> uploadimage = new Hashtable<>();
            uploadimage.put("photo",photouri);
            myRef.child(user.getUid()).updateChildren(uploadimage);
        }



        settingsButton = (ImageButton)v.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.profile_options,popupMenu.getMenu());
                if(Profile.getCurrentProfile() == null){
                    popupMenu.getMenu().findItem(R.id.editprofile).setEnabled(true);
                }
                else{
                    popupMenu.getMenu().findItem(R.id.editprofile).setEnabled(false);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.signout:
                                profileImage.setProfileId(null);
                                FirebaseAuth.getInstance().signOut();
                                LoginManager.getInstance().logOut();
                                ((MainActivity)getActivity()).loadFragment(SignupFragment.newInstance());
                                break;
                            case R.id.editprofile:
                                ((MainActivity)getActivity()).loadFragment(EditUserInfoFragment.newInstance());
                                break;
                            case R.id.bookmarks:
                                ((MainActivity)getActivity()).loadFragment(BookmarkFragment.newInstance());
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        return v;
    }

}