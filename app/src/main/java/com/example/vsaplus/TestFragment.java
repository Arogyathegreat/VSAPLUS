package com.example.vsaplus;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TestFragment extends Fragment {
    private TextView word;
    private TextView explanation;
    private Button obutton;
    private Button xbutton;
    private int score = 0;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> wordArrayList = new ArrayList<String>();
    private ArrayList<String> answerArrayList = new ArrayList<String>();
    private ArrayList<String> explanationArrayList = new ArrayList<String>();
    private int index = 0;
    private int count = 0;

    public static TestFragment newInstance() {
        return new TestFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_test, container, false);

        word = (TextView) v.findViewById(R.id.word);
        explanation = (TextView) v.findViewById(R.id.explanation);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        obutton = (Button) v.findViewById(R.id.o);
        xbutton = (Button) v.findViewById(R.id.x);

        View.OnClickListener listener =  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.o:
                        if(answerArrayList.get(index).equals("o")) {
                            score++;
                        }
                        break;
                    case R.id.x:
                        if(answerArrayList.get(index).equals("x")) {
                            score++;
                        }
                        break;
                }
                index++;
                if(wordArrayList.size() > index) {
                    word.setText(wordArrayList.get(index));
                    explanation.setText(explanationArrayList.get(index));
                }
                else {
                    Toast.makeText(getContext(),"점수 : " + score,Toast.LENGTH_LONG).show();
                }
            }
        };

        obutton.setOnClickListener(listener);
        xbutton.setOnClickListener(listener);

        db.collection("Test")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // 데이터를 가져오는 작업이 잘 동작했을 떄
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               wordArrayList.add(count,document.getId());
                               explanationArrayList.add(count,document.get("explanation").toString());
                               answerArrayList.add(count,document.get("answer").toString());
                               if(count == 0) {
                                   word.setText(wordArrayList.get(index));
                                   explanation.setText(explanationArrayList.get(index));
                               }
                               count++;
                               Log.d("TestFragment", document.getId() + " => " + document.getData());
                            }
                        }
                        // 데이터를 가져오는 작업이 에러났을 때
                        else {
                            Log.w("TestFragment", "Error => ", task.getException());
                        }
                    }
                });
        return v;
    }
}