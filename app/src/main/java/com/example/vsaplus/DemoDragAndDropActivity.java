package com.example.vsaplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.common.StringUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DemoDragAndDropActivity extends Activity implements View.OnDragListener, View.OnLongClickListener {
    private LinearLayout topcontainer;
    private LottieAnimationView checkMark;
    private FirebaseFirestore rootref = FirebaseFirestore.getInstance();
    private String imageUrl1;

    ImageView[] optionImg;
    List<String[]> questions = new ArrayList<>();
    List<String> photos = new ArrayList<>();

    HashMap<String[], String> data = new HashMap<>();

    String[] answerText;
    int superIndex;
    int score = 30;

    TextView hintText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop);

        topcontainer = (LinearLayout)findViewById(R.id.top_container);
        hintText = (TextView)findViewById(R.id.hint_text);


        checkMark = findViewById(R.id.green_tick);

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

        data.put(question1, photo1);
        data.put(question2, photo2);
        data.put(question3, photo3);
        data.put(question4, photo4);
        data.put(question5, photo5);

        List keys = new ArrayList(data.keySet());
        Collections.shuffle(keys);
        shuffle(data);


        findViewById(R.id.top_container).setOnDragListener(this);
        findViewById(R.id.bottom_container).setOnDragListener(this);



    }

    public void shuffle(HashMap<String[], String>toShuffle)
    {

        List<String[]> answer = new ArrayList<>();
        List<String> pictures = new ArrayList<>();


        for(Object key: toShuffle.keySet())
        {
            String[] temp = (String[])key;
            answer.add(temp);
            pictures.add(toShuffle.get(key));
        }

        Random random = new Random();
        superIndex = random.nextInt(3);
        Log.d("random", "index is:" + superIndex);

        for(int i = 0; i<3; i++)
        {
            ImageView imgTest = new ImageView(getApplicationContext());
            imgTest.setId(i);

            Picasso.get()
                    .load(pictures.get(i))
                    .fit()
                    .centerInside()
                    .into(imgTest);

            topcontainer.addView(imgTest);
            imgTest.getLayoutParams().height = 300;
            imgTest.getLayoutParams().width = 300;
            imgTest.setOnLongClickListener(DemoDragAndDropActivity.this::onLongClick);
        }

        answerText = answer.get(superIndex);
        String answerTextTemp = TextUtils.join("", answerText);
        hintText.setText(answerTextTemp);

    }

    public int checkAnswer(View draggedImageView)
    {
        Log.d("drag", "draggedImageViewid" + draggedImageView.getId());
        Log.d("drag", "superindex" + superIndex);
        if(draggedImageView.getId() == superIndex)
        {
            return 1;
        }
        else return 0;
    }




    @Override
    public boolean onLongClick(View imageView) {

        ClipData clipdata = ClipData.newPlainText("","");

        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imageView);
        imageView.startDrag(clipdata, shadowBuilder, imageView, 0);
        imageView.setVisibility(View.INVISIBLE);
        return true;
    }

    @Override
    public boolean onDrag(View receivingLayoutView, DragEvent dragEvent) {
        View draggedImageView = (View) dragEvent.getLocalState();
        ViewGroup draggedImageViewParentLayout = (ViewGroup)draggedImageView.getParent();
        LinearLayout bottomLinearLayout = (LinearLayout)receivingLayoutView;



        switch(dragEvent.getAction()){

            case DragEvent.ACTION_DRAG_STARTED:

                Log.i("Start", "drag action started");

                if(dragEvent.getClipDescription()
                    .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
                        Log.i("Start", "Can accept this data");
                        return true;
                }else{
                    Log.i("Start", "Cannot accept this data");
                }
// Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false;


            case DragEvent.ACTION_DRAG_ENTERED:

                Log.i("Start", "drag action entered");
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                Log.i("Start", "drag action location");
                return true;
/*triggered after ACTION_DRAG_ENTERED
                stops after ACTION_DRAG_EXITED*/

            case DragEvent.ACTION_DRAG_EXITED:
                Log.i("Start", "drag action exited");
                return true;
                //drag shadow has left the bounding box

            case DragEvent.ACTION_DROP:
                /* the listener receives this action type when
                  drag shadow released over the target view
            the action only sent here if ACTION_DRAG_STARTED returned true
            return true if successfully handled the drop else false*/

                switch(checkAnswer(draggedImageView)){
                    case 1:
                        Log.i("Start", "고급소주");
                        draggedImageViewParentLayout.removeView(draggedImageView);
                        bottomLinearLayout.addView(draggedImageView);
                        draggedImageView.setVisibility(View.VISIBLE);

                        if(bottomLinearLayout.getChildCount() == 2)
                        {
                            checkMark.playAnimation();
                            checkMark.addAnimatorListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    finish();
                                    startActivity(getIntent());
                                }


                        });
                        }
                        return true;
                    default:
                        Log.i("Start", "default");
                        score-= 5;
                        if(score<= 0) score = 0;
                        return false;
                }

            case DragEvent.ACTION_DRAG_ENDED:
                Log.i("Start", "drag action has ended");
                Log.i("Start", "getResult: "+dragEvent.getResult());


                if(!dragEvent.getResult()){
                    Log.i("Start", "setting visible");
                    draggedImageView.setVisibility(View.VISIBLE);
                }

                return true;

            default:
                Log.i("Start", "Unknown action type received by ondraglistener");
                break;
        }
        return false;


    }
}
