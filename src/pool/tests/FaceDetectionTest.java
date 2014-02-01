package pool.tests;

import java.awt.Color;
import java.awt.Graphics2D;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class FaceDetectionTest {
	public static void main (String[] args) {
		CVLoader.load();
		VideoCapture video = new VideoCapture(0);

		CascadeClassifier classifier = new CascadeClassifier("data/haarcascade_frontalface_alt.xml");
		ImgWindow window = ImgWindow.newWindow();
		if (video.isOpened()) {
			Mat mat = new Mat();
			while (!window.isClosed()) {
				loop(classifier, mat, window, video);
			}
		}
		video.release();
	}

	public static void loop (CascadeClassifier classifier, Mat mat, ImgWindow window, VideoCapture video) {
		video.read(mat);
		if (!mat.empty()) {
			MatOfRect rects = new MatOfRect();
			classifier.detectMultiScale(mat, rects);
			window.setImage(mat);
			Graphics2D g = window.begin();
			g.setColor(Color.RED);
			for(Rect r: rects.toArray()) {
				g.drawRect(r.x, r.y, r.width, r.height);
			}
			window.end();
		}
	}
}
