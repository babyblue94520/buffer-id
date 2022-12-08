package pers.clare.bufferid.manager;

public interface IdManager {

    long next(String id, String prefix);

    long increment(String id, String prefix, long incr);

    boolean exist(String id, String prefix);

    int save(String id, String prefix);

    int remove(String id, String prefix);
}
