package cn.com.ut.protocol.port;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.ut.protocol.DataConvert;
import cn.com.ut.protocol.model.MessageType;
import cn.com.ut.protocol.model.OnMessageEventListener;
import cn.com.ut.protocol.model.PortType;

/**
 * Created by zhangyihuang on 2016/12/23.
 * 端口基类
 */
public abstract class PortBase implements IPortBase {

    /**
     * 打印报文
     */
    protected boolean writeReport;

    /**
     * 接收缓冲区大小
     */
    protected int buffersLength;

    /**
     * 是否连接
     */
    protected boolean isConnected;

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 端口名称
     */
    protected String portName;

    /**
     * 端口类型
     */
    protected PortType portType;

    public PortType getPortType() {
        return portType;
    }

    /**
     * 端口状态改变事件,是否连接上
     */
    public OnPortStatusChangedListener mOnPortStatusChangedListener;

    public void setOnPortStatusChangedListener(OnPortStatusChangedListener onPortStatusChangedListener) {
        this.mOnPortStatusChangedListener = onPortStatusChangedListener;
    }

    /**
     * 数据到达事件
     */
    protected List<OnDataReceiveListener> mOnDataReceiveListener;

    /**
     * 端口报文
     */
    public OnPortReportListener mOnPortReportListener;

    public void setOnPortReportListener(OnPortReportListener onPortReportListener) {
        this.mOnPortReportListener = onPortReportListener;
    }

    /**
     * 端口产生的消息事件
     */
    public OnMessageEventListener mOnMessageEventListener;

    public void setOnMessageEventListener(OnMessageEventListener onMessageEventListener) {
        this.mOnMessageEventListener = onMessageEventListener;
    }

    /**
     * 发送失败
     */
    public OnSendFailEventListener mOnSendFailEventListener;

    /**
     * 超时时间，字节间超时
     */
    private int timeOut = 200;

    /**
     * 上一个系统启动豪秒数
     */
    private long lastSystemTickCout = System.currentTimeMillis();

    /**
     * 缓冲区初始大小1MB
     */
    private final int BUFFER_INITIALIZATION_SIZE = 1024 * 1024 * 1;

    /**
     * 缓冲区增量大小1MB
     */
    private final int BUFFER_INCREMENT_SIZE = 1024 * 1024 * 1;

    /**
     * 缓冲区最小空闲空间1KB
     */
    private final int BUFFER_MIN_EMPTY_SPACE = 1024 * 1;

    /**
     * 接收缓冲区
     */
    protected byte[] buffers;

    /**
     * 开始写入接收缓冲区的偏移量
     */
    protected int offset;

    /**
     * 开始写入接收缓冲区的偏移量
     */
    public int getOffset() {
        return offset;
    }

    /**
     * 可读数据区开始指针
     */
    private int readPointer;

    public PortBase() {
        mOnDataReceiveListener = new ArrayList<OnDataReceiveListener>();
    }

    public void addDataReceive(OnDataReceiveListener onDataReceive) {
        mOnDataReceiveListener.add(onDataReceive);
    }

    /**
     * 添加消息
     *
     * @param msg     内容
     * @param msgType 类型
     * @return
     */
    protected void addMessage(String msg, MessageType msgType) {
        if (null != mOnMessageEventListener) {
            mOnMessageEventListener.onMessageEvent(msg, msgType);
        }
    }

    /**
     * 添加发送失败
     *
     * @param msg 数据
     * @param msg 原因
     * @return
     */
    protected void addSendFailEvent(byte[] data, String msg) {
        if (null != mOnSendFailEventListener) {
            mOnSendFailEventListener.onSendFailEvent(data, msg);
        }
    }

    public void setWriteReport(boolean enableDatagram) {
        this.writeReport = enableDatagram;
    }

    /**
     * @param defaultAddr 连接的解锁器蓝牙地址
     */
    public void setPortConfig(String defaultAddr) {

    }

    /**
     * 设置是否启用报文日志
     *
     * @param enableDatagram 是否启用报文日志
     */
    public void setEnableDatagram(boolean enableDatagram) {
        this.writeReport = enableDatagram;
    }

    /**
     * @param defaultAddr    连接的解锁器蓝牙地址
     * @param enableDatagram 是否启用报文日志
     */
    public void setPortConfig(String defaultAddr, boolean enableDatagram) {
        this.writeReport = enableDatagram;
    }

