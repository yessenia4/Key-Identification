package com.rodriguez.foundmatch.camera_cv;

import org.opencv.core.Mat;

public class MatHelper {
    private Mat mat = null;
    private static final MatHelper instance = new MatHelper();

    public MatHelper() { }

    public static MatHelper getInstance() { return instance; }

    public Mat getMat() { return mat; }

    public void setMat(Mat mat) { this.mat = mat; }
}
