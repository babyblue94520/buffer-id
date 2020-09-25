package pers.clare.bufferid.service;

/**
 * 極速產生唯一ID
 */
public interface BufferIdService {


    /**
     * 取得字串ID 前綴+'00000001'
     *
     * @param buffer 預設緩衝區大小
     * @param id  群組
     * @param prefix 前綴
     * @param length 訂單編號長度
     * @return 前綴+'00000001'
     */
    public String next(long buffer, String id, String prefix, int length);

    /**
     * 取得數字ID
     *
     * @param buffer 預設緩衝區大小
     * @param id  群組
     * @param prefix 前綴
     * @return long
     */
    public Long next(long buffer, String id, String prefix);

    public int save(String id, String prefix);

    public int remove(String id, String prefix);
}
