package pers.clare.bufferid.manager.impl;


import pers.clare.bufferid.manager.IdManager;
import pers.clare.bufferid.util.IdUtil;
import pers.clare.util.Asserts;

import java.util.HashMap;
import java.util.Map;

public class LocalIdManager implements IdManager {
    private static final Object lock = new Object();
    private static final Map<String, Map<String, LocalId>> groups = new HashMap<String, Map<String, LocalId>>();

    @Override
    public long next(String group, String prefix) {
        return increment(group, prefix, 1);
    }

    @Override
    public String next(String group, String prefix, int length) {
        return IdUtil.addZero(prefix, String.valueOf(increment(group, prefix, 1)), length);
    }

    @Override
    public long increment(String group, String prefix, int incr) {
        Asserts.notNull(group, "group" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        Map<String, LocalId> g = groups.get(group);
        if (g == null) {
            throw new RuntimeException("group:" + group + " not found");
        }
        LocalId id = g.get(prefix);
        if (id == null) {
            throw new RuntimeException("prefix:" + prefix + " not found");
        }
        synchronized (id) {
            return id.count += incr;
        }
    }

    @Override
    public boolean exist(String group, String prefix) {
        Asserts.notNull(group, "group" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        Map<String, LocalId> g = groups.get(group);
        if (g == null) {
            return false;
        }
        return g.get(prefix) == null ? false : true;
    }

    @Override
    public int save(String group, String prefix) {
        Asserts.notNull(group, "group" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        if (exist(group, prefix)) {
            return 0;
        }
        synchronized (lock) {
            if (exist(group, prefix)) {
                return 0;
            }
            Map<String, LocalId> g = groups.get(group);
            if (g == null) {
                groups.put(group, (g = new HashMap<String, LocalId>()));
                g.put(prefix, new LocalId());
            } else {
                if (g.get(prefix) == null) {
                    g.put(prefix, new LocalId());
                }else{
                    return 0;
                }
            }
        }
        return 1;
    }

    @Override
    public int remove(String group, String prefix) {
        Asserts.notNull(group, "group" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        if (!exist(group, prefix)) {
            return 0;
        }
        synchronized (lock) {
            if (!exist(group, prefix)) {
                return 0;
            }
            Map<String, LocalId> g = groups.get(group);
            if (g == null) {
                return 0;
            }
            g.remove(g);
        }
        return 1;
    }
}

class LocalId {
    long count = 0;
}