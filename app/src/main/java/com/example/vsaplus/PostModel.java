package com.example.vsaplus;

public class PostModel {
    private String title;
    private String contents;
    private int Like;
    private int Hate;
    private String userUID;

    PostModel(){}

    PostModel(String title,String contents,int Like,int Hate,String userUID){
        this.title = title;
        this.contents = contents;
        this.Like = Like;
        this.Hate = Hate;
        this.userUID = userUID;
    }
    public String gettitle(){
        return title;
    }
    public String getContents(){
        return contents;
    }
    public int getLike(){return Like;}
    public int getHate(){return Hate;}
    public String getUserUID(){return userUID;}
}
