package edu.rit.se.beepbrake.Segment;

/**
 * Created by Bradley on 1/11/2016.
 */

import org.opencv.core.Mat;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Segment {

    private final Mat img;
    ConcurrentHashMap<String, Object> calculatedData;
    long createdAt;
    private Segment nextSeg;
    private Segment prevSeg;

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

    public Set<String> getKeys() {
        return calculatedData.keySet();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public Mat getImg() {
        return this.img;
    }
}

