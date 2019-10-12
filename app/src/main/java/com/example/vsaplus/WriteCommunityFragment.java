package com.example.vsaplus;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class WriteCommunityFragment extends Fragment {
    private EditText Title;
    private EditText contents;
    private ImageButton pic_upload;
    private ImageView picture;
    private Button commit;
    private ImageButton exit;
    private static int postnumber;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    public static WriteCommunityFragment newInstance(){
        return new WriteCommunityFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createpost,container,false);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("community");
        exit = view.findViewById(R.id.exit_post);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        contents = view.findViewById(R.id.post_contents);
        Title = view.findViewById(R.id.title);
        commit = view.findViewById(R.id.post);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(CommunityFragment.newInstance());
            }
        });
        myRef.child("number").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer buf = dataSnapshot.getValue(Integer.class);
                postnumber = buf;
                SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("postnumber",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences1.edit();
                editor.putInt("postnum",postnumber);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }
         );

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("postnumber", Context.MODE_PRIVATE);
        postnumber = sharedPreferences.getInt("postnum",0);
        if(user == null){
            ((MainActivity)getActivity()).loadFragment(SignupFragment.newInstance());
        }else {


            commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = Title.getText().toString();
                    String content = contents.getText().toString();
                    if (title == null || content == null||content.equals("")) {
                        Toast.makeText(getContext(), "내용 입력하세요.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    myRef.child("number").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Integer buf = dataSnapshot.getValue(Integer.class);
                            postnumber = buf;
                            postnumber=postnumber+1;
                            SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("postnumber",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences1.edit();
                            editor.putInt("postnum",postnumber);
                            editor.apply();
                            SharedPreferences sharedPreferences2 = getContext().getSharedPreferences("postnumber",Context.MODE_PRIVATE);
                            postnumber = sharedPreferences2.getInt("postnum",0);
                            Hashtable<String,Object> numeric = new Hashtable<>();
                            numeric.put("number", postnumber);
                            Map<String, Object> num = new Hashtable<>(numeric);
                            myRef.updateChildren(num).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("tag",postnumber+" is updated!");
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    SharedPreferences sharedPreferences2 = getContext().getSharedPreferences("postnumber",Context.MODE_PRIVATE);
                    postnumber = sharedPreferences2.getInt("postnum",0);
                    Hashtable<String, Object> upload = new Hashtable<>();
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("title", title);
                    data.put("content", content);
                    data.put("Like", 0);
                    data.put("Reply", 0);
                    data.put("userName", user.getDisplayName());
                    data.put("UserUid",user.getUid());
                    data.put("postnum",postnumber);
                    data.put("Likepeople",null);
                    upload.put(postnumber-2*postnumber + "", data);
                    Map<String, Object> update = new Hashtable<>(upload);
                    myRef.child("post").updateChildren(update)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("tag", "success"+postnumber);
                                    ((MainActivity) getActivity()).loadFragment(CommunityFragment.newInstance());
                                }
                            });
                }
            });
        }
        return view;
    }

}

