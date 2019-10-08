package com.example.vsaplus;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class DragAndDropActivity extends Activity implements View.OnDragListener, View.OnLongClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop);

        findViewById(R.id.img_1).setOnLongClickListener(this);
        findViewById(R.id.img_2).setOnLongClickListener(this);
        findViewById(R.id.img_3).setOnLongClickListener(this);


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
                    case R.id.img_1:
                        Log.i("Start", "고급소주");
                        ViewGroup draggedImageViewParentLayout = (ViewGroup)draggedImageView.getParent();
                        draggedImageViewParentLayout.removeView(draggedImageView);
                        LinearLayout bottomLinearLayout = (LinearLayout)receivingLayoutView;
                        bottomLinearLayout.addView(draggedImageView);
                        draggedImageView.setVisibility(View.VISIBLE);
                        return true;

                    case R.id.img_2:
                        Log.i("Start", "img2");
                        return false;
                    case R.id.img_3:
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
