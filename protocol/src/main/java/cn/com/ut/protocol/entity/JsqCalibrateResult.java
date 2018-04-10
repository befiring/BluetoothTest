package cn.com.ut.protocol.entity;

/**
 * 解锁器无线锁标定结果
 * Created by zhangyihuang on 2016/12/27.
 */
public class JsqCalibrateResult {

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
     * 标定类型
     */
    private byte calibratedType;

    public byte getCalibratedType() {
        return calibratedType;
    }

    public void setCalibratedType(byte calibratedType) {
        this.calibratedType = calibratedType;
    }

    /**
     * 标定结果
     */
    private byte calibratedResult;

    public byte getCalibratedResult() {
        return calibratedResult;
    }

    public void setCalibratedResult(byte calibratedResult) {
        this.calibratedResult = calibratedResult;
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