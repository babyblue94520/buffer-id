package pers.clare.bufferid.service;

import pers.clare.bufferid.manager.IdManager;
import pers.clare.lock.IdLock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 所有執行序共用同一個 buffer
 * 極速產生唯一ID
 */
public class SingleBufferIdService extends AbstractBufferIdService {
    public static final ConcurrentMap<String, ConcurrentMap<String, BufferId>> cache = new ConcurrentHashMap<>();

    private static final IdLock<Object> idLock = new IdLock<>() {
    };
    private static final IdLock<Object> prefixLock = new IdLock<>() {
    };

    public SingleBufferIdService(IdManager idManager) {
        super(idManager);
    }


    /**
     * 取得數字ID
     *
     * @param buffer 預設緩衝區大小
     * @param id     群組
     * @param prefix 前綴
     * @return long
     */
    public Long next(long buffer, String id, String prefix) {
        ConcurrentMap<String, BufferId> map;
        if ((map = cache.get(id)) == null) {
            synchronized (idLock.getLock(id)) {
                if ((map = cache.get(id)) == null) {
                    cache.put(id, map = new ConcurrentHashMap<>());
                }
            }
        }
        BufferId bi;
        if ((bi = map.get(prefix)) == null) {
            synchronized (prefixLock.getLock(prefix)) {
                if ((bi = map.get(prefix)) == null) {
                    map.put(prefix, bi = new BufferId());
                }
            }
        }
        synchronized (bi) {
            return bi.count < bi.max ? ++bi.count : next(buffer, id, prefix, bi);
        }
    }

    /**
     * 移除極速ID緩衝紀錄物件
     *
     * @param id     群組
     * @param prefix 前綴
     * @return 極速ID緩衝紀錄物件
     */
    public BufferId removeBufferId(String id, String prefix) {
        ConcurrentMap<String, BufferId> map;
        if ((map = cache.get(id)) == null) {
            return null;
        }
        return map.remove(prefix);
    }
}
