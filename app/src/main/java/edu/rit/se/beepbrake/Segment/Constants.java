package edu.rit.se.beepbrake.Segment;

/**
 * Created by richykapadia on 2/16/16.
 */
public class Constants {

    /* SEGMENT KEYS */
    public static final String ACCEL_X = "XAcl"; //double
    public static final String ACCEL_Y = "YAcl"; //double
    public static final String ACCEL_Z = "ZAcl"; //double
    public static final String GPS_LAT = "lat"; //double
    public static final String GPS_LNG = "lng"; //double
    public static final String GPS_SPD = "spd"; //double
    public static final String CAR_POSITIONS = "carPos"; //opencv.rect[]
    public static final String FRAME = "frame";

    /* MOCK JSON PARSING */
    public static final String MOCK_SEGMENT_JSON = "mock-segment-data";
    public static final String CAR_POS_X1_JSON = "br-x";
    public static final String CAR_POS_Y1_JSON = "br-y";
    public static final String CAR_POS_X2_JSON = "tl-x";
    public static final String CAR_POS_Y2_JSON = "tl-x";


}
