package net.jetensky.javadays.opencv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

public class HelloCV {

    public static void main(String[] args){
        System.load("/usr/share/OpenCV/java/libopencv_java320.so");
        Mat mat = Mat.eye(new Size(300,300), CvType.CV_8U);
        mat.setTo(new Scalar(128));
        
        Imgcodecs.imwrite("/tmp/a.png", mat);

        /*
        v2, in v3:
        read edgeDetection.jpg
        create jFrame to display mat
        single channel
        three channels
        Canny edge detection (Canny, mean)
        */
    }
}
