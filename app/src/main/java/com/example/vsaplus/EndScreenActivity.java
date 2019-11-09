package com.example.vsaplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unity3d.player.UnityPlayerActivity;

public class EndScreenActivity extends UnityPlayerActivity {

    String scoreUnity;
    TextView scoreView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);

        scoreView = (TextView)findViewById(R.id.game_score);

        Bundle extras = getIntent().getExtras();
        scoreUnity = extras.getString("score");
        scoreView.setText(scoreUnity);
        if(user!=null){
            myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long scoredata = (Long)dataSnapshot.child("score").getValue();
                    if(scoredata == null){
                        scoredata = 0L;
                    }
                    scoredata += Long.parseLong(scoreUnity);
                    myRef.child(user.getUid()).child("score").setValue(scoredata);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"Some error occured in score data pushing",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
