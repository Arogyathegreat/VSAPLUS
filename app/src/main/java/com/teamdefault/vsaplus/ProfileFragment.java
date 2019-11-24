package com.teamdefault.vsaplus;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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

        if (user != null) {
            // User is signed in
            score = v.findViewById(R.id.score);
            myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("tag", "onAuthStateChanged:signed_in:" + user.getUid());
                    String status = (String)dataSnapshot.child("status").getValue();
                    if(status == null){
                        status = "";
                    }
                    statusBox.setText(status);
                    String scoredata=null;
                    try {
                         scoredata = dataSnapshot.child("score").getValue().toString();

                    }
                    catch(NullPointerException e){
                        scoredata = "0";
                    }
                    finally {
                        score.setText(scoredata);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        statusBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    user = mAuth.getCurrentUser();
                    String statusdata = statusBox.getText().toString();
                    HashMap<String, String> profile = new HashMap<String, String>();
                    profile.put("status",statusdata);
                    Map<String, Object> inputprofile = new HashMap<String, Object>(profile);
                    myRef.child(user.getUid()).updateChildren(inputprofile);
                    statusBox.setText(statusdata);
                    }
                return false;
            }
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