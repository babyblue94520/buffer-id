package pers.clare.bufferid.service;

import pers.clare.bufferid.manager.IdManager;
import pers.clare.bufferid.util.Asserts;

import java.util.HashMap;
import java.util.Map;


/**
 * 所有執行序各別有一個 buffer
 * 極速產生唯一ID
 */
public class MultiBufferIdService extends AbstractBufferIdService {
    // 每個執行緒獨立的
    public static final ThreadLocal<Map<String, Map<String, BufferId>>> cache = new NamedBufferIdThreadLocal<>("BufferId Cache Holder");

    public MultiBufferIdService(IdManager idManager) {
        super(idManager);
    }

    public Long next(long minBuffer, long maxBuffer, String id, String prefix) {
        Map<String, Map<String, BufferId>> fastMap = cache.get();
        if (fastMap == null) {
            cache.set((fastMap = new HashMap<>()));
        }
        BufferId bi = fastMap.computeIfAbsent(id, k -> new HashMap<>())
                .computeIfAbsent(prefix, k -> new BufferId());
        return bi.count < bi.max ? ++bi.count : next(minBuffer, maxBuffer, id, prefix, bi);
    }

    /**
     * 移除極速ID緩衝紀錄物件
     *
     * @param id     群組
     * @param prefix 前綴
     */
    public void removeBufferId(String id, String prefix) {
        Map<String, Map<String, BufferId>> fastMap = cache.get();
        if (fastMap == null) return;

        Map<String, BufferId> map = fastMap.get(id);
        if (map == null) return;
        map.remove(prefix);
    }
}

class NamedBufferIdThreadLocal<T> extends ThreadLocal<T> {
    private final String name;

    NamedBufferIdThreadLocal(String name) {
        Asserts.notEmpty(name, "name");
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
