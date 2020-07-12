package pers.clare.bufferid.service;

/**
 * 極速產生唯一ID
 */
public interface BufferIdService {


    /**
     * 取得字串ID 前綴+'00000001'
     *
     * @param buffer 預設緩衝區大小
     * @param group  群組
     * @param prefix 前綴
     * @param length 訂單編號長度
     * @return 前綴+'00000001'
     */
    public String next(int buffer, String group, String prefix, int length);

    /**
     * 取得數字ID
     *
     * @param buffer 預設緩衝區大小
     * @param group  群組
     * @param prefix 前綴
     * @return long
     */
    public Long next(int buffer, String group, String prefix);

    public int save(String group, String prefix);

    public int remove(String group, String prefix);
}
