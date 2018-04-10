package cn.com.ut.protocol.entity;

/**
 * 修改设备蓝牙名称命令
 * Created by zhangyihuang on 2017/12/18.
 */
public class JsqBluetoothName {
    /**
     * 设备蓝牙名称
     */
    private byte[] bluetoothName;

    public byte[] getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(byte[] bluetoothName) {
        this.bluetoothName = bluetoothName;
    }
}