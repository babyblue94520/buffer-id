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

    public Long next(String id, String prefix) {
        return idManager.increment(id, prefix, 1);
    }

    public String next(String id, String prefix, int length) {
        return IdUtil.addZero(prefix, String.valueOf(next(id, prefix)), length);
    }

    /**
     * @return A~Z
     */
    public String nextAZ(String id, String prefix) {
        long next = next(id, prefix);
        StringBuilder sb = new StringBuilder();
        sb.append(toAZ(--next));
        while ((next /= 26) > 0) sb.insert(0, toAZ(--next));
        return sb.toString();
    }

    public char toAZ(long n) {
        return (char) (n % 26 + 65);
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

    /**
     * 動態計算需要的緩衝區大小
     */
    protected long calculationBuffer(long min, long max, BufferId bi) {
        long now = System.currentTimeMillis();
        long newBuffer = min;
        if (bi.lastTime != 0) {
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
        }
        bi.lastTime = now;

        return bi.lastBuffer = newBuffer;
    }

    public abstract void removeBufferId(String id, String prefix);
}
