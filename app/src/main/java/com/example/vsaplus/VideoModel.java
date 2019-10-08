package com.example.vsaplus;

/**
 * model class for calling video info from firestore and firebase
 */
public class VideoModel {

    private String VideoName;
    private String VideoId;

    public VideoModel(){}

    public VideoModel(String videoName, String videoId){
        this.VideoName = videoName;
        this.VideoId = videoId;
    }

    public String getVideoName(){return VideoName;}

    public String getVideoId(){return VideoId;}


}
