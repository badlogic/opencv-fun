package pool.tests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class HoughCircles {
	public static void main (String[] args) {
		CVLoader.load();
		Mat orig = Highgui.imread("data/topdown-6.jpg");
		Mat gray = new Mat();
		orig.copyTo(gray);
		
		// blur
//		Imgproc.medianBlur(gray, gray, 5);
//		Imgproc.GaussianBlur(gray, gray, new Size(3, 3), 100);
		
		// convert to grayscale
		Imgproc.cvtColor(gray, gray, Imgproc.COLOR_BGR2GRAY);
		
		// do hough circles
		Mat circles = new Mat();
		int minRadius = 10;
		int maxRadius = 18;
		Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT, 1, minRadius, 120, 10, minRadius, maxRadius);
		System.out.println(circles);
		
		ImgWindow.newWindow(gray);
		ImgWindow wnd = ImgWindow.newWindow(orig);			
		
//		while(!wnd.isClosed()) {
			wnd.setImage(orig);
			Graphics2D g = wnd.begin();
			g.setColor(Color.MAGENTA);
			g.setStroke(new BasicStroke(3));
			for(int i = 0; i < circles.cols(); i++) {
				double[] circle = circles.get(0, i);
				g.drawOval((int)circle[0] - (int)circle[2], (int)circle[1] - (int)circle[2], (int)circle[2] * 2, (int)circle[2] * 2);
			}		
			wnd.end();
//		}
	}
}
