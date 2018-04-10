package cn.com.ut.protocol.service;

/**
 * 多帧发送进度和信息反馈接口
 * Created by zhangyihuang on 2017/4/19.
 */
public abstract class IMultiFramesSendListener {

    /**
     * 发送信息事件
     */
    protected OnSendInfoListener mOnSendInfoListener;

    public void setOnSendInfoListener(OnSendInfoListener onSendInfoListener) {
        this.mOnSendInfoListener = onSendInfoListener;
    }

    /**
     * 错误信息事件
     */
    protected OnErrorInfoListener mOnErrorInfoListener;

    public void setOnErrorInfoListener(OnErrorInfoListener onErrorInfoListener) {
        this.mOnErrorInfoListener = onErrorInfoListener;
    }

    /**
     * 发送多帧的比例，前面为第几帧，后面为总帧数
     */
    protected OnMultiFramesSendListener mOnMultiFramesSendListener;

    public void setOnMultiFramesSendListener(OnMultiFramesSendListener onMultiFramesSendListener) {
        this.mOnMultiFramesSendListener = onMultiFramesSendListener;
    }

    /**
     * 设置发送比例
     *
     * @param index 第几帧
     * @param count 总帧数
     * @param info  信息
     */
    public abstract void setSendProgressEvent(int index, int count, String info);

    /**
     * 设置错误信息
     *
     * @param info
     */
    public abstract void setErrorInfoEvent(String info);

    /**
     * 设置发送信息
     *
     * @param info
     */
    public abstract void setSendInfoEvent(String info);


    public interface OnSendInfoListener {
        /**
         * 信息事件
         */
        void onSendInfo(String info);
    }

    public interface OnErrorInfoListener {
        /**
         * 错误信息事件
         */
        void onErrorInfo(String info);
    }

    public interface OnMultiFramesSendListener {
        /**
         * 发送多帧的比例，前面为第几帧，后面为总帧数
         */
        void onMultiFramesSend(int index, int count, String info);
    }
}