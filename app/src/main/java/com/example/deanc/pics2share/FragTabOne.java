package com.example.deanc.pics2share;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

/**
 * Created by DeanC on 7/19/2016.
 */
public class FragTabOne extends Fragment {

    View mView;

    public FragTabOne() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.frag_tab_one, container, false);
        changeBackgroundColor(mView);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        Log.d("MyTag", "Fragment One Paused");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d("MyTag", "Fragment One Resumed");
        super.onResume();
    }

    private void changeBackgroundColor(View v){

        int[] androidColors = getResources().getIntArray(R.array.androidcolors);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        v.setBackgroundColor(randomAndroidColor);

    }

}
