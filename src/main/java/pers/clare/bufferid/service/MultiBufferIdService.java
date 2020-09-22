package pers.clare.bufferid.service;

import pers.clare.bufferid.manager.IdManager;
import pers.clare.util.Asserts;

import java.util.HashMap;
import java.util.Map;


/**
 * 所有執行序各別有一個 buffer
 * 極速產生唯一ID
 */
public class MultiBufferIdService extends AbstractBufferIdService{
    // 每個執行緒獨立的
    public static final ThreadLocal<Map<String, Map<String, BufferId>>> cache = new NamedBufferIdThreadLocal<>("BufferId Cache Holder");

    public MultiBufferIdService(IdManager idManager) {
        super(idManager);
    }

    /**
     * 查詢或者建立 極速ID緩衝紀錄物件
     *
     * @param id  群組
     * @param prefix 前綴
     * @return 極速ID緩衝紀錄物件
     */
    public Long next(int buffer, String id, String prefix) {
        Map<String, Map<String, BufferId>> fastMap = cache.get();
        if (fastMap == null) {
            cache.set((fastMap = new HashMap<>()));
        }
        Map<String, BufferId> map;
        if ((map = fastMap.get(id)) == null) {
            fastMap.put(id, map = new HashMap<>());
        }
        BufferId bi;
        if ((bi = map.get(prefix)) == null) {
            map.put(prefix, bi = new BufferId());
        }
        return bi.count < bi.max ? ++bi.count : next(buffer, id, prefix, bi);
    }

    /**
     * 移除極速ID緩衝紀錄物件
     *
     * @param id  群組
     * @param prefix 前綴
     * @return 極速ID緩衝紀錄物件
     */
    public BufferId removeBufferId(String id, String prefix) {
        Map<String, Map<String, BufferId>> fastMap = cache.get();
        if (fastMap == null) {
            return null;
        }
        Map<String, BufferId> map;
        if ((map = fastMap.get(id)) == null) {
            return null;
        }
        return map.remove(prefix);
    }
}

class NamedBufferIdThreadLocal<T> extends ThreadLocal<T> {
    private final String name;

    NamedBufferIdThreadLocal(String name) {
        Asserts.notEmpty(name, "Name must not be empty");
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
