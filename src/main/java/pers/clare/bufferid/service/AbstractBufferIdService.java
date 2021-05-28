package pers.clare.bufferid.service;

import pers.clare.bufferid.manager.IdManager;
import pers.clare.bufferid.util.IdUtil;

public abstract class AbstractBufferIdService implements BufferIdService {
    // 预设計算緩衝區大小的時間
    protected static final long bufferMillisecond = 10000L;

    protected final IdManager idManager;

    public AbstractBufferIdService(IdManager idManager) {
        this.idManager = idManager;
    }

    public String next(long minBuffer, long maxBuffer, String id, String prefix, int length) {
        return IdUtil.addZero(prefix, String.valueOf(next(minBuffer, maxBuffer, id, prefix)), length);
    }

    public int save(String id, String prefix) {
        return idManager.save(id, prefix);
    }

    public int remove(String id, String prefix) {
        removeBufferId(id, prefix);
        return idManager.remove(id, prefix);
    }

    protected Long next(long minBuffer, long maxBuffer, String id, String prefix, BufferId bi) {
        long buffer = calculationBuffer(minBuffer, maxBuffer, bi);
        bi.max = idManager.increment(id, prefix, buffer);
        bi.count = bi.max - buffer + 1;
        return bi.count;
    }

    /**
     * 動態計算需要的緩衝區大小
     */
    protected long calculationBuffer(long min, long max, BufferId bi) {
        long now = System.currentTimeMillis();
        long newBuffer = min;
        if (bi.lastTime == 0) {
            bi.lastTime = now;
        } else {
            // 暫時不考慮溢位
            long t = now - bi.lastTime;
            if (t > 0) {
                newBuffer = (long) ((double) bi.lastBuffer / t * bufferMillisecond);
                if (newBuffer < min) {
                    newBuffer = min;
                } else if (max > 0 && newBuffer > max) {
                    newBuffer = max;
                }
            }
            bi.lastTime = now;
        }

        return bi.lastBuffer = newBuffer;
    }

    public abstract void removeBufferId(String id, String prefix);

}
