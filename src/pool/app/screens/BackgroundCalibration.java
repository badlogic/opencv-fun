
package pool.app.screens;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import pool.app.PoolApp;
import pool.app.Screen;
import pool.tests.ColorSpace;
import pool.utils.BallCluster;
import pool.utils.BallDetector;
import pool.utils.Circle;
import pool.utils.ClickCallback;
import pool.utils.ValueCallback;

/**
 * Asks the user to take a screenshot of the background
 * @author badlogic
 *
 */
public class BackgroundCalibration extends Screen {
	private BallDetector detector;
	private boolean takeScreenshot = false;

	public BackgroundCalibration (PoolApp app) {
		super(app);
	}

	@Override
	public void initialize () {
		app.getCameraView().addButton("next", new ClickCallback() {
			@Override
			public void clicked () {
				app.setScreen(new CameraScreen(app));
			}
		});

		
		app.getCameraView().addButton("Take Screenshot", new ClickCallback() {
			@Override
			public void clicked () {
				takeScreenshot = true;
			}
		});

		app.getCameraView().addLabel("threshold:", Color.WHITE);
		app.getCameraView().addSlider(0, 255, 30, new ValueCallback() {
			@Override
			public void valueChanged (int value) {
				app.getCalibration().setSubtractionThreshold(value);
			}
		});
	}

	@Override
	public void update () {
		// check if we clicked and take a background image
		Mat camFrame = app.getCamera().nextFrame();
		Mat finalFrame = camFrame;

		if (app.getCameraView().isClicked()) {
			app.getCalibration().setBackgroundImage(camFrame.clone());
			detector = new BallDetector(app.getCalibration());
			System.out.println("took background image");
		}

		// show the diff if we have a background image

		if (detector != null) {
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
			finalFrame = result;
		}
		
		if(takeScreenshot) {
			takeScreenshot = false;
			new File("screenshots").mkdirs();
			Highgui.imwrite("screenshots/sc-" + new Date().toString() + ".png", finalFrame);
		}

		app.getCameraView().setImage(finalFrame);
		finalFrame.release();
		camFrame.release();

		// draw inf on projector
		Mat buffer = app.getProjectorView().createBuffer();
		Core.rectangle(buffer, new Point(0, 0), new Point(buffer.cols(), buffer.rows()), new Scalar(0, 0, 0), -1);
		Core.putText(buffer, "Click to take background snapshot", new Point(20, 20), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 255,
			0));
		app.getProjectorView().setImage(buffer);
		buffer.release();
	}

	@Override
	public void dispose () {
		app.getCameraView().clearControlls();
	}
}
