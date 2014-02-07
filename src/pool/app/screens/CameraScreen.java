package pool.app.screens;

import org.opencv.core.Mat;

import pool.app.PoolApp;
import pool.app.Screen;

public class CameraScreen extends Screen {
	public CameraScreen (PoolApp app) {
		super(app);
	}

	@Override
	public void initialize () {
		
	}

	@Override
	public void update () {
		Mat camFrame = app.getCamera().nextFrame();
		app.getCameraView().setImage(camFrame);
		camFrame.release();
		System.out.println("what?");
	}

	@Override
	public void dispose () {
	}
}
