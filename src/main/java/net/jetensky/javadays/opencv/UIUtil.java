package net.jetensky.javadays.opencv;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

/**
 * This is not class to be used in production, but during development phase while debugging image processing
 */
public class UIUtil {

    public static void save(Mat mat) {
        Imgcodecs.imwrite("/tmp/a.png", mat);
    }

    public static void showWindow(Mat mat, int x) {
        BufferedImage bufferedImage = matToBufferedImage(mat);
        showWindow(bufferedImage, x);
    }

    private static BufferedImage matToBufferedImage(Mat frame) {
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);

        return image;
    }

    private static void showWindow(BufferedImage img, int x)  {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.setLocation(x, 0);
        frame.setSize(img.getWidth(), img.getHeight() + 30);
        frame.setTitle("Image " + img.getWidth() + "x" + img.getHeight() + ", type=" + img.getType());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static Mat load(String file) {
        return Imgcodecs.imread(file);
    }
}
