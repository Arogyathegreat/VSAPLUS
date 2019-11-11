package com.teamdefault.vsaplus;

import java.util.HashMap;

public class PostModel {
    private String title;
    private String content;
    private int like;
    private int reply;
    private String userName;
    private String userUid;
    private int postnum;
    private HashMap<String,Boolean> likepeople;
    private HashMap<String,Object> comments;
    PostModel(){}

    PostModel(String title,String content,int like,int reply,String userName,String userUid,int postnum,HashMap<String,Boolean> likepeople,HashMap<String,Object> comments){
        this.title = title;
        this.content = content;
        this.like = like;
        this.reply = reply;
        this.userName = userName;
        this.userUid = userUid;
        this.postnum = postnum;
        this.likepeople = likepeople;
        this.comments = comments;
    }
    public String gettitle(){
        return title;
    }
    public String getContent(){
        return content;
    }
    public int getLike(){return like;}
    public int getReply(){return reply;}
    public String getUserName(){return userName;}
    public String getUserUid(){return userUid;}
    public int getPostnum(){return postnum;}
    public HashMap<String,Boolean> getLikepeople(){return likepeople;}
    public void setLike(int like){this.like = like;}
    public void setLikepeople(HashMap<String,Boolean> liker){
        this.likepeople = liker;
    }
    public HashMap<String, Object> getcomments(){return comments;}
}
