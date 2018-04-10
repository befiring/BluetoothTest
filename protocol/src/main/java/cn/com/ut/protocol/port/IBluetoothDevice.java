package cn.com.ut.protocol.port;

import android.bluetooth.BluetoothDevice;

/**
 * Created by zhangyihuang on 2016/12/23.
 */
public abstract class IBluetoothDevice {
    /**
     * 物理地址
     */
    String macAddress;

    /**
     * 当前连接解锁器
     *
     * @return
     */
    public abstract BluetoothDevice getBluetoothDevice();
}