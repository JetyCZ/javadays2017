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

        // v6: In v7, load dark and light images, and apply threshold (inRange) 80, resp. 230 to V

        Mat hsv = new Mat();
        Imgproc.cvtColor(sample, hsv, Imgproc.COLOR_BGR2HSV);


        List<Mat> channels = new ArrayList<>();
        Core.split(hsv, channels);

        UIUtil.showWindow(sample, 900);
        UIUtil.showWindow(channels.get(1), 0);
        UIUtil.showWindow(channels.get(2), 0);


    }

    private static Mat foregroundMaskFromContour(Mat mat, List<MatOfPoint> contours) {
        Mat mask = new Mat(mat.size(), CvType.CV_8U);
        mask.setTo(new Scalar(0));
        Imgproc.drawContours(mask, contours, 0, new Scalar(255),-1);
        return mask;
    }

    private static void drawContours(Mat sample, List<MatOfPoint> contours) {
        Imgproc.drawContours(sample, contours, 0, new Scalar(0, 0, 255),3);
    }

    private static List<MatOfPoint> findContours(Mat dilated) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dilated, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    private static Mat dilate(Mat edges) {
        Mat dilated = new Mat();
        Imgproc.dilate(edges, dilated, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3,3)));
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
