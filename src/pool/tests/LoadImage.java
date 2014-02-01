
package pool.tests;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class LoadImage {
	public static void main (String[] args) {
		CVLoader.load();
		Mat img = Highgui.imread("data/topdown-1.jpg", Highgui.CV_LOAD_IMAGE_COLOR);
		ImgWindow window = ImgWindow.newWindow(img);
	}
}
