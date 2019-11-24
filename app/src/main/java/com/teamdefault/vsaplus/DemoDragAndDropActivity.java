package com.teamdefault.vsaplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DemoDragAndDropActivity extends Activity implements View.OnDragListener, View.OnLongClickListener {
    private LinearLayout topcontainer;
    private LinearLayout answercontainer;
    private LinearLayout checkMarkContainer;
    private LottieAnimationView checkMark;
    private FirebaseFirestore rootref = FirebaseFirestore.getInstance();
    private String imageUrl1;

    ImageView[] optionImg;
    List<String[]> questions = new ArrayList<>();
    List<String> photos = new ArrayList<>();


    String[] answerText;

    int score = 30;
    int hashmapIndex;
    Integer[] index;
    HashMap <String[], String> answerMapping = new HashMap<>();

    int refreshCheck = 4;
    int displayDataIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop);

        topcontainer = (LinearLayout)findViewById(R.id.top_container);
        answercontainer = (LinearLayout)findViewById(R.id.bottom_container);
        checkMarkContainer = (LinearLayout)findViewById(R.id.check_mark_container);

        checkMark = findViewById(R.id.green_tick);


        findViewById(R.id.top_container).setOnDragListener(this);
        findViewById(R.id.bottom_container).setOnDragListener(this);


        index = new Integer[]{0,1,2,3,4};
        List<Integer> index_shuffle = Arrays.asList(index);
        Collections.shuffle(index_shuffle);
        Log.d("outofbounds", "arrived"+ index_shuffle.toString());
        insertData();

    }


    public void insertData()
    {

        List<String[]> questions = new ArrayList<>();

        String[] question1 = new String[]{"안", "녕", "하", "세", "요"};
        String[] question2 = new String[]{"잘", "먹", "겠", "습", "니", "다"};
        String[] question3 = new String[]{"감", "사", "합", "니", "다"};
        String[] question4 = new String[]{"여", "보", "세", "요"};
        String[] question5 = new String[]{"죄", "송", "합", "니", "다"};

        questions.add(question1);
        questions.add(question2);
        questions.add(question3);
        questions.add(question4);
        questions.add(question5);


        String photo1 = "https://firebasestorage.googleapis.com/v0/b/team-default.appspot.com/o/demo%2Fgreet.jpg?alt=media&token=7b416195-2e37-41e4-afc6-dc47a079d397";
        String photo2 = "https://firebasestorage.googleapis.com/v0/b/team-default.appspot.com/o/demo%2Feat.jpg?alt=media&token=2d38803d-2004-412b-b8bd-c49bad036f2a";
        String photo3 = "https://firebasestorage.googleapis.com/v0/b/team-default.appspot.com/o/demo%2Fthanks.jpg?alt=media&token=bd38bf74-7860-45f9-a373-2dc79c2cec4f";
        String photo4 = "https://firebasestorage.googleapis.com/v0/b/team-default.appspot.com/o/demo%2Fhello.jpg?alt=media&token=ab5f9e4b-5f03-490d-bb05-c444cab434b0";
        String photo5 = "https://firebasestorage.googleapis.com/v0/b/team-default.appspot.com/o/demo%2Fsorry.jpg?alt=media&token=bc481f99-ee6e-46ac-9ce9-eff50b9fb5cf";

        List<String> photos = new ArrayList<>();

        photos.add(photo1);
        photos.add(photo2);
        photos.add(photo3);
        photos.add(photo4);
        photos.add(photo5);

        displayData(questions, photos);

    }

    public void displayData(List<String[]> questions, List<String> photos)
    {
        hashmapIndex = index[displayDataIndex];

        answerText = questions.get(hashmapIndex);
        List<Integer> image_id_shuffle = new ArrayList<>();
        List<String> image_shuffle = new ArrayList<>();
        Random random = new Random();
        image_shuffle.add(photos.get(hashmapIndex));
        image_id_shuffle.add(hashmapIndex);

        HashMap<Integer, String> mapping = new HashMap<>();
        mapping.put(hashmapIndex,photos.get(hashmapIndex));

        while(image_shuffle.size()<3)
        {
            int randomImg = random.nextInt(5);
            if(!image_shuffle.contains(photos.get(randomImg)))
            {
                image_id_shuffle.add(randomImg);
                image_shuffle.add(photos.get(randomImg));
                mapping.put(randomImg, photos.get(randomImg));
            }
        }


        for (int i = 0; i<3; i++)
        {
            ImageView imgDisplay = new ImageView(getApplicationContext());
            imgDisplay.setId((Integer)mapping.keySet().toArray()[i]);

            Picasso.get()
                    .load((String)mapping.values().toArray()[i])
                    .fit()
                    .centerInside()
                    .into(imgDisplay);

            topcontainer.addView(imgDisplay);
            imgDisplay.getLayoutParams().height = 300;
            imgDisplay.getLayoutParams().width= 300;
            imgDisplay.setOnLongClickListener(DemoDragAndDropActivity.this::onLongClick);
        }
        TextView hintText = new TextView(getApplicationContext());
        String answerTextJoin = TextUtils.join("",answerText);
        hintText.setText(answerTextJoin);
        hintText.setId(0);
        hintText.setTextSize(40);
        answercontainer.addView(hintText);
        displayDataIndex++;
    }


    public int checkAnswer(View draggedImageView)
    {

        if(draggedImageView.getId() == hashmapIndex)
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
        Handler handler = new Handler();

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
                        checkMark.getDuration();
                        if(bottomLinearLayout.getChildCount() == 2)
                        {
                            checkMark.setVisibility(View.VISIBLE);
                            checkMark.playAnimation();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(refreshCheck>0) {
                                        topcontainer.removeAllViews();
                                        bottomLinearLayout.removeAllViews();
                                        Log.d("checkmark", "called");
                                        insertData();
                                        checkMark.setVisibility(View.INVISIBLE);
                                        refreshCheck--;
                                    }
                                    else{
                                        Intent intent = new Intent(DemoDragAndDropActivity.this, EndScreenActivity.class);
                                        intent.putExtra("unityScore", score);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }, checkMark.getDuration());
                        }
                        return true;
                    default:
                        Log.i("Start", "default");
                        score-= 3;
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
