package pool.tests;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class HistogramEqualization {
	public static void main (String[] args) {
		CVLoader.load();
		
		// load the image
		Mat img = Highgui.imread("data/topdown-9.png");
		Mat equ = new Mat();
		img.copyTo(equ);
		Imgproc.blur(equ, equ, new Size(3, 3));
		
		Imgproc.cvtColor(equ, equ, Imgproc.COLOR_BGR2YCrCb);
		List<Mat> channels = new ArrayList<Mat>();
		Core.split(equ, channels);
		Imgproc.equalizeHist(channels.get(0), channels.get(0));
		Core.merge(channels, equ);
		Imgproc.cvtColor(equ, equ, Imgproc.COLOR_YCrCb2BGR);
		
		Mat gray = new Mat();
		Imgproc.cvtColor(equ, gray, Imgproc.COLOR_BGR2GRAY);
		Mat grayOrig = new Mat();
		Imgproc.cvtColor(img, grayOrig, Imgproc.COLOR_BGR2GRAY);
		
		ImgWindow.newWindow(img);
		ImgWindow.newWindow(equ);
		ImgWindow.newWindow(gray);
		ImgWindow.newWindow(grayOrig);
	}
}
