package cn.com.ut.protocol.model;

/**
 * 操作结果类型描述
 * Created by zhangyihuang on 2017/1/20.
 */
public class ResultTypeDescription {
    /**
     * 走错间隔
     */
    public static final byte WrongInterval = 0x01;

    /**
     * 正常操作
     */
    public static final byte Normal = 0x02;

    /**
     * 锁具故障
     */
    public static final byte LockFault = 0x03;

    /**
     * 跳步操作
     */
    public static final byte Skip = 0x04;

    /**
     * 状态上报
     */
    public static final byte StateReport = 0x05;

    /**
     * 操作异常，需提示用户，用户干预决定是否过项
     */
    public static final byte Exception = 0x06;
}