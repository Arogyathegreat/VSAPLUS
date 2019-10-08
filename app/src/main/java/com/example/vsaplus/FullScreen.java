package com.example.vsaplus;

import android.app.Activity;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * helper class for youtubeplayerview to go fullscreen
 */



public class FullScreen {

    private Activity context;   //takes the current context
    private View[] views;   //takes all of the views in the context

    /**
     * @param context
     * @param views to hide/show
     */

    public FullScreen(Activity context, View ... views) {
        this.context = context;
        this.views = views;
    }

    /**
     * call this method to enter full screen
     */
    public void enterFullScreen() {
        View decorView = context.getWindow().getDecorView();

            hideSystemUi(decorView);    //calls the hide systemUi function below to set fullscreen properly

            for(View view : views) {
                view.setVisibility(View.GONE);  //hides all the other views
                view.invalidate();
        }
    }

    /**
     * call this method to exit full screen
     */
    public void exitFullScreen() {
        View decorView = context.getWindow().getDecorView();

        showSystemUi(decorView);    //brings back things to portrait after or before fullscreen

        for(View view : views) {
            view.setVisibility(View.VISIBLE);   //sets all the other views visible
            view.invalidate();
        }
    }

    private void hideSystemUi(View mDecorView) {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE  //while in fullscreen set everything to fit the system windows
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION    // View would like its window to be laid out as if it has requested SYSTEM_UI_FLAG_HIDE_NAVIGATION, even if it currently hasn't.
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //View would like its window to be laid out as if it has requested SYSTEM_UI_FLAG_FULLSCREEN, even if it currently hasn't.
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION   //set the system navigation to be temporarily hidden
                        | View.SYSTEM_UI_FLAG_FULLSCREEN //sets the view so that it can take the whole screen
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); //view is interactive while hiding status bar or the navigation bar
    }

    private void showSystemUi(View mDecorView) {
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}


