package pers.clare.bufferid.manager;

import pers.clare.bufferid.exception.FormatRuntimeException;

public abstract class AbstractIdManager implements IdManager {
    @Override
    public long increment(String id, String prefix, long incr) {
        long num = doIncrement(id, prefix, incr);
        if (num < 0) {
            throw new FormatRuntimeException("id:\"%s\" and prefix:\"%s\" number too large", id, prefix);
        }
        return num;
    }

    abstract protected long doIncrement(String id, String prefix, long incr);

    public static void main(String[] args) {
        System.out.println(Long.MAX_VALUE + Long.MAX_VALUE);
    }
}
