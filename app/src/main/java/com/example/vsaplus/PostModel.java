package com.example.vsaplus;

public class PostModel {
    private String title;
    private String content;
    private int Like;
    private int Reply;
    private String userName;
    private String UserUid;
    private int postnum;

    PostModel(){}

    PostModel(String title,String content,int Like,int Reply,String userName,String UserUid,int postnum){
        this.title = title;
        this.content = content;
        this.Like = Like;
        this.Reply = Reply;
        this.userName = userName;
        this.UserUid = UserUid;
        this.postnum = postnum;
    }
    public String gettitle(){
        return title;
    }
    public String getContent(){
        return content;
    }
    public int getLike(){return Like;}
    public int getReply(){return Reply;}
    public String getUserName(){return userName;}
    public String getUserUid(){return UserUid;}
    public int getPostnum(){return postnum;}
}
