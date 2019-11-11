package com.teamdefault.vsaplus;

import static com.google.android.youtube.player.YouTubePlayer.PlayerStyle.DEFAULT;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Main youtubeplayer for the app
 */

public class YouTubePlayerActivity extends YouTubeFailureRecoveryActivity implements
    View.OnClickListener,
    YouTubePlayer.OnFullscreenListener {

  //not used but might be needed for later changes
  private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
      ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
      : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

  private LinearLayout baseLayout; //the whole layout
  private YouTubePlayerView playerView; //the youtubeplayer video player
  private YouTubePlayer player; //calling the YoutubePlayer class for all its methods
  private View otherViews;  //views except the youtubeplayerview
  private TextView mVideoTitle; //textview to show Videotitle
  private ImageButton bookmark; //bookmark button to set current video as a bookmark
  private FullScreen fullScreen = new FullScreen(this); //calling the helper class fullscreen for setting youtubeplayerview into fullscreen
  private boolean fullscreen; //boolean that checks if the youtubeplayerview is in fullscreen mode or not
  FirebaseDatabase database = FirebaseDatabase.getInstance(); //database reference
  DatabaseReference myRef = database.getReference("users"); //reference to the users section in the database
  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //returns current firebase user

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.fullscreen_demo);
    playerView = (YouTubePlayerView) findViewById(R.id.player);
    mVideoTitle = (TextView)findViewById(R.id.tv_videoTitle);
    bookmark = (ImageButton)findViewById(R.id.bookmarks) ;

    playerView.initialize(YoutubeConfig.YOUTUBE_API_KEY, this); //class YoutubeConfig has the youtubeapi key for initializing

    bookmark.setOnClickListener(new View.OnClickListener() {   //bookmark button onclicklistener
      @Override
      public void onClick(View view) {
        Bundle bundle = getIntent().getExtras();
        final String videoId = bundle.getString("videoId");
        final String videoTitle = bundle.getString("videoTitle");
        if(user != null ) {
          myRef.child(user.getUid()).child("bookmark").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
              HashMap<String,Object> bookmarked;
              if(!child.hasNext()){
                bookmarked = new HashMap<>();
                HashMap<String,String> videomodel = new HashMap<>();
                videomodel.put("VideoId",videoId);
                videomodel.put("VideoName",videoTitle);
                bookmarked.put(videoId,videomodel);
                Toast.makeText(getApplicationContext(),"bookmark saved!",Toast.LENGTH_SHORT).show();
              }
              else {
                while (child.hasNext()) {
                  if (child.next().getKey().equals(videoId)) {
                    bookmarked = new HashMap<>();
                    bookmarked.put(videoId,null);
                    myRef.child(user.getUid()).child("bookmark").updateChildren(bookmarked);
                    Toast.makeText(getApplicationContext(),"bookmark removed!",Toast.LENGTH_SHORT).show();
                    return;
                  }
                }
                bookmarked = new HashMap<>();
                HashMap<String,String> videomodel = new HashMap<>();
                videomodel.put("VideoId",videoId);
                videomodel.put("VideoName",videoTitle);
                bookmarked.put(videoId,videomodel);
              }
              myRef.child(user.getUid()).child("bookmark").updateChildren(bookmarked);
              Toast.makeText(getApplicationContext(),"bookmark saved!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
          });
        }else {
          Toast.makeText(getApplicationContext(), "Please login to access bookmarks feature!", Toast.LENGTH_SHORT).show();
        }
      }
    });

    doLayout();
  }

  @Override
  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,  //if the initialization was successful
      boolean wasRestored) {
    this.player = player;
    // Specify that we want to handle fullscreen behavior ourselves.
    player.setPlayerStyle(DEFAULT); //current youtubeplayerview style
    player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT); //flag for handling fullscreen
    player.setOnFullscreenListener(this);

    Bundle bundle = getIntent().getExtras();  //getting videoid and videotitle from bundle

    final String videoId = bundle.getString("videoId");
    final CharSequence videoTitle = bundle.getString("videoTitle");
    Log.d("Start", "video title is: " +  videoTitle);

    mVideoTitle.setText(videoTitle);

    if (!wasRestored) {
      player.cueVideo(""+ videoId);  //cue the video if initialization was successful, app will crash if no id is provided
    }
  }


  @Override
  protected YouTubePlayer.Provider getYouTubePlayerProvider() {
    return playerView;
  }

  @Override
  public void onClick(View v) {
    player.setFullscreen(!fullscreen);
  }


  //function to help with fullscreen
  private void doLayout() {

    if (fullscreen) {
      fullScreen.enterFullScreen();
    } else {
      fullScreen.exitFullScreen();
    }
  }

  @Override
  public void onFullscreen(boolean isFullscreen) {
    fullscreen = isFullscreen;
    doLayout();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    doLayout();
  }

}
