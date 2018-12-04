package com.rodriguez.foundmatch.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rodriguez.foundmatch.R;
import com.rodriguez.foundmatch.camera_cv.ImageProcessor;
import com.rodriguez.foundmatch.camera_cv.MatHelper;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class ExtractFeatActivity extends AppCompatActivity implements View.OnClickListener {
    Mat image;
    Mat binarize;
    SeekBar sk;
    TextView progressView;

    public void setImage(int value){
        Mat gray = ImageProcessor.doGray(image);
        binarize = ImageProcessor.doThreshold(gray,value);

        //convert
        Bitmap bm = Bitmap.createBitmap(binarize.cols(), binarize.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(binarize, bm);

        //change image
        ImageView iv = (ImageView) findViewById(R.id.captured_image);
        iv.setImageBitmap(bm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract);

        image = MatHelper.getInstance().getMat();
        setImage(50);

        //control seekbar
        progressView = (TextView) findViewById(R.id.progress);
        progressView.setText("Progress: 50");
        sk = (SeekBar) findViewById(R.id.slider);
        sk.setProgress(50);
        sk.setOnSeekBarChangeListener(seekBarChangeListener);

        //control Button
        Button beginBtn = (Button) findViewById(R.id.search_begin);
        beginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        //begin new activity
        Intent searchIntent = new Intent(this, SearchActivity.class);
        searchIntent.putExtra("thresValue", sk.getProgress());
        startActivity(searchIntent);
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            progressView.setText("Progress: " + progress);
            setImage(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };
}
