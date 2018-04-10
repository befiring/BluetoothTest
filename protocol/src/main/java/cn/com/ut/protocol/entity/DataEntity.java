package cn.com.ut.protocol.entity;

/**
 * 数据实体基类
 * Created by zhangyihuang on 2017/4/14.
 */
public class DataEntity {
    /**
     * 数据实体类型 (2字节)
     */
    public byte[] dataEntityType;

    /**
     * 数据内容长度
     */
    public byte dataContentLength;

    public DataEntity() {
        dataEntityType = new byte[2];
    }
}