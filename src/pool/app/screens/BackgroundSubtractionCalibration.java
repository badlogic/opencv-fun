
package pool.app.screens;

import java.awt.Color;
import java.io.File;
import java.util.Date;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;

import pool.app.PoolApp;
import pool.app.Screen;
import pool.utils.BackgroundSubtractor;
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
public class BackgroundSubtractionCalibration extends Screen {
	BackgroundSubtractor subtractor;
	
	public BackgroundSubtractionCalibration (PoolApp app) {
		super(app);
	}

	@Override
	public void initialize () {
		app.getCameraView().addButton("next", new ClickCallback() {
			@Override
			public void clicked () {
				app.setScreen(new BallsizeCalibration(app));
			}
		});

		app.getCameraView().addLabel("threshold:", Color.WHITE);
		app.getCameraView().addSlider(0, 255, app.getCalibration().getSubtractionThreshold(), new ValueCallback() {
			@Override
			public void valueChanged (int value) {
				app.getCalibration().setSubtractionThreshold(value);
			}
		});
		app.getCameraView().addLabel("Remove all balls from the table, click anywhere to take background image", Color.red);
	}

	@Override
	public void update () {
		// check if we clicked and take a background image
		Mat camFrame = app.getCamera().nextFrame();
		Mat finalFrame = camFrame;

		if (app.getCameraView().isClicked()) {
			app.getCalibration().setBackgroundImage(camFrame.clone());
			subtractor = new BackgroundSubtractor(app.getCalibration());
			System.out.println("took background image");
		}

		// show the diff if we have a background image

		if (subtractor != null) {
			finalFrame = subtractor.createMask(camFrame);
		}				

		app.getCameraView().setImage(finalFrame);
		finalFrame.release();
		camFrame.release();		
	}

	@Override
	public void dispose () {
		app.getCameraView().clearControlls();
	}
}
