package com.example.vsaplus;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class SearchFragment extends Fragment {

    private EditText mSearchField; //searchbar to take user input
    private RecyclerView mResultList; //recyclerview that returns the result of the search
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance(); //rootlevel instance of firestore
    public String mVideoID; //videoId of search results
    public String mVideoTitle; //videoTitle of search results


    public static SearchFragment newInstance(){
        return new SearchFragment();
    }


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);



        mSearchField = (EditText) view.findViewById(R.id.search_video);

        mResultList = (RecyclerView) view.findViewById(R.id.video_recyclerView);
        mResultList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() { //search is done here
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){ //setting so that there is a search button in the keyboard
                    String searchText = mSearchField.getText().toString(); //takes the current text in the search field
                    firebaseVideoSearch(searchText); //sends it to search in firestore
                    return true;
                }
                return false;
            }
        });

        return view;
    }


    private void firebaseVideoSearch(String searchText) {

        //hide keyboard after search is pressed
        mSearchField.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSearchField.getWindowToken(), 0);

        //checking if the reference is working
        rootRef.collection("VSAPLUS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Start", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d("Start", "Error getting documents: ", task.getException());
                }
            }
        });

        //if the searchtext isnt empty then do the query and search
        if (!searchText.matches("")) {

            //setting query in the preferred location in firestore
            Query query = rootRef.collection("VSAPLUS").document("Courses").collection("Introduction to Hangul").orderBy("VideoName_insensitive", Query.Direction.ASCENDING).startAt(searchText).endAt(searchText + "\uf8ff");

          //options for adapter, takes the query above and sets it to videomodel class
            FirestoreRecyclerOptions<VideoModel> options = new FirestoreRecyclerOptions.Builder<VideoModel>()
                    .setQuery(query, VideoModel.class)
                    .build();

            //takes the above info as the adapter
            FirestoreRecyclerAdapter<VideoModel, VideosViewHolder> adapter = new FirestoreRecyclerAdapter<VideoModel, VideosViewHolder>(options) {
                @Override
                //takes the info for videomodelclass and lets videosviewholder function to set the videoname and id
                protected void onBindViewHolder(@NonNull VideosViewHolder videosViewHolder, int position, @NonNull VideoModel videoModel) {
                    videosViewHolder.setVideoName_ID(videoModel.getVideoName(), videoModel.getVideoId());


                    Log.d("Start", "VideoName data: " + videoModel.getVideoName());
                }

                @NonNull
                @Override
                //takes the custom ui from customlist_search
                public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlist_search, parent, false);
                    return new VideosViewHolder(view);

                }

        } ;
        mResultList.setAdapter(adapter); //sets the adapter to recyclerview
        adapter.startListening();
        }else { //if the search query was empty show the toast below
            Toast.makeText(getContext(), "Please enter search query", Toast.LENGTH_SHORT).show();
        }
    }


        //view holder class
    public class VideosViewHolder extends RecyclerView.ViewHolder {

            View mView; //takes the recyclerview

            VideosViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
            }

            void setVideoName_ID(final String videoName, final String videoId) {
                TextView nameView = mView.findViewById(R.id.tv_name);
                TextView idView = mView.findViewById(R.id.tv_id);
                nameView.setText(videoName); //sets the video name in the recyclerview
                idView.setText(videoId); //this is pretty much useless not removed because of errors

                nameView.setOnClickListener(new View.OnClickListener() { //listener for the if the user clicks a nameview in the recyclerview
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), videoName, Toast.LENGTH_SHORT).show();
                        mVideoID = videoId;
                        mVideoTitle = videoName;
                        Log.d("Start", "id to be sent: " + mVideoID);
                        Intent sendVideoId = new Intent(getActivity(), YouTubePlayerActivity.class);
                        sendVideoId.putExtra("videoId", mVideoID);
                        sendVideoId.putExtra("videoTitle", mVideoTitle);
                        getActivity().startActivity(sendVideoId);  //starts the youtubeplayer with videotitle and id
                    }
                });
            }
        }
}