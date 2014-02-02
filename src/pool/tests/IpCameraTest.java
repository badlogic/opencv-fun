package pool.tests;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;
import pool.utils.IpCamera;

public class IpCameraTest {
	public static void main (String[] args) {
		CVLoader.load();
		IpCamera cam = new IpCamera();
		
		ImgWindow wnd = ImgWindow.newWindow();
		
		while(!wnd.closed) {
			wnd.setImage(cam.nextFrame("http://192.168.1.7:8080/shot.jpg"));
		}
		
	}
}
