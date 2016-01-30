package edu.rit.se.beepbrake;

/**
 * Created by Bradley on 1/11/2016.
 */
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.rit.se.beepbrake.buffer.BufferManager;

public class SegmentSync {
    private BufferManager buf;

    ConcurrentHashMap<String, ArrayList<Object>> aggData;
    ConcurrentHashMap<String, Object> singleData;

    public SegmentSync(BufferManager bm) {
        buf = bm;
        aggData = new ConcurrentHashMap<String, ArrayList<Object>>();
        singleData = new ConcurrentHashMap<String, Object>();
    }


    public synchronized void makeSegment() {
        ConcurrentHashMap<String, ArrayList<Object>> tempAgg = new ConcurrentHashMap<>(aggData); // Proper Copy
        aggData = new ConcurrentHashMap<String, ArrayList<Object>>();

        ConcurrentHashMap<String, Object> tempSing = new ConcurrentHashMap<>(singleData);
        singleData = new ConcurrentHashMap<String, Object>();

        HashMap<String, Object> segMap = new HashMap<String, Object>();

        Iterator itAgg = tempAgg.entrySet().iterator();
        while (itAgg.hasNext()) {
            Map.Entry pair = (Map.Entry) itAgg.next();
            String name = pair.getKey().toString();
            ArrayList<Object> data = tempAgg.get(pair.getKey());
            Double avgData = 0.0;

            for (int i = 0; i < data.size(); i++) {
                avgData += ((Number) data.get(i)).doubleValue();
            }
            avgData = avgData / data.size();
            segMap.put(name, avgData);
        }

        Iterator itSing = tempSing.entrySet().iterator();
        while (itSing.hasNext()) {
            Map.Entry pair = (Map.Entry) itSing.next();

            segMap.put(pair.getKey().toString(), tempSing.get(pair.getKey()));
        }
        Segment seg = new Segment(segMap);
        buf.addSegment(seg);
    }

    public void UpdateDataAgg(HashMap<String, Object> map) {
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

        makeSegment();
    }

    public void UpdateDataSingle(HashMap<String, Object> map) {
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            singleData.put(pair.getKey().toString(), pair.getValue());
        }
    }

    protected void onResume(){
        aggData = new ConcurrentHashMap<String, ArrayList<Object>>();
        singleData = new ConcurrentHashMap<String, Object>();
    }

    protected void onPause(){
        aggData = null;
        singleData = null;
    }
}
