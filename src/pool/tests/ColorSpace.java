
package pool.tests;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class ColorSpace {
	public static void main (String[] args) {
		CVLoader.load();
		Mat orig = Highgui.imread("data/topdown-6.jpg");
		Mat hsv = new Mat();
		Imgproc.cvtColor(orig, hsv, Imgproc.COLOR_BGR2YCrCb);
		
		List<Mat> channels = new ArrayList<Mat>();
		for(int i = 0; i < hsv.channels(); i++) {
			Mat channel = new Mat();
			channels.add(channel);
		}
		Core.split(hsv, channels);
		
		for(Mat channel: channels) {
			ImgWindow.newWindow(channel);
		}
	}
	
	public static Mat getChannel(Mat orig, int colorSpace, int channelIdx) {
		Mat hsv = new Mat();
		Imgproc.cvtColor(orig, hsv, colorSpace);
		List<Mat> channels = new ArrayList<Mat>();
		for(int i = 0; i < hsv.channels(); i++) {
			Mat channel = new Mat();
			channels.add(channel);
		}
		Core.split(hsv, channels);
		return channels.get(channelIdx);
	}
}
