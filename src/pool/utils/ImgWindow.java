
package pool.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Mat;

@SuppressWarnings("serial")
public class ImgWindow extends JPanel {
	volatile BufferedImage img;
	volatile Graphics2D graphics;
	volatile boolean closed = false;
	JFrame frame;
	volatile int x, y;

	ImgWindow(JFrame frame) {
		this.frame = frame;
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed (WindowEvent e) {
				closed = true;
			}
		});
		this.setFocusable(true);
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved (MouseEvent e) {
				x = e.getX();
				y = e.getY();
			}
			
			@Override
			public void mouseDragged (MouseEvent e) {
				x = e.getX();
				y = e.getY();
			}
		});
	}
	
	public void paintComponent (Graphics g) {
		BufferedImage temp = img;
		if (img != null) {
			g.drawImage(temp, 0, 0, temp.getWidth(), temp.getHeight(), this);
		}
	}
	
	public void setImage(Mat mat) {
		if(mat == null) {
			img = null;
		} else {
			img = matToBufferedImage(mat);
			if(!(this.getWidth() == mat.cols() && this.getHeight() == mat.rows())) {
				setPreferredSize(new Dimension(mat.cols(), mat.rows()));
				frame.pack();
			}
		}
		repaint();
	}
	
	public void drawMouseCoords() {
		if(graphics == null) return;
		graphics.drawString(x + ", " + y, 10, 20);
	}
	
	public boolean isClosed() {
		return closed;
	}

	public static ImgWindow newWindow() {
		return newWindow(null);
	}
	
	public Graphics2D begin() {
		if(img != null) {
			graphics = img.createGraphics();
			return graphics;
		} else {
			return null;
		}
	}
	
	public void end() {
		if(graphics != null) {
			graphics.dispose();
			graphics = null;
			repaint();
		}
	}
	
	public static ImgWindow newWindow(Mat mat) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(400, 400);
		ImgWindow panel = new ImgWindow(frame);
		frame.setContentPane(panel);
		frame.setVisible(true);
		panel.setImage(mat);		
		return panel;
	}

	public static BufferedImage matToBufferedImage (Mat matrix) {
		if(matrix.channels() == 1) {
			int cols = matrix.cols();
			int rows = matrix.rows();
			int elemSize = (int)matrix.elemSize();
			byte[] data = new byte[cols * rows * elemSize];
			int type;
			matrix.get(0, 0, data);
			switch (matrix.channels()) {
			case 1:
				type = BufferedImage.TYPE_BYTE_GRAY;
				break;
			case 3:
				type = BufferedImage.TYPE_3BYTE_BGR;
				// bgr to rgb
				byte b;
				for (int i = 0; i < data.length; i = i + 3) {
					b = data[i];
					data[i] = data[i + 2];
					data[i + 2] = b;
				}
				break;
			default:
				return null;
			}
			
			BufferedImage image2 = new BufferedImage(cols, rows, type);
			image2.getRaster().setDataElements(0, 0, cols, rows, data);
			return image2;
		}
		
		if(matrix.channels() == 3) {
	      int width = matrix.width(), height = matrix.height(), channels = matrix.channels() ;  
	      byte[] sourcePixels = new byte[width * height * channels];  
	      matrix.get(0, 0, sourcePixels);  
	      // create new image and get reference to backing data  
	      BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);  
	      final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();  
	      System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);  
	      return image;
		}
		
		return null;
	}
}
