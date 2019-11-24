package com.teamdefault.vsaplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DemoMakeWordActivity extends AppCompatActivity{

    LinearLayout userAnswerContainer;
    LinearLayout userOptionContainer;

    List<CharSequence>buffer;

    ImageView imageHint;
    LottieAnimationView checkMark;

    Integer[] index ;

    static int refreshCheck = 4;
    static int score = 30;

    int displayDataIndex = 0;

    List<String[]> questions = new ArrayList<>();
    List<String> photos = new ArrayList<>();
    int mapIndex;
    List<String> answerCheck = new ArrayList<>();

    private FirebaseFirestore rootref = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_word);

        checkMark = (LottieAnimationView)findViewById(R.id.green_tick);
        userAnswerContainer = (LinearLayout)findViewById(R.id.tv_ans_container);
        userOptionContainer = (LinearLayout)findViewById(R.id.tv_letter_container);
        imageHint = (ImageView)findViewById(R.id.image_hint);


        index = new Integer[]{0,1,2,3,4};
        List<Integer> index_shuffle = Arrays.asList(index);
        Collections.shuffle(index_shuffle);

        insertData();

    }

    public void insertData()
    {
        mapIndex = index[displayDataIndex];

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


        questions.add(question1);
        questions.add(question2);
        questions.add(question3);
        questions.add(question4);
        questions.add(question5);


        photos.add(photo1);
        photos.add(photo2);
        photos.add(photo3);
        photos.add(photo4);
        photos.add(photo5);

        String[] toDisplay = questions.get(mapIndex);
        answerCheck = Arrays.asList(toDisplay);

        Log.d("shuffleTest", "before displayData " + TextUtils.join("",toDisplay));

        displayData(toDisplay, photos.get(mapIndex));
        displayDataIndex++;

    }

    public void displayData(String[] question, String photo)
    {

        List<String> answerShuffled = new ArrayList<>();

        Collections.addAll(answerShuffled, question);
        Collections.shuffle(answerShuffled);

        Picasso.get()
                .load(photo)
                .fit()
                .centerInside()
                .into(imageHint);

        for(int i = 0; i<question.length; i++)
        {
            TextView answerView = new TextView(getApplicationContext());
            answerView.setId(i+1);
            answerView.setText(answerShuffled.get(i));
            answerView.setTextSize(40);
            answerView.setPadding(20,10,10,10);
            userOptionContainer.addView(answerView);
            answerView.setOnClickListener(textViewOnClick(answerView, i));
        }


    }

    View.OnClickListener textViewOnClick(TextView textView, int index)
    {
        buffer = new ArrayList<CharSequence>();
        Handler handler = new Handler();
        answerCheck = Arrays.asList(questions.get(mapIndex));
        Log.d("shuffleTest", "at onclick "+ answerCheck.toString());

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence textFromTV = textView.getText();
                TextView answerView = new TextView(getApplicationContext());
                buffer.add(textFromTV);
                int tempIndex = answerCheck.indexOf(textFromTV);
                if(buffer.indexOf(textFromTV) == tempIndex)
                {
                    textView.setVisibility(View.INVISIBLE);
                    answerView.setId(index * 100);
                    answerView.setTextSize(40);
                    answerView.setText(textFromTV);
                    userAnswerContainer.addView(answerView);
                }
                else
                {
                    buffer.remove(textFromTV);
                    score -= 2;
                    if(score <= 0) score = 0;
                }


                if(userAnswerContainer.getChildCount() == answerCheck.size())
                {
                    checkMark.playAnimation();
                    checkMark.setVisibility(View.VISIBLE);
                    checkMark.playAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(refreshCheck>0) {
                                userAnswerContainer.removeAllViews();
                                userOptionContainer.removeAllViews();
                                Log.d("checkmark", "called");
                                insertData();
                                checkMark.setVisibility(View.INVISIBLE);
                                refreshCheck--;
                            }
                            else{
                                Intent intent = new Intent(DemoMakeWordActivity.this, EndScreenActivity.class);
                                intent.putExtra("unityScore", score);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }, checkMark.getDuration());
                }

            }
        };
        return onClickListener;
    }
}
