package com.example.deanc.pics2share;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Random;

/**
 * Created by DeanC on 7/19/2016.
 */
public class FragTabOne extends Fragment {

    View mView;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference imagesRef;

    public FragTabOne() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final String pic_Storage_URL = "gs://pics2share-3a170.appspot.com/";

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(pic_Storage_URL);
        imagesRef = storageRef.child("images");

        mView = inflater.inflate(R.layout.frag_tab_one, container, false);

        GridView gridView = (GridView)mView.findViewById(R.id.gridview);
        gridView.setAdapter(new MyAdapter(getActivity()));
        //changeBackgroundColor(mView);

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
