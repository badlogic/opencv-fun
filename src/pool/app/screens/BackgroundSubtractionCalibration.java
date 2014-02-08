
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
	
	public void drawCircles(List<MatOfPoint> contours, Mat img) {
		List<Circle> circles = new ArrayList<Circle>();
		for (int i = 0; i < contours.size(); i++) {
			float[] radius = new float[1];
			Point center = new Point();
			Imgproc.minEnclosingCircle(new MatOfPoint2f(contours.get(i).toArray()), center, radius);
			circles.add(new Circle(center.x, center.y, 34));
			Core.circle(img, center, 34, new Scalar(0, 255, 0), 2);
		}
	}
	
	private Mat detect3 (Mat cam, Mat background) {
		// background subtraction
		Mat diff = cam.clone();
		background = background.clone();

		// remove noise
		Imgproc.blur(background, background, new Size(3, 3));
		Imgproc.blur(diff, diff, new Size(3, 3));

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
		Imgproc.dilate(result, result, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,  new Size(5, 5)));
		
		Mat mask = result.clone();
		cam.copyTo(result, mask);		
		
		// get distancetransform, convert to gray for display
		Mat dist = new Mat();
		Imgproc.distanceTransform(mask, dist, Imgproc.CV_DIST_L2, 3);
		Core.normalize(dist, dist, 0, 1.0, Core.NORM_MINMAX);
		Mat origDist = dist.clone();
		Core.convertScaleAbs(origDist, origDist, 255, 0);

		Imgproc.threshold(dist, dist, 0.50, 1., Imgproc.THRESH_BINARY);
		Core.convertScaleAbs(dist, dist, 255, 0);

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//		Imgproc.findContours(dist.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//		Imgproc.drawContours(result, contours, -1, new Scalar(0, 0, 255));
//		drawCircles(contours, result);
		
		Imgproc.findContours(mask.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(result, contours, -1, new Scalar(0, 0, 255), 2);

		return result;
	}
	
	

	private Mat detect2 (Mat cam, Mat background) {
		// background subtraction
		Mat diff = cam.clone();
		background = background.clone();
		
		// remove noise
		Imgproc.blur(background, background, new Size(7,7));
		Imgproc.blur(diff, diff, new Size(7,7));
		
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
		
		Imgproc.dilate(result, result, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,  new Size(5, 5)));
		
		Mat mask = result.clone();
		cam.copyTo(result, mask);
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    
	   Imgproc.findContours(mask.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
//	   Imgproc.drawContours(result, contours, -1, new Scalar(0, 0, 255));
	   
	   for(int i = 0; i < contours.size(); i++) {
	   	double area = Imgproc.contourArea(contours.get(i));	   	
	   	
	   	if(area > Math.PI * 25 * 25) {
	   		float[] radius = new float[1];
   			Point center = new Point();
   			Imgproc.minEnclosingCircle(new MatOfPoint2f(contours.get(i).toArray()), center, radius);
	   		
	   		// normal circle
	   		if(area < Math.PI * 40 * 40) {	
	   			Core.circle(result, center, 35, new Scalar(0, 255, 0), 2);
	   		} else {
	   			int numBalls = (int)(area / (Math.PI * 35 * 35));
	   			Imgproc.drawContours(result, contours, i, new Scalar(0, 0, 255), 2);
	   			Core.putText(result, "balls:" + numBalls, center, Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255), 2);
	   		}
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
