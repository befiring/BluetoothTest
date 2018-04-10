package cn.com.ut.protocol.entity;

/**
 * 解锁器验电结果类
 * Created by zhangyihuang on 2017/12/18.
 */
public class JsqYDResult {
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