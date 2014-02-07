package pool.utils;

import org.opencv.core.Mat;

public interface Camera {
	public Mat nextFrame();
	public void release();
}
