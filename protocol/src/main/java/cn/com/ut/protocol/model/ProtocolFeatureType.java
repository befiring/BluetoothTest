package cn.com.ut.protocol.model;

/**
 * 规约功能类型
 * Created by zhangyihuang on 2016/12/22.
 */
public class ProtocolFeatureType {

    /**
     * 监控，用于与监控通信
     */
    public static final int Monitoring = 0x0001;

    /**
     * 传票
     */
    public static final int SendTicket = 0x0002;

    /**
     * 接票
     */
    public static final int ReceiveTicket = 0x0004;

    /**
     * 监护
     */
    public static final int Guardianship = 0x0008;

    /**
     * 解锁，用于与解锁器通信
     */
    public static final int Unlock = 0x0010;

    /**
     * 操作钥匙
     */
    public static final int OperationKey = 0x0020;

    /**
     * 数据同步
     */
    public static final int DataSync = 0x0040;

    /**
     * 文件传输
     */
    public static final int FileTransfer = 0x0080;
}