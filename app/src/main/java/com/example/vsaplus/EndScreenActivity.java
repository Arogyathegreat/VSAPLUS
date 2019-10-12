package com.example.vsaplus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.vsagames.demo.GameTestActivity;

public class EndScreenActivity extends AppCompatActivity {

    private GameTestActivity gameTestActivity;
    String scoreUnity;
    TextView scoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);

        scoreView = (TextView)findViewById(R.id.game_score);

        Bundle extras = getIntent().getExtras();
        scoreUnity = extras.getString("score");

        scoreView.setText(scoreUnity);

    }
}
