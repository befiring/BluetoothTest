package cn.com.ut.protocol.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import cn.com.ut.protocol.AppRuntime;
import cn.com.ut.protocol.BoolResult;
import cn.com.ut.protocol.model.MessageType;
import cn.com.ut.protocol.model.OnMessageEventListener;
import cn.com.ut.protocol.model.PortType;
import cn.com.ut.protocol.port.IPortBase;
import cn.com.ut.protocol.port.ProcessResult;

/**
 * 规约基类
 * Created by zhangyihuang on 2016/12/26.
 */
public abstract class ProtocolBase extends IProtocolBase implements IPortBase.OnDataReceiveListener {

    /**
     * 发送队列
     */
    private LinkedBlockingQueue<Frame> sendQueue = new LinkedBlockingQueue<>();
    private ExecutorService executorService;
    private Future<?> futureResult;

    /**
     * 发送帧时间间隔，默认10ms
     */
    private int sendInterval = 10;

    /**
     * 当前使用端口
     */
    private IPortBase mPort;

    /**
     * 规约内部使用的帧
     */
    protected Frame innerFrame;

    /**
     * 规约类型
     */
    protected int protocolFeatureType;

    public int getProtocolFeatureType() {
        return protocolFeatureType;
    }

    public void setProtocolFeatureType(int protocolFeatureType) {
        this.protocolFeatureType = protocolFeatureType;
    }

    /**
     * 规约名
     */
    protected String protocolName;

    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    /**
     * 是否启用
     */
    protected boolean isEnabled = false;

    /**
     * 启用
     */
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * 规约产生的消息事件
     */
    public OnMessageEventListener mOnMessageEventListener;

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
     * 获得当前的帧
     */
    protected abstract Frame getFrame();

    /**
     * 校验出错
     *
     * @param frame 收到的校验出错帧
     */
    protected abstract void crcError(Frame frame);

    /**
     * 处理接收到的数据帧
     *
     * @param frame
     */
    protected abstract void processReceiveFrame(Frame frame);

    /**
     * 触发接收到规约帧事件，会以线程池处理，让接收处理线程不会堵在那
     *
     * @param frame
     */
    protected void triggerReceiveFrame(Frame frame) {
        processReceiveFrame(frame);
    }

    /**
     * 当前使用端口, 请在端口关闭的情况设置该属性
     */
    public IPortBase getPort() {
        return mPort;
    }

    public void setPort(IPortBase port) {
        mPort = port;
        mPort.addDataReceive(this);
    }

