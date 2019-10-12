package com.example.vsaplus;

import java.util.HashMap;

public class Bookmarked {
   private HashMap<String,Object> videomodel;

   Bookmarked(){}
   Bookmarked(String ID,HashMap<String,Object> videomodel){

       this.videomodel = videomodel;
   }
   public HashMap<String,Object> getVideomodel(){return videomodel;}
   public void setVideomodel(HashMap<String,Object> videomodel){
       this.videomodel = videomodel;
   }

}
