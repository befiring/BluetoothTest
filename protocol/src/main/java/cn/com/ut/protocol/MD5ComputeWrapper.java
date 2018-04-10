package cn.com.ut.protocol;

import java.security.MessageDigest;

/**
 * MD码计算封装
 * Created by zhangyihuang on 2017/4/5.
 */
public class MD5ComputeWrapper {

    /**
     * 计算MD5码
     *
     * @param computering
     * @return
     */
    public static byte[] computeMD5(byte[] computering) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(computering);
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return md.digest();//new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}