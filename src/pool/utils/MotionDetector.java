package pool.utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import pool.tests.ColorSpace;

public class MotionDetector {
	private float thresholdPercentage = 0.001f;
	private Mat lastImage;
	private Mat mask;
	
	/**
	 * @return true if motion was detected compared to the last frame
	 */
	public boolean detect(Mat frame) {
		if(lastImage == null) {
			lastImage = frame.clone();
			return true;
		}
		
		Mat diff = new Mat();
		Core.absdiff(lastImage, frame, diff);
		Imgproc.threshold(diff, diff, 35, 255, Imgproc.THRESH_BINARY);
		
		// extract color channels and merge them to single bitmask
		Mat r = ColorSpace.getChannel(diff, 2);
		Mat g = ColorSpace.getChannel(diff, 1);
		Mat b = ColorSpace.getChannel(diff, 0);

		mask = r.clone();
		Core.add(mask, g, mask);
		Core.add(mask, b, mask);
		
		float changes = Core.countNonZero(mask) / (float)( frame.cols() * frame.rows());
		r.release();
		g.release();
		b.release();
		lastImage.release();
		lastImage = frame.clone();
		return thresholdPercentage < changes;
	}

	public Mat getMask () {
		return mask;
	}
}