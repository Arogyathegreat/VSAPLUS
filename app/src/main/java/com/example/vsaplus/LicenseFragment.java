package com.example.vsaplus;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class LicenseFragment extends Fragment {


    public LicenseFragment() {
        // Required empty public constructor
    }

    public static LicenseFragment newInstance() {
        return new LicenseFragment();
    }


    TextView license;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_license, container, false);

        license = (TextView)view.findViewById(R.id.license_tv);

        InputStream inputStream = getResources().openRawResource(R.raw.license);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;

        try{
            i = inputStream.read();
            while(i!= -1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        license.setText(byteArrayOutputStream.toString());


        return view;
    }

}
