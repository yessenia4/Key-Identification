package com.rodriguez.foundmatch.Keys;

import android.graphics.Bitmap;
import android.os.Parcel;

import java.io.Serializable;

public class KeyInformation {
    private int keyID;
    private String keyName;
    private String keyDescription;
    private Bitmap keyImage;
    private Double[] features;

    public KeyInformation(){

    }

    public KeyInformation(int keyID, String keyName, String keyDescription, Bitmap keyImage, Double[] features){
        this.keyID = keyID;
        this.keyName = keyName;
        this.keyDescription = keyDescription;
        this.keyImage = keyImage;
        this.features = features;
    }

    public int getKeyID(){ return keyID; }

    public void setKeyID(int keyID) { this.keyID = keyID; }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyDescription() {
        return keyDescription;
    }

    public void setKeyDescription(String keyDescription) { this.keyDescription = keyDescription; }

    public Bitmap getKeyImage() {
        return keyImage;
    }

    public void setKeyImage(Bitmap keyImage) {
        this.keyImage = keyImage;
    }

    public Double[] getFeatures() { return features; }

    public void setFeatures(Double[] features) { this.features = features; }

}