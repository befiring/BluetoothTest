package cn.com.ut.protocol.entity;

/**
 * 修改设备蓝牙名称命令应答
 * Created by zhangyihuang on 2017/12/18.
 */
public class JsqModifyBluetoothResult {
    /**
     * 修改蓝牙名称结果
     * 0x01：修改成功；
     * 0x02：蓝牙名称过长；
     * 0x03：蓝牙名称不符合命名规范；
     * 0xFE：其他错误
     */
    private byte modifyResult;

    public byte getModifyResult() {
        return modifyResult;
    }

    public void setModifyResult(byte modifyResult) {
        this.modifyResult = modifyResult;
    }
}