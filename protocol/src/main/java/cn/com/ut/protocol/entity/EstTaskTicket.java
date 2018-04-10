package cn.com.ut.protocol.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 临时授权票
 * Created by zhangyihuang on 2017/3/29.
 */
public class EstTaskTicket {

    /**
     * 段标志 1字节
     */
    public byte partFlag;

    /**
     * 开票设备ID 2字节
     */
    public short makeTicketDeviceId;

    /**
     * 开票设备类型描述 1字节
     */
    public byte makeTicketDeviceType;

    /**
     * 操作票任务号 1字节
     */
    public byte eTicketTaskId;

    /**
     * 操作票序号 4字节
     */
    public int eTicketSerial;

    /**
     * 操作票版本号 4字节
     */
    public int eTicketVersion;

    /**
     * 本次操作的站号 2字节
     */
    public short stationNo;

    /**
     * 操作票属性 2字节
     */
    public short eTicketProperty;

    /**
     * 操作票长度 2字节
     */
    public short eTicketLength;

    /**
     * 操作票特性字节 1字节
     */
    public byte eTicketAttribute;

    /**
     * 本张操作票帧数 2字节
     */
    public short eTicketFrames;

    /**
     * 操作票扩展任务号 2字节
     */
    public short eTicketTaskIDEx;

    /**
     * 地线锁码表起始地址 4字节
     */
    public int dxMatrixOffset;

    /**
     * 临时授权人员表起始地址 4字节
     */
    public int authPersonTableAddr;

    /**
     * 校准时间 6字节
     */
    public byte[] calibrationTime;

    /**
     * 有效时间 4字节
     */
    public int effectiveTime;

    /**
     * 是否有序票 1字节
     */
    public byte isOrderTicket;

    ////////////////////////////////////////授权票数据帧////////////////////////////////////////////
    public EstTaskTicket() {
        ticketList = new ArrayList<>();
        partFlag = 0x00;//段标志
        dxMatrixOffset = 0x00;//地线锁码表起始地址
    }

    public List<Ticket> ticketList;//操作票
    public AuthPersonTable authPersonTable;//临时授权人员表

    public class Ticket {
        public TicketFormat ticketFormat;//操作票格式
        public ShowContents contents;//显示内容
    }

    /**
     * 操作票格式
     */
    public class TicketFormat {
        public short deviceId;//设备ID（2字节，先高后低）
        public short descInfo;//本项描述信息（2字节，先高后低）
        public short contentPointer;//显示内容指针（2字节，先高后低）
    }

    /**
     * 显示内容
     * 显示内容共分成三个
     * 第一个显示项为当前步显示内容、以0x00结束
     * 第二个显示项为密码区 密码区以0x00结束
     * 第三个显示项为V2.0及以后版本操作票的RFID锁码信息，以“00h”为结束符
     */
    public class ShowContents {
        public String opHint;//操作描述
        public String passwordArea;//密码区
        /**
         * 18位的RFID、编号、属性组合字符串
         * RFID码值（12字节）＋设备编号(4字节)＋属性类型（2字节）
         */
        public String eighteenLengthRfidCode;
    }

    /**
     * 临时授权人员列表
     * 每条登陆人员信息的格式如下：
     * 班组ID（4字节），班组名（16字节），姓名（12字节），密码（10字节），RFID码（12字节），权限（4字节），
     * 权限有效开始时间(12字节)，权限有效结束时间(12字节)，操作人ID（4个字节）0x0D 0x0A
     */
    public class AuthPersonTable {
        public int personInfoCount;//登陆人员信息条数N
        public List<PersonInfoDes> personInfoDesList;

        public AuthPersonTable() {
            personInfoDesList = new ArrayList<>();
        }

        public class PersonInfoDes {
            public int teamId; //班组ID
            public String teamName; //班组名
            public String name; //姓名
            public String password; //密码
            public String rfidCode; //RFID码
            public String power; //权限
            public String validStartTime; //权限有效开始时间
            public String validEndTime; //权限有效结束时间
            public int operatorId; //操作人ID
        }
    }
}