package pool.app;

import pool.app.screens.BackgroundSubtractionCalibration;
import pool.app.screens.CameraCalibrationScreen;
import pool.utils.CVLoader;
import pool.utils.ImgWindow;
import pool.utils.IpCamera;

public class PoolApp {
	private final ImgWindow cameraView;
	private final ImgWindow projectorView;
	private final Calibration calibration;
	private final IpCamera camera;
	private Screen lastScreen;
	private Screen screen;
	
	public PoolApp(String cameraUrl) {
		CVLoader.load();
		cameraView = ImgWindow.newWindow();
		cameraView.setTitle("Camera");
		projectorView = ImgWindow.newUndecoratedWindow();
		projectorView.moveToDisplay(1);
		projectorView.maximize();
		projectorView.setTitle("Projector");
		calibration = new Calibration(1280, 800);
		camera = new IpCamera(cameraUrl);
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
	
	public IpCamera getCamera() {
		return camera;
	}
	
	public boolean isClosed() {
		return cameraView.closed || projectorView.closed;
	}
	
	public void update() {
		if(lastScreen != null) {
			lastScreen.dispose();
			lastScreen = null;
		}
		if(screen != null) {
			screen.update();
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
	}
	
	public void setScreen(Screen screen) {
		this.lastScreen = this.screen;
		this.screen = screen;
	}

	public static void main (String[] args) {
		PoolApp app = new PoolApp("http://192.168.1.17:8080");
		app.setScreen(new BackgroundSubtractionCalibration(app));
		while(!app.isClosed()) {
			app.update();			
		}
	}
}
