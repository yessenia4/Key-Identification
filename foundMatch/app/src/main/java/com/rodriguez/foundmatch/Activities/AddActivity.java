package com.rodriguez.foundmatch.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rodriguez.foundmatch.DatabaseManagment.MyDBHandler;
import com.rodriguez.foundmatch.Keys.KeyHelper;
import com.rodriguez.foundmatch.Keys.KeyInformation;
import com.rodriguez.foundmatch.Keys.MatchesHelper;
import com.rodriguez.foundmatch.R;
import com.rodriguez.foundmatch.camera_cv.MatHelper;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView image;
    Button add;
    EditText name;
    EditText desc;
    Bitmap bm;
    KeyInformation keyObj = KeyHelper.getInstanceS().getKey();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // convert to bitmap:
        Mat mat = MatHelper.getInstance().getMat();
        bm = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bm);

        //set mat to imageview
        image = (ImageView) findViewById(R.id.add_image);
        image.setImageBitmap(bm);

        //control button
        add = (Button) findViewById(R.id.add_info);
        add.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        MyDBHandler currentDB = MyDBHandler.getDbInstance(getApplicationContext());
        name = (EditText) findViewById(R.id.name);
        desc = (EditText) findViewById(R.id.description);

        String nameEntered = name.getText().toString();
        String descEntered = desc.getText().toString();

        if(nameEntered==null || descEntered==null){
            Toast.makeText(getApplicationContext(), "Missing Information...", Toast.LENGTH_SHORT).show();
        }
        else{
            ArrayList<KeyInformation> dbKeys = currentDB.loadHandler();
            int id = dbKeys.size();

            keyObj = KeyHelper.getInstanceS().getKey();
            keyObj.setKeyID(id+1);
            keyObj.setKeyImage(bm);
            keyObj.setKeyName(nameEntered);
            keyObj.setKeyDescription(descEntered);

            currentDB.addHandler(keyObj);
            Toast.makeText(getApplicationContext(), "Key was added...", Toast.LENGTH_SHORT).show();

            //begin again
            //clear match set
            MatchesHelper.getMatchInstance().emptyArray();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
