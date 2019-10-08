package com.example.vsaplus;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;


import org.jetbrains.annotations.NotNull;

/**
 * testing if the videoplayer worked as a fragment, coudnt so depreceated
 */

public class VideoPlayerFragment extends Fragment {


    private YouTubePlayerView youTubePlayerView;
    private FullScreenHelp fullScreenHelp;
    private YouTubePlayerTracker youTubePlayerTracker = new YouTubePlayerTracker();
    private YouTubePlayer youTubePlayer;
    private Float currentTime;
    private MainActivity mainActivity;

    public VideoPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        youTubePlayerView = view.findViewById(R.id.youtube_player_view);

        mainActivity = new MainActivity();

        initYoutubePlayerView();
        return view;
    }

//    @Override
//    public void onBackPressed() {
//        if (youTubePlayerView.isFullScreen())
//            youTubePlayerView.exitFullScreen();
//        else
//            super.getActivity().onBackPressed;
//    }

    private void initYoutubePlayerView() {
        getLifecycle().addObserver(youTubePlayerView);
        fullScreenHelp = new FullScreenHelp(getActivity());

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                String videoId = "S0Q4gqBUs7c";
                youTubePlayer.loadVideo(videoId, 0);

                addFullScreenListenerToPlayer();
                addCustomActionsToPlayer();

            }
        });


    }

    private void addFullScreenListenerToPlayer(){
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullScreenHelp.enterFullScreen();


            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fullScreenHelp.exitFullScreen();
            }
        });
    }

    private void addCustomActionsToPlayer(){
        Drawable rewindTenSeconds = ContextCompat.getDrawable(getActivity(),R.drawable.ic_rewind10);
        Drawable skipTenSeconds = ContextCompat.getDrawable(getActivity(),R.drawable.ic_skip10);
        assert rewindTenSeconds!= null;
        assert skipTenSeconds!= null;





        View.OnClickListener rewind = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Rewinded 10 secs", Toast.LENGTH_SHORT).show();
            }
        };

        youTubePlayerView.getPlayerUiController().setCustomAction1(rewindTenSeconds, rewind);

        View.OnClickListener skip = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Skipped 10 secs", Toast.LENGTH_SHORT).show();
            }
        };

        youTubePlayerView.getPlayerUiController().setCustomAction2(skipTenSeconds, skip);
    }

    private void removeCustomActionsFromPlayer() {
        youTubePlayerView.getPlayerUiController().showCustomAction1(false);
        youTubePlayerView.getPlayerUiController().showCustomAction2(false);
    }


}

