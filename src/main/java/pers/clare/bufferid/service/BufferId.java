package pers.clare.bufferid.service;

/**
 * 極速ID緩衝紀錄物件
 */
class BufferId{
    // 上次取編號緩衝區時間
    long lastTime = 0;
    // 上次取得的緩衝區大小
    long lastBuffer = 0;
    // 累計值
    long count = 0;
    // 緩衝區最大值
    long max = 0;
}
