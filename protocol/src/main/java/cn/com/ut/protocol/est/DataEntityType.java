package cn.com.ut.protocol.est;

/**
 * 数据实体类型
 * 上传、下传单帧命令定义
 * Created by zhangyihuang on 2017/4/13.
 */
public class DataEntityType {

    /**
     * 上位机系统请求自学数据校验码
     */
    public static final int SELFCHECKCODE = 0x01;

    /**
     * 上位机系统告诉钥匙不需要自学
     */
    public static final int KEYSNOLEARN = 0x02;

    /**
     * 上位机系统发送设置万能钥匙命令
     */
    public static final int SENDSETKEY = 0x03;

    /**
     * 解锁器上传RFID码(专用于解锁器)
     */
    public static final int UPLOADRFID = 0x04;

    /**
     * 获取解锁器软件版本(专用于解锁器)
     */
    public static final int GETJSQVERSION = 0x09;

    /**
     * 解锁器软件版本上报(专用于解锁器)
     */
    public static final int JSQVERSIONREPORT = 0x0A;

    /**
     * 解锁器应用程序下载开始(专用于解锁器)
     */
    public static final int JSQSOFTWAREDOWNLOADSTART = 0x0B;

    /**
     * 解锁器应用程序下载数据(专用于解锁器)
     */
    public static final int JSQDOWNLOADDATA = 0x0C;

    /**
     * 解锁器应用程序下载完毕(专用于解锁器)
     */
    public static final int JSQSOFTWAREDOWNLOADEND = 0x0D;

    /**
     * 解锁器应用程序下载结果上报(专用于解锁器)
     */
    public static final int JSQSOFTWAREDOWNLOADRESULTREPORT = 0x0E;

}