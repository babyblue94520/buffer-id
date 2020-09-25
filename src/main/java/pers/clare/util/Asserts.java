package pers.clare.util;

import pers.clare.bufferid.exception.FormatIllegalArgumentException;

public interface Asserts {
    String NotEmptyMessage = "%s can't be empty";
    String NotNullMessage = "%s can't be null";

    static void notEmpty(Object object, String name) {
        if (object == null) {
            throw new FormatIllegalArgumentException(NotEmptyMessage, name);
        }
    }

    static void notNull(Object object, String name) {
        if (object == null) {
            throw new FormatIllegalArgumentException(NotNullMessage, name);
        }
    }
}