    /**
     * 发送数据
     *
     * @param data
     * @return
     */
    public abstract void send(byte[] data);

    /**
     * 发送数据
     *
     * @param cmdName     发送报文命令名
     * @param frameNum    当前第几帧
     * @param totalFrames 总帧数
     * @param data        报文字节数组
     */
    public abstract void send(String cmdName, int frameNum, int totalFrames, byte[] data);

    /**
     * 打开
     *
     * @return
     */
    public abstract boolean open();

    /**
     * 关闭
     *
     * @return
     */
    public abstract void close();

    /**
     * 重新设置接收缓存
     */
    private void resetRevice() {
        readPointer = 0;
        offset = 0;
    }

    protected void initializeBuffer() {
        buffers = new byte[BUFFER_INITIALIZATION_SIZE];
        buffersLength = buffers.length;
    }

    /**
     * 设置端口状态改变消息
     */
    protected void onPortStatusChanged() {
        if (null != mOnPortStatusChangedListener)
            mOnPortStatusChangedListener.onPortStatusChanged(isConnected);
    }

    /**
     * 确发接收到数据事件
     */
    protected void reviceDataEvent() {
        ProcessResult result = ProcessResult.Null;
        if (null != mOnDataReceiveListener) {
            for (OnDataReceiveListener dataEvent : mOnDataReceiveListener) {
                result = dataEvent.onDataReceive(buffers, readPointer, offset - readPointer);
                if (result == ProcessResult.Finished || result == ProcessResult.WaitingData || result == ProcessResult.FullHead) {
                    break;//符合某种规约
                }
            }
        }
        if (result == ProcessResult.Finished || result == ProcessResult.Null) {
            resetRevice();
        }
    }

    public void writeReportToFile() {
        writeReportToFile(buffers, 0, offset, false);
    }

    /**
     * 端口报文写入文件，文件超过1M将删除重写，文件名不能使用了操作系统设备保留字，如com、con、lpt等
     *
     * @param buffer 数据
     * @param offset 偏移量
     * @param count  总数
     * @param send   是否为发送
     * @return
     */
    protected void writeReportToFile(byte[] buffer, int offset, int count, boolean send) {
        writeReportToFile("", buffer, offset, count, count, -1, -1, send);
    }

    /**
     * 端口报文写入文件，文件超过1M将删除重写，文件名不能使用了操作系统设备保留字，如com、con、lpt等
     *
     * @param cmdName 发送报文命令的名称
     * @param buffer  数据
     * @param offset  偏移量
     * @param count   总数
     * @param send    是否为发送
     */
    protected void writeReportToFile(String cmdName, byte[] buffer, int offset, int count, int frameNum, int totalFrames, boolean send) {
        writeReportToFile(cmdName, buffer, offset, count, count, frameNum, totalFrames, send);
    }

    /**
     * 端口报文写入文件，文件超过1M将删除重写，文件名不能使用了操作系统设备保留字，如com、con、lpt等
     *
     * @param buffer 数据缓冲区
     * @param offset 偏移量
     * @param count  本次操作长度
     * @param amount 数据包总长度
     * @param isSend 是否为发送（否则为接收）
     * @return
     */
    protected void writeReportToFile(String cmdName, byte[] buffer, int offset, int count, int amount, int frameNum, int totalFrames, boolean isSend) {
        if (count <= 0 || mOnPortReportListener == null || !writeReport)
            return;

        long currentSystemTickCout = lastSystemTickCout;
        lastSystemTickCout = System.currentTimeMillis();
        StringBuffer reportBuilder = new StringBuffer();

        String str = "%s:%s(%s) 偏:%s 总:%s 帧[%s：%s]";
        str = String.format(str, isSend ? "发" : "收", cmdName, lastSystemTickCout - currentSystemTickCout, offset, amount, frameNum, totalFrames);
        reportBuilder.append(str);
        reportBuilder.append("\r\n");
        reportBuilder.append(DataConvert.bytesToString(buffer, offset, count, " "));
        String report = reportBuilder.toString();
        if (null != mOnPortReportListener) {
            mOnPortReportListener.onPortReport(report);
        }
    }

    protected void autoIncrementBuffer() {
        // 如果缓冲区空闲空间不足，则增加缓冲区大小
        while (buffers.length < offset + BUFFER_MIN_EMPTY_SPACE) {
            Arrays.copyOf(buffers, buffers.length + BUFFER_INCREMENT_SIZE);
            buffersLength = buffers.length;
        }
    }
}