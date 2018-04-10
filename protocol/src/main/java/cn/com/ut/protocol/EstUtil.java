package cn.com.ut.protocol;

import cn.com.ut.protocol.est.CommandCode;

/**
 * Created by zhangyihuang on 2017/4/15.
 */
public class EstUtil {
    /**
     * 得到应用的中文名称
     *
     * @param cmd
     * @return
     */
    public static String getCommandName(int cmd) {
        switch (cmd) {
            case CommandCode.KEYRECALLDATA:
                return "电脑钥匙追忆回传数据";
            default:
                return String.valueOf(cmd);
        }
    }
}