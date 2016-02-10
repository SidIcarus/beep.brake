package edu.rit.se.beepbrake;

/**
 * Created by Bradley on 1/11/2016.
 */
import java.util.Date;
import java.util.HashMap;
import org.opencv.core.Mat;

public class Segment {

    private Segment nextSeg;
    private Segment prevSeg;
    private Mat img;

    HashMap<String, Object> calculatedData;
    long createdAt;

    public Segment(HashMap<String, Object> map, Mat img) {
        calculatedData = map;
        createdAt = new Date().getTime();
        this.img = img;
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
        return calculatedData.get(item);
    }

    public void addDataObject(String name, Object item) {
        calculatedData.put(name, item);
    }

    public Mat getImg() {
        return this.img;
    }
};
