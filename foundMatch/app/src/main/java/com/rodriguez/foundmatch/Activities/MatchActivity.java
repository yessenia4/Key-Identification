package com.rodriguez.foundmatch.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.rodriguez.foundmatch.Keys.KeyHelper;
import com.rodriguez.foundmatch.Keys.KeyInformation;
import com.rodriguez.foundmatch.Keys.KeyProperties;
import com.rodriguez.foundmatch.R;

public class MatchActivity extends AppCompatActivity {
    KeyInformation keyObj;
    TextView nameView;
    TextView descView;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        //get matching key information
        keyObj = KeyHelper.getInstanceS().getKey();

        nameView = (TextView) findViewById(R.id.keymatch_name);
        descView = (TextView) findViewById(R.id.keymatch_desc);
        imgView = (ImageView) findViewById(R.id.keymatch_image);

        nameView.setText(keyObj.getKeyName());
        descView.setText(keyObj.getKeyDescription());
        imgView.setImageBitmap(keyObj.getKeyImage());
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
