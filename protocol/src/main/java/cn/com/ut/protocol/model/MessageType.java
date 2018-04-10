package cn.com.ut.protocol.model;

/**
 * 钥匙消息类型
 * Created by zhangyihuang on 2016/12/22.
 */
public enum MessageType {
    /**
     * 一般提示信息
     */
    Info,

    /**
     * 警告信息
     */
    Warning,

    /**
     * 通知
     */
    Notice,

    /**
     * 运行错误信息
     */
    Error,

    /**
     * 扑捉到的例外
     */
    Exception
}