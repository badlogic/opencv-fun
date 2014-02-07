
package pool.tests;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class CaptureVideo {
	public static void main (String[] args) {
		CVLoader.load();
		
		VideoCapture video = new VideoCapture(0);

		ImgWindow window = ImgWindow.newWindow();
		if (video.isOpened()) {
			Mat mat = new Mat();
			while (!window.closed) {
				loop(mat, window, video);
			}
		}
		video.release();
	}

	public static void loop (Mat mat, ImgWindow window, VideoCapture video) {
		video.read(mat);
		if (!mat.empty()) {
			window.setImage(mat);
		}
	}
}
