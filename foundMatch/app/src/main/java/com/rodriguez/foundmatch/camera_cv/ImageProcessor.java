package com.rodriguez.foundmatch.camera_cv;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import com.rodriguez.foundmatch.Descriptors.Complex;
import com.rodriguez.foundmatch.Descriptors.FFT;

public class ImageProcessor {

    // Basic thresholding
    public static Mat doThreshold(Mat mat, double th) {
        Mat dest = new Mat(mat.rows(), mat.cols(), mat.type());
        Imgproc.threshold(mat, dest, th, 255, Imgproc.THRESH_BINARY);

        return dest;
    }

    public static Mat doGray(Mat mat){
        Mat gray=new Mat(mat.rows(),mat.cols(),mat.type());
        Imgproc.cvtColor(mat,gray,Imgproc.COLOR_BGR2GRAY);

        return gray;
    }

    public static Mat doEdges(Mat mat){
        Mat edgeImg=new Mat(mat.rows(),mat.cols(),mat.type());
        Imgproc.Canny(mat,edgeImg,100,80);
        //Note: 100 and 80 are used as the threshold -> have a slider to vary with images.

        return edgeImg;
    }

    //Got from Dr.Quwieder during Spring 2018 Image Proecessing
    public static Mat doNegative(Mat mat) {
        Mat negate = new Mat(mat.rows(), mat.cols(), mat.type());
        for (int i = 0; i < mat.rows(); i++) {
            for (int j = 0; j < mat.cols(); j++) {
                byte[] data = new byte[1];
                mat.get(i, j, data);
                data[0] = (byte) (255 - (data[0] & 0xff));
                negate.put(i, j, data);
            }
        }
        return negate;
    }
    
