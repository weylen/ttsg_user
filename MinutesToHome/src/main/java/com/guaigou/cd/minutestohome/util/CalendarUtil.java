package com.guaigou.cd.minutestohome.util;
import com.guaigou.cd.minutestohome.http.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by weylen on 2016-08-16.
 */
public class CalendarUtil {

    public static final String getStandardDate(int day, int month, int year){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return format.format(calendar.getTime());
    }

    public static final String getStandardTime(int minute, int hour){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return format.format(calendar.getTime());
    }

    public static final String getStandardDateTime(Calendar calendar){
        if (calendar != null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            return format.format(calendar.getTime());
        }
        return Constants.EMPTY_STRING;
    }

    /**
     * 日期格式 yyyy-MM-dd HH:mm
     * @param s1
     * @param s2
     * @return
     */
    public static int compare(String s1, String s2){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        try {
            Date d1 = format.parse(s1);
            Date d2 = format.parse(s2);
            return d1.compareTo(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }


}
