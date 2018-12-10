package com.rodriguez.foundmatch.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.rodriguez.foundmatch.DatabaseManagment.MyDBHandler;
import com.rodriguez.foundmatch.Keys.KeyHelper;
import com.rodriguez.foundmatch.Keys.KeyInformation;
import com.rodriguez.foundmatch.Keys.MatchesHelper;
import com.rodriguez.foundmatch.R;
import com.rodriguez.foundmatch.camera_cv.ImageProcessor;
import com.rodriguez.foundmatch.camera_cv.MatHelper;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {
    String distance = "";
    KeyInformation closestKeyMatch;
    KeyInformation closestKeyMatch2;
    KeyInformation closestKeyMatch3;
    TextView message;
    Double[] features;
    Mat image = MatHelper.getInstance().getMat();

    public void setImage(Mat mat){
        //convert
        Bitmap bm = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bm);

        //change image
        ImageView iv = (ImageView) findViewById(R.id.test_image);
        iv.setImageBitmap(bm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        int progress = getIntent().getIntExtra("thresValue",50);

        //get Mat - already processed
        setImage(image);

        //add animation to text
        message = (TextView) findViewById(R.id.text_search);
        //message.setText("Trial 1 - Mat...");
        if(getCurrentFeatures(image, progress))
            message.setText("Searching...");
        else
            message.setText("Features NOT Obtained...");
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.blink);
        message.startAnimation(anim);
    }

    @Override
    protected void onStart(){
        super.onStart();
        getNextView();
    }

    public boolean getCurrentFeatures(Mat original, int progress){
        Mat gray = ImageProcessor.doGray(original);
        Mat binMat = ImageProcessor.doThreshold(gray, progress);

        features = ImageProcessor.doFDDescriptorsComplexDistance(binMat,512);

        if(!features.equals(null)) {
            //Toast.makeText(getApplicationContext(), "Features were obtained...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    public double GetDistance(Double[] featA, Double[] featB){
        double dist = 0.0;
        for(int i=0; i<featA.length; i++)
            dist += (double) (featA[i] - featB[i]) * (double) (featA[i] - featB[i]);

        return Math.sqrt(dist);
    }

    public int GetMatch(){
        MyDBHandler currentDB = MyDBHandler.getDbInstance(getApplicationContext());
        ArrayList<KeyInformation> dbKeys = currentDB.loadHandler();

        if(!dbKeys.isEmpty()) {
            //get three closest keys
            double shortestDistance1 = Float.MAX_VALUE;
            double shortestDistance2 = Float.MAX_VALUE;
            double shortestDistance3 = Float.MAX_VALUE;
            int size = dbKeys.size();

            for(int i = 0; i<size; i++){
                Double[] compFeatures = dbKeys.get(i).getFeatures();
                Double currentDistance = GetDistance(features,compFeatures);

                if(shortestDistance1 > currentDistance && shortestDistance2 > currentDistance && shortestDistance3 > currentDistance){
                    closestKeyMatch3 = closestKeyMatch2;
                    closestKeyMatch2 = closestKeyMatch;
                    closestKeyMatch = dbKeys.get(i);
                    shortestDistance3 = shortestDistance2;
                    shortestDistance2 = shortestDistance1;
                    shortestDistance1 = currentDistance;
                }
                else if (shortestDistance2 > currentDistance && shortestDistance3 > currentDistance) {
                        closestKeyMatch3 = closestKeyMatch2;
                        closestKeyMatch2 = dbKeys.get(i);
                        shortestDistance3 = shortestDistance2;
                        shortestDistance2 = currentDistance;
                }
                else if (shortestDistance3 > currentDistance) {
                        closestKeyMatch3 = dbKeys.get(i);
                        shortestDistance3 = currentDistance;
                }
            }

            return 1;
        }
        else {
            return 0;
        }
    }

    public void getNextView(){
        //store key being identified
        KeyInformation keyMatch = new KeyInformation();
        keyMatch.setFeatures(features);
        KeyHelper.getInstanceS().setKey(keyMatch);

        int result = GetMatch();
        MatchesHelper.getMatchInstance().emptyArray();

        if(result == 1){
            //save closest keyz
            MyDBHandler currentDB = MyDBHandler.getDbInstance(getApplicationContext());
            ArrayList<KeyInformation> dbKeys = currentDB.loadHandler();

            if(dbKeys.size() == 1)
                MatchesHelper.getMatchInstance().addToArray(closestKeyMatch);
            else if(dbKeys.size() == 2) {
                MatchesHelper.getMatchInstance().addToArray(closestKeyMatch);
                MatchesHelper.getMatchInstance().addToArray(closestKeyMatch2);
            }
            else if (dbKeys.size() >= 3) {
                MatchesHelper.getMatchInstance().addToArray(closestKeyMatch);
                MatchesHelper.getMatchInstance().addToArray(closestKeyMatch2);
                MatchesHelper.getMatchInstance().addToArray(closestKeyMatch3);
            }

            //then start new instance
            Intent matchIntent = new Intent(this, MatchActivity2.class);
            startActivity(matchIntent);

        }
        else if(result == 0){
            //Toast.makeText(getApplicationContext(),"Database is empty...", Toast.LENGTH_SHORT).show();

            Intent noMatchIntent = new Intent(this, NoMatchActivity.class);
            startActivity(noMatchIntent);
        }
    }
}
