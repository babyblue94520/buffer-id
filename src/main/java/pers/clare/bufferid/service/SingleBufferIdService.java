package pers.clare.bufferid.service;

import pers.clare.bufferid.manager.IdManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 所有執行序共用同一個 buffer
 * 極速產生唯一ID
 */
public class SingleBufferIdService extends AbstractBufferIdService {
    public static final ConcurrentMap<String, ConcurrentMap<String, AtomicBufferId>> cache = new ConcurrentHashMap<>();

    public SingleBufferIdService(IdManager idManager) {
        super(idManager);
    }


    public Long next(long minBuffer, long maxBuffer, String id, String prefix) {
        AtomicBufferId bi = cache.computeIfAbsent(id, (key) -> new ConcurrentHashMap<>())
                .computeIfAbsent(prefix, (key) -> new AtomicBufferId());

        long next = bi.next();
        if (next > 0) return next;
        synchronized (bi) {
            next = bi.next();
            if (next > 0) return next;
            return next(minBuffer, maxBuffer, id, prefix, bi);
        }
    }

    protected Long next(long minBuffer, long maxBuffer, String id, String prefix, AtomicBufferId bi) {
        long buffer = calculationBuffer(minBuffer, maxBuffer, bi);
        long max = idManager.increment(id, prefix, buffer);
        bi.atomic = new AtomicBufferId.Atomic(max, max - buffer);
        return bi.next();
    }

    /**
     * 移除極速ID緩衝紀錄物件
     *
     * @param id     群組
     * @param prefix 前綴
     */
    public void removeBufferId(String id, String prefix) {
        ConcurrentMap<String, AtomicBufferId> map = cache.get(id);
        if (map == null) return;
        map.remove(prefix);
    }

}
