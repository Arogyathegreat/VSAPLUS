package com.example.vsaplus;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamelistFragment extends Fragment {

    Button adventureGame;
    Button visualNovelGame;
    Button turretGame;
    Button wordGame;
    int index;
    Random random = new Random();

    public GamelistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gamelist,container,false);

        adventureGame = view.findViewById(R.id.adventure_game);
        visualNovelGame = view.findViewById(R.id.visualnovel_game);
        turretGame = view.findViewById(R.id.turret_game);
        wordGame = view.findViewById(R.id.draganddrop_game);

        wordGame.setOnClickListener(new View.OnClickListener() { //listener for goTest button takes the user to TestFragment for test
            @Override
            public void onClick(View view) {
                index = random.nextInt(2);
                Log.d("randomint", ""+index);
                Intent intent1 = new Intent(getActivity(), MakeWordActivity.class);
                Intent intent2 = new Intent(getActivity(), DragAndDropActivity.class);
                if(index == 1) getActivity().startActivity(intent1);
                else    getActivity().startActivity(intent2);
            }
        });

        return view;

    }
}
