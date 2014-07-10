package pool.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import pool.app.Calibration;

public class BallDetector {
	private Calibration calib;
	private BackgroundSubtractor subtractor;
	private Mat mask;
	private Mat debug;
	
	private final List<Circle> balls = new ArrayList<Circle>();
	private final List<BallCluster> ballClusters = new ArrayList<BallCluster>();
	private final List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	
	public BallDetector(Calibration calib) {
		this.calib = calib;
		this.subtractor = new BackgroundSubtractor(calib);
	}
	
	public void detect(Mat camera) {
		mask = subtractor.createMask(camera);		
		extractCircles(mask, balls, ballClusters, contours);
	}
	
	private void extractCircles (Mat mask, List<Circle> balls, List<BallCluster> ballClusters, List<MatOfPoint> contours) {
		// clear input
		balls.clear();
		ballClusters.clear();
		contours.clear();
		
		// find the contours
		Imgproc.findContours(mask.clone(), contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		// iterate through the contours, find single balls and clusters of balls touching each other
		double minArea = Math.PI * (calib.getBallRadius() * 0.9f) * (calib.getBallRadius() * 0.9f); // minimal ball area
		double maxArea = Math.PI * (calib.getBallRadius() * 1.1f) * (calib.getBallRadius() * 1.1f); // maximal ball area

		for (int i = 0; i < contours.size(); i++) {
			double area = Imgproc.contourArea(contours.get(i));
			if (area > minArea) {				
				if (area < maxArea) {
					// we found a ball
					float[] radius = new float[1];
					Point center = new Point();
					Imgproc.minEnclosingCircle(new MatOfPoint2f(contours.get(i).toArray()), center, radius);
					balls.add(new Circle(center.x, center.y, calib.getBallRadius()));
				} else {
					// we found a cluster of balls
					int numBalls = (int)(area / (Math.PI * calib.getBallRadius() * calib.getBallRadius() * 0.9));
					
					// draw the contours to a bit mask
					Mat hough = Mat.zeros(mask.size(), CvType.CV_8U);
					Imgproc.drawContours(hough, contours, i, new Scalar(255, 255, 255), -2);
					
					// detect hough circles, try different params until we hit the number of balls
					Mat houghCircles = new Mat();
					int hit = 0;
					for(int j = 8; j < 20; j++) {
						Imgproc.HoughCircles(hough, houghCircles, Imgproc.CV_HOUGH_GRADIENT, 2, calib.getBallRadius() * 0.9 * 2, 255, j, (int)(calib.getBallRadius() * 0.9), (int)(calib.getBallRadius() * 1.1));
						if(houghCircles.cols() <= numBalls) {
							hit++;
							if(hit == 4) break;
						}
					}
					
					
					List<Circle> estimatedCircles = new ArrayList<Circle>();
					for(int j = 0; j < houghCircles.cols(); j++) {
						double[] circle = houghCircles.get(0, j);
						if(circle != null) {
							estimatedCircles.add(new Circle(circle[0], circle[1], calib.getBallRadius()));
						}
					}
					
					ballClusters.add(new BallCluster(contours.get(i), numBalls, estimatedCircles));
				}
			}
		}
	}

	public Mat getMask () {
		return mask;
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

	public Mat getDebug () {
		return debug;
	}
}
