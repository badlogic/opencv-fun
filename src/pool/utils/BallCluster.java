package pool.utils;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

public class BallCluster {
	private final MatOfPoint contour;
	private final int numBalls;
	private final RotatedRect minRect;
	
	
	public BallCluster(MatOfPoint contour, int numBalls) {
		this.contour = contour;
		this.numBalls = numBalls;
		this.minRect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
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
}
