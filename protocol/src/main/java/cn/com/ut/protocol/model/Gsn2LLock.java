package cn.com.ut.protocol.model;

import cn.com.ut.protocol.entity.JsqOperateResult;

/**
 * GSN2L
 * Created by zhangyihuang on 2017/1/20.
 */
public class Gsn2LLock extends LockBase implements ILockBase {

    /**
     * 验电操作
     */
    protected boolean isYd;

    public boolean getIsYd() {
        return isYd;
    }

    public void setIsYd(boolean isYd) {
        this.isYd = isYd;
    }

    public Gsn2LLock(short lockType) {
        super(lockType);
    }

    /**
     * 锁具名称
     */
    @Override
    public String getLockName() {
        if (isYd) {
            return super.getLockName() + "验电";
        } else {
            return super.getLockName() + "电编码";
        }
    }

    @Override
    public void receiveOptResult(JsqOperateResult result) {
        byte resultType = (byte) ((result.getOperResultDesLow() & 0xF0) >> 4);
        byte resultLow = (byte) (result.getOperResultDesLow() & 0x0F);

        if (resultType == ResultTypeDescription.Normal) {
            if (resultLow == 0x01) {
                mOnVoltageReportListener.onVoltageReport(result.getAVoltagePercentage(), result.getBVoltagePercentage(), result.getCVoltagePercentage(), GsnResult.ELECTRICITY_YES);
            } else {
                if (resultLow == 0x02) {
                    mOnVoltageReportListener.onVoltageReport(result.getAVoltagePercentage(), result.getBVoltagePercentage(), result.getCVoltagePercentage(), GsnResult.ELECTRICITY_NO);
                } else {
                    if (resultLow == 0x03) {
                        mOnMessageActionListener.onMessageEvent("锁具错误！", MessageType.Exception);
                    } else {
                        mOnMessageActionListener.onMessageEvent("解锁结果异常！", MessageType.Exception);
                    }
                }
            }
        } else {
            mOnMessageActionListener.onMessageEvent(getResultDescriptionNote(resultType), MessageType.Exception);
        }
    }

//    /**
//     * 处理操作结果
//     *
//     * @param resultType 结果类型
//     * @param result     结果
//     */
//    @Override
//    public void handleResult(byte resultType, byte result) {
//        super.handleResult(resultType, result);
//
//        if (resultType == ResultTypeDescription.Normal) {
//            if (result == 0x01) {
//                mOnMessageActionListener.onMessageEvent(getLockName() + "有电", MessageType.Warning);
//            } else {
//                if (result == 0x02) {
//                    mOnCompleteActionListener.onCompleteAction(this, getLockName() + "无电");
//                } else {
//                    if (result == 0x02) {
//                        mOnCompleteActionListener.onCompleteAction(this, getLockName() + "解锁成功");
//                    } else {
//                        mOnMessageActionListener.onMessageEvent("解锁结果异常！", MessageType.Exception);
//                    }
//                }
//            }
//        } else {
//            mOnMessageActionListener.onMessageEvent(getResultDescriptionNote(resultType), MessageType.Exception);
//        }
//    }
}