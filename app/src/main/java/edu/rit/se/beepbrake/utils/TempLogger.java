package edu.rit.se.beepbrake.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by richykapadia on 1/11/16.
 * <p/>
 * Testing/logging performance concerns
 * 1) analyzing the most recent frame
 * 2) number of frames skipped
 */
public class TempLogger {

    public static boolean LOGGING = false;

    public static String TAG = "TempLogger";
    //TODO If we use this logger separate constants out to another file
    //COUNTS
    public static String TOTAL_FRAMES = "Total Frames";
    public static String ANALYZED_FRAMES = "Frames Analyzed";
    //MARK BY TIME
    public static String HAAR_TIME = "Haar Detection";
    //MARK BY VALUE
    public static String SLACK_TIME = "Slack Time";

    //Timing logs
    public static ConcurrentHashMap<String, Long> runningLogs = new ConcurrentHashMap<String, Long>();
    public static ConcurrentHashMap<String, ArrayList<Long>> storedLogs = new ConcurrentHashMap<String, ArrayList<Long>>();

    //log count checks
    public static int MAX_LOG_LEN = 10000;
    public static int numLogs = 0;
    public static boolean bPrintingLogs = false;

    //Value Logs
    /* Since values in a hash are immutable
     * AtomicIntegers are used to avoid excessive
     * putting/removing to/from the hash */
    public static HashMap<String, AtomicInteger> countLogs = new HashMap<String, AtomicInteger>();


    /**
     * The first time the function is called with a unique mark name
     * the "Start Time" is recorded
     * The second time the "Stop Time" is taken
     * The duration is calculated and placed in the storedLog
     *
     * @param markName - unique identifier for the measure to be logged
     */
    public static synchronized void addMarkTime(String markName) {
        if (!LOGGING) return;

        validateLogLen();
        Long l = System.currentTimeMillis();
        if (!runningLogs.containsKey(markName)) runningLogs.put(markName, l);
        else {
            //calc diff and store
            Long durr = l - runningLogs.get(markName);
            if (!storedLogs.containsKey(markName)) {
                storedLogs.put(markName, new ArrayList<Long>());
            }
            storedLogs.get(markName).add(durr);
            numLogs++;
            //clear running log
            runningLogs.remove(markName);
        }
    }

    /**
     * @param markName
     * @param value
     */
    public static synchronized void addValueMark(String markName, Long value) {
        if (!LOGGING) return;

        validateLogLen();
        //place directly in stored
        if (!storedLogs.containsKey(markName)) storedLogs.put(markName, new ArrayList<Long>());

        storedLogs.get(markName).add(value);
        numLogs++;
    }

    /**
     * increment a count by 1 every time this is called per unique markName
     *
     * @param markName - unique identifier for count
     */
    public static synchronized void incrementCount(String markName) {
        if (!LOGGING) return;

        validateLogLen();
        if (!countLogs.containsKey(markName)) {
            AtomicInteger aInt = new AtomicInteger(0);
            countLogs.put(markName, aInt);
            numLogs++;
        }
        countLogs.get(markName).incrementAndGet();
    }


    private static synchronized void validateLogLen() {
        if (!LOGGING) return;

        if (numLogs >= MAX_LOG_LEN) {
            printLogs();
            //clear logs
            for (String key : storedLogs.keySet()) storedLogs.get(key).clear();

            storedLogs.clear();
            numLogs = 0;
        }
    }

    public static synchronized void printLogs() {
        if (!LOGGING) return;

        bPrintingLogs = true;
        Log.d(TAG, "Num Logs: " + numLogs);
        //total frames counts
        int total = 0;
        int analyzed = 0;
        int missed = 0;
        if (countLogs.containsKey(TOTAL_FRAMES)) total = countLogs.get(TOTAL_FRAMES).get();
        if (countLogs.containsKey(ANALYZED_FRAMES)) analyzed = countLogs.get(ANALYZED_FRAMES).get();

        missed = total - analyzed;

        Log.d(TAG, "------------------FRAME COUNTS------------------");
        Log.d(TAG, TOTAL_FRAMES + ": " + total);
        Log.d(TAG, ANALYZED_FRAMES + ": " + analyzed);
        Log.d(TAG, "Missed: " + missed);

        ArrayList<Long> haarTime = new ArrayList<>();
        if (storedLogs.containsKey(HAAR_TIME)) haarTime = storedLogs.get(HAAR_TIME);

        Long min = 0l;
        Long max = 0l;
        Long avg = 0l;
        Long med = 0l;

        Collections.sort(haarTime);
        if (!haarTime.isEmpty()) {
            min = haarTime.get(0);
            max = haarTime.get(haarTime.size() - 1);
            med = haarTime.get(haarTime.size() / 2);
            for (Long l : haarTime) avg += l;

            avg = avg / haarTime.size();
        }

        Log.d(TAG, "------------------ Haar Time Stats ------------------");
        Log.d(TAG, "Min: " + min);
        Log.d(TAG, "Max: " + max);
        Log.d(TAG, "Med: " + med);
        Log.d(TAG, "Avg: " + avg);

        //Calc Slack Time

        Log.d(TAG, "------------------ Car Slack Time Stats ------------------");
        ArrayList<Long> slackDur = new ArrayList<>();
        if (storedLogs.containsKey(SLACK_TIME + "CarDetector")) {
            slackDur = storedLogs.get(SLACK_TIME + "CarDetector");
            printMeasures(slackDur);
        }

        Log.d(TAG, "------------------ Lane Slack Time Stats ------------------");
        slackDur = new ArrayList<>();
        if (storedLogs.containsKey(SLACK_TIME + "LaneDetector")) {
            slackDur = storedLogs.get(SLACK_TIME + "LaneDetector");
            printMeasures(slackDur);
        }

        //clear logs
        for (String key : storedLogs.keySet()) storedLogs.get(key).clear();

        storedLogs.clear();

        bPrintingLogs = false;
    }


    public static boolean isPrintingLogs() {
        return bPrintingLogs;
    }


    public static void printMeasures(ArrayList<Long> durr) {
        if (!LOGGING) return;

        //Stats for slack time
        Collections.sort(durr);
        long min = 0, max = 0, med = 0, avg = 0;
        if (!durr.isEmpty() && durr.size() != 0) {
            min = durr.get(0);
            max = durr.get(durr.size() - 1);
            med = durr.get(durr.size() / 2);

            avg = 0l;
            for (Long l : durr) avg += l;

            avg = avg / durr.size();
        }

        Log.d(TAG, "Min: " + min);
        Log.d(TAG, "Max: " + max);
        Log.d(TAG, "Med: " + med);
        Log.d(TAG, "Avg: " + avg);
    }
}
