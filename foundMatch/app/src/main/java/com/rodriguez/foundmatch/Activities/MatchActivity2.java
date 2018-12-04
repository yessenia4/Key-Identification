package com.rodriguez.foundmatch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rodriguez.foundmatch.Keys.KeyHelper;
import com.rodriguez.foundmatch.Keys.KeyInformation;
import com.rodriguez.foundmatch.Keys.MatchesHelper;
import com.rodriguez.foundmatch.R;

import java.util.ArrayList;

public class MatchActivity2 extends AppCompatActivity implements View.OnClickListener{
    KeyInformation keyObj;
    KeyInformation keyObjM1;
    KeyInformation keyObjM2;
    KeyInformation keyObjM3;
    TextView nameView;
    TextView descView;
    ImageView imgView;
    TextView nameView2;
    TextView descView2;
    ImageView imgView2;
    TextView nameView3;
    TextView descView3;
    ImageView imgView3;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match2);

        //get info for key being searched in case user wants to add key
        keyObj = KeyHelper.getInstanceS().getKey();

        //get ArrayList of Matches
        ArrayList<KeyInformation> matches = MatchesHelper.getMatchInstance().getArray();

        //get Views
        nameView = (TextView) findViewById(R.id.match_name1);
        descView = (TextView) findViewById(R.id.match_description1);
        imgView = (ImageView) findViewById(R.id.match_image1);
        nameView2 = (TextView) findViewById(R.id.match_name2);
        descView2 = (TextView) findViewById(R.id.match_description2);
        imgView2 = (ImageView) findViewById(R.id.match_image2);
        nameView3 = (TextView) findViewById(R.id.match_name3);
        descView3 = (TextView) findViewById(R.id.match_description3);
        imgView3 = (ImageView) findViewById(R.id.match_image3);

        if(matches.size() == 1){
            keyObjM1 = matches.get(0);

            nameView.setText(keyObjM1.getKeyName());
            descView.setText(keyObjM1.getKeyDescription());
            imgView.setImageBitmap(keyObjM1.getKeyImage());
        }
        else if(matches.size()== 2){
            keyObjM1 = matches.get(0);
            keyObjM2 = matches.get(1);

            nameView.setText(keyObjM1.getKeyName());
            descView.setText(keyObjM1.getKeyDescription());
            imgView.setImageBitmap(keyObjM1.getKeyImage());

            nameView2.setText(keyObjM2.getKeyName());
            descView2.setText(keyObjM2.getKeyDescription());
            imgView2.setImageBitmap(keyObjM2.getKeyImage());
        }
        else {
            keyObjM1 = matches.get(0);
            keyObjM2 = matches.get(1);
            keyObjM3 = matches.get(2);

            nameView.setText(keyObjM1.getKeyName());
            descView.setText(keyObjM1.getKeyDescription());
            imgView.setImageBitmap(keyObjM1.getKeyImage());

            nameView2.setText(keyObjM2.getKeyName());
            descView2.setText(keyObjM2.getKeyDescription());
            imgView2.setImageBitmap(keyObjM2.getKeyImage());

            nameView3.setText(keyObjM3.getKeyName());
            descView3.setText(keyObjM3.getKeyDescription());
            imgView3.setImageBitmap(keyObjM3.getKeyImage());
        }

        addButton = (Button) findViewById(R.id.add_button_close);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        //begin again
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        Intent addIntent = new Intent(this, AddActivity.class);
        startActivity(addIntent);
    }
}
