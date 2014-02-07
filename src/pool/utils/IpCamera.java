package pool.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;

public class IpCamera implements Camera {	
	private Mat frame;
	private final String url;
	private final Thread thread;
	
	public IpCamera(final String url) {
		this.frame = Mat.zeros(new Size(200, 200), CvType.CV_8UC3);
		this.url = url;
		thread = new Thread(new Runnable() {
			@Override
			public void run () {
				while(true) {
					try {
						ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						byte[] buffer = new byte[10 * 1024];
						URLConnection con = new URL(url + "/shot.jpg").openConnection();
						InputStream in = con.getInputStream();
						int read = -1;
						while((read = in.read(buffer)) != -1) {
							bytes.write(buffer, 0, read);
						}
						DataOutputStream writer = new DataOutputStream(new FileOutputStream(new File("img.jpg")));
						writer.write(bytes.toByteArray());
						writer.close();
						Mat mat =  Highgui.imread("img.jpg");
						synchronized(this) {
							frame = mat;
						}
					} catch(Throwable t) {
						t.printStackTrace();
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	public Mat nextFrame() {
		synchronized(this) {
			return frame.clone();
		}
	}
	
	public void focus() {
		try {
			URLConnection con = new URL(url + "/focus").openConnection();
			while(con.getInputStream().read() != -1);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void release() {
		thread.stop();
	}
}
