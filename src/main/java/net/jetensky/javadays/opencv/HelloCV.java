package net.jetensky.javadays.opencv;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class HelloCV {

    public static void main(String[] args){
        System.load("/usr/share/OpenCV/java/libopencv_java320.so");
        Mat mat = Mat.eye(new Size(300,300), CvType.CV_8UC3);
        mat.setTo(new Scalar(255,0,255));        // UIUtil.showWindow(mat);

        Mat sample = UIUtil.load(HelloCV.class.getResource("/img/edgeDetection.jpg").getFile());

        UIUtil.showWindow(sample);
        UIUtil.showWindow(edgeDetection(sample));

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
