package com.teamdefault.vsaplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vsagames.demo.GameTestActivity;

public class EndScreenActivity extends AppCompatActivity {
    final String TAG = "EndScreenActivity";

    GameTestActivity gameTestActivity = new GameTestActivity();
    int scoreUnity;
    TextView scoreView;
    Button goBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);
        scoreView = (TextView) findViewById(R.id.game_score);
        goBack = (Button)findViewById(R.id.goBack);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bd = getIntent().getExtras();
        if(bd!= null) {
            scoreUnity = (int)bd.get("unityScore");
            String test = (scoreUnity) + " points";
            scoreView.setText(test);
        }
        Log.d(TAG, "score is: "+ scoreUnity);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        if(user!=null){
            myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long scoredata = (Long)dataSnapshot.child("score").getValue();
                    if(scoredata == null){
                        scoredata = 0L;
                    }
                    scoredata += Long.parseLong(scoreUnity+"");
                    myRef.child(user.getUid()).child("score").setValue(scoredata);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"Some error occured in score data pushing",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
        }
        return true;
    }
}

