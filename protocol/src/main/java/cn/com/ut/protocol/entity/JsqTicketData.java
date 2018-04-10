package cn.com.ut.protocol.entity;

/**
 * 下发操作票数据帧(0x001B)
 * Created by zhangyihuang on 2017/1/21.
 */
public class JsqTicketData {

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
     * 当前数据帧帧号 2字节
     */
    private int curFrame;

    public int getCurFrame() {
        return curFrame;
    }

    public void setCurFrame(int curFrame) {
        this.curFrame = curFrame;
    }

    /**
     * 操作票数据
     */
    private byte[] ticketData;

    public byte[] getTicketData() {
        return ticketData;
    }

    public void setTicketData(byte[] ticketData) {
        this.ticketData = ticketData;
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