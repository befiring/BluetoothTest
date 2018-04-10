package cn.com.ut.protocol.entity;

/**
 * 解锁器应用程序下载完毕
 * Created by zhangyihuang on 2017/4/24.
 */
public class EstJsqDownloaded extends DataEntity {
    /**
     * 操作标志字节01写完成，其它未完成写操作失败
     */
    public byte operationMark;

    public EstJsqDownloaded() {
        super();
        dataEntityType[0] = 0x00;
        dataEntityType[1] = 0x0D;
        dataContentLength = 0x01;
        operationMark = 0x01;
    }
}