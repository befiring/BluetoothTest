package cn.com.ut.protocol;

import cn.com.ut.protocol.core.IProtocolBase;
import cn.com.ut.protocol.port.IPortBase;

/**
 * 通道
 * Created by zhangyihuang on 2017/1/19.
 */
public abstract class IChannel {

    /**
     * 端口
     */
    private IPortBase port;

    public IPortBase getPort() {
        return port;
    }

    public void setPort(IPortBase port) {
        this.port = port;
    }

    /**
     * 是否共享的端口, 与其他通道共享一个端口
     */
    private boolean isSharedPort;

    public boolean IsSharedPort() {
        return isSharedPort;
    }

    public void setIsSharedPort(boolean isSharedPort) {
        this.isSharedPort = isSharedPort;
    }

    /**
     * 规约
     */
    private IProtocolBase protocol;

    public IProtocolBase getProtocol() {
        return protocol;
    }

    public void setProtocol(IProtocolBase protocol) {
        this.protocol = protocol;
    }

    /**
     * ID
     */
    private String guid;

    public String getId() {
        return guid;
    }

    public void setId(String guid) {
        this.guid = guid;
    }

    /**
     * 名称
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
     * 是否开启
     *
     * @return
     */
    public abstract boolean isOpen();

}