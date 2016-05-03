package edu.rit.se.beepbrake.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Preference {

    String FILE_NAME = "beep_brake_settings";

    boolean defaultBoolean() default false;

    float defaultFloat() default 0;

    int defaultInt() default 0;

    long defaultLong() default 0;

    int defaultResource() default 0;

    String defaultString() default "";

    boolean exportable() default true;

    boolean hasDefault() default false;

    @PreferenceType int type() default PreferenceType.NULL;
}
