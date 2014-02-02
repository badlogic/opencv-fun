package pool.tests;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class HoughLines {
	public static void main (String[] args) {
		CVLoader.load();
		
		// load the image
		Mat img = Highgui.imread("data/topdown-6.jpg");
		
		// generate gray scale and blur
		Mat gray = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.blur(gray, gray, new Size(3, 3));
		
		// detect the edges
		Mat edges = new Mat();
		int lowThreshold = 50;
		int ratio = 3;
		Imgproc.Canny(gray, edges, lowThreshold, lowThreshold * ratio);
		
		Mat lines = new Mat();
		Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 50, 50, 10);
		
		for(int i = 0; i < lines.cols(); i++) {
			double[] val = lines.get(0, i);
			Core.line(img, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2);
		}
		
		ImgWindow.newWindow(edges);
		ImgWindow.newWindow(gray);
		ImgWindow.newWindow(img);
	}
}
