package pers.clare.bufferid.service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 極速ID緩衝紀錄物件
 */
class AtomicBufferId extends BufferId{

    Atomic atomic;

    long next() {
        if (atomic == null) return 0;
        return atomic.next();
    }

    static class Atomic {
        // 累計值
        AtomicLong index;
        // 緩衝區最大值
        long max;

        Atomic(long max, long index) {
            this.max = max;
            this.index = new AtomicLong(index);
        }

        long next() {
            long c = index.incrementAndGet();
            if (c > max) return 0;
            return c;
        }
    }
}
