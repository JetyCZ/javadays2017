package net.jetensky.javadays.opencv;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class HelloCV {

    public static void main(String[] args){
        System.load("/usr/share/OpenCV/java/libopencv_java320.so");
        Mat mat = Mat.eye(new Size(300,300), CvType.CV_8UC3);
        mat.setTo(new Scalar(255,0,255));        // UIUtil.showWindow(mat);

        Mat sample = UIUtil.load(HelloCV.class.getResource("/img/edgeDetection.jpg").getFile());


        Mat edges = edgeDetection(sample);
        Mat dilated = dilate(edges);

        List<MatOfPoint> contours = findContours(dilated);

        Mat backgroundMask = backgroundMaskFromContour(edges, contours);

        drawContours(sample, contours);

        // UIUtil.showWindow(sample, 0);
        Mat foregroundMat = new Mat();

        List<Mat> bgrChannels = new ArrayList<>();
        Core.split(sample, bgrChannels);

        Core.bitwise_and(bgrChannels.get(0), backgroundMask, foregroundMat);

        
        UIUtil.showWindow(foregroundMat, 900);


    }

    private static Mat backgroundMaskFromContour(Mat mat, List<MatOfPoint> contours) {
        Mat backgroundMask = new Mat(mat.size(), mat.type());
        backgroundMask.setTo(new Scalar(0));
        drawContours(backgroundMask, contours, new Scalar(255), -1);
        return backgroundMask;
    }

    private static void drawContours(Mat sample, List<MatOfPoint> contours) {
        Scalar color = new Scalar(0, 0, 255);
        int thickness = 5;
        drawContours(sample, contours, color, thickness);
    }

    private static void drawContours(Mat sample, List<MatOfPoint> contours, Scalar color, int thickness) {
        Imgproc.drawContours(sample, contours, 0, color, thickness);
    }

    private static List<MatOfPoint> findContours(Mat dilated) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dilated, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    private static Mat dilate(Mat edges) {
        Mat dilated = new Mat();
        Imgproc.dilate(edges, dilated, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(31,31)));
        return dilated;
    }


    private static Mat edgeDetection(Mat sample) {
        Mat canny = new Mat();
        double mean = Core.mean(sample).val[0];
        double threshold1MeanPercentage = 0.3;
        double threshold2MeanPercentage = 3*threshold1MeanPercentage;
        Imgproc.Canny(sample, canny,
                threshold1MeanPercentage * mean, threshold2MeanPercentage * mean, 3, true);

        return canny;
    }
}
