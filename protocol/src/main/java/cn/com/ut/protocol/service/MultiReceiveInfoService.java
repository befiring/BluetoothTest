package cn.com.ut.protocol.service;

/**
 * 多帧接收信息
 * Created by zhangyihuang on 2017/4/15.
 */
public class MultiReceiveInfoService extends IMultiReceiveInfoService {
    /**
     * 设置接收比例
     *
     * @param index 第几帧
     * @param count 总帧数
     * @param info  信息
     */
    @Override
    public void setReceiveIndexEvent(int index, int count, String info) {
        if (mOnReceiveIndexEventListener != null) {
            mOnReceiveIndexEventListener.onReceiveIndexEvent(index, count, info);
        }
    }

    /**
     * 设置错误信息
     *
     * @param info
     */
    @Override
    public void setErrorInfoEvent(String info) {
        if (mOnErrorInfoEventListener != null) {
            mOnErrorInfoEventListener.onErrorInfoEvent(info);
        }
    }

    /**
     * 设置信息
     *
     * @param info
     */
    @Override
    public void setInfoEvent(String info) {
        if (mOnInfoEventListener != null) {
            mOnInfoEventListener.onInfoEvent(info);
        }
    }

    @Override
    public void onReturnData(Object data) {

    }

    @Override
    public void onReturnNull() {

    }
}