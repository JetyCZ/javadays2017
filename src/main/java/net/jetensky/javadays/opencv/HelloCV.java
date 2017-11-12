package net.jetensky.javadays.opencv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

public class HelloCV {

    public static void main(String[] args){
        System.load("/usr/share/OpenCV/java/libopencv_java320.so");
        Mat mat = Mat.eye(new Size(3,3), CvType.CV_8U);
        System.out.println(mat.dump());

        // v1: In v2, save gray 300x300 mat to disk
    }
}
