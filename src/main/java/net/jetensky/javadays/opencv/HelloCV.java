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

        /*Mat flagThresholdDark = threshold(flagDark, 80);
        Mat flagThresholdLight = threshold(flagLight, 230);*/

        Mat flagThresholdDark = kmeans(flagDark);
        // Mat flagThresholdLight = kmeans(flagLight);

        /*UIUtil.showWindow(flagThresholdDark, 0);
        UIUtil.showWindow(flagThresholdLight, 900);*/

        // in v9, quantize flag's images with k-means cluster, and
        // instead of applying threshold, substract background as darker cluster.

    }

    private static Mat kmeans(Mat mat) {

        // Mat data, int K, Mat bestLabels, TermCriteria criteria, int attempts, int flags

        TermCriteria termCriteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat labels = new Mat();
        Mat data = new Mat(5,1, CvType.CV_32F);

        data.put(0,0,10);
        data.put(1,0,11);
        data.put(2,0,25);
        data.put(3,0,104);
        data.put(4,0,106);

        Mat centers = new Mat();
        Core.kmeans(data, 3, labels, termCriteria, 1, 0, centers);

        System.out.println("Data: " + data.dump());
        System.out.println("Centers: " + centers.dump());
        System.out.println("Labels:" + labels.dump());
        return null;
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
