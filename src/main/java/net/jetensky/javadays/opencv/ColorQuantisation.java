package net.jetensky.javadays.opencv;

import org.opencv.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ColorQuantisation {

    private Mat quantizedImg;
    private List<ClusterInfo> clusterInfos;

    public List<ClusterInfo> clusters(Mat sourceImg, int k) {

        Mat samples32f = new Mat();
        Mat allPixelsInOneRow = sourceImg.reshape(1, sourceImg.cols() * sourceImg.rows());

        allPixelsInOneRow.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat labels = new Mat(sourceImg.width()*sourceImg.height(), 1, CvType.CV_32SC1);
        labels.setTo(new Scalar(0));
        Mat centers = new Mat();
        Core.kmeans(samples32f, k,        labels,                  criteria,            1, Core.KMEANS_PP_CENTERS + Core.KMEANS_USE_INITIAL_LABELS, centers);
        samples32f.release();

        clusterInfos = createClustersBinaryImages(sourceImg, labels, centers, k);
        clusterInfos.sort(Comparator.comparing(o -> new Double(o.centerLab[0])));
        return clusterInfos;
    }

    public List<ClusterInfo> createClustersBinaryImages(Mat sourceImg, Mat pixelClusterIndexes, Mat clusterCenters, int k) {
        clusterCenters.convertTo(clusterCenters, CvType.CV_8UC1, 255.0);
        clusterCenters.reshape(k);

        quantizedImg = new Mat(sourceImg.size(), sourceImg.type());

        List<ClusterInfo> clusterInfos = new ArrayList<ClusterInfo>();

        byte[] clusterCentersBuff = copyMatToByteArray(clusterCenters);
        int clusterCentersSize = clusterCenters.rows();
        for (int i = 0; i < clusterCentersSize; i++) {
            Mat clusterMat = Mat.zeros(sourceImg.size(), CvType.CV_8U);
            ClusterInfo clusterInfo = new ClusterInfo(clusterMat);
            double[] centerL = clusterCenters.get(i, 0);
            double[] centerA = clusterCenters.get(i, 1);
            double[] centerB = clusterCenters.get(i, 2);

            double[] centerLab = {centerL[0], centerA[0], centerB[0]};
            clusterInfo.setCenterLab(centerLab);
            clusterInfos.add(clusterInfo);
        }
        List<ClusterInfo> sortedByLightnessClusterInfos = new ArrayList<>();
        sortedByLightnessClusterInfos.addAll(clusterInfos);

        Collections.sort(sortedByLightnessClusterInfos, (c1, c2) -> Double.compare(c2.centerLab[0], c1.centerLab[0]));
        int lightnessOrder = 0;
        for (ClusterInfo clusterInfo : sortedByLightnessClusterInfos) {
            clusterInfo.setLightnessOrder(lightnessOrder);
            lightnessOrder++;
        }
        quantitizeSingleCellPixels(sourceImg, pixelClusterIndexes, clusterInfos, clusterCentersBuff);
        return clusterInfos;
    }

    private void quantitizeSingleCellPixels(Mat sourceImg, Mat pixelClusterIndexes, List<ClusterInfo> clusterInfos, byte[] clusterCentersBuff) {
        int[] clusterPixelCounts = new int[clusterInfos.size()];
        byte[][] clusterMattBuffers = new byte[clusterInfos.size()][];
        for (int i=0;i<clusterInfos.size();i++) {
            clusterPixelCounts[i]=0;
            byte[] buffForClusterMat = copyMatToByteArray(clusterInfos.get(i).cluster);
            clusterMattBuffers[i] = buffForClusterMat;
        }

        byte quantizedImgBuff[] = copyMatToByteArray(quantizedImg);
        int[] pixelClusterIndexesBuff = copyMatToIntArray(pixelClusterIndexes);

        // buff[y*quantizedImg.height() + x + 2]
        // quantizedImg.get(y,x);

        int pixelIdx = 0;
        int arrayIdx = 0;
        int totalPixels = sourceImg.rows()*sourceImg.cols();
        while (pixelIdx<totalPixels) { // This is performance better version of iterating for (rows) > for (columns)
            int pixelClusterIndex = pixelClusterIndexesBuff[pixelIdx];

            clusterMattBuffers[pixelClusterIndex][pixelIdx] = (byte) 255;
            clusterPixelCounts[pixelClusterIndex]++;

            int centerArrIdx = pixelClusterIndex*3;
            double c1 = clusterCentersBuff[centerArrIdx++];
            double c2 = clusterCentersBuff[centerArrIdx++];
            double c3 = clusterCentersBuff[centerArrIdx];

            // double[] quantizedPixel = {c1, c2, c3};
            // quantizedImg.put(y, x, quantizedPixel);
            quantizedImgBuff[arrayIdx++] = (byte) c1;
            quantizedImgBuff[arrayIdx++] = (byte) c2;
            quantizedImgBuff[arrayIdx++] = (byte) c3;

            pixelIdx++;
        }
        quantizedImg.put(0,0,quantizedImgBuff);
        for (int i=0;i<clusterInfos.size();i++) {
            copyArrayToMat(clusterMattBuffers[i], clusterInfos.get(i).cluster);
        }
    }


    public static class ClusterInfo {
        /**
         * If pixel is assigned to this cluster, its value is white, otherwise its value is black
         */
        Mat cluster;
        private int lightnessOrder;
        private double[] centerLab;

        public ClusterInfo(Mat cluster) {
            this.cluster = cluster;
        }

        public void setLightnessOrder(int lightnessOrder) {
            this.lightnessOrder = lightnessOrder;
        }
        public int getLightnessOrder() {
            return lightnessOrder;
        }

        public Mat getCluster() {
            return cluster;
        }

        public void setCenterLab(double[] center) {
            this.centerLab = center;
        }

        public double[] getCenterLab() {
            return centerLab;
        }

    }

    public static byte[] copyMatToByteArray(Mat mat) {
        int byteSize = (int) (mat.total() * mat.channels());
        byte arrayBuff[] = new byte[byteSize];
        mat.get(0, 0, arrayBuff);
        return arrayBuff;
    }
    public static int[] copyMatToIntArray(Mat mat) {
        int byteSize = (int) (mat.total() * mat.channels());
        int arrayBuff[] = new int[byteSize];
        mat.get(0, 0, arrayBuff);
        return arrayBuff;
    }

    public static void copyArrayToMat(byte[] array, Mat mat) {
        mat.put(0,0,array);
    }

    public Mat getQuantizedImg() {
        return quantizedImg;
    }
}