    public ProtocolBase() {
        this.innerFrame = getFrame();
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * 处理收到的数据
     *
     * @param data  数据
     * @param index 开始位
     * @param count 总数
     * @return 4:收到完整帧并处理，3:数据不够等数据， 2:处理完同步头，1：只处理了部份同步头，0：没找到同步头
     */
    public ProcessResult onDataReceive(byte[] data, int index, int count) {
        int offset = 0; //偏移量
        HeadData headData = new HeadData(offset);
        ProcessResult result = findHead(data, index, count, headData); //处理结果

        if (result == ProcessResult.FullHead) {
            offset = headData.offset;
            int receiveLength = count - offset + innerFrame.getHead().length; //收到的数据长度
            if (receiveLength > innerFrame.getContentStart()) {
                //至少要收到正文开始的长度
                int frameLength = innerFrame.getFrameLength(data, index + offset - innerFrame.getHead().length);
                //给出同步头的开始位
                if (frameLength <= receiveLength) { //满足了一帧数据
                    //                        数据         偏移量        长度
                    if (innerFrame.checkCrc(data, index + offset, frameLength - innerFrame.getHead().length)) {
                        Frame curFrame = innerFrame.handleFrame(data, index + offset - innerFrame.getHead().length, frameLength);
                        mPort.writeReportToFile();
                        triggerReceiveFrame(curFrame);
                        int processLength = offset - innerFrame.getHead().length + frameLength; //被处理了的数据长度
                        onDataReceive(data, index + processLength, count - processLength); //递归
                        result = ProcessResult.Finished;
                    } else {
                        //校验出错
                        Frame curFrame = innerFrame.handleFrame(data, index + offset - innerFrame.getHead().length, frameLength);
                        crcError(curFrame);
                        result = ProcessResult.Finished;
                    }
                } else {
                    result = ProcessResult.WaitingData;//等数据
                }
            } else {
                result = ProcessResult.WaitingData;//等数据
            }
        }
        return result;
    }

    /**
     * 找同步头
     *
     * @param data     数据
     * @param index    开始位
     * @param count    总数
     * @param headData 处理完后同步头的偏移位置
     * @return 2:处理完同步头，1：只处理了部份同步头，0：没找到同步头
     */
    protected ProcessResult findHead(byte[] data, int index, int count, HeadData headData) {
        int headNum = 0; //同步头处理到第几个数据
        for (; headData.offset < count; headData.offset++) {
            if (headNum < innerFrame.getHead().length) {
                if (data[index + headData.offset] == innerFrame.getHead()[headNum]) {
                    headNum++;//找同步头
                } else {
                    headNum = 0;
                }
            } else if (headNum == innerFrame.getHead().length) { //找到同步头，符合某种规约
                break;
            }
        }
        if (headNum == innerFrame.getHead().length)
            return ProcessResult.FullHead;
        else if (headNum > 0 && headNum < innerFrame.getHead().length)
            return ProcessResult.PartHead;
        else
            return ProcessResult.Null;
    }

    /**
     * 启动发送队列
     *
     * @param enabled 是否启用
     * @return
     */
    protected void enableSendQueueWorker(boolean enabled) {
        if (enabled) {
            if (null != futureResult) {
                futureResult.cancel(true);
                futureResult = null;
            }
            sendQueue.clear();
            futureResult = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    sendQueueWorker();
                }
            });
        } else {
            if (null != futureResult) {
                futureResult.cancel(true);
                futureResult = null;
            }
        }
    }

    /**
     * 发送工作队列
     */
    protected void sendQueueWorker() {
        try {
            Frame frame = null;
            while (isEnabled()) {
                frame = sendQueue.take();
                //Log.w("ProtocolBase1111", DataWriteUtils.getFrameBytes(frame.getAllFrameBytes()));
                sendData(frame);
            }
            //Log.w("ProtocolBase2222", DataWriteUtils.getFrameBytes(frame.getAllFrameBytes()));
        } catch (InterruptedException ex) {
            addMessage(getProtocolName() + "发送异常" + ex.getMessage(), MessageType.Exception);
        }
    }

    /**
     * 由端口发送数据，提供给派生类重写
     *
     * @param frame
     * @return
     */
    protected void sendData(Frame frame) {
        try {
            getPort().send(frame.getCmdName(), frame.getFrameNum(), frame.getTotalFrames(), frame.getAllFrameBytes());
        } catch (Exception ex) {
            frame.sendFailure(ex.getMessage());
        }
    }

    /**
     * 插入发送队列
     *
     * @param frame
     * @return
     */
    public void enqueueSendData(Frame frame) {
        try {
            if (getPort().isConnected()) {
                if (getPort().getPortType() != PortType.BluetoothClient) {
                    AppRuntime.setIsBusinessCommunicationBusy();
                }
                //Log.w("ProtocolBase3333", DataWriteUtils.getFrameBytes(frame.getAllFrameBytes()));
                sendQueue.put(frame);
            } else {
                frame.sendFailure(getProtocolName() + "发送失败,端口未连接!");
            }
        } catch (InterruptedException ex) {
            addMessage(getProtocolName() + "插入队列失败" + ex.getMessage(), MessageType.Exception);
        }
    }

    /**
     * 尝试发送三次
     *
     * @param frame   发送帧
     * @param timeOut 第次超时时间
     * @return
     */
    public BoolResult sendThree(Frame frame, long timeOut) throws InterruptedException {
        if (getPort().isConnected()) {
            final ReentrantLock sendLock = frame.getSendLock();
            sendLock.lockInterruptibly();
            try {
                int i = 0;
                for (; i < 3; i++) {
                    enqueueSendData(frame);
                    if (frame.await(timeOut, TimeUnit.MILLISECONDS))
                        break;
                }
                if (i >= 3) {
                    frame.sendFailure(getProtocolName() + "通讯超时");
                    onTimeOut();
                }
            } finally {
                sendLock.unlock();
            }
        } else {
            frame.sendFailure(getProtocolName() + "发送失败,端口未连接!");
        }
        return frame.getSendResult();
    }

    /**
     * 发送一次
     *
     * @param frame   发送帧
     * @param timeOut 第次超时时间
     * @return
     */
    public BoolResult sendOne(Frame frame, long timeOut) throws InterruptedException {
        if (getPort().isConnected()) {
            final ReentrantLock sendLock = frame.getSendLock();
            sendLock.lockInterruptibly();
            try {
                enqueueSendData(frame);
                if (!frame.await(timeOut, TimeUnit.SECONDS)) {
                    frame.sendFailure(getProtocolName() + "通讯超时");
                    onTimeOut();
                }
            } finally {
                sendLock.unlock();
            }
        } else {
            frame.sendFailure(getProtocolName() + "发送失败,端口未连接!");
        }
        return frame.getSendResult();
    }

    public void onTimeOut(){

    }

    class HeadData {
        public int offset;

        public HeadData(int offset) {
            this.offset = offset;
        }
    }
}