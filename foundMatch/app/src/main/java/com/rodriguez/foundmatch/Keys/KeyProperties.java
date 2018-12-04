package com.rodriguez.foundmatch.Keys;

import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import com.rodriguez.foundmatch.DatabaseManagment.MyDBHandler;
import com.rodriguez.foundmatch.camera_cv.ImageProcessor;

import org.opencv.core.Mat;

public class KeyProperties {
    Mat originalMat;
    Double[] features;
    KeyInformation closestKeyMatch;
    MyDBHandler myDBHandler;

    //Get original Image of Key...the one being displayed to the user
    public void setOriginalMat(Mat originalMat) { this.originalMat = originalMat; }

    public Mat getOriginalMat(){ return originalMat; }

    //Do the following operations to get its Properties and Features

    //this will be used if the GetMatch returns true
    public KeyInformation getClosestKeyMatch(){
        return closestKeyMatch;
    }

    //public void setFeatures(Mat binaryMatmat, int thresValue, int numFeautres){
    public void setFeatures(Double[] features){
       this.features = features;
    }

    public Double[] getFeatures() {
        return features;
    }

    public Double GetDistance(Double[] featA, Double[] featB){
        double dist = 0;
        for(int i=0; i<featA.length; i++)
            dist += (featA[i]-featB[i])*(featA[i]-featB[i]);

        return Math.sqrt(dist);
    }

    public boolean GetMatch(){
        ArrayList<KeyInformation> dbKeys = myDBHandler.loadHandler();

        if(!dbKeys.isEmpty()) {
            //getting shortest distance
            double shortestDistance = Float.MAX_VALUE;
            for (int i = 0; i < dbKeys.size(); i++) {
                Double[] compFeatures = dbKeys.get(i).getFeatures();
                double currDistance = GetDistance(features, compFeatures);
                if (shortestDistance > currDistance) {
                    closestKeyMatch = dbKeys.get(i);
                    shortestDistance = currDistance;
                }
            }

            //check if it is a match or not
            //have a range -> if yes, return true
            //if not, return false

            return true;
        }
        else {
            return false;
        }
    }

}
