package net.jetensky.javadays.opencv;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class HelloCV {

    public static void main(String[] args){
        System.load("/usr/share/OpenCV/java/libopencv_java320.so");

        Mat sample = UIUtil.load(HelloCV.class.getResource("/img/edgeDetection.jpg").getFile());

        /*
        Mat edges = edgeDetection(sample);
        Mat dilated = dilate(edges);

        List<MatOfPoint> contours = findContours(dilated);

        Mat backgroundMask = backgroundMaskFromContour(edges, contours);

        drawContours(sample, contours);

        Mat withoutBackground = applyMaskToAllChannels(sample, backgroundMask);

        UIUtil.showWindow(withoutBackground, 900);
        */

        Mat sampleLab = new Mat();
        // sample.setTo(new Scalar(255,255,255));

        Imgproc.cvtColor(sample, sampleLab, Imgproc.COLOR_BGR2HSV);
        Imgcodecs.imwrite("/tmp/a.png", sampleLab);

        List<Mat> channelsHsv = new ArrayList<>();

        Core.split(sampleLab, channelsHsv);
        UIUtil.showWindow(channelsHsv.get(1), 0);
        UIUtil.showWindow(sample, 900);

    }

    private static Mat applyMaskToAllChannels(Mat matWithThreeChannels, Mat backgroundMask) {
        List<Mat> bgrChannels = new ArrayList<>();
        Core.split(matWithThreeChannels, bgrChannels);

        for (Mat bgrChannel : bgrChannels) {
            Core.bitwise_and(bgrChannel, backgroundMask, bgrChannel);
        }
        Mat withoutBackground = new Mat();
        Core.merge(bgrChannels, withoutBackground);
        return withoutBackground;
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
