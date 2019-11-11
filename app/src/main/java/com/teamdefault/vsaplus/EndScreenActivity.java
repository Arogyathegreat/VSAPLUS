package com.teamdefault.vsaplus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

