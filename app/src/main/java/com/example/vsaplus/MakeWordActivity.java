package com.example.vsaplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MakeWordActivity extends AppCompatActivity{

    String actualAnswer;
    String[] userAnswer;
    String[] userOptions;
    String userOptionsTest;
    TextView[] userInput;
    TextView[] optionInput;

    LinearLayout userAnswerContainer;
    LinearLayout userOptionContainer;

    List<CharSequence>buffer;

    private FirebaseFirestore rootref = FirebaseFirestore.getInstance();
    int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_word);

        rootref.collection("WordmakingGame").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<String> list = new ArrayList<>();
                    for(QueryDocumentSnapshot document: task.getResult()){
                        list = (List<String>)document.get("userInputBuffer");
                    }
                    Log.d("WordMake", list.toString());
                }else{
                    Log.d("WordMake", "Error getting documents: ", task.getException());
                }
            }
        });

        actualAnswer = "등원";
        userAnswer = new String[]{"등", "원"};
        userOptions= new String[]{"등","원", "사", "과"};

        List<String> stringshuffle = Arrays.asList(userOptions);


        Collections.shuffle(stringshuffle);
        int optionLength = userOptions.length;

        userAnswerContainer = (LinearLayout)findViewById(R.id.tv_ans_container);
        userOptionContainer = (LinearLayout)findViewById(R.id.tv_letter_container);

        optionInput = new TextView[optionLength];

        for(i = 0; i<optionLength; i++)
        {
            TextView optionTest = new TextView(this);
            optionTest.setId(i+1);

            optionTest.setText(userOptions[i]);
            optionTest.setTextSize(40);
            optionTest.setPadding(20, 10 , 10, 10);

            userOptionContainer.addView(optionTest);
            optionTest.setOnClickListener(handleOnClick(optionTest, i));

        }

    }

    View.OnClickListener handleOnClick(final TextView textView, final int index)
    {
        buffer = new ArrayList<CharSequence>();
        return new View.OnClickListener(){
            public void onClick(View v){
                CharSequence textFromTV = textView.getText();
                TextView answerView = new TextView(getApplicationContext());
                buffer.add(textFromTV);
                Log.d("Test", "shuffled index: "+index);
                int temp = Arrays.asList(userAnswer).indexOf(textFromTV);
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


            }
        };
    }
}
