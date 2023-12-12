package com.holland.infrastructure.kit.kit;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public class DateKit {
    public static final String YYYY = "yyyy";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHH = "yyyyMMddHH";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DTF_YYYY_MM_DD = DateTimeFormatter.ofPattern(YYYY_MM_DD);
    public static final DateTimeFormatter DTF_HH_MM_SS = DateTimeFormatter.ofPattern(HH_MM_SS);
    public static final DateTimeFormatter DTF_YYYYMMDDHHMMSS = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS);
    public static final DateTimeFormatter DTF_YYYYMMDDHH = DateTimeFormatter.ofPattern(YYYYMMDDHH);
    public static final DateTimeFormatter DTF_YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);

    public static String format(Temporal temporal, DateTimeFormatter formatter) {
        return formatter.format(temporal);
    }
}
