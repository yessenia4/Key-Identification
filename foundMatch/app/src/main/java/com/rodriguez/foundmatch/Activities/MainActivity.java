package com.rodriguez.foundmatch.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rodriguez.foundmatch.DatabaseManagment.MyDBHandler;
import com.rodriguez.foundmatch.R;
import com.rodriguez.foundmatch.camera_cv.CameraPreview;

import org.opencv.android.CameraBridgeViewBase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView instruct = (TextView) findViewById(R.id.welcome);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.blink);
        instruct.startAnimation(anim);

        ImageButton searchBtn = (ImageButton) findViewById(R.id.image_button);
        searchBtn.setOnClickListener(this);

        Button keysButton = (Button) findViewById(R.id.keys_button);
        keysButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_button:
                Intent searchIntent = new Intent(this, CameraPreview.class);
                startActivity(searchIntent);
                break;

            case R.id.keys_button:
                Intent keysViewIntent = new Intent(this, MyKeysActivity.class);
                startActivity(keysViewIntent);
                break;
        }
    }
}