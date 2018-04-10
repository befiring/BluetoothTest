package cn.com.ut.protocol.entity;

/**
 * 解锁器软件版本
 * Created by zhangyihuang on 2017/4/13.
 */
public class EstJsqVersion extends DataEntity{

    /**
     * 应用程序版本号 (5字节，先高后低)
     */
    public String appVersion;

    /**
     * 图库版本号(5字节，先高后低)
     */
    public String galleryVersion;

}