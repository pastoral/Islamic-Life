package com.harbingerstudio.islamiclife.islamiclife.utils;

import org.joda.time.Chronology;
import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.IslamicChronology;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by User on 5/4/2017.
 */

public class ArabicUtils {
    Chronology iso, hijri;
    public String getArabicDate(String date) {
        iso = ISOChronology.getInstanceUTC();
        hijri = IslamicChronology.getInstanceUTC();
        LocalDate todayIso = new LocalDate(2013, 03, 31, iso);
        LocalDate todayHijri = new LocalDate(todayIso.toDateTimeAtStartOfDay(),
                hijri);
        return todayHijri.toString();
    }

    public  String getDateFromTimestamp(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }

    public String splitDate(String str){
        String[] strArray = new String[3];
        strArray[0] = str.substring(0,4);
        strArray[1] = str.substring(5,7);
        strArray[2] = str.substring(8);
        String date = strArray[2]+"-"+strArray[1]+"-"+strArray[0];
        return  date;
    }
}

