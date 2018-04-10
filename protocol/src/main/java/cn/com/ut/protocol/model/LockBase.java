package cn.com.ut.protocol.model;

import java.util.HashMap;
import java.util.Map;

import cn.com.ut.protocol.entity.JsqCalibrateResult;
import cn.com.ut.protocol.entity.JsqOperateResult;

/**
 * 基本锁具, 机械锁,电编码锁,机械锁+电编码锁,无线锁-电编码锁,无线锁-机械锁,无线智能防火门锁
 * Created by zhangyihuang on 2017/1/19.
 */
public class LockBase implements ILockBase {
    /**
     * 锁具类型，按解锁器与智能手持机应用通讯规约定义
     */
    private short lockType;

    /**
     * 锁具名称
     */
    private String lockName;

    /**
     * 锁码
     */
    private long rfid;

    /**
     * 操作描述
     */
    private short description;

    /**
     * 操作附加信息
     */
    private byte[] extraInfo;

    /**
     * 操作结果类型描述
     */
    private Map<Byte, String> resultDescriptionNoteMap;

    /**
     * 解锁器操作结果
     */
    private JsqOperateResult operateResult;

    /**
     * 操作完成事件
     */
    protected ILockBase.OnCompleteActionListener mOnCompleteActionListener;

    /**
     * 解锁器复位事件
     */
    protected ILockBase.OnJsqResetActionListener mOnJsqResetActionListener;

    /**
     * 提示消息事件
     */
    protected OnMessageEventListener mOnMessageActionListener;

    /**
     * 电压值监听事件
     */
    protected ILockBase.OnVoltageReportListener mOnVoltageReportListener;

    public LockBase(short lockType) {
        setLockType(lockType);
        resultDescriptionNoteMap = new HashMap<Byte, String>();
        resultDescriptionNoteMap.put(new Byte(ResultTypeDescription.WrongInterval), "走错间隔");
        resultDescriptionNoteMap.put(new Byte(ResultTypeDescription.Normal), "正常操作");
        resultDescriptionNoteMap.put(new Byte(ResultTypeDescription.LockFault), "锁具故障");
        resultDescriptionNoteMap.put(new Byte(ResultTypeDescription.Skip), "跳步操作");
        resultDescriptionNoteMap.put(new Byte(ResultTypeDescription.StateReport), "状态上报");
        resultDescriptionNoteMap.put(new Byte(ResultTypeDescription.Exception), "操作异常");
    }

    @Override
    public String getLockName() {
        Long lockTypeLong = Long.valueOf(lockType);
        lockName = LockInfo.getLockName(lockTypeLong);
        return lockName;
    }

    @Override
    public short getLockType() {
        return lockType;
    }

    @Override
    public void setLockType(short lockType) {
        this.lockType = lockType;
    }

    @Override
    public long getRfid() {
        return rfid;
    }

    @Override
    public void setRfid(long rfid) {
        this.rfid = rfid;
    }

    @Override
    public void setDescription(short description) {
        this.description = description;
    }

    @Override
    public short getDescription() {
        return description;
    }

    @Override
    public byte[] getExtraInfo() {
        return extraInfo;
    }

    @Override
    public void setExtraInfo(byte[] extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public JsqOperateResult getOperateResult() {
        return operateResult;
    }

    /**
     * 收到操作结果
     *
     * @param result 操作结果
     */
    @Override
    public void receiveOptResult(JsqOperateResult result) {
        operateResult = result;
        byte bitHigh4 = (byte) ((result.getOperResultDesLow() & 0xF0) >> 4);
        byte bitLow4 = (byte) (result.getOperResultDesLow() & 0x0F);
        handleResult(bitHigh4, bitLow4);
    }

    @Override
    public void receiveCalibrateResult(JsqCalibrateResult result) {

    }

    @Override
    public void setOnCompleteActionListener(OnCompleteActionListener onCompleteActionListener) {
        this.mOnCompleteActionListener = onCompleteActionListener;
    }

    @Override
    public void setOnJsqResetActionListener(OnJsqResetActionListener onJsqResetActionListener) {
        this.mOnJsqResetActionListener = onJsqResetActionListener;
    }

    @Override
    public void setOnMessageActionListener(OnMessageEventListener onMessageEventListener) {
        this.mOnMessageActionListener = onMessageEventListener;
    }

    @Override
    public void setOnVoltageReportListener(OnVoltageReportListener onVoltageReportListener) {
        this.mOnVoltageReportListener = onVoltageReportListener;
    }

    /**
     * 处理操作结果
     *
     * @param resultType 结果类型
     * @param result     结果
     */
    public void handleResult(byte resultType, byte result) {
        if (resultType == ResultTypeDescription.Normal) {
            if (result == 0x01) {
                String msg = "[%s]解锁成功！";
                msg = String.format(msg, lockName);
                mOnCompleteActionListener.onCompleteAction(this, msg);
            } else {
                mOnMessageActionListener.onMessageEvent("解锁结果异常！", MessageType.Exception);
            }
        } else if (resultType == ResultTypeDescription.Skip) {
            if (result == 0x01) {
                mOnCompleteActionListener.onCompleteAction(this, "跳步成功");
            } else {
                mOnMessageActionListener.onMessageEvent("跳步失败！", MessageType.Exception);
            }
        } else {
            mOnMessageActionListener.onMessageEvent(getResultDescriptionNote(resultType), MessageType.Exception);
        }
    }

    /**
     * 得到操作结果类型描述
     *
     * @param resultType 结果类型
     */
    protected String getResultDescriptionNote(byte resultType) {
        for (Byte key : resultDescriptionNoteMap.keySet()) {
            if (key.byteValue() == resultType) {
                return resultDescriptionNoteMap.get(key);
            }
        }
        return "未知的操作结果类型";
    }
}