package cn.com.ut.protocol.jsq;

/**
 * 解锁器错误码
 * Created by zhangyihuang on 2016/12/26.
 */
public class JsqErrorCode {

    /**
     * 字符数不符
     */
   public static final byte LENGTHERROR = 0x01;

    /**
     * CRC校验错误
     */
    public static final byte CRCERROR= 0x02;

    /**
     * 无效的命令码
     */
    public static final byte INVALIDCMD = 0x03;
}