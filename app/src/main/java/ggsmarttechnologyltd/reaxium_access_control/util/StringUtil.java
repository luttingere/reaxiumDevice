package ggsmarttechnologyltd.reaxium_access_control.util;

/**
 * Created by ULISES HARRIS on 22/05/2015.
 */
public class StringUtil {

    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static String numberPattern = "[0-9]+";

    public static boolean isValidEmail(String email) {
        if (email.matches(emailPattern))
            return true;
        return false;
    }

    public static boolean isValidNumber(String text) {
        if (text.matches(numberPattern))
            return true;
        return false;
    }


    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
