package cn.com.ut.protocol.entity;

/**
 * 解锁器无线锁信息
 * Created by zhangyihuang on 2016/12/27.
 */
public class JsqWxsInfoResult {

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
     * 无线锁信息
     */
    private byte[] wxsInfo;

    public byte[] getWxsInfo() {
        return wxsInfo;
    }

    public void setWxsInfo(byte[] wxsInfo) {
        this.wxsInfo = wxsInfo;
    }
}