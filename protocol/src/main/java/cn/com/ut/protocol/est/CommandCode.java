package cn.com.ut.protocol.est;

/**
 * 应用层命令字
 * Created by zhangyihuang on 2017/4/10.
 */
public class CommandCode {
    /**
     * 电脑钥匙自学(0x01)
     */
    public static final int KEYSELFSTUDY = 0x01;

    /**
     * 临时授权票(0x02)
     */
    public static final int TRANSMISSIONTEMPAUTHTICKET = 0x02;

    /**
     * 电脑钥匙追忆回传数据（0X84）
     */
    public static final int KEYRECALLDATA = 0x84;

    /**
     * 上位机系统下传单帧数据(0x6B，新功能通用命令)
     */
    public static final int DOWNLOADSINGLEFRAMEDATA = 0x6B;

    /**
     * 钥匙上传单帧数据
     */
    public static final int UPLOADSINGLEFRAMEDATA = 0x6D;

}