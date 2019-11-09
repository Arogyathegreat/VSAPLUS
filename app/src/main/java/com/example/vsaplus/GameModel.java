package com.example.vsaplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameModel {

    private String questionName;
    private String photoUrl;

    public GameModel(){}

    public GameModel(String questionName, String photoUrl)
    {
        this.questionName = questionName;
        this.photoUrl = photoUrl;
    }

    public String getQuestionName()
    {
        return questionName;
    }

    public String getPhotoUrl()
    {
        return photoUrl;
    }

}
