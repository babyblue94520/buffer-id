package pers.clare.bufferid.util;

public abstract class IdUtil {
    public static String addZero(String prefix, String number, int length) {
        char[] result = new char[length];
        int l = length - number.length();
        prefix.getChars(0, prefix.length(), result, 0);
        number.getChars(0, number.length(), result, l);
        for (int i = prefix.length(); i < l; i++) {
            result[i] = '0';
        }
        return new String(result);
    }
}
