package cn.com.ut.protocol.entity;

/**
 * 自学数据校验码实体类(钥匙上传APP MD5校验码)
 * Created by zhangyihuang on 2017/4/10.
 */
public class EstSelfCheckedCode extends DataEntity {

    /**
     * 自学数据MD5码 (16字节)
     */
    public byte[] md5;

    public EstSelfCheckedCode() {
        super();
        md5 = new byte[16];
    }
}