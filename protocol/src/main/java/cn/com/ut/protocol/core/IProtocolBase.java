package cn.com.ut.protocol.core;


import cn.com.ut.protocol.BoolResult;
import cn.com.ut.protocol.model.OnMessageEventListener;
import cn.com.ut.protocol.port.IPortBase;

/**
 * 规约接口
 * Created by zhangyihuang on 2016/12/22.
 */
public abstract class IProtocolBase {
    /**
     * 发送帧时间间隔，默认10ms
     */
    protected int sendInterval;

    /**
     * 规约类型
     */
    protected int protocolFeatureType;

    /**
     * 规约名
     */
    protected String protocolName;

    /**
     * 启用
     */
    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean value);

    /**
     * 规约产生的消息事件
     */
    protected OnMessageEventListener mOnMessageEventListener;

    /**
     * 当前使用端口, 请在端口关闭的情况设置该属性
     */
    protected IPortBase port;

    /**
     * 发送一次
     *
     * @param frame   发送帧
     * @param timeOut 超时时间
     * @return BoolResult
     */
    abstract BoolResult sendOne(Frame frame, long timeOut) throws InterruptedException;

    /**
     * 尝试发送三次
     *
     * @param frame   发送帧
     * @param timeOut 超时时间
     * @return BoolResult
     */
    abstract BoolResult sendThree(Frame frame, long timeOut) throws InterruptedException;

    /**
     * 发送队列,没有返回，和是否成功
     *
     * @param frame 发送帧
     */
    abstract void enqueueSendData(Frame frame);
}