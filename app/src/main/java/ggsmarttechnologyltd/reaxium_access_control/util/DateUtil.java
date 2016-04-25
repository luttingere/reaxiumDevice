package ggsmarttechnologyltd.reaxium_access_control.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by ULISES HARRIS on 27/05/2015.
 */
public class DateUtil {

    public static final String DATE_FORMAT = "dd-M-yyyy hh:mm:ss";
    public static final String DATE_FORMAT_COMPLETE = "yyyy-MM-dd'T'HH:mm:ssz";
    public static final String DATE_FORMAT_VIDEOS = "dd/MM/yyyy";
    public static final String DATE_FORMAT_VIDEOS_EN = "MM/dd/yyyy";
    public static final String DATE_FORMAT_FIGHTER = "yyyy-MM-dd";
    public static final String DATE_FORMAT_FULL = "EEE MMM d HH:mm:ss zzz yyyy";

    public static String getTimeText(Date date) {
        Calendar calendar = Calendar.getInstance();

        long diff = calendar.getTimeInMillis() - date.getTime();
        long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long diffHours = TimeUnit.MILLISECONDS.toHours(diff);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diff);

        if (diffMinutes <= 0) {
            return diffSeconds + " seconds ago";
        } else if (diffMinutes > 0 & diffHours <= 0) {
            return diffMinutes + " mins ago";
        } else if (diffHours > 0 & diffInDays <= 0) {
            return diffHours + " hours ago";
        }

        return diffInDays + " days go";
    }


    public static String getVideoDate(String date){
        date = date.replace("+00:00","+0000");
        SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT_COMPLETE);

        Date d = null;
        try {
            d = f.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long milliseconds = d.getTime();
        f = new SimpleDateFormat(DATE_FORMAT_VIDEOS);
        String dateText = f.format(milliseconds);
        return dateText;
    }

    public static String getVideoDate(String date, String language){
        String dateText = date;
        date = date.replace("+00:00","+0000");
        SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT_COMPLETE);

        Date d = null;
        try {
            d = f.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long milliseconds = d.getTime();
        if(language.equalsIgnoreCase("EN")) {
            f = new SimpleDateFormat(DATE_FORMAT_VIDEOS_EN);
        } else {
            f = new SimpleDateFormat(DATE_FORMAT_VIDEOS);
        }

        dateText = f.format(milliseconds);

        return dateText;
    }

    public static String formatDate(Date date){
        String formatDate="";

        SimpleDateFormat formato = new SimpleDateFormat(DateUtil.DATE_FORMAT_VIDEOS);
        formatDate = formato.format(date);
        return formatDate;
    }

    public static String formatDateEN(Date date){
        String formatDate="";

        SimpleDateFormat formato = new SimpleDateFormat(DateUtil.DATE_FORMAT_VIDEOS_EN);
        formatDate = formato.format(date);
        return formatDate;
    }

    public static int getAge (int year, int month, int day) {

        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, age;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day);
        age = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --age;
        }
        if(age < 0)
            throw new IllegalArgumentException("Age < 0");
        return age;
    }

}
