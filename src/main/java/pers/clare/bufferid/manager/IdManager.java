package pers.clare.bufferid.manager;

public interface IdManager {

    public long next(String group, String prefix);

    public String next(String group, String prefix, int length);

    public long increment(String group, String prefix, int incr);

    public boolean exist(String group, String prefix);

    public int save(String group, String prefix);

    public int remove(String group, String prefix);
}
