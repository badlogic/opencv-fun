package pool.utils;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class OpenCVCamera implements Camera {
	private final VideoCapture capture;
	
	public OpenCVCamera() {
		CVLoader.load();
		capture = new VideoCapture(0);
		while(!capture.isOpened());
	}
	
	@Override
	public Mat nextFrame () {
		Mat image = new Mat();
		capture.read(image);
		return image.clone();
	}

	@Override
	public void release () {
		capture.release();
	}
}
