package cn.com.ut.protocol.entity;

/**
 * 解锁器无线锁标定
 * Created by zhangyihuang on 2017/1/21.
 */
public class JsqWxsCalibrate {

    /**
     * 锁具类型ID码
     */
    private int lockTypeId;

    public int getLockTypeId() {
        return lockTypeId;
    }

    public void setLockTypeId(int lockTypeId) {
        this.lockTypeId = lockTypeId;
    }

    /**
     * 子类型(标定类型)
     */
    private byte calibratedType;

    public byte getCalibratedType() {
        return calibratedType;
    }

    public void setCalibratedType(byte calibratedType) {
        this.calibratedType = calibratedType;
    }

    /**
     * 标定值
     */
    private byte[] calibratedValue;

    public byte[] getCalibratedValue() {
        return calibratedValue;
    }

    public void setCalibratedValue(byte[] calibratedValue) {
        this.calibratedValue = calibratedValue;
    }
}