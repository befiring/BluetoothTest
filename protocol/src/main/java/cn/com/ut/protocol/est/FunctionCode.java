package cn.com.ut.protocol.est;

/**
 * 链路层命令字
 * Created by zhangyihuang on 2017/3/29.
 */
public class FunctionCode {
    /**
     * 确认 7F
     */
    public static final int ACK = 0x7F;

    /**
     * 不确认 00
     */
    public static final int NAK = 0x00;

    /**
     * 握手命令01
     */
    public static final int LINK = 0x01;

    /**
     * 新版本程序握手命令11
     */
    public static final int LINK1 = 0x11;

    /**
     * 其他设备下传单帧数据命令DNINFO 02
     */
    public static final int DNINFO = 0x02;

    /**
     * 其他设备下传多帧数据数据帧命令DNDATA 03
     */
    public static final int DNDATA = 0x03;

    /**
     * 其他设备上传单帧数据命令UPINFO 04
     */
    public static final int UPINFO = 0x04;

    /**
     * 其他设备上传多帧数据数据帧命令UPDATA 05
     */
    public static final int UPDATA = 0x05;

    /**
     * 其他设备下传多帧数据信息帧命令DNDATAINFO 06
     */
    public static final int DNDATAINFO = 0x06;

    /**
     * 其他设备上传多帧数据信息帧命令UPDATAINFO 07
     */
    public static final int UPDATAINFO = 0x07;

    /**
     * 其他设备下传单帧数据命令（透明通道） 21
     */
    public static final int PSDNINFO = 0x21;

}