package edu.rit.se.beepbrake.segment;

// v

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import edu.rit.se.beepbrake.buffer.BufferManager;

public class SegmentSync {
    ConcurrentHashMap<String, ArrayList<Object>> aggData;
    ConcurrentHashMap<String, Object> singleData;
    private BufferManager buf;
    private ReentrantLock lock = new ReentrantLock();
    private boolean isRunning;

    public SegmentSync(BufferManager bm) {
        buf = bm;
        aggData = new ConcurrentHashMap<String, ArrayList<Object>>();
        singleData = new ConcurrentHashMap<String, Object>();
        isRunning = true;
    }

    public synchronized void makeSegment(Mat img, HashMap<String, Object> camData) {
        this.lock.lock();
        this.UpdateDataSingle(camData);

        ConcurrentHashMap<String, ArrayList<Object>> tempAgg = new ConcurrentHashMap<>(aggData); // Proper Copy
        aggData = new ConcurrentHashMap<String, ArrayList<Object>>();

        ConcurrentHashMap<String, Object> tempSing = new ConcurrentHashMap<>(singleData);
        singleData = new ConcurrentHashMap<String, Object>();

        ConcurrentHashMap<String, Object> segMap = new ConcurrentHashMap<String, Object>();

        Iterator itAgg = tempAgg.entrySet().iterator();
        while (itAgg.hasNext()) {
            Map.Entry pair = (Map.Entry) itAgg.next();
            String name = pair.getKey().toString();
            ArrayList<Object> data = tempAgg.get(pair.getKey());
            Double avgData = 0.0;

            for (int i = 0; i < data.size(); i++) avgData += ((Number) data.get(i)).doubleValue();

            avgData = avgData / data.size();
            segMap.put(name, avgData);
        }

        Iterator itSing = tempSing.entrySet().iterator();
        while (itSing.hasNext()) {
            Map.Entry pair = (Map.Entry) itSing.next();

            segMap.put(pair.getKey().toString(), tempSing.get(pair.getKey()));
        }

        Segment seg = new Segment(segMap, img);
        buf.addSegment(seg);
        lock.unlock();
    }

    public void UpdateDataAgg(HashMap<String, Object> map) {
        lock.lock();
        Iterator it = map.entrySet().iterator();

        //Iterate over all items to be added
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (aggData.containsKey(pair.getKey())) {
                aggData.get(pair.getKey()).add(pair.getValue());
            } else {
                ArrayList<Object> a = new ArrayList<Object>();
                a.add(pair.getValue());
                aggData.put(pair.getKey().toString(), a);
            }
        }
        lock.unlock();

        //makeSegment(); Used for demonstration purposes with android sensors
    }

    public void UpdateDataSingle(HashMap<String, Object> map) {
        lock.lock();
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            singleData.put(pair.getKey().toString(), pair.getValue());
        }
        lock.unlock();
    }

    public void onResume() {
        isRunning = true;
        aggData = new ConcurrentHashMap<String, ArrayList<Object>>();
        singleData = new ConcurrentHashMap<String, Object>();
    }

    public void onPause() {
        isRunning = false;
        aggData = null;
        singleData = null;
    }

    public boolean isRunning() { return isRunning; }
}
