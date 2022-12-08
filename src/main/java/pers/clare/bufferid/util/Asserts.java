package pers.clare.bufferid.util;

import pers.clare.bufferid.exception.BufferIdIllegalArgumentException;

public interface Asserts {
    static void notEmpty(Object object, String name) {
        if (object == null) {
            throw new BufferIdIllegalArgumentException("%s can't be empty", name);
        }
    }

    static void notNull(Object object, String name) {
        if (object == null) {
            throw new BufferIdIllegalArgumentException("%s can't be null", name);
        }
    }
}
