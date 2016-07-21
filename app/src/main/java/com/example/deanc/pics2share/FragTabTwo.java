package com.example.deanc.pics2share;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DeanC on 7/19/2016.
 */
public class FragTabTwo extends Fragment {

    View mView;
    private Uri fileUri; // file URI to store image/video
    public static Bitmap rotatedBitmap;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_TAKE_IMAGE = 2;

    public FragTabTwo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.frag_tab_two, container, false);
        ImageView camera = (ImageView) mView.findViewById(R.id.camera);
        ImageView gallery = (ImageView) mView.findViewById(R.id.gallery);

        // 1. We'll store photos in a file. We set the file name here.
        fileUri = Uri.fromFile(new File(createFileName()));

        setFonts();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);

            }
        });

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
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

    public String createFileName(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ext = ".jpg";
        String name = "/Pics2Share_";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String outputFilePath = path + name + timeStamp + ext;
        return outputFilePath;
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
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

//          ImageView imageView = (ImageView) findViewById(R.id.imgView);
//          imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }

        if (requestCode == RESULT_TAKE_IMAGE && resultCode == Activity.RESULT_OK) {

            // We've successfully captured the image.
            try {

                // Now we need to ensure our photo is not unnecessarily rotated.
                Matrix matrix = new Matrix();
                ExifInterface ei = new ExifInterface(fileUri.getPath());

                // Get orientation of the photograph
                int orientation = ei.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                // In case image is rotated, we rotate it back
                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                }

                // Now we get bitmap from the photograph and apply the rotation matrix above
                BitmapFactory.Options options = new BitmapFactory.Options();

                // Down-sizing image as it can throw OutOfMemory Exception for larger images.
                options.inSampleSize = 2;

                Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                // Now display the image on the UI

//                Intent intent = new Intent(getActivity(), Touch.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);

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
}
