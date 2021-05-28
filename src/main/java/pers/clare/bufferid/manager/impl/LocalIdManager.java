package pers.clare.bufferid.manager.impl;


import pers.clare.bufferid.exception.FormatRuntimeException;
import pers.clare.bufferid.manager.AbstractIdManager;
import pers.clare.bufferid.util.IdUtil;
import pers.clare.bufferid.util.Asserts;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class LocalIdManager extends AbstractIdManager {
    private static final ConcurrentMap<String, ConcurrentMap<String, LocalId>> ids = new ConcurrentHashMap<>();

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
        ConcurrentMap<String, LocalId> g = ids.get(id);
        if (g == null) throw new FormatRuntimeException("id:%s not found", id);
        LocalId localId = g.get(prefix);
        if (localId == null) throw new FormatRuntimeException("prefix:%s not found", id);
        return localId.count.getAndAdd(incr);
    }

    @Override
    public boolean exist(String id, String prefix) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");
        ConcurrentMap<String, LocalId> g = ids.get(id);
        if (g == null) return false;
        return g.get(prefix) != null;
    }

    @Override
    public int save(String id, String prefix) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");
        LocalId instance = new LocalId();
        return instance.equals(ids.computeIfAbsent(id, (key) -> new ConcurrentHashMap<>())
                .computeIfAbsent(prefix, (key) -> instance)) ? 1 : 0;
    }

    @Override
    public int remove(String id, String prefix) {
        Asserts.notNull(id, "id");
        Asserts.notNull(prefix, "prefix");
        ConcurrentMap<String, LocalId> g = ids.get(id);
        if (g == null) return 0;
        g.remove(prefix);
        return 1;
    }
}

class LocalId {
    AtomicLong count = new AtomicLong();
}
