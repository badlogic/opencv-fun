package pool.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class IpCamera {	
	public Mat nextFrame(String url) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] buffer = new byte[100 * 1024];
		try {
			URLConnection con = new URL(url).openConnection();
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
}
