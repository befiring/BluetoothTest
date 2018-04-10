package cn.com.ut.protocol.port;

/**
 * Created by zhangyihuang on 2016/12/23.
 */
public interface IBluetoothClientPort extends IPortBase {

    /**
     * 当前连接解锁器
     */
    IBluetoothDevice getCurrentDevice();

    /**
     * 切换解锁器
     *
     * @param macAddress 物理地址
     * @return
     */
    void switchDevice(String macAddress);
}