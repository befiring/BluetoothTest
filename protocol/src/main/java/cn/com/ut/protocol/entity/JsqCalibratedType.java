package cn.com.ut.protocol.entity;

/**
 * 无线锁标定类型
 * Created by zhangyihuang on 2016/12/27.
 */

public class JsqCalibratedType {

    /**
     * 验电
     */
    public static final byte YD = 0x01;

    /**
     * 电编码锁
     */
    public static final byte DBMS = 0x02;

    /**
     * 电编码锁电流表
     */
    public static final byte DBMSDL = 0x03;
}