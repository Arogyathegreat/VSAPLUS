package com.example.vsaplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameModel {

    private String gameAnswer;
    //private List<String> gameAnswerArray = new ArrayList<>();
    //private List<String> gameOptions = new ArrayList<>();
    private String gameType;
    private String photoUrl;
    private HashMap<String, String> Game;

    public GameModel(){}

    public GameModel(String gameType, String photoUrl, String gameAnswer, HashMap<String, String> Game)
    {
        this.gameAnswer = gameAnswer;
        //this.gameAnswerArray = gameAnswerArray;
        //this.gameOptions = gameOptions;
        this.gameType = gameType;
        this.photoUrl = photoUrl;
        this.Game = Game;
    }

    public HashMap<String, String> getGame(){return Game;}

    public String getGameAnswer(){
        return gameAnswer;
    }

//    public List<String> getGameAnswerArray(){
//        return gameAnswerArray;
//    }
//
//    public List<String> getGameOptions(){
//        return gameOptions;
//    }

    public String getGameType(){
        return gameType;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }

}
