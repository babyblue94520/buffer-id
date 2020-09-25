package pers.clare.bufferid.manager;

public interface IdManager {

    public long next(String id, String prefix);

    public String next(String id, String prefix, int length);

    public long increment(String id, String prefix, long incr);

    public boolean exist(String id, String prefix);

    public int save(String id, String prefix);

    public int remove(String id, String prefix);
}
