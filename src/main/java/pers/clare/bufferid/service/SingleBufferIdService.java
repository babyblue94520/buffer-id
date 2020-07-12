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
     * 查詢或者建立 極速ID緩衝紀錄物件
     *
     * @param group  群組
     * @param prefix 前綴
     * @return 極速ID緩衝紀錄物件
     */
    BufferId findBufferId(String group, String prefix) {
        ConcurrentMap<String, BufferId> map;
        BufferId bi;
        if ((map = cache.get(group)) == null) {
            synchronized (groupLock.getLock(group)) {
                if ((map = cache.get(group)) == null) {
                    cache.put(group, map = new ConcurrentHashMap<>());
                    map.put(prefix, bi = new BufferId());
                    return bi;
                }
            }
        }
        if ((bi = map.get(prefix)) == null) {
            synchronized (prefixLock.getLock(prefix)) {
                if ((bi = map.get(prefix)) == null) {
                    map.put(prefix, bi = new BufferId());
                }
            }
        }
        return bi;
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
