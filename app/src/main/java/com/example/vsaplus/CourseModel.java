package com.example.vsaplus;

/**
 * classmodel for getting coursename and coursetype data from firestore
 */
public class CourseModel {

    private String Course_name;
    private String Course_type;

    public CourseModel(){}

    public CourseModel(String course_name, String course_type){
        this.Course_name = course_name;
        this.Course_type = course_type;
    }

    public String getCourse_name(){return Course_name;}
    public String getCourse_type(){return Course_type;}
}
