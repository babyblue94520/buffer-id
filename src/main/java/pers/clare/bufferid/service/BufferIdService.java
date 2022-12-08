package pers.clare.bufferid.service;

/**
 * 極速產生唯一ID
 */
public interface BufferIdService {

    /**
     * 根據負載計算緩衝大小
     *
     * @param id        群組
     * @param prefix    前綴
     */
    Long next(String id, String prefix);

    /**
     * 根據負載計算緩衝大小
     * 取得字串ID 前綴+'00000001'
     *
     * @param id        群組
     * @param prefix    前綴
     * @param length    訂單編號長度
     * @return 前綴+'00000001'
     */
    String next(String id, String prefix, int length);

    /**
     * 根據負載計算緩衝大小
     * 取得字串ID 前綴+'00000001'
     *
     * @param minBuffer 最小緩衝區大小 不可為 0
     * @param maxBuffer 最大緩衝區大小 0:則為無限制
     * @param id        群組
     * @param prefix    前綴
     */
    Long next(long minBuffer, long maxBuffer, String id, String prefix);

    /**
     * 根據負載計算緩衝大小
     * 取得字串ID 前綴+'00000001'
     *
     * @param minBuffer 最小緩衝區大小 不可為 0
     * @param maxBuffer 最大緩衝區大小 0:則為無限制
     * @param id        群組
     * @param prefix    前綴
     * @param length    訂單編號長度
     * @return 前綴+'00000001'
     */
    String next(long minBuffer, long maxBuffer, String id, String prefix, int length);

    int save(String id, String prefix);

    int remove(String id, String prefix);
}
