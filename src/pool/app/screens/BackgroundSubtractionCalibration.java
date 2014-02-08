
package pool.app.screens;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import pool.app.PoolApp;
import pool.app.Screen;
import pool.tests.ColorSpace;
import pool.utils.Circle;
import pool.utils.ClickCallback;
import pool.utils.ValueCallback;

public class BackgroundSubtractionCalibration extends Screen {
	private Mat backgroundImage;
	private int threshold = 20;

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

		if (app.getCameraView().isClicked()) {
			backgroundImage = camFrame.clone();
			System.out.println("took background image");
		}

		// show the diff if we have a background image

		if (backgroundImage != null) {
			finalFrame = detect2(camFrame, backgroundImage);			
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

	public List<Circle> detectCircle (Mat img, int minRadius, int maxRadius) {
		Mat circles = new Mat();
		List<Circle> result = new ArrayList<Circle>();
		Imgproc.HoughCircles(img, circles, Imgproc.CV_HOUGH_GRADIENT, 1, minRadius, 1, 10, minRadius, maxRadius);
		for (int i = 0; i < circles.cols(); i++) {
			double[] circle = circles.get(0, i);
			result.add(new Circle(circle[0], circle[1], circle[2]));
		}
		return result;
	}

	private Mat detect2 (Mat cam, Mat background) {
		// background subtraction
		Mat diff = cam.clone();
		background = background.clone();
		
		// remove noise
		Imgproc.blur(background, background, new Size(5,5));
		Imgproc.blur(diff, diff, new Size(5,5));
		
		// take abs diff and create binary image in all 3 channels
		Core.absdiff(background, diff, diff);		
		Imgproc.threshold(diff, diff, threshold, 255, Imgproc.THRESH_BINARY);

		// extract color channels and merge them to single bitmask
		Mat r = ColorSpace.getChannel(diff, 2);
		Mat g = ColorSpace.getChannel(diff, 1);
		Mat b = ColorSpace.getChannel(diff, 0);
		
		Mat result = r.clone();
		Core.add(result, g, result);
		Core.add(result, b, result);
		
		Mat mask = result.clone();
		cam.copyTo(result, mask);
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    
	   Imgproc.findContours(mask, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
//	   Imgproc.drawContours(result, contours, -1, new Scalar(0, 0, 255));
	   
	   for(int i = 0; i < contours.size(); i++) {
	   	double area = Imgproc.contourArea(contours.get(i));	   	
	   	
	   	if(area > Math.PI * 25 * 25) {
	   		float[] radius = new float[1];
   			Point center = new Point();
   			Imgproc.minEnclosingCircle(new MatOfPoint2f(contours.get(i).toArray()), center, radius);
	   		
	   		// normal circle
	   		if(area < Math.PI * 36 * 36) {	
	   			Core.circle(result, center, (int)radius[0], new Scalar(0, 255, 0), 2);
	   		} else {
	   			int numBalls = (int)Math.round(area / (Math.PI * 33 * 33));
	   			Imgproc.drawContours(result, contours, i, new Scalar(0, 0, 255), 2);
	   			Core.putText(result, "balls:" + numBalls, center, Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255), 2);
	   		}
	   	}
	   }	
		
		return result;
	}
	
	private Mat detect (Mat cam, Mat background) {
		// diff
		Mat diff = new Mat();
		Core.absdiff(background, cam, diff);
		Imgproc.threshold(diff, diff, threshold, 255, Imgproc.THRESH_BINARY);

		Imgproc.blur(diff, diff, new Size(9, 9));

		Mat r = ColorSpace.getChannel(diff, 2);
		Mat g = ColorSpace.getChannel(diff, 1);
		Mat b = ColorSpace.getChannel(diff, 0);

		// add with saturate to get binary mask
		Mat result = r.clone();
		Core.add(result, g, result);
		Core.add(result, b, result);
		r.release();
		g.release();
		b.release();

//		for(int i = 0; i < 10; i++) {
//			Imgproc.erode(result, result, Mat.ones(new Size(3, 3), CvType.CV_32F));
//		}
		
//		Imgproc.dilate(result, result, new Mat());
		
		if (true) {
			List<Circle> circles = detectCircle(result, 35, 40);
			result = cam;
			for (Circle circle : circles) {
				Core.circle(result, new Point(circle.x, circle.y), (int)circle.radius, new Scalar(0, 0, 255), 2);
			}
		}
		return result;
	}

	@Override
	public void dispose () {
		if (backgroundImage != null) {
			backgroundImage.release();
		}
		app.getCameraView().clearControlls();
	}
}
