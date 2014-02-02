package pool.app.screens;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import pool.app.PoolApp;
import pool.app.Screen;

public class BackgroundSubtractionCalibration extends Screen {
	private Mat backgroundImage;

	public BackgroundSubtractionCalibration (PoolApp app) {
		super(app);
	}

	@Override
	public void update () {
		// check if we clicked and take a background image
		Mat camFrame = app.getCamera().nextFrame();
		if(app.getCameraView().isClicked()) {
			backgroundImage = camFrame.clone();	
			System.out.println("took background image");
		}
		
		// show the diff if we have a background image
		if(backgroundImage != null) {
			Core.absdiff(camFrame, backgroundImage, camFrame);
			Imgproc.cvtColor(camFrame, camFrame, Imgproc.COLOR_BGR2GRAY);
			Imgproc.threshold(camFrame, camFrame, 40, 255, Imgproc.THRESH_BINARY);
		}
		app.getCameraView().setImage(camFrame);
		camFrame.release();
		
		// draw inf on projector
		Mat buffer = app.getProjectorView().createBuffer();
		Core.rectangle(buffer, new Point(0, 0), new Point(buffer.cols(), buffer.rows()), new Scalar(0, 0, 0), -1);
		Core.putText(buffer, "Click to take background snapshot",  new Point(20, 20), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 255, 0));
		app.getProjectorView().setImage(buffer);
		buffer.release();
	}

	@Override
	public void dispose () {
		if(backgroundImage != null) {
			backgroundImage.release();
		}
	}
}