    public static MatOfPoint doContour(Mat mat){
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        //Mat contour = new Mat(mat.rows(),mat.cols(),CvType.CV_8SC1);
        MatOfPoint2f currentContour2f;

        Imgproc.findContours(mat,contours,hierarchy,Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

        if(contours.isEmpty())
            Imgproc.findContours(doNegative(mat),contours,hierarchy,Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

        //From the maximum contour, extract the following:
        double maxArea = 0;
        double maxPeri = 0;
        //
        MatOfPoint maxContour = contours.get(0);
        int maxContourIndex = 0;
        MatOfPoint currentContour;
        RotatedRect rect = new RotatedRect();
        for (int i = 0; i < contours.size(); i++) {
            currentContour = contours.get(i);
            // System.out.println("current contour size is:" + currentContour.size());

            // Extract features:
            double area = Imgproc.contourArea(currentContour);
            currentContour2f = new MatOfPoint2f(currentContour.toArray());
            double perimeter = Imgproc.arcLength(currentContour2f, true);
            rect = Imgproc.minAreaRect(currentContour2f);

            if (area > maxArea) {
                maxArea = area;
                maxPeri = perimeter;
                maxContour = currentContour;
                maxContourIndex = i;
            }
        }

        System.out.println("Max Area index: " + maxContourIndex);
        System.out.println("Max Area is: " + maxArea);
        System.out.println("Max Perimeter is: " + maxPeri);
        System.out.println("Max contour size is: " + maxContour.size());

        return (MatOfPoint) maxContour;
    }

    public static Double[] doFDDescriptorsComplexDistance(Mat originalImage, int noOfFeaturesToRetain)
    {
        //get max contour:
        MatOfPoint matOfPoint = doContour(originalImage);

        //convert to array of Points
        Point[] points = matOfPoint.toArray();

        // start FFT
        Complex[] complextData2 = convertPointArray2ComplexArray(points);

        //Normalized length
        Complex[] complextData = normalizedSizeToBackward(complextData2, 512);

        //For radial distance
        Complex[] distanceData = new Complex[complextData.length];

        //Extract Average
        float XCentroid = 0;
        float YCentroid = 0;
        for (Complex complextData1 : complextData) {
            XCentroid += complextData1.re();
            YCentroid += complextData1.im();
        }
        XCentroid /= complextData.length;
        YCentroid /= complextData.length;
        // System.out.println("Original data Centroids " + XCentroid + "  "+ YCentroid);

        // find index of max distance
        int maxIndex =0;
        double maxDistance=0;
        for (int i = 0; i < complextData.length; i++)
        {
            Double distance= Math.sqrt( Math.pow((complextData[i].re() - XCentroid),2.00)+ Math.pow((complextData[i].im() - YCentroid),2.00));
            distanceData  [i] = new Complex(distance,0);
            if(distance > maxDistance)
            {
                maxIndex =i;
                maxDistance=distance;
            }
        }

        //Rotate to the max radius point
        Complex[] distanceRotationNormalized = new Complex[distanceData.length];
        for (int i = maxIndex; i < maxIndex+ distanceData.length; i++)
        {
            distanceRotationNormalized[i- maxIndex] = distanceData[i% distanceData.length];
        }

        //fft
        Complex[] fftData = doFFT(distanceRotationNormalized);

        // Normalize by dividing by the first coeff.
        Double[] features = new Double[noOfFeaturesToRetain];
        for (int i = 0; i < noOfFeaturesToRetain; i++)
        {
            features[i] = fftData[i].abs()/fftData[0].abs();
        }
        return features;
    }

    public static Mat doFDDescriptorsComplexDistanceReconstruction(Mat originalImage, int noOfFeaturesToRetain)
    {

        // get max contour:
        MatOfPoint matOfPoint = doContour(originalImage);

        //convert to array of Points
        Point[] points = matOfPoint.toArray();

        // start FFT
        Complex[] complextData2 = convertPointArray2ComplexArray(points);

        //Normalized length
        Complex[] complextData = normalizedSizeToBackward(complextData2, 512);

        //For radial distance
        Complex[] distanceData = new Complex[complextData.length];

        // For angles
        double[] distanceAngle1  = new double[complextData.length];
        double[] distanceAngle1N = new double[complextData.length];
        double[] distanceAngle2  = new double[complextData.length];
        double[] distanceAngle2N = new double[complextData.length];
        for (int i = 0; i < 10; i++) {
            System.out.println("1st 10 Original data: " + complextData[i].toString());
        }

        //Extract Average
        float XCentroid = 0;
        float YCentroid = 0;
        for (Complex complextData1 : complextData) {
            XCentroid += complextData1.re();
            YCentroid += complextData1.im();
        }
        XCentroid /= complextData.length;
        YCentroid /= complextData.length;
        System.out.println("Original data Centroids " + XCentroid + "  "+ YCentroid);

        // find index of max distance
        int maxIndex =0;
        double maxDistance=0;
        for (int i = 0; i < complextData.length; i++)
        {
            double distance= Math.sqrt( Math.pow((complextData[i].re() - XCentroid),2.00)+
                    Math.pow((complextData[i].im() - YCentroid),2.00));
            distanceData  [i] = new Complex(distance,0);
            distanceAngle1[i] = Math.acos((complextData[i].re() - XCentroid)/distanceData[i].re());
            distanceAngle2[i] = Math.asin((complextData[i].im() - YCentroid)/distanceData[i].re());
            if(distance > maxDistance)
            {
                maxIndex =i;
                maxDistance=distance;
            }
        }

        //Rotate to the max radius point
        Complex[] distanceRotationNormalized = new Complex[distanceData.length];
        for (int i = maxIndex; i < maxIndex+ distanceData.length; i++)
        {
            distanceRotationNormalized[i- maxIndex] = distanceData[i% distanceData.length];
            distanceAngle1N[i- maxIndex] = distanceAngle1[i% distanceData.length];
            distanceAngle2N[i- maxIndex] = distanceAngle2[i% distanceData.length];
        }

        //fft
        Complex[] fftData = doFFT(distanceRotationNormalized);

        //Keep some not all
        Complex[] fftDataRetained = new Complex[fftData.length];
        for (int i = 0; i < fftData.length; i++)
        {
            if(i <noOfFeaturesToRetain)
                fftDataRetained[i] = fftData[i];
            else
                fftDataRetained[i] = new Complex(0,0);
        }

        // Normalize by dividing by the first coeff.
        // Restore Means & rotation
        double[] features = new double[noOfFeaturesToRetain];
        for (int i = 0; i < noOfFeaturesToRetain; i++)
        {
            features[i] = fftData[i].abs()/fftData[0].abs();
        }
        // Done with the features


        // Extra:
        // Check for reconstruction
        // Inverse FFT
        Complex[] complextDataRecovered = doIFFT(fftDataRetained);

        //Restore Means & rotation
        for (int i = 0; i < complextDataRecovered.length; i++)
        {
            double complextDataRecovered_x = (complextDataRecovered[i].scale(Math.cos(distanceAngle1N[i]))).re()+XCentroid;
            double complextDataRecovered_y = (complextDataRecovered[i].scale(Math.sin(distanceAngle2N[i]))).re()+YCentroid;
            complextDataRecovered[i] = new Complex(Math.abs(complextDataRecovered_x),Math.abs(complextDataRecovered_y) );
        }

        Point[] points2 = convertComplexArray2PointArray(complextDataRecovered);
        MatOfPoint recoveredContour = convertPointArray2MatOfPoint(points2);
        Mat FDReconstructedImg      = drawContourMat(recoveredContour, originalImage);
        return FDReconstructedImg;
    }

    //
    public static Complex[] doFFT(Complex[] data) {
        Complex[] fft = FFT.FFT(data);
        return fft;
    }

    //
    public static Complex[] doIFFT(Complex[] fftData) {
        Complex[] data = FFT.ifft(fftData);
        return data;
    }

    //
    public static Complex[] convertPointArray2ComplexArray(Point[] points) {
        Complex[] data = new Complex[points.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Complex(points[i].x, points[i].y);
        }
        return data;
    }

    //
    public static Point[] convertComplexArray2PointArray(Complex[] complex) {
        Point[] points = new Point[complex.length];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point((int) (complex[i].re()+0.5), (int) (complex[i].im()+0.50));
        }
        return points;
    }

    //
    public static MatOfPoint convertPointArray2MatOfPoint(Point[] points) {
        MatOfPoint contour = new MatOfPoint(points);
        return contour;
    }

    public static Mat drawContourMat(MatOfPoint contour, Mat source) {
        Mat contourImg = new Mat(source.rows(), source.cols(), CvType.CV_8UC1, new Scalar(0));

        List<MatOfPoint> maxContourList = new ArrayList<>();
        maxContourList.add(contour);
        Imgproc.drawContours(contourImg, maxContourList, 0, new Scalar(255), 1);  // -1 for all indexes
        return contourImg;
    }

    public static Complex[] normalizedSizeToBackward(Complex[] complextData, int newSize) {
        Complex[] normalizedData = new Complex[newSize];
        float scaleFactor = (float) complextData.length /(newSize - 1);
        for (int i = 0; i <  newSize; i++)
        {
            int old_i = (int) (i * scaleFactor + 0.50);
            if (old_i < 0) {
                old_i = 0;
            }
            if (old_i > (complextData.length - 1)) {
                old_i = complextData.length - 1;
            }
            normalizedData[i] = complextData[old_i];
        }

        normalizedData[0] = complextData[0];
        normalizedData[newSize - 1] = complextData[complextData.length - 1];
        for (int i = 1; i < newSize - 1; i++) {
            if (normalizedData[i] == null) {
                normalizedData[i] = normalizedData[i - 1];
            }
        }
        return normalizedData;
    }
    //End of code from Dr.Quwieder during Spring 2018 Image Proecessing

}
