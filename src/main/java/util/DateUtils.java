package util;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * thread Safe
 * Created by ThomasYu on 2018/6/21
 */
public class DateUtils {

    public static final String PATTERN_YMDHMSSS = "yyyyMMddHHmmssSSS";
    public static final String PATTERN_YMDHMS = "yyyyMMddHHmmss";
    public static final String PATTERN_YMD = "yyyy-MM-dd";
    public static final String PATTERN_MD = "MM-dd";
    public static final String PATTERN_MD_SPOT = "yyyy.MM.dd";
    public static final String PATTERN_MD_SPOT_HMS = "yyyy.MM.dd HH:mm:ss";
    public static final String PATTERN_YMD_FOR_LINE = "yyyyMMdd";
    public static final String PATTERN_Y_M_D_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_YMD_CHINESE_UNIT = "yyyy年MM月dd日";
    public static final String PATTERN_YMD_START = "yyyy-MM-dd 00:00:00";
    public static final String PATTERN_DMY = "dd-MM-yyyy";
    public static final String PATTERN_SLASH_DMY = "dd/MM/yyyy";
    public static final String PATTERN_Y_M_D_HM = "yyyy-MM-dd HH:mm";

    public static DateTimeFormatter FORMATTER_YMDHMSSS = DateTimeFormat.forPattern(PATTERN_YMDHMSSS);
    public static DateTimeFormatter FORMATTER_YMDHMS = DateTimeFormat.forPattern(PATTERN_YMDHMS);
    public static DateTimeFormatter FORMATTER_YMD = DateTimeFormat.forPattern(PATTERN_YMD);
    public static DateTimeFormatter FORMATTER_MD = DateTimeFormat.forPattern(PATTERN_MD);
    public static DateTimeFormatter FORMATTER_MD_SPOT = DateTimeFormat.forPattern(PATTERN_MD_SPOT);
    public static DateTimeFormatter FORMATTER_MD_SPOT_HMS = DateTimeFormat.forPattern(PATTERN_MD_SPOT_HMS);
    public static DateTimeFormatter FORMATTER_YMD_FOR_LINE = DateTimeFormat.forPattern(PATTERN_YMD_FOR_LINE);
    public static DateTimeFormatter FORMATTER_Y_M_D_HMS = DateTimeFormat.forPattern(PATTERN_Y_M_D_HMS);
    public static DateTimeFormatter FORMATTER_YMD_CHINESE_UNIT = DateTimeFormat.forPattern(PATTERN_YMD_CHINESE_UNIT);
    public static DateTimeFormatter FORMATTER_YMD_START = DateTimeFormat.forPattern(PATTERN_YMD_START);
    public static DateTimeFormatter FORMATTER_DMY = DateTimeFormat.forPattern(PATTERN_DMY);
    public static DateTimeFormatter FORMATTER_SLASH_DMY = DateTimeFormat.forPattern(PATTERN_SLASH_DMY);
    public static DateTimeFormatter FORMATTER_Y_M_D_HM = DateTimeFormat.forPattern(PATTERN_Y_M_D_HM);

//    public static Date parseDate(String text, DateTimeFormatter formatter) {
//        DateTime dateTime = parseDateTime(text, formatter);
//        if (dateTime != null) {
//            return dateTime.toDate();
//        }
//        return null;
//    }

//    public static DateTime parseDateTime(String text, DateTimeFormatter formatter) {
//        if (text != null && !text.isEmpty() && !StringConstant.NULL_STRING.equals(text)) {
//            return formatter.parseDateTime(text);
//        }
//        return null;
//    }

    public static String format(Date date, DateTimeFormatter formatter) {
        if (date != null) {
            return formatter.print(date.getTime());
        }
        return null;
    }

    public static String format(DateTimeFormatter formatter) {
        return formatter.print(System.currentTimeMillis());
    }


    public static Date calculateDate(Date fromDate, int type, int interval) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.add(type, interval);
        return calendar.getTime();
    }

    public static int getSpecifyField(Date date, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(field);
    }

    public static int getBetweenDays(Date from) {
        if (from != null) {
            DateTime fromD = new DateTime(from).withTimeAtStartOfDay();
            DateTime toD = DateTime.now().withTimeAtStartOfDay();
            return Days.daysBetween(fromD, toD).getDays();
        }
        return 0;
    }

    public static int getBetweenDays(Date from, Date to) {
        if (from != null && to != null) {
            DateTime fromD = new DateTime(from).withTimeAtStartOfDay();
            DateTime toD = new DateTime(to).withTimeAtStartOfDay();
            return Days.daysBetween(fromD, toD).getDays();
        }
        return 0;
    }

    public static int getBetweenDays(DateTime from, DateTime to) {
        if (from != null && to != null) {
            return Days.daysBetween(from.withTimeAtStartOfDay(), to.withTimeAtStartOfDay()).getDays();
        }
        return 0;
    }

    public static int getBetweenDays(DateTime from) {
        if (from != null) {
            return Days.daysBetween(from.withTimeAtStartOfDay(), DateTime.now().withTimeAtStartOfDay()).getDays();
        }
        return 0;
    }

    public static Date addSecond(Date date, int second) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(Calendar.SECOND, second);
        return calender.getTime();
    }

    public static Date addMinute(Date date, int minute) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(Calendar.MINUTE, minute);
        return calender.getTime();
    }

    public static Date addHour(Date date, int hour) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(Calendar.HOUR_OF_DAY, hour);
        return calender.getTime();
    }

    public static Date addDay(Date date, int day) {
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(Calendar.DAY_OF_YEAR, day);
        return calender.getTime();
    }


    public static final String ZONEID_SHANGHAI = "Asia/Shanghai";
    public static final String ZONEID_JAKARTA = "Asia/Jakarta";
    public static final String ZONEID_COLOMBO = "Asia/Colombo";

    /**
     * 从当前的北京时间 转为新时区的时间
     *
     * @param date      当前的北京时间
     * @param newZoneId 新时区 timezone id
     */
    public static Date getDateByTimeZone(Date date, String newZoneId) {
        return getDateByTimeZone(date, ZONEID_SHANGHAI, newZoneId);
    }

    /**
     * 从当前旧的时间 转为新时区的时间
     *
     * @param date      当前旧时区的时间
     * @param oldZoneId 旧时区的 timezone id
     * @param newZoneId 新时区的 timezone id
     * @return
     */
    public static Date getDateByTimeZone(Date date, String oldZoneId, String newZoneId) {
        TimeZone oldZone = TimeZone.getTimeZone(oldZoneId);
        TimeZone newZone = TimeZone.getTimeZone(newZoneId);
        return getDateByTimeZone(date, oldZone, newZone);
    }

    public static Date getDateByTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
        Date dateTmp = null;
        if (date == null) {
            return dateTmp;
        }
        int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
        dateTmp = new Date(date.getTime() - timeOffset);
        return dateTmp;
    }

}
