package cn.com.ut.protocol;

import java.util.Date;

/**
 * Created by zhangyihuang on 2016/12/24.
 */
public class AppRuntime {

    private static long _communicationTime = new Date().getTime();

    /**
     * 业务通信忙，主要用于解锁器的重连
     *
     * @return
     */
    public static boolean getIsBusinessCommunicationBusy() {
        long ts = new Date().getTime() - _communicationTime;
        return ts < 5000;
    }

    /**
     * 设置当前时间
     */
    public static void setIsBusinessCommunicationBusy() {
        _communicationTime = new Date().getTime();
    }

}