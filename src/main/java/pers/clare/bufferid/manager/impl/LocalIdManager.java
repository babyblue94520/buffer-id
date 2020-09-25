package pers.clare.bufferid.manager.impl;


import pers.clare.bufferid.manager.AbstractIdManager;
import pers.clare.bufferid.util.IdUtil;
import pers.clare.util.Asserts;

import java.util.HashMap;
import java.util.Map;

public class LocalIdManager extends AbstractIdManager {
    private static final Map<String, Map<String, LocalId>> ids = new HashMap<String, Map<String, LocalId>>();

    @Override
    public long next(String id, String prefix) {
        return increment(id, prefix, 1);
    }

    @Override
    public String next(String id, String prefix, int length) {
        return IdUtil.addZero(prefix, String.valueOf(increment(id, prefix, 1)), length);
    }

    @Override
    protected long doIncrement(String id, String prefix, long incr) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");
        Map<String, LocalId> g = ids.get(id);
        if (g == null) {
            throw new RuntimeException("id:" + id + " not found");
        }
        LocalId localId = g.get(prefix);
        if (localId == null) {
            throw new RuntimeException("prefix:" + prefix + " not found");
        }
        synchronized (localId) {
            return localId.count += incr;
        }
    }

    @Override
    public boolean exist(String id, String prefix) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");
        Map<String, LocalId> g = ids.get(id);
        if (g == null) {
            return false;
        }
        return g.get(prefix) == null ? false : true;
    }

    @Override
    public int save(String id, String prefix) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");
        if (exist(id, prefix)) {
            return 0;
        }
        synchronized (this) {
            if (exist(id, prefix)) {
                return 0;
            }
            Map<String, LocalId> g = ids.get(id);
            if (g == null) {
                ids.put(id, (g = new HashMap<>()));
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
    public int remove(String id, String prefix) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");
        if (!exist(id, prefix)) {
            return 0;
        }
        synchronized (this) {
            if (!exist(id, prefix)) {
                return 0;
            }
            Map<String, LocalId> g = ids.get(id);
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
