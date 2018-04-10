package cn.com.ut.protocol.entity;

/**
 * 下发操作票信息帧
 * Created by zhangyihuang on 2017/1/21.
 */
public class JsqTicketInfo {

    /**
     * 有效起始时间
     * 年月日时分秒各占一个字节，总共6个字节
     * 例如：11/05/16 08:42:25表示为110516084225（十进制BCD码）
     */
    private String startTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * 有效结束时间 byte[]
     */
    private String finishTime;

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * 操作票数据总字节长度 4字节
     */
    private int ticketLength;

    public int getTicketLength() {
        return ticketLength;
    }

    public void setTicketLength(int ticketLength) {
        this.ticketLength = ticketLength;
    }

    /**
     * 操作票数据总帧数 2字节
     */
    private int ticketFrames;

    public int getTicketFrames() {
        return ticketFrames;
    }

    public void setTicketFrames(int ticketFrames) {
        this.ticketFrames = ticketFrames;
    }

    /**
     * 操作票数据版本号(1字节)
     */
    private byte optDateVersion;

    public byte getOptDateVersion() {
        return optDateVersion;
    }

    public void setOptDateVersion(byte optDateVersion) {
        this.optDateVersion = optDateVersion;
    }

    /**
     * 检验和 4字节
     */
    private int sumcheck;

    public int getSumcheck() {
        return sumcheck;
    }

    public void setSumcheck(int sumcheck) {
        this.sumcheck = sumcheck;
    }

    /**
     * 强制验电闭锁前导项表起始地址 4字节
     */
    private int yDBSStartAddr;

    public int getYDBSStartAddr() {
        return yDBSStartAddr;
    }

    public void setYDBSStartAddr(int addr) {
        this.yDBSStartAddr = addr;
    }

    /**
     * 锁码表起始地址
     */
    private int colleCodeStartAddr;

    public int getColleCodeStartAddr() {
        return colleCodeStartAddr;
    }

    public void setColleCodeStartAddr(int colleCodeStartAddr) {
        this.colleCodeStartAddr = colleCodeStartAddr;
    }


    /**
     * 操作票序号
     */
    private byte[] serialNumber;

    public byte[] getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(byte[] serialNumber) {
        this.serialNumber = serialNumber;
    }

}