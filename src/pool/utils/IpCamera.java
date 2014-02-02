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

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class IpCamera {	
	private String url;
	
	public IpCamera(String url) {
		this.url = url;
	}
	
	public Mat nextFrame() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] buffer = new byte[10 * 1024];
		try {
			URLConnection con = new URL(url + "/shot.jpg").openConnection();
			InputStream in = con.getInputStream();
			int read = -1;
			while((read = in.read(buffer)) != -1) {
				bytes.write(buffer, 0, read);
			}
			DataOutputStream writer = new DataOutputStream(new FileOutputStream(new File("img.jpg")));
			writer.write(bytes.toByteArray());
			writer.close();
			return Highgui.imread("img.jpg");
		} catch (Exception e) {
			return null;
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
}
