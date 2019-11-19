package pers.clare.bufferid.service;

import org.springframework.stereotype.Service;
import pers.clare.bufferid.manager.IdManager;
import pers.clare.bufferid.util.IdUtil;
import pers.clare.util.Asserts;

import java.util.HashMap;
import java.util.Map;


/**
 * 極速產生唯一ID
 */
@Service
public class BufferIdService {
    // 每個執行緒獨立的
    public static final ThreadLocal<Map<String, Map<String, BufferId>>> cache2 = new NamedBufferIdThreadLocal<>("BufferId Cache Holder");
    // 预设計算緩衝區大小的時間
    private static final long bufferMillisecond = 10000L;

    private IdManager idManager;

    public BufferIdService(IdManager idManager) {
        this.idManager = idManager;
    }

    /**
     * 取得字串ID 前綴+'00000001'
     *
     * @param buffer 預設緩衝區大小
     * @param group  群組
     * @param prefix 前綴
     * @param length 訂單編號長度
     * @return 前綴+'00000001'
     */
    public String next(int buffer, String group, String prefix, int length) {
        return IdUtil.addZero(prefix, String.valueOf(next(buffer, group, prefix)), length);
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
        BufferId bi = findBufferId(group, prefix);
        return bi.count >= bi.max ? next(buffer, group, prefix, bi) : ++bi.count;
    }

    /**
     * 取得下一個ID緩衝區
     *
     * @param buffer 預設緩衝區大小
     * @param group  群組
     * @param prefix 前綴
     * @param bi     極速ID緩衝紀錄物件
     * @return long
     */
    private Long next(int buffer, String group, String prefix, BufferId bi) {
        long now = System.currentTimeMillis();
        // 動態計算需要的緩衝區大小
        if (bi.lastTime == 0) {
            bi.lastTime = now;
        } else {
            // 暫時不考慮溢位
            long t = now - bi.lastTime;
            if (t == 0) {
                buffer = (int) (bi.lastBuffer * 10);
            } else {
                buffer = (int) ((double) bi.lastBuffer / t * bufferMillisecond);
            }
            bi.lastTime = now;
        }

        bi.lastBuffer = buffer;
        //取得當時最大訂單號
        bi.max = idManager.increment(group, prefix, buffer);
        bi.count = bi.max - buffer + 1;
        return bi.count;
    }

    /**
     * 查詢或者建立 極速ID緩衝紀錄物件
     *
     * @param group  群組
     * @param prefix 前綴
     * @return 極速ID緩衝紀錄物件
     */
    private BufferId findBufferId(String group, String prefix) {
        Map<String, Map<String, BufferId>> fastMap = cache2.get();
        Map<String, BufferId> map;
        BufferId bi;
        if (fastMap == null) {
            cache2.set((fastMap = new HashMap<>()));
            fastMap.put(group, map = new HashMap<>());
            map.put(prefix, bi = new BufferId());
            return bi;
        }
        if ((map = fastMap.get(group)) == null) {
            fastMap.put(group, map = new HashMap<>());
            map.put(prefix, bi = new BufferId());
            return bi;
        }
        if ((bi = map.get(prefix)) == null) {
            map.put(prefix, bi = new BufferId());
        }
        return bi;
    }
}

/**
 * 極速ID緩衝紀錄物件
 */
class BufferId {
    // 上次取編號緩衝區時間
    long lastTime = 0;
    // 上次取得的緩衝區大小
    long lastBuffer = 0;
    // 累計值
    long count = 0;
    // 緩衝區最大值
    long max = 0;
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