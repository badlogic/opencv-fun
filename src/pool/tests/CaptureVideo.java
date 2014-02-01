package pool.tests;

import java.awt.Color;
import java.awt.Graphics2D;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import pool.utils.CVLoader;
import pool.utils.ImgWindow;

public class CaptureVideo {
	public static void main (String[] args) {
		CVLoader.load();
		VideoCapture video = new VideoCapture(0);
		
		ImgWindow window = ImgWindow.newWindow();
		if(video.isOpened()) {
			Mat mat = new Mat();
			while(!window.isClosed()) {
				video.read(mat);
				if(!mat.empty()) {
					window.setImage(mat);
					Graphics2D g = window.begin();
					g.setColor(Color.WHITE);
					g.drawLine(0, 0, 100, 100);
					window.end();
				}
			}
		}
		video.release();
	}
}
