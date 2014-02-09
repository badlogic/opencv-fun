package pool.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import pool.tests.ColorSpace;

public class BallDetector {
	private int threshold = 20;
	private int blurSize = 7;
	private int morphSize = 5;
	private int ballRadius = 35;

	private Mat background;
	private Mat mask;
	
	private final List<Circle> balls = new ArrayList<Circle>();
	private final List<BallCluster> ballClusters = new ArrayList<BallCluster>();
	private final List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	
	public BallDetector(Mat background) {
		this.background = background;
	}
	
	public void detect(Mat camera) {
		mask = createMask(camera);		
		extractCircles(mask, balls, ballClusters, contours);
	}
	
	private Mat createMask(Mat camera) {				
		// copy as we are going to destruct those images maybe
		Mat camBlur= camera.clone();
		Mat backgroundBlur = background.clone();

		// remove noise
		Imgproc.blur(backgroundBlur, backgroundBlur, new Size(blurSize, blurSize));
		Imgproc.blur(camBlur, camBlur, new Size(blurSize, blurSize));

		// take abs diff and create binary image in all 3 channels
		Mat diff = new Mat();
		Core.absdiff(backgroundBlur, camBlur, diff);
		Imgproc.threshold(diff, diff, threshold, 255, Imgproc.THRESH_BINARY);

		// extract color channels and merge them to single bitmask
		Mat r = ColorSpace.getChannel(diff, 2);
		Mat g = ColorSpace.getChannel(diff, 1);
		Mat b = ColorSpace.getChannel(diff, 0);

		Mat mask = r.clone();
		Core.add(mask, g, mask);
		Core.add(mask, b, mask);
		
		// dilate to remove some black gaps within balls
		Imgproc.dilate(mask, mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(morphSize, morphSize)));

		return mask;
	}
	
	private void extractCircles (Mat mask, List<Circle> balls, List<BallCluster> ballClusters, List<MatOfPoint> contours) {
		// clear input
		balls.clear();
		ballClusters.clear();
		contours.clear();
		
		// find the contours
		Imgproc.findContours(mask.clone(), contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		// iterate through the contours, find single balls and clusters of balls touching each other
		double minArea = Math.PI * (ballRadius * 0.9f) * (ballRadius * 0.9f); // minimal ball area
		double maxArea = Math.PI * (ballRadius * 1.1f) * (ballRadius * 1.1f); // maximal ball area

		for (int i = 0; i < contours.size(); i++) {
			double area = Imgproc.contourArea(contours.get(i));
			if (area > minArea) {				
				if (area < maxArea) {
					// we found a ball
					float[] radius = new float[1];
					Point center = new Point();
					Imgproc.minEnclosingCircle(new MatOfPoint2f(contours.get(i).toArray()), center, radius);
					balls.add(new Circle(center.x, center.y, ballRadius));
				} else {
					// we found a cluster of balls
					int numBalls = (int)(area / (Math.PI * ballRadius * ballRadius));
					ballClusters.add(new BallCluster(contours.get(i), numBalls));
				}
			}
		}
	}

	public Mat getBackground () {
		return background;
	}

	public void setBackground (Mat background) {
		this.background = background;
	}

	public Mat getMask () {
		return mask;
	}

	public void setMask (Mat mask) {
		this.mask = mask;
	}

	public int getThreshold () {
		return threshold;
	}

	public void setThreshold (int threshold) {
		this.threshold = threshold;
	}

	public int getBlurSize () {
		return blurSize;
	}

	public void setBlurSize (int blurSize) {
		this.blurSize = blurSize;
	}

	public int getMorphSize () {
		return morphSize;
	}

	public void setMorphSize (int morphSize) {
		this.morphSize = morphSize;
	}

	public int getBallRadius () {
		return ballRadius;
	}

	public void setBallRadius (int ballRadius) {
		this.ballRadius = ballRadius;
	}

	public List<Circle> getBalls () {
		return balls;
	}

	public List<BallCluster> getBallClusters () {
		return ballClusters;
	}

	public List<MatOfPoint> getContours () {
		return contours;
	}
}
