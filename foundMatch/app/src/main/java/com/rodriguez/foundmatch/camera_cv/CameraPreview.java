package com.rodriguez.foundmatch.camera_cv;

//Code was obtained from https://www.youtube.com/watch?v=PQeMGRZZRzI//

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rodriguez.foundmatch.Activities.ExtractFeatActivity;
import com.rodriguez.foundmatch.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class CameraPreview extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener {
    ImageButton takeImageButton;
    private static final String TAG = "OCVSample:Activity";
    CameraBridgeViewBase cameraBridgeViewBase;
    Mat mat;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status){
            switch (status){
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    cameraBridgeViewBase.enableView();
                } break;
                default: {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_ocv);
        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.javacam);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        //have button to capture picture
        takeImageButton = (ImageButton) findViewById(R.id.got_image);
        takeImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Mat mRgbaT = mat.t();
        Core.flip(mat.t(), mRgbaT, 1);
        Imgproc.resize(mRgbaT, mRgbaT, mat.size());

        //get mat object
        MatHelper.getInstance().setMat(mRgbaT);

        //begin new activity
        Intent searchIntent = new Intent(this, ExtractFeatActivity.class);
        startActivity(searchIntent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!OpenCVLoader.initDebug())
            Toast.makeText(getApplicationContext(), "There is a problem in opencv", Toast.LENGTH_LONG).show();
        else
            mLoaderCallback.onManagerConnected(mLoaderCallback.SUCCESS);
    }

    @Override
    protected void onPause(){
        super.onPause();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected void onDestroy(){ super.onDestroy(); }

    @Override
    public void onCameraViewStarted(int width, int height){
        mat=new Mat(width,height,CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped(){
        mat.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
            mat = inputFrame.rgba();
            Mat mRgbaT = mat.t();
            Core.flip(mat.t(), mRgbaT, 1);
            Imgproc.resize(mRgbaT, mRgbaT, mat.size());
            return mRgbaT;
    }
}