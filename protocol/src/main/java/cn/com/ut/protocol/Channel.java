package cn.com.ut.protocol;

/**
 * 通道
 * Created by zhangyihuang on 2017/1/19.
 */
public class Channel extends IChannel {

    @Override
    public boolean open() {
        this.getProtocol().setEnabled(true);
        if (this.getPort().isConnected())
            return true;
        else
            return this.getPort().open();
    }

    @Override
    public void close() {
        this.getPort().close();
        this.getProtocol().setEnabled(false);
    }

    @Override
    public boolean isOpen() {
        return this.getProtocol().isEnabled() && this.getPort().isConnected();
    }
}