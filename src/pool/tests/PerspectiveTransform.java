
package pool.tests;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class PerspectiveTransform {
	static List<Point> corners = new ArrayList<Point>();
	static List<Point> target = new ArrayList<Point>();
	static List<Point> newPoints = new ArrayList<Point>();
	static Mat img;
	static Mat proj;
	static Mat trans;
	
	public static void main (String[] args) {
		CVLoader.load();
		img = Highgui.imread("data/topdown-6.jpg");				
		
		ImgWindow origWnd = ImgWindow.newWindow(img);
		ImgWindow projWnd = ImgWindow.newWindow();		
		
		target.add(new Point(0, 0));
		target.add(new Point(img.cols(), 0));
		target.add(new Point(img.cols(), img.rows()));
		target.add(new Point(0, img.rows()));
		
		newPoints.add(new Point(100, 100));
		
		while(!origWnd.closed) {			
			doRegistration(origWnd);
			doProjection(projWnd);
			
			// draw calibration points		
			for(Point p: corners) {
				Core.circle(img, p, 2, new Scalar(0, 255, 0), 2);
			}
			for(Point p: newPoints) {
				Core.circle(img, p, 2, new Scalar(255, 255, 0), 2);
			}
			
			// draw new points	
			if(proj != null) {
				Mat transformed = new Mat();
				if(newPoints.size() > 0) {
					Core.perspectiveTransform(Converters.vector_Point2f_to_Mat(newPoints), transformed, trans);
					List<Point> transPoints = new ArrayList<Point>();
					Converters.Mat_to_vector_Point2f(transformed, transPoints);
					for(Point p: transPoints) {
						Core.circle(proj, p, 2, new Scalar(255, 255, 0), 2);
					}
				}
			}
			
			origWnd.setImage(img);
			projWnd.setImage(proj);
		}
	}
	
	/** Waits for the user to define the 4 corner points, starting
	 * top left, counter clockwise
	 * @param wnd
	 */
	public static void doRegistration(ImgWindow wnd) {
		if(corners.size() < 4) {
			if(wnd.isClicked()) {
				System.out.println("added calibration point");
				corners.add(new Point(wnd.mouseX, wnd.mouseY));
			}
		} else {
			if(wnd.isClicked()) {
				newPoints.add(new Point(wnd.mouseX, wnd.mouseY));
			}
		}
	}
	
	private static void doProjection (ImgWindow projWnd) {
		if(corners.size() == 4) {
			Mat cornersMat = Converters.vector_Point2f_to_Mat(corners);
			Mat targetMAt = Converters.vector_Point2f_to_Mat(target);
			trans = Imgproc.getPerspectiveTransform(cornersMat, targetMAt);
			proj = new Mat();
			Imgproc.warpPerspective(img, proj, trans, new Size(img.cols(), img.rows()));
			if(projWnd.isClicked()) {
				// add points to draw onto the original space
			}
		}
	}	
}
