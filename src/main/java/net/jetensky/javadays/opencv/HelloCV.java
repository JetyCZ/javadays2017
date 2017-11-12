package net.jetensky.javadays.opencv;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class HelloCV {

    public static void main(String[] args){
        System.load("/usr/share/OpenCV/java/libopencv_java320.so");

        Mat flagDark = UIUtil.load(HelloCV.class.getResource("/img/dark.png").getFile());
        Mat flagLight = UIUtil.load(HelloCV.class.getResource("/img/light.png").getFile());

        Mat flagThresholdDark = threshold(flagDark, 80);
        Mat flagThresholdLight = threshold(flagLight, 230);

        UIUtil.showWindow(flagThresholdDark, 0);
        UIUtil.showWindow(flagThresholdLight, 900);

        // v7: In v8, implement k-means as CV_32F data sample
        // dump data, centers, labels

    }

    private static Mat threshold(Mat flagDark, int valueLowerThreshold) {
        Mat flagThreshold = new Mat();
        Imgproc.cvtColor(flagDark, flagThreshold, Imgproc.COLOR_BGR2HSV);
        Core.inRange(flagThreshold,
                new Scalar(0,0, valueLowerThreshold),
                new Scalar(255,255,255),
                flagThreshold
                );
        return flagThreshold;
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
