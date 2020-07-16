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

    private static final IdLock<Object> groupLock = new IdLock<Object>() {
    };
    private static final IdLock<Object> prefixLock = new IdLock<Object>() {
    };

    public SingleBufferIdService(IdManager idManager) {
        super(idManager);
    }


    /**
     * 取得數字ID
     *
     * @param buffer 預設緩衝區大小
     * @param group  群組
     * @param prefix 前綴
     * @return long
     */
    public Long next(int buffer, String group, String prefix) {
        ConcurrentMap<String, BufferId> map;
        if ((map = cache.get(group)) == null) {
            synchronized (groupLock.getLock(group)) {
                if ((map = cache.get(group)) == null) {
                    cache.put(group, map = new ConcurrentHashMap<>());
                }
            }
        }
        synchronized (prefixLock.getLock(prefix)) {
            BufferId bi;
            if ((bi = map.get(prefix)) == null) {
                map.put(prefix, bi = new BufferId());
            }
            return bi.count < bi.max ? ++bi.count : next(buffer, group, prefix, bi);
        }
    }

    /**
     * 移除極速ID緩衝紀錄物件
     *
     * @param group  群組
     * @param prefix 前綴
     * @return 極速ID緩衝紀錄物件
     */
    BufferId removeBufferId(String group, String prefix) {
        ConcurrentMap<String, BufferId> map;
        if ((map = cache.get(group)) == null) {
            return null;
        }
        return map.remove(prefix);
    }
}
