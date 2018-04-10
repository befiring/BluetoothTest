package cn.com.ut.protocol.entity;

/**
 * 自学数据授权实体类(app下传钥匙授权信息)
 * Created by zhangyihuang on 2017/4/10.
 */
public class EstSelfStudyAuthorize extends DataEntity {

    /**
     * 登录人Id (2字节)
     */
    public short loginId;

    /**
     * 校准时间(6字节)
     */
    public byte[] calibrationTime;

    /**
     * 有效时间(4字节)
     */
    public int effectiveTime;

    public EstSelfStudyAuthorize() {
        super();
        dataEntityType[0] = 0x00;
        dataEntityType[1] = 0x01;
        dataContentLength = 0x0C;
    }
}