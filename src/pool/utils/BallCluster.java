package pool.utils;

import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

public class BallCluster {
	private final MatOfPoint contour;
	private final int numBalls;
	private final List<Circle> estimatedCircles;
	private final RotatedRect minRect;
	
	
	public BallCluster(MatOfPoint contour, int numBalls, List<Circle> estimatedCircles) {
		this.contour = contour;
		this.numBalls = numBalls;
		this.minRect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
		this.estimatedCircles = estimatedCircles;
	}

	public MatOfPoint getContour () {
		return contour;
	}

	public int getNumBalls () {
		return numBalls;
	}

	public RotatedRect getMinRect () {
		return minRect;
	}

	public List<Circle> getEstimatedCircles () {
		return estimatedCircles;
	}
}
