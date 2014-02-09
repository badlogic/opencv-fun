
package pool.tests;

import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import pool.utils.BallCluster;
import pool.utils.BallDetector;
import pool.utils.CVLoader;
import pool.utils.Circle;
import pool.utils.ImgWindow;

public class BallDetection {
	public static void main (String[] args) {
		CVLoader.load();
		
		ImgWindow wnd = ImgWindow.newWindow();
		BallDetector detector = new BallDetector(Highgui.imread("screenshots/positions/background.png"));
		Mat camera = Highgui.imread("screenshots/positions/camera.png");
		
		 while(true) {
			 detect(wnd, detector, camera);
		 }
	}

	private static void detect (ImgWindow wnd, BallDetector detector, Mat camera) {
		detector.detect(camera);
		
		Mat result = new Mat();
		camera.copyTo(result, detector.getMask());
		
		for(Circle ball: detector.getBalls()) {
			Core.circle(result, new Point(ball.x, ball.y), (int)ball.radius, new Scalar(0, 255, 0), 2);
		}
		
		for(int i = 0; i < detector.getBallClusters().size(); i++) {
			BallCluster cluster = detector.getBallClusters().get(i);
//			Imgproc.drawContours(result, Arrays.asList(cluster.getContour()), 0, new Scalar(0, 0, 255), 2);			
			for(Circle circle: cluster.getEstimatedCircles()) {
				Core.circle(result, new Point(circle.x, circle.y), (int)circle.radius, new Scalar(255, 0, 255), 2);
			}
			Core.putText(result, cluster.getNumBalls() + " balls", cluster.getMinRect().center, Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 0));
		}
		
		Core.putText(result, wnd.mouseX + ", " + wnd.mouseY, new Point(20, 30), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 255));
		wnd.setImage(result);
	}		
}
