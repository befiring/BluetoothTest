package com.zouwei.bluetoothtest;

import java.io.Serializable;

/**
 * 蓝牙连接解锁器设备
 * Created by Wang Meng on 2017/12/11.
 */
public class BluDevice implements Serializable {

    public BluDevice() {
    }

    public BluDevice(String mBluName, String mAddress) {
        this.mBluName = mBluName;
        this.mAddress = mAddress;
    }

    private String mBluName;

    public String getBluName() {
        return mBluName;
    }

    public void setBluName(String bluName) {
        this.mBluName = bluName;
    }

    private String mAddress;

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    private boolean mIsPaired;

    public boolean getIsPaired() {
        return mIsPaired;
    }

    public void setIsPaired(boolean isPaired) {
        this.mIsPaired = isPaired;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BluDevice){
            BluDevice device= (BluDevice) obj;
            return mBluName.equals(device.getBluName()) && mAddress.equals(device.getAddress());
        }
        return false;
    }

    @Override
    public String toString() {
        return mBluName+"\n"+mAddress;
    }
}