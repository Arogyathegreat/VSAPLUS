package com.example.vsaplus;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Fragment to show all the materials available in a selected Course
 */

public class CourseContentsFragment extends Fragment {

    private TextView mCourseName; //CourseName in the title card
    private TextView mCourseType;   //CourseType in the title card
    private String sCourseName; //String for bundle args coming from homefragment
    private String sCourseType; //String for bundle args coming from homefragment
    private RelativeLayout colorLayout; //title card relativelayout

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


        colorChangewithType(sCourseType); //calling for the color change function with the course type string

        mCourseType.setText(sCourseType);
        mCourseName.setText(sCourseName);

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

}
