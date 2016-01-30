package edu.rit.se.beepbrake;

/**
 * Created by Bradley on 1/11/2016.
 */
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Segment {

    private Segment nextSeg;
    private Segment prevSeg;

    HashMap<String, Object> calculatedData;
    long createdAt;

    public Segment(HashMap<String, Object> map) {
        calculatedData = map;
        createdAt = new Date().getTime();
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
};
