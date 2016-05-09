package edu.rit.se.beepbrake.constants;

// Created by richykapadia on 2/16/16.
@SuppressWarnings("HardCodedStringLiteral")
public interface SegmentConstants {

    // SEGMENT KEYS
    String
        // Doubles
        ACCEL_X = "XAcl",
        ACCEL_Y = "YAcl",
        ACCEL_Z = "ZAcl",
        GPS_LAT = "lat",
        GPS_LNG = "lng",
        GPS_SPD = "spd",

        //opencv.rect[]
        CAR_POSITIONS = "carPos",

        FRAME = "frame",

        // MOCK JSON PARSING
        MOCK_SEGMENT_JSON = "mock-segment-data",
        CAR_POS_X = "bottomLeft_x",
        CAR_POS_Y = "bottomLeft_y",
        CAR_POS_WIDTH = "Rect_Width",
        CAR_POS_HEIGHT = "Rect_Height";

    /** Max amount of time (in millis) between segments to allow before removing old ones */
    long timeDiff = 6000L;

    /** Max length (in millis) of an event */
    long maxTime = 20000L;
}
