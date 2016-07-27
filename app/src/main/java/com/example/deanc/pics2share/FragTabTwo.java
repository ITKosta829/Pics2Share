package com.example.deanc.pics2share;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by DeanC on 7/19/2016.
 */
public class FragTabTwo extends Fragment {

    View mView;
    static Uri fileUri; // file URI to store image/video
    public static Bitmap rotatedBitmap;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_TAKE_IMAGE = 2;
    private final String pic_Storage_URL = "gs://pics2share-3a170.appspot.com/";
    private final String pic_JSON_URL = "https://pics2share-3a170.firebaseio.com/";
    static String filePath = "";
    static String fileName = "";

    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference images;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    public FragTabTwo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.frag_tab_two, container, false);
        changeBackgroundColor(mView);
        ImageView camera = (ImageView) mView.findViewById(R.id.camera);
        ImageView gallery = (ImageView) mView.findViewById(R.id.gallery);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(pic_Storage_URL);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("MyTag", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("MyTag", "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.signOut();
        mAuth.signInAnonymously()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("MyTag", "signInAnonymously:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("MyTag", "signInAnonymously", task.getException());
                        }
                    }
                });

        setFonts();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. We'll store photos in a file. We set the file name here.
                createFilePath();
                FragTabTwo.fileUri = Uri.fromFile(new File(filePath));
                captureImage();

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFileName();
                pickImage();
            }
        });

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onPause() {
        Log.d("MyTag", "Fragment Two Paused");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d("MyTag", "Fragment Two Resumed");
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void changeBackgroundColor(View v) {

        int[] androidColors = getResources().getIntArray(R.array.androidcolors);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        v.setBackgroundColor(randomAndroidColor);

    }

    public void pickImage() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void createFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ext = ".jpg";
        String name = "Pics2Share_";
        fileName = name + timeStamp + ext;
    }

    public void createFilePath() {
        createFileName();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        filePath = path + "/" + fileName;
    }

    private void captureImage() {

        // First check if there is a camera
        boolean deviceHasCamera = getContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);

        // Our phone has a camera. Lets start the native camera
        if (deviceHasCamera) {

            // Create intent to take a picture
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Tell the intent that we need the camera to store the photo in
            // our file defined earlier
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // Start the activity with the intent created above. When this
            // activity finishes, the method onActivityResult(...) is called
            startActivityForResult(intent, RESULT_TAKE_IMAGE);

        } else {
            Log.i("CAMERA_APP", "No camera found");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {

            // We've successfully selected an image from the gallery.

            Uri selectedImage = data.getData();
            uploadFromLocalFile(selectedImage);

        }

        if (requestCode == RESULT_TAKE_IMAGE && resultCode == Activity.RESULT_OK) {

            // We've successfully captured the image.

            try {

                uploadFromLocalFile(fileUri);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setFonts() {
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "ANDYB.TTF");

        TextView tv1 = (TextView) mView.findViewById(R.id.camera_details);
        tv1.setTypeface(tf);
        TextView tv2 = (TextView) mView.findViewById(R.id.gallery_details);
        tv2.setTypeface(tf);

    }

    public void uploadFromLocalFile(Uri file) {

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        images = storageRef.child("images/" + fileName);
        Log.d("MyTag", "File URI Segment: " + fileName);

        UploadTask uploadTask = images.putFile(file, metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("MyTag", "Upload Failed");
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("MyTag", "Upload Success");
                // taskSnapshot.getMetadata(); // contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });

    }

}
