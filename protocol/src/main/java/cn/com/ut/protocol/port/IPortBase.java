package cn.com.ut.protocol.port;

import cn.com.ut.protocol.model.OnMessageEventListener;
import cn.com.ut.protocol.model.PortType;

/**
 * Created by zhangyihuang on 2016/12/22.
 * 端口接口
 */
public interface IPortBase {

    void addDataReceive(OnDataReceiveListener onDataReceive);

    PortType getPortType();

    boolean isConnected();

    /**
     * @param defaultAddr 连接的解锁器蓝牙地址
     */
    void setPortConfig(String defaultAddr);

    /**
     * 设置是否启用报文日志
     *
     * @param enableDatagram 是否启用报文日志
     */
    void setEnableDatagram(boolean enableDatagram);

    /**
     * @param defaultAddr    连接的解锁器蓝牙地址
     * @param enableDatagram 是否启用报文日志
     */
    void setPortConfig(String defaultAddr, boolean enableDatagram);

    /**
     * 发送数据
     *
     * @param data
     */
    void send(byte[] data);

    /**
     * 发送数据
     *
     * @param cmdName     发送报文命令名
     * @param frameNum    当前第几帧
     * @param totalFrames 总帧数
     * @param data        报文字节数组
     */
    void send(String cmdName, int frameNum, int totalFrames, byte[] data);

    /**
     * 打开
     *
     * @return
     */
    boolean open();

    /**
     * 关闭
     */
    void close();

    /**
     * 设置端口状态改变事件监听接口
     */
    void setOnPortStatusChangedListener(OnPortStatusChangedListener onPortStatusChangedListener);

    /**
     * 设置端口产生的消息事件
     */
    void setOnMessageEventListener(OnMessageEventListener onMessageEventListener);

    /**
     * 设置接收端口报文事件
     */
    void setOnPortReportListener(OnPortReportListener onPortReportListener);

    /**
     * 输入日志到文件
     */
    void writeReportToFile();

    interface OnPortStatusChangedListener {
        /**
         * 端口状态改变事件,连接状态改变
         */
        void onPortStatusChanged(boolean bState);
    }

    interface OnDataReceiveListener {
        ProcessResult onDataReceive(byte[] buffers, int index, int count);
    }

    interface OnPortReportListener {
        void onPortReport(String report);
    }

    interface OnSendFailEventListener {
        void onSendFailEvent(byte[] buffers, String msg);
    }
}