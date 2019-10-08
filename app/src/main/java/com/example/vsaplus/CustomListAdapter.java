package com.example.vsaplus;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;




/**
 * //depreceated class file used to practice custom list might need to be removed
 */


public class CustomListAdapter extends BaseAdapter {

    private ArrayList<CustomListItem>mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public CustomListItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_custom,parent,false);
        }

        ImageView iv_img = (ImageView)convertView.findViewById(R.id.iv_img);
        TextView tv_name = (TextView)convertView.findViewById(R.id.tv_name);
        //TextView tv_contents = (TextView)convertView.findViewById(R.id.tv_contents);

        CustomListItem customListItem = getItem(position);

        iv_img.setImageDrawable(customListItem.getIcon());
        tv_name.setText(customListItem.getCourseName());
        //tv_contents.setText(customListItem.getContents());

        return convertView;
    }

    public void addItem(Drawable img, String courseName){
        CustomListItem customListItem = new CustomListItem();

        customListItem.setIcon(img);
        customListItem.setCourseName(courseName);
        //customListItem.setContents(contents);

        mItems.add(customListItem);
    }
}
