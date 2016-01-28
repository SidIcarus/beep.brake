package Lane;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import org.opencv.core.Rect;

public class Lane {
	
	public static final int DASHED = 0;
	public static final int SOLID = 1;
	public static final int DOUBLE_YELLOW = 2;
	
	private int currentLaneType = -1;
	private Stack<Rect> rio = new Stack<Rect>();
	//ArrayList<Line> visableLines = new ArrayList<Line>();
	LinkedList<Line> visableLines = new LinkedList<Line>();
	
	public Lane(){
		
	}
	
	public void expandRoi(){
		
	}
	
	public void retractRoi(){
		
	}
	
	/**
	 * 
	 * @return current lane type or -1 if not known
	 */
	public int getLaneType(){
		return currentLaneType;
	}
}
