package cn.com.ut.protocol.bluetooth;

import android.bluetooth.BluetoothDevice;

import cn.com.ut.protocol.port.IBluetoothDevice;

/**
 * Created by zhangyihuang on 2016/12/23.
 */
public class BluetoothDeviceWarpper extends IBluetoothDevice {
    private BluetoothDevice _bluetoothDevice;

    public String getMacAddress() {
        return _bluetoothDevice.getAddress();
    }

    public BluetoothDeviceWarpper(BluetoothDevice bluetoothDevice) {
        _bluetoothDevice = bluetoothDevice;
    }

    public BluetoothDevice getBluetoothDevice() {
        return _bluetoothDevice;
    }
}