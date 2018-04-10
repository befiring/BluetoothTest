package cn.com.ut.protocol.entity;

/**
 * APP告诉钥匙不需要自学
 * 钥匙收到此命令回复ACK
 * Created by zhangyihuang on 2017/4/10.
 */
public class EstNoNeedLearn extends DataEntity {

    /**
     * 保留（8字节）
     */
    public byte[] retain = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    public EstNoNeedLearn() {
        super();
        dataEntityType[0] = 0x00;
        dataEntityType[1] = 0x02;
        dataContentLength = 0x08;
    }
}