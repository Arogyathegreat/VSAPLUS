package com.teamdefault.vsaplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DemoMakeWordActivity extends AppCompatActivity{

    String actualAnswer;
    String[] userAnswer;
    String[] userOptions;


    String userOptionsTest;
    TextView[] userInput;
    TextView[] optionInput;

    TextView[] answerFragments;
    String imageUrl;

    LinearLayout userAnswerContainer;
    LinearLayout userOptionContainer;

    List<CharSequence>buffer;

    ImageView imageHint;
    LottieAnimationView checkMark;

    List<String> list = new ArrayList<>();
    List<String> answer = new ArrayList<>();

    static int refreshCheck = 4;
    static int score = 30;
    boolean response;
    boolean check;

    private FirebaseFirestore rootref = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_word);

        checkMark = (LottieAnimationView)findViewById(R.id.green_tick);
        userAnswerContainer = (LinearLayout)findViewById(R.id.tv_ans_container);
        userOptionContainer = (LinearLayout)findViewById(R.id.tv_letter_container);
        imageHint = (ImageView)findViewById(R.id.image_hint);

        String[] question1 = new String[]{"안", "녕", "하", "세", "요"};
        String[] question2 = new String[]{"잘", "먹", "겠", "습", "니", "다"};
        String[] question3 = new String[]{"감", "사", "합", "니", "다"};
        String[] question4 = new String[]{"여", "보", "세", "요"};
        String[] question5 = new String[]{"죄", "송", "합", "니", "다"};



        String photo1 = "https://firebasestorage.googleapis.com/v0/b/team-default.appspot.com/o/demo%2Fgreet.jpg?alt=media&token=7b416195-2e37-41e4-afc6-dc47a079d397";
        String photo2 = "https://firebasestorage.googleapis.com/v0/b/team-default.appspot.com/o/demo%2Feat.jpg?alt=media&token=2d38803d-2004-412b-b8bd-c49bad036f2a";
        String photo3 = "https://firebasestorage.googleapis.com/v0/b/team-default.appspot.com/o/demo%2Fthanks.jpg?alt=media&token=bd38bf74-7860-45f9-a373-2dc79c2cec4f";
        String photo4 = "https://firebasestorage.googleapis.com/v0/b/team-default.appspot.com/o/demo%2Fhello.jpg?alt=media&token=ab5f9e4b-5f03-490d-bb05-c444cab434b0";
        String photo5 = "https://firebasestorage.googleapis.com/v0/b/team-default.appspot.com/o/demo%2Fsorry.jpg?alt=media&token=bc481f99-ee6e-46ac-9ce9-eff50b9fb5cf";
        HashMap<String[], String> makeWordData = new HashMap<String[], String>();

        makeWordData.put(question1, photo1);
        makeWordData.put(question2, photo2);
        makeWordData.put(question3, photo3);
        makeWordData.put(question4, photo4);
        makeWordData.put(question5, photo5);
        check = true;
        shuffle(makeWordData);

    }


    public void shuffle(HashMap<String[], String> toShuffle)
    {

        for (Object key : toShuffle.keySet()) {
            Log.d("check", ""+ check);
            if(check == true) {
                List<String> temp = new ArrayList<>();
                List<String> answer = new ArrayList<>();
                String[] tempKey = (String[]) key;
                int length = tempKey.length;
                for (int i = 0; i < length; i++) {
                    temp.add(tempKey[i]);
                    answer.add(tempKey[i]);
                }
                Log.d("list", "list is: " + answer.toString());
                Collections.shuffle(temp);
                Log.d("list", "list is shuffled: " + temp.toString());
                Log.d("list", "list is shuffled: " + answer.toString());
                imageSetter(temp, answer);

                Picasso.get()
                        .load(toShuffle.get(key))
                        .fit()
                        .centerInside()
                        .into(imageHint);
            }
        }
    }

    public void imageSetter(List<String>wordList,List<String>answer)
    {
        int j;
        response = false;
        answerFragments = new TextView[wordList.size()];
        for(j = 0; j<wordList.size(); j++)
        {
            TextView answerFragment = new TextView(this);
            answerFragment.setId(j+1);
            answerFragment.setText(wordList.get(j));
            answerFragment.setTextSize(40);
            answerFragment.setPadding(20,10,10,10);
            userOptionContainer.addView(answerFragment);
            check = false;
            answerFragment.setOnClickListener(handleOnClick(answer, answerFragment, j));
        }
    }

    View.OnClickListener handleOnClick(final List<String> answer, final TextView textView, final int index)
    {
        buffer = new ArrayList<CharSequence>();
        View.OnClickListener  onClickListener =  new View.OnClickListener(){
            public void onClick(View v){
                CharSequence textFromTV = textView.getText();
                TextView answerView = new TextView(getApplicationContext());
                buffer.add(textFromTV);
                Log.d("Test", "shuffled index: "+index);
                int temp = answer.indexOf(textFromTV);
                Log.d("Test", "non shuffled index: "+temp);
                Log.d("Test", "buffer: "+buffer.indexOf(textFromTV));
                if(buffer.indexOf(textFromTV)==temp){
                    textView.setVisibility(View.INVISIBLE);
                    answerView.setId(index*100);
                    answerView.setTextSize(40);
                    answerView.setText(textFromTV);
                    userAnswerContainer.addView(answerView);
                }
                else {
                    buffer.remove(textFromTV);
                    score -= 2;
                    if(score <= 0) score = 0;
                }

                temp = answer.size();
                Log.d("size", "answersizef: " + temp);

                if(userAnswerContainer.getChildCount() == answer.size())
                {
                    checkMark.playAnimation();
                    checkMark.addAnimatorListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(refreshCheck>0) {
                                userAnswerContainer.removeAllViews();
                                userOptionContainer.removeAllViews();
                                recreate();
                                refreshCheck--;
                                Log.d("refresh", ""+ refreshCheck);
                            }else
                                {
                                    Intent intent = new Intent(DemoMakeWordActivity.this, EndScreenActivity.class);
                                    intent.putExtra("unityScore", score);
                                    startActivity(intent);
                                    finish();
                                }
                        }
                    });
                }

            }

        };

        return onClickListener;
    }


}
