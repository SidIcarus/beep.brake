package edu.rit.se.beepbrake.annotations;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@formatter:off
@IntDef({PreferenceType.NULL, PreferenceType.ALL, PreferenceType.BOOL, PreferenceType.DATE,
         PreferenceType.FLOAT, PreferenceType.INT, PreferenceType.LONG, PreferenceType.STR,
         PreferenceType.STR_SET, PreferenceType.TIME_ZONE, PreferenceType.INVALID})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE})
//@formatter:on
public @interface PreferenceType {
    int NULL = 0, ALL = 1, BOOL = 2, DATE = 3, FLOAT = 4,
        INT = 5, LONG = 6, STR = 7, STR_SET = 8, TIME_ZONE = 9, INVALID = -1;
}
