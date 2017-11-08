package net.jetensky.javadays.opencv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class HelloCV {

    public static void main(String[] args){
        System.load("/usr/share/OpenCV/java/libopencv_java320.so");
        Mat mat = Mat.eye(new Size(300,300), CvType.CV_8UC3);
        mat.setTo(new Scalar(255,0,255));        // UIUtil.showWindow(mat);

        UIUtil.showWindow(mat);
        
    }
}
