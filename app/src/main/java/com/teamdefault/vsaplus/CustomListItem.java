package com.teamdefault.vsaplus;

import android.graphics.drawable.Drawable;

/**
 * depreceated model class for custom list now uses coursemodel class
 */

public class CustomListItem {

    private Drawable icon;
    private String courseName;
//    private String contents;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

//    public String getContents() {
//        return contents;
//    }
//
//    public void setContents(String contents) {
//        this.contents = contents;
//    }
}
