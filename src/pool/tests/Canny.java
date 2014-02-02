
package pool.tests;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class Canny {
	public static void main (String[] args) {
		CVLoader.load();
		
		// load the image
		Mat img = Highgui.imread("data/topdown-10.png");
		
		// generate gray scale and blur
		Mat gray = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.blur(gray, gray, new Size(3, 3));
		
		// detect the edges
		Mat edges = new Mat();
		int lowThreshold = 50;
		int ratio = 3;
		Imgproc.Canny(gray, edges, lowThreshold, lowThreshold * ratio);
		
		ImgWindow.newWindow(edges);
	}
}
