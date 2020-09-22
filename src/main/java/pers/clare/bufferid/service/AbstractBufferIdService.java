package pers.clare.bufferid.service;

import pers.clare.bufferid.manager.IdManager;
import pers.clare.bufferid.util.IdUtil;

public abstract class AbstractBufferIdService implements BufferIdService {
    // 预设計算緩衝區大小的時間
    private static final long bufferMillisecond = 10000L;

    private IdManager idManager;

    public AbstractBufferIdService(IdManager idManager) {
        this.idManager = idManager;
    }

    /**
     * 取得字串ID 前綴+'00000001'
     *
     * @param buffer 預設緩衝區大小
     * @param id  群組
     * @param prefix 前綴
     * @param length 訂單編號長度
     * @return 前綴+'00000001'
     */
    public String next(int buffer, String id, String prefix, int length) {
        return IdUtil.addZero(prefix, String.valueOf(next(buffer, id, prefix)), length);
    }

    /**
     * @param id
     * @param prefix
     * @return
     */
    public int save(String id, String prefix) {
        return this.idManager.save(id, prefix);
    }

    /**
     * @param id
     * @param prefix
     * @return
     */
    public int remove(String id, String prefix) {
        removeBufferId(id, prefix);
        return this.idManager.remove(id, prefix);
    }

    /**
     * 取得下一個ID緩衝區
     *
     * @param buffer 預設緩衝區大小
     * @param id  群組
     * @param prefix 前綴
     * @param bi     極速ID緩衝紀錄物件
     * @return long
     */
    protected Long next(int buffer, String id, String prefix, BufferId bi) {
        long now = System.currentTimeMillis();
        // 動態計算需要的緩衝區大小
        if (bi.lastTime == 0) {
            bi.lastTime = now;
        } else {
            int originBuffer = buffer;
            // 暫時不考慮溢位
            long t = now - bi.lastTime;
            if (t > 0) {
                buffer = (int) ((double) bi.lastBuffer / t * bufferMillisecond);
                if (buffer < originBuffer) {
                    buffer = originBuffer;
                }
            }
            bi.lastTime = now;
        }

        bi.lastBuffer = buffer;
        //取得當時最大訂單號
        bi.max = idManager.increment(id, prefix, buffer);
        bi.count = bi.max - buffer + 1;
        return bi.count;
    }

    public abstract BufferId removeBufferId(String id, String prefix);

}
