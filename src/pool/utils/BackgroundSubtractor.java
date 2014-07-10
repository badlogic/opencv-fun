package pool.utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import pool.app.Calibration;
import pool.tests.ColorSpace;

public class BackgroundSubtractor {
	private Calibration calib;

	public BackgroundSubtractor(Calibration calib) {
		this.calib = calib;
	}
	
	public Mat createMask(Mat camera) {				
		// copy as we are going to destruct those images maybe
		Mat camBlur= camera.clone();
		Mat backgroundBlur = calib.getBackgroundImage().clone();

		// remove noise
		Imgproc.blur(backgroundBlur, backgroundBlur, new Size(calib.getBlurSize(), calib.getBlurSize()));
		Imgproc.blur(camBlur, camBlur, new Size(calib.getBlurSize(), calib.getBlurSize()));

		// take abs diff and create binary image in all 3 channels
		Mat diff = new Mat();
		Core.absdiff(backgroundBlur, camBlur, diff);
		Imgproc.threshold(diff, diff, calib.getSubtractionThreshold(), 255, Imgproc.THRESH_BINARY);

		// extract color channels and merge them to single bitmask
		Mat r = ColorSpace.getChannel(diff, 2);
		Mat g = ColorSpace.getChannel(diff, 1);
		Mat b = ColorSpace.getChannel(diff, 0);

		Mat mask = r.clone();
		Core.add(mask, g, mask);
		Core.add(mask, b, mask);
		
		// dilate to remove some black gaps within balls
		Imgproc.dilate(mask, mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(calib.getMorphSize(), calib.getMorphSize())));

		return mask;
	}
}
