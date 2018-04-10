package cn.com.ut.protocol.model;

import cn.com.ut.protocol.entity.JsqCalibrateResult;
import cn.com.ut.protocol.entity.JsqOperateResult;

/**
 * 锁具基类接口定义
 * Created by zhangyihuang on 2017/1/19.
 */
public interface ILockBase {

    /**
     * 锁具名称
     */
    public String getLockName();

    /**
     * 锁具类型，按解锁器与智能手持机应用通讯规约定义
     */
    public short getLockType();

    public void setLockType(short lockType);

    /**
     * 锁码
     */
    public long getRfid();

    public void setRfid(long rfid);

    public void setDescription(short description);

    /**
     * 操作描述
     */
    public short getDescription();

    /**
     * 操作附加信息
     */
    public byte[] getExtraInfo();

    /**
     * 操作附加信息
     */
    public void setExtraInfo(byte[] extraInfo);

    /**
     * 收到操作结果
     *
     * @param result 操作结果
     */
    void receiveOptResult(JsqOperateResult result);

    /**
     * 解锁器操作结果
     */
    JsqOperateResult getOperateResult();

    /**
     * 接收无线锁标定结果
     *
     * @param result
     */
    void receiveCalibrateResult(JsqCalibrateResult result);

    /**
     * 设置操作完成事件监听接口
     */
    void setOnCompleteActionListener(OnCompleteActionListener onCompleteActionListener);

    /**
     * 设置解锁器复位事件监听接口
     */
    void setOnJsqResetActionListener(OnJsqResetActionListener onJsqResetActionListener);

    /**
     * 设置消息事件监听接口
     */
    void setOnMessageActionListener(OnMessageEventListener onMessageEventListener);

    /**
     * 电压值监听
     */
    void setOnVoltageReportListener(OnVoltageReportListener onVoltageReportListener);

    public interface OnCompleteActionListener {
        /**
         * 操作完成事件
         */
        void onCompleteAction(ILockBase lockBase, String msg);
    }

    public interface OnJsqResetActionListener {
        /**
         * 解锁器复位事件
         */
        void onJsqResetAction();
    }

    public interface OnVoltageReportListener {
        /**
         * 电压 A/B/C三相值
         */
        void onVoltageReport(Byte aVoltagePercentage, Byte bVoltagePercentage, Byte cVoltagePercentage, int gsnResult);
    }
}