package pool.app.screens;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import pool.app.PoolApp;
import pool.app.Screen;
import pool.tests.ColorSpace;
import pool.utils.Circle;
import pool.utils.ClickCallback;
import pool.utils.ValueCallback;

public class BackgroundSubtractionCalibration extends Screen {
	private Mat backgroundImage;
	private int threshold = 50;

	public BackgroundSubtractionCalibration (PoolApp app) {
		super(app);
	}
	
	@Override
	public void initialize () {
		app.getCameraView().addButton("next", new ClickCallback() {
			@Override
			public void clicked () {
				System.out.println("fuuck");
				app.setScreen(new CameraScreen(app));
			}
		});
		
		app.getCameraView().addLabel("LAbel:", Color.WHITE);
		
		app.getCameraView().addSlider(0, 255, 40, new ValueCallback() {
			@Override
			public void valueChanged (int value) {
				threshold = value;
			}
		});
	}

	@Override
	public void update () {
		// check if we clicked and take a background image
		Mat camFrame = app.getCamera().nextFrame();
		Mat finalFrame = camFrame;
		
		if(app.getCameraView().isClicked()) {
			backgroundImage = camFrame.clone();	
			System.out.println("took background image");
		}
		
		// show the diff if we have a background image		
		
		if(backgroundImage != null) {
			// diff
			Mat diff = new Mat();
			Core.absdiff(backgroundImage, camFrame, diff);								
			Imgproc.threshold(diff, diff, threshold, 255, Imgproc.THRESH_BINARY);			
		
			Imgproc.blur(diff, diff, new Size(9, 9));
			
			Mat r = ColorSpace.getChannel(diff, 2);
			Mat g = ColorSpace.getChannel(diff, 1);
			Mat b = ColorSpace.getChannel(diff, 0);
			
			// add with saturate to get binary mask
			finalFrame = r.clone();
			Core.add(finalFrame, g, finalFrame);
			Core.add(finalFrame, b, finalFrame);
			r.release();
			g.release();
			b.release();
			
			Imgproc.dilate(finalFrame, finalFrame, new Mat());
			
			if(true) {
				List<Circle> circles = detectCircle(finalFrame, 35, 40);				
				finalFrame = camFrame;
				for(Circle circle: circles) {
					Core.circle(finalFrame, new Point(circle.x, circle.y), (int)circle.radius, new Scalar(0, 0, 255), 2);
				}
			}
		}		
		
		app.getCameraView().setImage(finalFrame);
		finalFrame.release();
		camFrame.release();
		
		// draw inf on projector
		Mat buffer = app.getProjectorView().createBuffer();
		Core.rectangle(buffer, new Point(0, 0), new Point(buffer.cols(), buffer.rows()), new Scalar(0, 0, 0), -1);
		Core.putText(buffer, "Click to take background snapshot",  new Point(20, 20), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(0, 255, 0));
		app.getProjectorView().setImage(buffer);
		buffer.release();
	}
	
	public List<Circle> detectCircle(Mat img, int minRadius, int maxRadius) {
		Mat circles = new Mat();
		List<Circle> result = new ArrayList<Circle>();
		Imgproc.HoughCircles(img, circles, Imgproc.CV_HOUGH_GRADIENT, 1, minRadius, 1, 10, minRadius, maxRadius);
		for(int i = 0; i < circles.cols(); i++) {
			double[] circle = circles.get(0, i);
			result.add(new Circle(circle[0], circle[1], circle[2]));
		}
		return result;
	}

	@Override
	public void dispose () {
		if(backgroundImage != null) {
			backgroundImage.release();
		}
		app.getCameraView().clearControlls();
	}
}
