package cn.com.ut.protocol.jsq;

/**
 * 应用层命令字
 * Created by zhangyihuang on 2016/12/27.
 */
public class CommandCode {

    /**
     * 帧错误应答
     */
    public static final int FRAMEERROR = 0x0000;

    /**
     * RFID锁码上传
     */
    public static final int UPRFID = 0x0001;

    /**
     * RFID锁码上传应答
     */
    public static final int UPRFIDACK = 0x0002;

    /**
     * 下发操作指令
     */
    public static final int OPTCOMMAND = 0x0003;

    /**
     * 收到操作指令应答
     */
    public static final int OPTCOMMANDACK = 0x0004;

    /**
     * 下发RFID非法
     */
    public static final int RFIDERROR = 0x0005;

    /**
     * 收到"RFID非法"应答
     */
    public static final int RFIDERRORACK = 0x0006;

    /**
     * 操作结果上传
     */
    public static final int UPOPTRESULT = 0x0007;

    /**
     * 收到操作结果应答
     */
    public static final int UPOPTRESULTACK = 0x0008;

    /**
     * 解锁器复位
     */
    public static final int RESET = 0x0009;

    /**
     * 解锁器复位应答
     */
    public static final int RESETACK = 0x000A;

    /**
     * 获取解锁器软件版本
     */
    public static final int JSQVERSION = 0x000B;

    /**
     * 获取解锁器软件版本应答
     */
    public static final int JSQVERSIONACK = 0x000C;

    /**
     * 直接操作指令
     */
    public static final int DIRECTOPTCOMMAND = 0x000D;

    /**
     * 直接操作指令应答
     */
    public static final int DIRECTOPTCOMMANDACK = 0x000E;

    /**
     * 无线锁信息获取
     */
    public static final int WXSINFO = 0x000F;

    /**
     * 无线锁信息获取应答
     */
    public static final int WXSINFOACK = 0x0010;

    /**
     * 接收无线锁信息
     */
    public static final int WXSINFORECEIVE = 0x0011;

    /**
     * 接收无线锁信息应答
     */
    public static final int WXSINFORECEIVEACK = 0x0012;

    /**
     * 无线锁标定
     */
    public static final int WXSCALIBRATE = 0x0013;

    /**
     * 无线锁标定应答
     */
    public static final int WXSCALIBRATEACK = 0x0014;

    /**
     * 无线锁标定结果上传
     */
    public static final int WXSCALIBRATEDRESULT = 0x0015;

    /**
     * 无线锁标定结果上传应答
     */
    public static final int WXSCALIBRATEDRESULTACK = 0x0016;

    /**
     * 操作票信息帧
     */
    public static final int TICKETINFOFRAME = 0x0019;

    /**
     * 操作票信息帧应答
     */
    public static final int TICKETINFOFRAMEACK = 0x001A;

    /**
     * 操作票数据帧
     */
    public static final int TICKETDATAFRAME = 0x001B;

    /**
     * 操作票数据帧应答
     */
    public static final int TICKETDATAFRAMEACK = 0x001C;

    /**
     * 查询验电命令
     */
    public static final int QUERYYDCOMMAND = 0x0300;

    /**
     * 验电结果应答命令
     */
    public static final int YDRESULTCOMMANDACK = 0x0301;

    /**
     * 修改设备蓝牙名称命令
     */
    public static final int MODIFYBLUETOOTHNAME = 0x0302;

    /**
     * 修改设备蓝牙名称命令应答
     */
    public static final int MODIFYBLUETOOTHNAMEACK = 0x0303;

    /**
     *  验电标定命令
     */
    public static final int YDCALIBRATECOMMAND = 0x0304;

    /**
     * 验电标定应答命令
     */
    public static final int YDCALIBRATECOMMANDACK = 0x0305;
}