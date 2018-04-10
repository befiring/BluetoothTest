package cn.com.ut.protocol.entity;

/**
 * 解锁器操作结果类
 * Created by zhangyihuang on 2016/12/27.
 */
public class JsqOperateResult {

    /**
     * RFID码
     */
    private long rfid;

    public long getRfid() {
        return rfid;
    }

    public void setRfid(long rfid) {
        this.rfid = rfid;
    }

    /**
     * 操作时间
     */
    private byte[] operateTime;

    public byte[] getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(byte[] operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * 操作设备ID
     */
    private byte operateDeviceId;

    public byte getOperateDeviceId() {
        return operateDeviceId;
    }

    public void setOperateDeviceId(byte operateDeviceId) {
        this.operateDeviceId = operateDeviceId;
    }

    /**
     * 操作结果描述高2字节(操作属性)
     */
    private short operResultDesHigh;

    public short getOperResultDesHigh() {
        return operResultDesHigh;
    }

    public void setOperResultDesHigh(short operResultDesHigh) {
        this.operResultDesHigh = operResultDesHigh;
    }

    /**
     * 操作结果描述低1字节
     */
    private byte operResultDesLow;

    public byte getOperResultDesLow() {
        return operResultDesLow;
    }

    public void setOperResultDesLow(byte operResultDesLow) {
        this.operResultDesLow = operResultDesLow;
    }

    /**
     * 当前操作票序号
     */
    private int curSerialNumber;

    public int getCurSerialNumber() {
        return curSerialNumber;
    }

    public void setCurSerialNumber(int curSerialNumber) {
        this.curSerialNumber = curSerialNumber;
    }

    /**
     * A相电压百分比1字节
     */
    private byte AVoltagePercentage;

    public byte getAVoltagePercentage() {
        return AVoltagePercentage;
    }

    public void setAVoltagePercentage(byte AVoltagePercentage) {
        this.AVoltagePercentage = AVoltagePercentage;
    }

    /**
     * B相电压百分B比1字节
     */
    private byte BVoltagePercentage;

    public byte getBVoltagePercentage() {
        return BVoltagePercentage;
    }

    public void setBVoltagePercentage(byte BVoltagePercentage) {
        this.BVoltagePercentage = BVoltagePercentage;
    }

    /**
     * C相电压百分比1字节
     */
    private byte CVoltagePercentage;

    public byte getCVoltagePercentage() {
        return CVoltagePercentage;
    }

    public void setCVoltagePercentage(byte CVoltagePercentage) {
        this.CVoltagePercentage = CVoltagePercentage;
    }

    /**
     * 电压状态1字节
     */
    private byte voltageCondition;

    public byte getVoltageCondition() {
        return voltageCondition;
    }

    public void setVoltageCondition(byte voltageCondition) {
        this.voltageCondition = voltageCondition;
    }

    /**
     * 标定值1字节
     */
    private byte calibrationValue;

    public byte getCalibrationValue() {
        return calibrationValue;
    }

    public void setCalibrationValue(byte calibrationValue) {
        this.calibrationValue = calibrationValue;
    }

    /**
     * 设备类型1字节
     */
    private byte deviceType;

    public byte getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }
}