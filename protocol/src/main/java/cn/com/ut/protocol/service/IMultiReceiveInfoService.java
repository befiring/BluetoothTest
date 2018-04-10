package cn.com.ut.protocol.service;

/**
 * 多帧接收信息接口
 * Created by zhangyihuang on 2017/4/15.
 */
public abstract class IMultiReceiveInfoService {
    /**
     * 信息事件
     */
    protected OnInfoEventListener mOnInfoEventListener;

    public void setOnInfoEventListener(OnInfoEventListener onInfoEventListener) {
        this.mOnInfoEventListener = onInfoEventListener;
    }

    /**
     * 错误信息事件
     */
    protected OnErrorInfoEventListener mOnErrorInfoEventListener;

    public void setOnErrorInfoEventListener(OnErrorInfoEventListener onErrorInfoEventListener) {
        this.mOnErrorInfoEventListener = onErrorInfoEventListener;
    }

    /**
     * 接收到的比例，前面为第几帧，后面为总帧数
     */
    protected OnReceiveIndexEventListener mOnReceiveIndexEventListener;

    public void setOnReceiveIndexEventListener(OnReceiveIndexEventListener onReceiveIndexEventListener) {
        this.mOnReceiveIndexEventListener = onReceiveIndexEventListener;
    }

    /**
     * 设置接收比例
     *
     * @param index 第几帧
     * @param count 总帧数
     * @param info  信息
     */
    public abstract void setReceiveIndexEvent(int index, int count, String info);

    /**
     * 设置错误信息
     *
     * @param info
     */
    public abstract void setErrorInfoEvent(String info);

    /**
     * 设置信息
     *
     * @param info
     */
    public abstract void setInfoEvent(String info);

    public abstract void onReturnData(Object data);

    public abstract void onReturnNull();

    public interface OnInfoEventListener {
        /**
         * 信息事件
         */
        void onInfoEvent(String info);
    }

    public interface OnErrorInfoEventListener {
        /**
         * 错误信息事件
         */
        void onErrorInfoEvent(String info);
    }

    public interface OnReceiveIndexEventListener {
        /**
         * 接收到的比例，前面为第几帧，后面为总帧数
         */
        void onReceiveIndexEvent(int index, int count, String info);
    }
}