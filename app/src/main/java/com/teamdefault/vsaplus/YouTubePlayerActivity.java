package com.teamdefault.vsaplus;

import static com.google.android.youtube.player.YouTubePlayer.PlayerStyle.DEFAULT;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vsagames.demo.GameTestActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Main youtubeplayer for the app
 */

public class YouTubePlayerActivity extends YouTubeFailureRecoveryActivity implements
    View.OnClickListener,
    YouTubePlayer.OnFullscreenListener, YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.PlaybackEventListener {

  //not used but might be needed for later changes
  private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
      ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
      : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
  private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

  private long starttime = 0;
  private long endtime  = 0;
  private long timebuf = 0;
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
  private Button goGame;
  private String courseName;
  private String gameType;
  private String photoUrl;
  private String gameAnswer;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.fullscreen_demo);
    playerView = (YouTubePlayerView) findViewById(R.id.player);
    mVideoTitle = (TextView)findViewById(R.id.tv_videoTitle);
    bookmark = (ImageButton)findViewById(R.id.bookmarks) ;
    goGame = (Button)findViewById(R.id.game_man);

    playerView.initialize(YoutubeConfig.YOUTUBE_API_KEY, this); //class YoutubeConfig has the youtubeapi key for initializing
    Bundle bundle = getIntent().getExtras();
    courseName = bundle.getString("courseName");

    bookmark.setOnClickListener(new View.OnClickListener() {   //bookmark button onclicklistener
      @Override
      public void onClick(View view) {
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

    Log.d("courseName", "name is: "+ courseName);

    DocumentReference docRef = rootRef.collection("Demo").document(courseName);

    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
      @Override
      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        DocumentSnapshot document = task.getResult();

        gameType = document.getString("gameType");

        if(gameType.equals("tower")) {
          gameAnswer = document.getString("gameAnswer");
          photoUrl = document.getString("photoUrl");
          Log.d("gameTest", "URL: " + photoUrl);
        }

        goGame.setText(gameType);

        goGame.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent toUnity = new Intent(YouTubePlayerActivity.this, GameTestActivity.class);
            Intent toMakeWord = new Intent(YouTubePlayerActivity.this, DemoMakeWordActivity.class);
            if(gameType.equals("makeword")){
              YouTubePlayerActivity.this.startActivity(toMakeWord);
            }
            else {
              toUnity.setAction(Intent.ACTION_SEND);
              toUnity.putExtra("gameType", "tower");
              toUnity.putExtra("gameAnswer", gameAnswer);
              Log.d("gameTest", "URL: " + photoUrl);
              YouTubePlayerActivity.this.startActivity(toUnity);
            }
          }
        });
      }        });






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
    player.setPlaybackEventListener(this);
    player.setPlayerStateChangeListener(this);
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

  @Override
  public void onLoading (){
  }
  @Override
  public void onLoaded(String videoId){
    player.play();
    starttime = System.currentTimeMillis();
  }
  public void onAdStarted (){}
  public void onError (YouTubePlayer.ErrorReason reason){}
  public void onVideoEnded (){}
  public void onVideoStarted (){

  }
  @Override
  public void onPlaying(){

  }
  @Override
  public void onBuffering (boolean isBuffering){

  }
  @Override
  public void onPaused (){

  }
  @Override
  public void onSeekTo (int newPositionMillis){

  }
  @Override
  public void onStopped (){

  }
  public void onVideoFinished(){
    endtime = System.currentTimeMillis();
    timebuf = endtime - starttime;

    if(timebuf >= player.getDurationMillis()*0.8){
      myRef.child(user.getUid()).child("score").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          long score = dataSnapshot.getValue(Long.class);
          score += 10L;
          myRef.child(user.getUid()).child("score").setValue(score);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
    }

  }


  @Override
  public void onBackPressed() {
    super.onBackPressed();
  onVideoFinished();
}


}
