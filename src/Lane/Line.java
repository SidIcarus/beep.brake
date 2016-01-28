package Lane;

import java.util.Stack;

import org.opencv.core.Point;

public class Line {
	
	private Point pt1;
	private Point pt2;
		
	public Line(double[] data){
		if(data.length == 4){
			this.pt1 = new Point(data[0], data[1]);
			this.pt2 = new Point(data[2], data[3]);
		}
	}
	
	/**
	 * get angle
	 * @return
	 */
	public float getAngle(){
		double dx = pt1.x - pt2.x;
		double dy = pt1.y - pt2.y;
		float angle = (float) (Math.abs((Math.atan2(dy, dx) * 180/Math.PI)) % 180);
		return angle;
	}
	
	
}
