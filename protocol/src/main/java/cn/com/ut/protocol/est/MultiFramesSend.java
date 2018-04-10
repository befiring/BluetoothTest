package cn.com.ut.protocol.est;

/**
 * 多帧发送
 * Created by zhangyihuang on 2017/3/29.
 */
public class MultiFramesSend extends MultiFramesSendBase {
    private ProtocolEst protocol;

    public MultiFramesSend(ProtocolEst protocol) {
        this.protocol = protocol;
    }
}