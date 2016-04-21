package edu.rit.se.beepbrake.Segment;

/**
 * Created by Bradley on 1/11/2016.
 */
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.opencv.core.Mat;
import java.util.Iterator;
import java.util.Set;

public class Segment {

    private Segment nextSeg;
    private Segment prevSeg;
    private final Mat img;

    ConcurrentHashMap<String, Object> calculatedData;
    long createdAt;

    public Segment(ConcurrentHashMap<String, Object> map, Mat img) {
        calculatedData = map;
        createdAt = new Date().getTime();
        this.img = new Mat();
        img.copyTo(this.img);

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

    public Set<String> getKeys(){
        return calculatedData.keySet();
    }

    public long getCreatedAt(){
        return createdAt;
    }

    public Mat getImg() {
        return this.img;
    }
}

