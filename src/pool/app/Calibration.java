package pool.app;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * Stores the thresholds and values necessary to calibrate the 
 * camera, projector and detection algorithms.
 * 
 * <p>For the <b>camera</b> we need the corner points of the playing area in camera
 * space, as well as an axis aligned rectangle representing the playing area space. 
 * The quadliteral marking the playing area in the camera is then transformed
 * to the axis aligned playing area space. This is necessary so we can use axis aligned x/y coordinates for balls for
 * the physics simulation.</p>
 * 
 * <p>For the <b>projector</b> we need four points that denote
 * the corner pixels of the projector image in camera space. From this we 
 * can calculate the projector corner positions in the playing area space.
 * We can also calculate positions of points in projector space to camera space.
 * @author badlogic
 *
 */
public class Calibration {
	private List<Point> playAreaCameraSpace = new ArrayList<Point>();
	private List<Point> playArea = new ArrayList<Point>();
	private List<Point> projectorCameraSpace = new ArrayList<Point>();
	
	private Mat cameraToPlayArea;
	private Mat playAreaToCamera;
	private Mat playAreaToProjector;
	
	public Calibration(int playAreaWidth, int playAreaHeight) {
		playArea.add(new Point(0, 0));
		playArea.add(new Point(playAreaWidth, 0));
		playArea.add(new Point(playAreaWidth, playAreaHeight));
		playArea.add(new Point(0, playAreaHeight));
	}

	public List<Point> getPlayAreaCameraSpace () {
		return playAreaCameraSpace;
	}

	public List<Point> getPlayArea () {
		return playArea;
	}

	public List<Point> getProjectorCameraSpace () {
		return projectorCameraSpace;
	}
	
	public void calibrate() {
		
	}
}
