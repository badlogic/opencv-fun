
package pool.app.screens;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import pool.app.PoolApp;
import pool.app.Screen;
import pool.utils.BallCluster;
import pool.utils.BallDetector;
import pool.utils.Circle;
import pool.utils.ClickCallback;

/** Put a ball on the table, and mark its diameter with the mouse by dragging a line. Assumes that a background image has been set
 * in the calibration.
 * @author badlogic */
public class BallsizeCalibration extends Screen {
	private BallDetector detector;
	private volatile float startX, startY, endX, endY;

	public BallsizeCalibration (PoolApp app) {
		super(app);		
	}

	@Override
	public void initialize () {
		app.getCameraView().addMouseListener(new MouseAdapter() {								
			@Override
			public void mousePressed (MouseEvent e) {
				startX = endX = e.getX();
				startY = endY = e.getY();
			}					
		});
		app.getCameraView().addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged (MouseEvent e) {
				endX = e.getX();
				endY = e.getY();
			}
		});
		
		app.getCameraView().addButton("prev", new ClickCallback() {
			@Override
			public void clicked () {
				app.setScreen(new BackgroundSubtractionCalibration(app));
			}
		});
		app.getCameraView().addButton("next", new ClickCallback() {
			@Override
			public void clicked () {
				app.setScreen(new CameraScreen(app));
			}
		});	
		app.getCameraView().addLabel("Put a ball on the table, drag a line to mark its diameter!", Color.red);
		detector = new BallDetector(app.getCalibration());
	}

	@Override
	public void update () {
		// check if we clicked and take a background image
		Mat camFrame = app.getCamera().nextFrame();

		detector.detect(camFrame);
		Mat result = new Mat();
		camFrame.copyTo(result, detector.getMask());
		
		for(Circle ball: detector.getBalls()) {
			Core.circle(result, new Point(ball.x, ball.y), (int)ball.radius, new Scalar(0, 255, 0), 2);
		}
		
		for(int i = 0; i < detector.getBallClusters().size(); i++) {
			BallCluster cluster = detector.getBallClusters().get(i);
			for(Circle circle: cluster.getEstimatedCircles()) {
				Core.circle(result, new Point(circle.x, circle.y), (int)circle.radius, new Scalar(255, 0, 255), 2);
			}
			Core.putText(result, cluster.getNumBalls() + " balls", cluster.getMinRect().center, Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 0));
		}
		
		Core.line(result, new Point(startX, startY), new Point(endX, endY), new Scalar(0, 255, 0));
		float diameter = (float)Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY));
		if(diameter >  0) {
			app.getCalibration().setBallRadius(diameter / 2);
		}
		Core.putText(result, "diameter: " + diameter, new Point(startX, startY +20), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 255, 0));

		app.getCameraView().setImage(result);
		result.release();
		camFrame.release();
	}

	@Override
	public void dispose () {
		app.getCameraView().clearControlls();
	}
}
