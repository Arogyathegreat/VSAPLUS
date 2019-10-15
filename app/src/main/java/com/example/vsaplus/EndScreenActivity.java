package com.example.vsaplus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.vsagames.demo.GameTestActivity;

public class EndScreenActivity extends GameTestActivity {

    GameTestActivity gameTestActivity = new GameTestActivity();
    public int scoreUnity;
    TextView scoreView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);

        Bundle extras = getIntent().getExtras();
        scoreUnity = extras.getInt("score");

        scoreView = (TextView) findViewById(R.id.game_score);
    }
}

