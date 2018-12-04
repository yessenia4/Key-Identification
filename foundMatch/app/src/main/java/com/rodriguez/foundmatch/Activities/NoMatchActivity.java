package com.rodriguez.foundmatch.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.rodriguez.foundmatch.Keys.KeyHelper;
import com.rodriguez.foundmatch.Keys.KeyInformation;
import com.rodriguez.foundmatch.R;
import com.rodriguez.foundmatch.camera_cv.MatHelper;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class NoMatchActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image;
    Button add;
    KeyInformation keyObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomatch);

        //get features to pass to add if needed
        keyObj = KeyHelper.getInstanceS().getKey();


        // convert to bitmap:
        Mat mat = MatHelper.getInstance().getMat();
        Bitmap bm = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bm);

        //set mat to imageview
        image = (ImageView) findViewById(R.id.no_keymatch_image);
        image.setImageBitmap(bm);

        //control button
        add = (Button) findViewById(R.id.add_button);
        add.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent addIntent = new Intent(this, AddActivity.class);
        //addIntent.putExtra("key object", keyObj);
        startActivity(addIntent);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        //begin again
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
