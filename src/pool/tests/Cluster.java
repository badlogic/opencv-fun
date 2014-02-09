
package pool.tests;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class Cluster {
	public static void main (String[] args) {		
		CVLoader.load();
		Mat img = Highgui.imread("data/cluster.png");
		
		ImgWindow wnd = ImgWindow.newWindow();
		wnd.setImage(img);
		
		
	}
}
