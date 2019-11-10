package com.example.vsaplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

/**
 * Homefragment contains everything shown in the main screen
 */

public class Homefragment extends Fragment {

    private RecyclerView mCourses;  //recyclerview that contains all the courses
    private CardView signupCard;    //cardview that contains signup button if there is no user available
    private ListView mListView;
    private Button signin;  //signin button
    private Button goTest;  //test button at the bottom might be moved somewhere else
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance(); //rootlevel instance of firestore
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //gets current user if available
    private ImageButton searchButton;

//function to start a new instance of homefragment generally called from other fragments
    public static Homefragment newInstance() {
        Homefragment fragment = new Homefragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home2,container,false);
        mCourses = view.findViewById(R.id.recycler_view);

        signupCard = view.findViewById(R.id.signup_card);

        //if a user is signed in then the signin button and its card will disappear
        if(user!= null)
        {
            signupCard.setVisibility(View.GONE);
        }

        signin = view.findViewById(R.id.sign_in);
        mCourses.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchButton = view.findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(SearchFragment.newInstance());
            }
        });

        signin.setOnClickListener(new View.OnClickListener() { //listener for sigin button takes the user to SignupFragment for signup
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(SignupFragment.newInstance());
            }
        });

//        goTest.setOnClickListener(new View.OnClickListener() { //listener for goTest button takes the user to TestFragment for test
//            @Override
//            public void onClick(View view) {
//                //((MainActivity)getActivity()).loadFragment(TestFragment.newInstance());
//                ((MainActivity)getActivity()).loadFragment(LicenseFragment.newInstance());
//
//            }
//        });


        setRecyclerView(); //calling the function below to do query stuff
        return view;

    }


    private void setRecyclerView(){
        Query query = rootRef.collection("Demo"); //query the main root for courses
        FirestoreRecyclerOptions<CourseModel> options = new FirestoreRecyclerOptions.Builder<CourseModel>()  //recycler options taking Coursemodel class and query which is later set on the adapter
                .setQuery(query, CourseModel.class)
                .build();

        FirestoreRecyclerAdapter<CourseModel, VideosViewHolder> adapter = new FirestoreRecyclerAdapter<CourseModel, VideosViewHolder>(options) { //adapter for the query
            @Override
            //takes course name and course type from coursemodel class and gives it to videosviewholder which will set the name in the recyclerview
            protected void onBindViewHolder(@NonNull VideosViewHolder videosViewHolder, int position, @NonNull CourseModel courseModel) {
                videosViewHolder.setVideoName_ID(courseModel.getCourse_name(), courseModel.getCourse_type());

                Log.d("Start", "VideoName data: " +  courseModel.getCourse_name() );
            }

            @NonNull
            @Override
            //inflates custom view listview_custom
            public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_custom, parent, false);
                return new VideosViewHolder(view);

            }
        };

        mCourses.setAdapter(adapter); //setting the adapter on the recycler view
        adapter.startListening();   //adapter will start listening for changes
    }

    public class VideosViewHolder extends RecyclerView.ViewHolder {

        View mView;

        VideosViewHolder(View itemView) {  //taking the current recyclerview
            super(itemView);
            mView = itemView;
        }

        void setVideoName_ID(final String courseName, final String courseType) {
            TextView nameView = mView.findViewById(R.id.tv_name); //name of the course
            RelativeLayout courseList = mView.findViewById(R.id.course_result); //relativelayout from the custom listview
            nameView.setText(courseName); //sets the coursename into the recyclerview


            courseList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), courseName, Toast.LENGTH_SHORT).show();
                    Log.d("Start", "coursename to be sent: " + courseName);

                    CourseContentsFragment courseContentsFragment = new CourseContentsFragment(); //new instance of coursecontentsfragment
                    FragmentTransaction transaction = getFragmentManager().beginTransaction(); //start a transaction
                    Bundle args = new Bundle(); //creating a bundle to send info to coursecontentsfragment
                    args.putString("CourseName", courseName);
                    args.putString("CourseType", courseType);
                    courseContentsFragment.setArguments(args); //sets the argument
                    Log.d("Start", "args " + args);
                    transaction.replace(R.id.bottom_navigation_frame, courseContentsFragment); //replacing current view for coursecontents fragment
                    transaction.commit(); //commit the replacement
                }
            });
        }
    }

}
