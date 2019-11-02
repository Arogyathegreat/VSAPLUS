package com.example.vsaplus;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Bookmark fragment that contains user selected bookmarked videos
 */
public class BookmarkFragment extends Fragment {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseRecyclerAdapter<VideoModel, VideosViewHolder> adapter;
    public static BookmarkFragment newInstance() {
        BookmarkFragment fragment = new BookmarkFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    private RecyclerView mResultList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        mResultList = (RecyclerView) view.findViewById(R.id.recyclerView);
        mResultList.setLayoutManager(new LinearLayoutManager(getActivity()));


        if(user != null){
            com.google.firebase.database.Query query = myRef.child(user.getUid()).child("bookmark").orderByValue();
            setRecyclerView(query);
        }
        return view;
    }

    public void setRecyclerView(com.google.firebase.database.Query query) {
        FirebaseRecyclerOptions<VideoModel> options = new FirebaseRecyclerOptions.Builder<VideoModel>()
                .setQuery(query,VideoModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<VideoModel, VideosViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VideosViewHolder videosViewHolder, int position, @NonNull VideoModel videoModel) {
                videosViewHolder.setVideoName_ID(videoModel.getVideoName(), videoModel.getVideoId(),getActivity());

                Log.d("Start", "VideoName data: " +  videoModel.getVideoName() );
            }

            @NonNull
            @Override
            public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarks_customview, parent, false);
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

    void setVideoName_ID(final String videoName, final String videoId, final Activity activity) {
        TextView nameView = mView.findViewById(R.id.tv_name);//fix
        TextView idView = mView.findViewById(R.id.tv_id);//fix
        ImageButton removal = mView.findViewById(R.id.delete_bookmark);
        nameView.setText(videoName);
        idView.setText(videoId);

        removal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String mVideoID = videoId;
                myRef.child(user.getUid()).child("bookmark").child(mVideoID).removeValue();
                ((MainActivity)getActivity()).loadFragment(BookmarkFragment.newInstance());
            }
        });

        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(, videoName, Toast.LENGTH_SHORT).show();
                String mVideoID = videoId;
                String mVideoTitle = videoName;
                Log.d("Start", "id to be sent: " + mVideoID);
                Intent sendVideoId = new Intent(activity, YouTubePlayerActivity.class);
                sendVideoId.putExtra("videoId", mVideoID);
                sendVideoId.putExtra("videoTitle", mVideoTitle);
                activity.startActivity(sendVideoId);
            }
        });
    }
}

}
