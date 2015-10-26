package myfirstapp.app;

import java.util.Date;
import java.util.HashMap;

public class Segment {

	private Segment nextSeg;
	private Segment prevSeg;
	
	HashMap<String, Object> calculatedData;
	Date createdAt;
	
	public Segment(HashMap<String, Object> map) {
		calculatedData = map;
		createdAt = new Date();
	}
	
	public Segment getNextSeg() {
		return nextSeg;
	}
	
	public void setNextSeg(Segment nextSeg) {
		this.nextSeg = nextSeg;
	}
	
	public Segment getPrevSeg() {
		return prevSeg;
	}
	
	public void setPrevSeg(Segment prevSeg) {
		this.prevSeg = prevSeg;
	}
	
	public Object getDataObject(String item) {
		if (calculatedData.containsKey(item)) {
			return calculatedData.get(item);
		} else {
			return null;
		}
	}
	
	public void addDataObject(String name, Object item) {
		calculatedData.put(name, item);
	}
	
}
