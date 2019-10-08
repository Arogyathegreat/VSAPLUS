package com.example.vsaplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.PlayerUiController;

import org.jetbrains.annotations.NotNull;

/**
 * depreceated class used for early tests can be deleted
 */
public class VideoPlayerActivity extends AppCompatActivity {

    public YouTubePlayerView youTubePlayerView;
    private FullScreen fullScreen = new FullScreen(this);
    private RecyclerView mResultList;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    public String mVideoID;
    private TextView mVideoTitle;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video_player);
        mResultList = (RecyclerView)findViewById(R.id.video_recyclerView);
        mResultList.setLayoutManager(new LinearLayoutManager(VideoPlayerActivity.this));
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        mVideoTitle = (TextView)findViewById(R.id.tv_videoTitle);

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        initYouTubePlayerView();
        watchNext();
    }

    @Override
    public void onBackPressed() {
        if(youTubePlayerView.isFullScreen())
            youTubePlayerView.exitFullScreen();
        else
            super.onBackPressed();
    }

    private void watchNext(){

        Query query = rootRef.collection("VSAPLUS")
                .document("Courses")
                .collection("Introduction to Hangul")
                .orderBy("VideoName_insensitive", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<VideoModel> options = new FirestoreRecyclerOptions.Builder<VideoModel>()
                .setQuery(query, VideoModel.class)
                .build();

        FirestoreRecyclerAdapter<VideoModel, VideosViewHolder> adapter = new FirestoreRecyclerAdapter<VideoModel, VideosViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VideosViewHolder videosViewHolder, int i, @NonNull VideoModel videoModel) {
                videosViewHolder.setVideoName_ID(videoModel.getVideoName(), videoModel.getVideoId());
            }

            @NonNull
            @Override
            public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlist_search, parent, false);
                return new VideosViewHolder(view);
            }
        };
        mResultList.setAdapter(adapter);
        adapter.startListening();

    }

    public class VideosViewHolder extends RecyclerView.ViewHolder {

        View mView;

        VideosViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setVideoName_ID(final String videoName, final String videoId) {
            TextView nameView = mView.findViewById(R.id.tv_name);
            TextView idView = mView.findViewById(R.id.tv_id);
            nameView.setText(videoName);
            idView.setText(videoId);

            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), videoName, Toast.LENGTH_SHORT).show();
                    Log.d("Start", "id to be sent: " + videoId);
                    Log.d("Start", "title to be sent: " + videoName);
                    Intent sendVideoId = new Intent(getApplicationContext(), VideoPlayerActivity.class);
                    sendVideoId.putExtra("videoId", videoId);
                    sendVideoId.putExtra("videoTitle", videoName);
                    startActivity(sendVideoId);
                }
            });
        }
    }


    private void initYouTubePlayerView(){
        getLifecycle().addObserver(youTubePlayerView);

        Bundle bundle = getIntent().getExtras();

        final String videoId = bundle.getString("videoId");
        final CharSequence videoTitle = bundle.getString("videoTitle");
        Log.d("Start", "video title is: " +  videoTitle);

        mVideoTitle.setText(videoTitle);


        Log.d("Start", "id recieved! " + videoId);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo("ImYu9dJM4kQ", 0);

               //addCustomActionsToPlayer();
                addFullScreenListenerToPlayer();
            }
        });

    }

    private void addFullScreenListenerToPlayer(){
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullScreen.enterFullScreen();
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fullScreen.exitFullScreen();
            }
        });
    }

    private void addCustomActionsToPlayer(){
        Drawable plusTenSecondsIcon = ContextCompat.getDrawable(VideoPlayerActivity.this, R.drawable.ic_skip10);
        Drawable minusTenSecondsIcon = ContextCompat.getDrawable(VideoPlayerActivity.this, R.drawable.ic_rewind10);
        assert plusTenSecondsIcon != null;
        assert minusTenSecondsIcon != null;

        View.OnClickListener rewind = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VideoPlayerActivity.this,"REWINDED", Toast.LENGTH_SHORT).show();
            }
        };
        youTubePlayerView.getPlayerUiController().setCustomAction1(minusTenSecondsIcon, rewind);


        View.OnClickListener skip = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VideoPlayerActivity.this,"SKIPPED", Toast.LENGTH_SHORT).show();
            }
        };

        youTubePlayerView.getPlayerUiController().setCustomAction2(plusTenSecondsIcon, skip);
    }
}
