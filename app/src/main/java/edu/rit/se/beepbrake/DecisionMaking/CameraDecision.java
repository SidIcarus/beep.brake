package edu.rit.se.beepbrake.decisionMaking;

// Created by Ryan 2/8/16

import android.util.Log;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import edu.rit.se.beepbrake.segment.Constants;
import edu.rit.se.beepbrake.buffer.BufferManager;

public class CameraDecision extends Decision {

    private static final String TAG = "CAMERA-DECISION";

    private final Rect curr, prev;
    private final Point carRear;

    public CameraDecision(DecisionManager decMan, BufferManager bufMan) {
        super(decMan, bufMan);
        curr = new Rect(0, 0, 0, 0);
        prev = new Rect(0, 0, 0, 0);
        carRear = new Point(0, 0);
    }

    public void run() {
        Log.d(TAG, "Running");

        //Analysis loop
        while (running) {
            if (!requestSegment()) continue;

            //Check for position data, lol maybe change json
            // TODO: "pos" : [x1,y1,x2,y2] (segment writes obj address)
            if (curSeg.getDataObject(Constants.CAR_POS_X) != null && curSeg.getDataObject(Constants.CAR_POS_Y) != null &&
                    curSeg.getDataObject(Constants.CAR_POS_WIDTH) != null && curSeg.getDataObject(Constants.CAR_POS_HEIGHT) != null) {

                int x = (int) curSeg.getDataObject(Constants.CAR_POS_X);
                int y = (int) curSeg.getDataObject(Constants.CAR_POS_Y);
                int w = (int) curSeg.getDataObject(Constants.CAR_POS_WIDTH);
                int h = (int) curSeg.getDataObject(Constants.CAR_POS_HEIGHT);
                double[] data = new double[]{x, y, w, h};
                curr.set(data);

                carRear.x = curr.br().x - curr.width / 2;
                carRear.y = curr.br().y;

                int y2 = curSeg.getImg().height();
                int x2 = curSeg.getImg().width() / 2;
                double dist = Math.sqrt(Math.pow(carRear.x - x2, 2) + Math.pow(carRear.y - y2, 2));

                if (curr.area() > (curSeg.getImg().size().area() / 6) && dist < curSeg.getImg().height() / 5) warn();

                prev.set(data);
            }
        }
        Log.d(TAG, "Finished");
    }

    protected void warn(){
        curSeg.addDataObject("CameraWarning", "true");
        super.warn();
    }
}
