package cn.com.ut.protocol.service;

/**
 * 多帧发送信息
 * Created by zhangyihuang on 2017/4/19.
 */
public class MultiFramesSendService extends IMultiFramesSendListener {

    @Override
    public void setSendProgressEvent(int index, int count, String info) {
        if (mOnMultiFramesSendListener != null) {
            mOnMultiFramesSendListener.onMultiFramesSend(index, count, info);
        }
    }

    @Override
    public void setErrorInfoEvent(String info) {
        if (mOnErrorInfoListener != null) {
            mOnErrorInfoListener.onErrorInfo(info);
        }
    }

    @Override
    public void setSendInfoEvent(String info) {
        if (mOnSendInfoListener != null) {
            mOnSendInfoListener.onSendInfo(info);
        }
    }
}