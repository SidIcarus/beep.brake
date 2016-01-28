package util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import swing.VideoReader;

import org.opencv.core.Point;

public class RegionSelector implements MouseMotionListener, MouseListener{

	private java.awt.Point starting = new java.awt.Point();
	private java.awt.Point ending = new java.awt.Point();
	private final int frameIndex;
	public static ArrayList<Rect> selected = new ArrayList<Rect>();
	private FrameSource source;
	
	
	public RegionSelector(int frameIndex, FrameSource source){
		this.frameIndex = frameIndex;
		this.source = source;
	}
	
	@Override
	public void mousePressed(MouseEvent e) { 
		this.starting.x = e.getX();
		this.starting.y = e.getY();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		this.ending.x = e.getX();
		this.ending.y = e.getY();
		//awt point --> opencvPoint
		Point p1 = new Point(starting.getX(), starting.getY());
		Point p2 = new Point(ending.getX(), ending.getY());
		
		updateRect(frameIndex, p1, p2);

	}


	@Override
	public void mouseReleased(MouseEvent e) {
		this.ending.x = e.getX();
		this.ending.y = e.getY();
		//point --> opencvPoint
		org.opencv.core.Point p1 = new Point(starting.getX(), starting.getY());
		org.opencv.core.Point p2 = new Point(ending.getX(), ending.getY());
		
		updateRect(frameIndex, p1, p2);
		//store rect
		selected.add(new Rect(p1,p2));
		
	}

	private void updateRect(int screenIndex, Point p1, Point p2){
		Mat m = source.getCurrentFrame();
		Mat disp = new Mat();
		m.copyTo(disp);
		String text = "(" + p1.x + ", " + p1.y + " )(" + p2.x + " ," + p2.y + " )";
		Imgproc.putText(disp, text, new Point(20,20), 1, 1.2, new Scalar(50,220,100));
		Imgproc.putText(disp, text, new Point(21,21), 1, 1.2, new Scalar(100,50,220));
		Imgproc.putText(disp, text, new Point(22,22), 1, 1.2, new Scalar(220,100,50));
		
		Imgproc.rectangle(disp, p1, p2, new Scalar(200,50,50));
		//redraw other rects
		for(Rect r : selected){
			Imgproc.rectangle(disp, r.tl(), r.br(), new Scalar(255,10,10));
		}
		
		VideoReader.displayImage(disp, screenIndex);
	}
	
	public void updateRect(int screenIndex){
		Mat m = this.source.getCurrentFrame();
		Mat disp = new Mat();
		m.copyTo(disp);
		//redraw other rects
		for(Rect r : selected){
			Imgproc.rectangle(disp, r.tl(), r.br(), new Scalar(255,10,10));
		}
		
		VideoReader.displayImage(disp, screenIndex);
	}
	
	
	
	/* shouldn't need */
	@Override
	public void mouseMoved(MouseEvent e) { }
	
	@Override
	public void mouseClicked(MouseEvent e) { }
	
	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) {	}


		
}
