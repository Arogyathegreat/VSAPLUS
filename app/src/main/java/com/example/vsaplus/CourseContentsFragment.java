package com.example.vsaplus;


import android.content.Intent;
import android.os.Bundle;

import com.unity3d.player.UnityPlayerActivity;
import com.vsagames.demo.GameTestActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.events.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to show all the materials available in a selected Course
 */

public class CourseContentsFragment extends Fragment {

    private TextView mCourseName; //CourseName in the title card
    private TextView mCourseType;   //CourseType in the title card
    private String sCourseName; //String for bundle args coming from homefragment
    private String sCourseType; //String for bundle args coming from homefragment
    private RelativeLayout colorLayout; //title card relativelayout
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private RecyclerView lectureList;
    private String gameAnswer;
    private List<String> gameAnswerArray = new ArrayList<>();
    private List<String> gameOptions = new ArrayList<>();
    private String photoUrl;
    private String gameType;
    private Button goGame;

    public static CourseContentsFragment newInstance() {
        CourseContentsFragment fragment = new CourseContentsFragment();
        return fragment;
    }

    public CourseContentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Bundle for course name and course type coming from homefragment
        Bundle args = getArguments();

        Log.d("Start", "argsreciever: " + args);

        if(args!= null) {

            sCourseName  = args.getString("CourseName");
            sCourseType = args.getString("CourseType");
        }

        Log.d("Start", "Course name is: " + sCourseName);

        View view =  inflater.inflate(R.layout.fragment_course_contents, container, false);
        mCourseType = (TextView) view.findViewById(R.id.course_type);
        mCourseName = (TextView) view.findViewById(R.id.course_name);
        colorLayout = (RelativeLayout) view.findViewById(R.id.head_container);
        lectureList = (RecyclerView)view.findViewById(R.id.lecture_container);
        lectureList.setLayoutManager(new LinearLayoutManager(getActivity()));
        goGame = (Button)view.findViewById(R.id.go_game);


        colorChangewithType(sCourseType); //calling for the color change function with the course type string


        Query query = rootRef.collection(sCourseName).orderBy("Video_insensitive", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<VideoModel> options = new FirestoreRecyclerOptions.Builder<VideoModel>()
                .setQuery(query, VideoModel.class)
                .build();

        FirestoreRecyclerAdapter<VideoModel, VideosViewHolder>adapter = new FirestoreRecyclerAdapter<VideoModel, VideosViewHolder>(options) {
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
        lectureList.setAdapter(adapter);
        adapter.startListening();

        mCourseType.setText(sCourseType);
        mCourseName.setText(sCourseName);

        DocumentReference docRef = rootRef.collection("Introduction to Hangul").document("History of Hangul");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                gameOptions = (List<String>)document.get("gameOptions");
                gameAnswer = document.getString("gameAnswer");
                gameAnswerArray = (List<String>)document.get("gameAnswerArray");
                gameType = document.getString("gameType");
                photoUrl = document.getString("photoUrl");
                Log.d("gameTest", "URL: " + photoUrl);


                goGame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toUnity = new Intent(getActivity(), GameTestActivity.class);
                        toUnity.setAction(Intent.ACTION_SEND);
                        toUnity.putExtra("gameType", gameType);
                        toUnity.putExtra("gameAnswer", gameAnswer);
                        toUnity.putExtra("photoUrl", photoUrl);
                        Log.d("gameTest", "URL: " + photoUrl);
                        getActivity().startActivity(toUnity);
                    }
                });
            }
        });


        return view;
    }


    public void colorChangewithType(String courseType){


        //switch depending on the type of the course, currently "Beginner", "Intermediate", "Advanced"
        switch(courseType){

            case "Beginner":
                colorLayout.setBackgroundColor(getResources().getColor(R.color.beginner));
                mCourseType.setTextColor(getResources().getColor(R.color.beginner_tv));
                break;

            case "Intermediate":
                colorLayout.setBackgroundColor(getResources().getColor(R.color.intermediate));
                mCourseType.setTextColor(getResources().getColor(R.color.intermediate_tv));
                break;

            case "Advanced":
                colorLayout.setBackgroundColor(getResources().getColor(R.color.advanced));
                mCourseType.setTextColor(getResources().getColor(R.color.advanced_tv));
                break;
        }

    }

    public class VideosViewHolder extends RecyclerView.ViewHolder{

        View mView;

        VideosViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        void setVideoName_ID(final String videoName, final String videoId){
            TextView nameView = mView.findViewById(R.id.tv_name);
            RelativeLayout layout = mView.findViewById(R.id.video_layout);
            nameView.setText(videoName);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendVideoId = new Intent(getActivity(), YouTubePlayerActivity.class);
                    sendVideoId.putExtra("videoId", videoId);
                    sendVideoId.putExtra("videoTitle", videoName);
                    getActivity().startActivity(sendVideoId);
                }
            });
        }
    }

}
