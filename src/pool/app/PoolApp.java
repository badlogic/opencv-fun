package pool.app;

import javax.media.jai.PerspectiveTransform;

import pool.app.screens.BackgroundSubtractionCalibration;
import pool.app.screens.CameraScreen;
import pool.utils.CVLoader;
import pool.utils.Camera;
import pool.utils.ImgWindow;
import pool.utils.IpCamera;
import pool.utils.OpenCVCamera;

public class PoolApp {
	private final ImgWindow cameraView;
	private final ImgWindow projectorView;
	private final Calibration calibration;
	private final Camera camera;
	private Screen lastScreen;
	private Screen screen;
	
	public PoolApp(Camera camera) {
		CVLoader.load();
		cameraView = ImgWindow.newWindow();
		cameraView.setTitle("Camera");
		projectorView = ImgWindow.newUndecoratedWindow();
		projectorView.moveToDisplay(1);
//		projectorView.maximize();
		projectorView.setTitle("Projector");
		calibration = new Calibration(1280, 800);
		this.camera = camera;
	}
	
	public ImgWindow getCameraView () {
		return cameraView;
	}

	public ImgWindow getProjectorView () {
		return projectorView;
	}

	public Calibration getCalibration () {
		return calibration;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public boolean isClosed() {
		return cameraView.closed || projectorView.closed;
	}
	
	public synchronized void update() {
		if(lastScreen != null) {
			lastScreen.dispose();
			lastScreen = null;
			screen.initialize();
		}
		if(screen != null) {
			cameraView.processEvents();
			projectorView.processEvents();
			screen.update();
		}
	}
	
	public synchronized void setScreen(Screen screen) {
		this.lastScreen = this.screen;
		this.screen = screen;
		if(lastScreen == null) screen.initialize();
	}

	public static void main (String[] args) {		
		PoolApp app = new PoolApp(new OpenCVCamera());
		app.setScreen(new BackgroundSubtractionCalibration(app));
		while(!app.isClosed()) {
			app.update();	
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
}
