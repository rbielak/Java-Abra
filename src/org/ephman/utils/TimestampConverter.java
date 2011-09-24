/**
 * Title:			A Date Formatting class <p>
 * Description:  	To make sure all dates are formatted to and from String in GMT <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */

package org.ephman.utils;

import java.util.*;
import java.text.*;
import java.sql.Timestamp;

public class TimestampConverter {

    protected static SimpleDateFormat formatter;

    protected static String DEFAULT_FORMAT;

    static {
        DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
        formatter = new SimpleDateFormat (DEFAULT_FORMAT);
		formatter.setTimeZone (java.util.TimeZone.getTimeZone("GMT"));
    }

    /** routine to format a timestamp in the default way
     *  'yyyy-MM-dd HH:mm:ss'
     *
     *  @param time the timestamp to turn into String
     */

    public static String format (Timestamp time) {
        return formatter.format (time);
    }

    /** routine to format a timestamp using the 'timeFormat'
     *
     *  @param time the timestamp to turn into String
     *  @param timeFormat a format string @link SimpleDateFormat
     *  @return String that is properly formatted
     */
    public static String format (Timestamp time, String timeFormat) {
        formatter.applyPattern(timeFormat);
        String date = formatter.format(time);
        formatter.applyPattern(DEFAULT_FORMAT);
        return date;
    }

    /** routine to parse a string in the default way
     *  'yyyy-MM-dd HH:mm:ss' to timestamp
     *
     *  @param date a string which should be turned into a timestamp
     *  @throws ParseException if the string is not parseable
     *  @return Timestamp representing the string that was passed
     */
    public static Timestamp parse (String date) throws ParseException {
        long time = formatter.parse(date).getTime();
        return new Timestamp (time);
    }

    /** routine to parse a string using the passed
     *  timeFormat to timestamp
     *
     *  @param timeFormat the format to use when parsing the given date
     *  @param date a string which should be turned into a timestamp
     *  @throws ParseException if the string is not parseable
     *  @return Timestamp representing the string that was passed
     */
    public static Timestamp parse (String date, String timeFormat) throws ParseException {
        formatter.applyPattern(timeFormat);
        Timestamp ts = new Timestamp (formatter.parse (date).getTime());
        formatter.applyPattern(DEFAULT_FORMAT);
        return ts;
    }

}
