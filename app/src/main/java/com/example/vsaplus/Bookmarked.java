package com.example.vsaplus;

import java.util.HashMap;

public class Bookmarked {
   private HashMap<String,Object> videomodel;
   private String ID;
   Bookmarked(){}
   Bookmarked(String ID,HashMap<String,Object> videomodel){
       this.ID = ID;
       this.videomodel = videomodel;
   }
   public HashMap<String,Object> getVideomodel(){return videomodel;}
   public void setVideomodel(HashMap<String,Object> videomodel){
       this.videomodel = videomodel;
   }
}
