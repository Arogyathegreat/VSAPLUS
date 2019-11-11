package com.teamdefault.vsaplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MakeWordActivity extends AppCompatActivity{

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

    int temp;

    private FirebaseFirestore rootref = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_word);

        checkMark = (LottieAnimationView)findViewById(R.id.green_tick);
        userAnswerContainer = (LinearLayout)findViewById(R.id.tv_ans_container);
        userOptionContainer = (LinearLayout)findViewById(R.id.tv_letter_container);
        imageHint = (ImageView)findViewById(R.id.image_hint);

        DocumentReference docRef = rootref.collection("WordmakingGame").document("사과");
        Source source  = Source.CACHE;
       docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    answer = (List<String>) document.get("userInputBuffer");
                    list = (List<String>) document.get("userInputOptions");
                    imageUrl = document.getString("photoUrl");
                    Picasso.get()
                            .load(imageUrl)
                            .fit()
                            .centerInside()
                            .into(imageHint);
                    Collections.shuffle(list);
                    imageSetter(list, answer);

                    Log.d("WordMakeanswer", answer.toString());
                    Log.d("WordMakeoptions", list.toString());
                }

            else

            {
                Log.d("WordMake", "Error getting documents: ", task.getException());
            }
        }
        });

        Log.d("size", "answercount: " + userAnswerContainer.getChildCount());
        Log.d("size", "answersize: " + temp);

        HashMap<List<String>, String> data = new HashMap<List<String>, String>();

    }

    public void imageSetter(List<String>wordList,List<String>answer)
    {
        int j;

        answerFragments = new TextView[wordList.size()];
        for(j = 0; j<wordList.size(); j++)
        {
            TextView answerFragment = new TextView(this);
            answerFragment.setId(j+1);
            answerFragment.setText(wordList.get(j));
            answerFragment.setTextSize(40);
            answerFragment.setPadding(20,10,10,10);
            userOptionContainer.addView(answerFragment);
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
                else buffer.remove(textFromTV);

                temp = answer.size();
                Log.d("size", "answersizef: " + temp);

                if(userAnswerContainer.getChildCount() == temp)
                {
                    checkMark.playAnimation();
                    checkMark.addAnimatorListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            finish();
                        }
                    });
                }

            }

        };

        return onClickListener;
    }
}
