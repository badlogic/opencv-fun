package pool.app.screens;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import pool.app.PoolApp;
import pool.app.Screen;

public class CameraCalibrationScreen extends Screen {
	public CameraCalibrationScreen (PoolApp app) {
		super(app);
	}
	
	@Override
	public void initialize () {
	}

	@Override
	public void update () {
		// draw cam view
		Mat image = app.getCamera().nextFrame();
		
		app.getCameraView().setImage(image);
		
		Mat fb = app.getProjectorView().createBuffer();
		Core.rectangle(fb, new Point(0, 0), new Point(fb.cols(), fb.rows()), new Scalar(0, 0, 0), -1);
		Core.rectangle(fb, new Point(20, 20), new Point(fb.cols()-40, fb.rows()-20), new Scalar(255, 255, 255), 1);
		app.getProjectorView().setImage(fb);
	}

	@Override
	public void dispose () {
	}
}
