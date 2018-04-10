package cn.com.ut.protocol.model;

/**
 * Created by zhangyihuang on 2016/12/22.
 */
public interface OnMessageEventListener {
    void onMessageEvent(String msg, MessageType msgType);
}