package cn.com.ut.protocol.entity;

/**
 * 下发操作指令数据
 * Created by zhangyihuang on 2017/1/19.
 */
public class JsqOptCommand {

    /**
     * 锁具类型ID码
     */
    private short lockTypeId;

    public short getLockTypeId() {
        return lockTypeId;
    }

    public void setLockTypeId(short lockTypeId) {
        this.lockTypeId = lockTypeId;
    }

    /**
     * Rfid
     */
    private long rfid;

    public long getRfid() {
        return rfid;
    }

    public void setRfid(long rfid) {
        this.rfid = rfid;
    }

    /**
     * 解锁器操作属性
     */
    private int jsqOptProperty;

    public int getJsqOptProperty() {
        return jsqOptProperty;
    }

    public void setJsqOptProperty(int jsqOptProperty) {
        this.jsqOptProperty = jsqOptProperty;
    }

    /**
     * 操作附加信息
     */
    private byte[] operateExtraInfo;

    public byte[] getOperateExtraInfo() {
        return operateExtraInfo;
    }

    public void setOperateExtraInfo(byte[] operateExtraInfo) {
        this.operateExtraInfo = operateExtraInfo;
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