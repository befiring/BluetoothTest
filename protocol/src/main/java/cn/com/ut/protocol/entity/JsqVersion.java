package cn.com.ut.protocol.entity;

/**
 * Created by zhangyihuang on 2017/10/23.
 */
public class JsqVersion {
    /**
     * 图库版本号(5字节，先高后低)
     */
    public String galleryVersion;

    /**
     * APP版本号 (5字节，先高后低)
     */
    public String appVersion;

    /**
     * 解锁器型号 (N字节，先高后低)
     */
    public String jsqKeyModel;
}