package cn.com.ut.protocol.entity;

/**
 * Created by zhangyihuang on 2017/1/19.
 */
public class JsqLockType {

    /**
     * 机械锁
     */
    public static final short JXS = 0x01;

    /**
     * 电编码锁
     */
    public static final short DBMS = 0x02;

    /**
     * 电控锁
     */
    public static final short DKS = 0x03;

    /**
     * 单向电控锁
     */
    public static final short DXDKS = 0x04;

    /**
     * GSN验电
     */
    public static final short GSN = 0x05;

    /**
     * 无源验电
     */
    public static final short WYYD = 0x06;

    /**
     * 有源验电
     */
    public static final short YYYD = 0x07;

    /**
     * 超级防空锁
     */
    public static final short CJFKS = 0x08;

    /**
     * 反馈式电编码锁
     */
    public static final short FKSDBMS = 0x09;

    /**
     * 智能锁具
     */
    public static final short ZNSJ = 0x0A;

    /**
     * 智能压板
     */
    public static final short ZNYB = 0x0B;

    /**
     * 有源电位置检测
     */
    public static final short YYDWZJC = 0x0C;

    /**
     * 无源电位置检测
     */
    public static final short WYDWZJC = 0x0D;

    /**
     * 机械锁+电编码锁
     */
    public static final short JXSDBMS = 0x0E;

    /**
     * 电编码锁2H
     */
    public static final short DBMS2H = 0x0F;

    /**
     * 电编码锁3H
     */
    public static final short DBMS3H = 0x10;

    /**
     * 巡检锁
     */
    public static final short XJS = 0x11;

    /**
     * 无线锁-电编码锁
     */
    public static final short WXSDBMS = 0x12;

    /**
     * 无线锁-机械锁
     */
    public static final short WXSJXS = 0x13;

    /**
     * 无线锁-高压带电显示闭锁装置(GSN2L)
     */
    public static final short WXSGSN2L = 0x14;

    /**
     * 无线智能面板锁
     */
    public static final short WXZNMBS = 0x15;

    /**
     * 无线智能防火门锁
     */
    public static final short WXZNFHMS = 0x16;

    /**
     * 智能面板锁
     */
    public static final short ZNMBS = 0x17;

    /**
     * 智能防火门锁
     */
    public static final short ZNFHMS = 0x18;

    /**
     * GSN2验电器
     */
    public static final short GSN2 = 0x19;

    /**
     * 无线锁-外装式智能门锁
     */
    public static final short WXSWZSZNMS = 0x1A;

    /**
     * 跳步钥匙
     */
    public static final short TBYS = 0x79;

    /**
     * 锌合金挂锁
     */
    public static final short XHJGS = 0x80;

    /**
     * 塑料挂锁
     */
    public static final short SLGS = 0x81;

    /**
     * 链条锁
     */
    public static final short LTS = 0x82;

    /**
     * 把手球锁
     */
    public static final short BSQS = 0x83;

    /**
     * 把手锁
     */
    public static final short BSS = 0x84;

    /**
     * 插芯锁
     */
    public static final short CXS = 0x85;

    /**
     * 防盗盒锁
     */
    public static final short FDHS = 0x86;

    /**
     * 平面锁
     */
    public static final short PMS = 0x87;

    /**
     * 连杆锁
     */
    public static final short LGS = 0x88;

    /**
     * 防火门锁
     */
    public static final short FHMS = 0x89;

    /**
     * 圆孔锁
     */
    public static final short YKS = 0x8A;

    /**
     * 智能锁芯锁具
     */
    public static final short ZNSXSJ = 0x8B;

    /**
     * GDY1D验电锁
     */
    public static final short GYD1D = 0x8C;
}