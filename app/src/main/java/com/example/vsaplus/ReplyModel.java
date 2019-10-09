package com.example.vsaplus;

public class ReplyModel {
    private String userName;
    private String comment;
   private String picture;
    ReplyModel(){}
    ReplyModel(String userName,String comment,String picture){
        this.userName = userName;
        this.comment = comment;
        this.picture = picture;
    }

    public String getComment() {
        return comment;
    }

    public String getUserName() {
        return userName;
    }

    public String getPicture() {
        return picture;
    }
}
