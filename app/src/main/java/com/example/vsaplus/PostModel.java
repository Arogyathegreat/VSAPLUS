package com.example.vsaplus;

import java.util.HashMap;
import java.util.Hashtable;

public class PostModel {
    private String title;
    private String content;
    private int Like;
    private int Reply;
    private String userName;
    private String UserUid;
    private int postnum;
    private HashMap<String,Boolean> Likepeople;
    PostModel(){}

    PostModel(String title,String content,int Like,int Reply,String userName,String UserUid,int postnum,HashMap<String,Boolean> Likepeople){
        this.title = title;
        this.content = content;
        this.Like = Like;
        this.Reply = Reply;
        this.userName = userName;
        this.UserUid = UserUid;
        this.postnum = postnum;
        this.Likepeople = Likepeople;
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
    public HashMap<String,Boolean> getLikepeople(){return Likepeople;}
    public void setLike(int like){this.Like = like;}
    public void setLikepeople(HashMap<String,Boolean> liker){
        this.Likepeople = liker;
    }
}
