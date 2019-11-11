package com.teamdefault.vsaplus;

import androidx.annotation.NonNull;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DragAndDropActivity extends Activity implements View.OnDragListener, View.OnLongClickListener {
    private LinearLayout topcontainer;
    private LottieAnimationView checkMark;
    private FirebaseFirestore rootref = FirebaseFirestore.getInstance();
    private String imageUrl1;

    ImageView[] optionImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop);

        topcontainer = (LinearLayout)findViewById(R.id.top_container);


        checkMark = findViewById(R.id.green_tick);

        optionImg = new ImageView[3];
        rootref.collection("WordmakingGame")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ImageView imgTest = new ImageView(getApplicationContext());
                                imgTest.setId(i+1);


                                Log.d("test", document.getId() + " => " + document.getString("photoUrl"));
                                imageUrl1 = document.getString("photoUrl");
                                Log.d("test", "1 "+imageUrl1);

                                Picasso.get()
                                        .load(imageUrl1)
                                        .fit()
                                        .centerInside()
                                        .into(imgTest,new Callback.EmptyCallback() {
                                            @Override
                                            public void onSuccess() {
                                                // Index 0 is the image view.
                                                Log.d("tag", "SUCCESS" + imageUrl1);

                                            }
                                        });

                                topcontainer.addView(imgTest);
                                imgTest.getLayoutParams().height = 350;
                                imgTest.getLayoutParams().width = 350;
                                imgTest.setOnLongClickListener(DragAndDropActivity.this::onLongClick);

                                i++;
                            }
                        } else {
                            Log.d("test", "Error getting documents: ", task.getException());
                        }
                    }
                });
        Log.d("test", "2 "+imageUrl1);


        findViewById(R.id.top_container).setOnDragListener(this);
        findViewById(R.id.bottom_container).setOnDragListener(this);



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

                switch(draggedImageView.getId()){
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
                                    Intent intent = new Intent(DragAndDropActivity.this, MakeWordActivity.class);
                                    startActivity(intent);
                                    finish();
                                }


                        });
                        }
                        return true;

                    case 2:
                        Log.i("Start", "img2");
                        return false;
                    case 3:
                        Log.i("Start", "img3");
                        return false;
                    default:
                        Log.i("Start", "default");
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
