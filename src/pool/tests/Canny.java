
package pool.tests;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class Canny {
	public static void main (String[] args) {
		CVLoader.load();

		VideoCapture video = new VideoCapture(0);
		ImgWindow wnd = ImgWindow.newWindow();

		Mat img = new Mat();
		while (video.isOpened()) {
			video.read(img);
			loop(img, wnd);
		}
	}

	public static void loop (Mat img, ImgWindow wnd) {
		// generate gray scale and blur
		Mat gray = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
//		Imgproc.blur(gray, gray, new Size(3, 3));

		// detect the edges
		Mat edges = new Mat();
		int lowThreshold = 60;
		int ratio = 3;
		Imgproc.Canny(img, edges, lowThreshold, lowThreshold * ratio);
		wnd.setImage(edges);
	}
}
