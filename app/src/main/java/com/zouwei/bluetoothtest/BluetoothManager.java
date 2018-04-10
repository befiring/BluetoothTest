package com.zouwei.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import cn.com.ut.protocol.entity.JsqBluetoothName;
import cn.com.ut.protocol.model.ILockBase;
import cn.com.ut.protocol.model.OnMessageEventListener;
import cn.com.ut.protocol.port.IBluetoothClientPort;
import cn.com.ut.protocol.port.IPortBase;
import cn.com.ut.protocol.service.IJsqOperationService;
import cn.com.ut.protocol.service.JsqOperationService;


/**
 * Created by Wang Meng on 2017/8/8.
 */

public class BluetoothManager {

    private static IJsqOperationService jsqOperationService;
    private static BluDevice currentDevice;

    public static final String defaultAddr = "00:15:83:1E:09:5F";

    /**
     * 连接默认蓝牙时的初始化
     */
    private static void initJsqService(Context context) {
//        jsqOperationService = null;
//        String defaultAddr = "";
//        if (currentDevice != null) {
//            defaultAddr = currentDevice.getAddress();
//        }
//        if (TextUtils.isEmpty(defaultAddr)) {
//            return;
//        }
        jsqOperationService = new JsqOperationService(context, defaultAddr, false, true);
    }

    /**
     * 连接指定蓝牙时的初始化
     */
    private static void initJsqService(Context context, String address, boolean isClassicBluetooth) {
        if (TextUtils.isEmpty(address)) {
            return;
        }
        jsqOperationService = null;
//        boolean enableDatagram = ShareUtil.getInstance().getDatagramEnable();
        jsqOperationService = new JsqOperationService(context, address, false, isClassicBluetooth);
    }

    /**
     * 连接默认蓝牙地址
     */
    public static void connect(Context context) {
        if (isConnected()) {
            return;
        }
        initJsqService(context);
        open();
    }

    /**
     * *连接指定蓝牙设备
     */
    public static void connect(Context context, BluDevice device, boolean isClassicBluetooth) {
        currentDevice = device;
        String address = "";
        if (currentDevice != null) {
            address = currentDevice.getAddress();
        }
        if (!TextUtils.isEmpty(address)) {
            initJsqService(context, address, isClassicBluetooth);
            open();
        }
    }

    public static BluDevice getCurrentDevice() {
        return currentDevice;
    }

    private static void open() {
        if (jsqOperationService != null) {
            jsqOperationService.open();
        }
    }

    public static void close() {
        if (jsqOperationService != null) {
            jsqOperationService.close();
        }
    }

    public static void registerListener(IJsqOperationService.OnGetLockFuncListener onGetLockFuncListener,
                                        ILockBase.OnCompleteActionListener onCompleteActionListener,
                                        OnMessageEventListener onMessageEventListener,
                                        ILockBase.OnVoltageReportListener onVoltageReportListener,
                                        IJsqOperationService.OnOperationEventListener onOperationEventListener,
                                        IPortBase.OnPortStatusChangedListener onPortStatusChangedListener,
                                        IJsqOperationService.OnYDResultReportListener onYDResultReportListener,
                                        IJsqOperationService.OnModifyBluetoothResultListener onModifyBluetoothResultListener,
                                        IJsqOperationService.OnYDCalibrateResultReportListener onYDCalibrateResultReportListener) {
        if (jsqOperationService != null) {
            jsqOperationService.setOnGetLockFuncListener(onGetLockFuncListener);
            jsqOperationService.setOnCompleteActionListener(onCompleteActionListener);
            jsqOperationService.setOnMessageActionListener(onMessageEventListener);
            jsqOperationService.setOnVoltageReportListener(onVoltageReportListener);
            jsqOperationService.setOnOperationEventListener(onOperationEventListener);
            jsqOperationService.setOnPortStatusChangedListener(onPortStatusChangedListener);
            jsqOperationService.setOnYDResultReportListener(onYDResultReportListener);
            jsqOperationService.setOnModifyBluetoothResultListener(onModifyBluetoothResultListener);
            jsqOperationService.setOnYDCalibrateResultReportListener(onYDCalibrateResultReportListener);
        }

    }

    public static void setTimeOutListener(IJsqOperationService.OnTimeOutListener listener) {
        if (jsqOperationService != null) {
            jsqOperationService.setmOnTimeOutListener(listener);
        }
    }

    public static void unregisterListener() {
        if (jsqOperationService != null) {
            jsqOperationService.setOnGetLockFuncListener(null);
            jsqOperationService.setOnCompleteActionListener(null);
            jsqOperationService.setOnMessageActionListener(null);
            jsqOperationService.setOnVoltageReportListener(null);
            jsqOperationService.setOnOperationEventListener(null);
            jsqOperationService.setOnPortStatusChangedListener(null);
            jsqOperationService.setOnYDResultReportListener(null);
            jsqOperationService.setOnModifyBluetoothResultListener(null);
        }

    }

    public static boolean isConnected() {
        return jsqOperationService != null && jsqOperationService.isConnected();
    }


    /**
     * 解锁
     */
    public static void unlock(int historyId) {
        jsqOperationService.unlockCurLock(historyId);
    }


    /**
     * 解锁
     */
    public static void unlock() {
        jsqOperationService.unlockCurLock();
    }

    public static void unlock(ILockBase lockBase) {
        jsqOperationService.setCurLockBase(lockBase.getRfid(), lockBase.getLockType());
        jsqOperationService.unlockCurLock();
    }

    /**
     * 查询验电
     */
    public static void queryYandianqi() {
        if (jsqOperationService != null)
            jsqOperationService.sendQueryElectricityCommand();
    }

    /**
     * 修改蓝牙名称
     *
     * @param name 新蓝牙名称
     */
    public static void modifyBluetoothName(String name) {
        if (jsqOperationService != null) {
            JsqBluetoothName jbn = new JsqBluetoothName();
            jbn.setBluetoothName(name.getBytes());
            jsqOperationService.sendModifyBluetoothNameCommand(jbn);
        }
    }

    /**
     * 标定验电器
     */
    public static void calibrateYandianqi() {
        if (jsqOperationService != null) {
            jsqOperationService.sendYDCalibrateCommand();
        }
    }


    /**
     * 复位
     */
    public static void resetOperation() {
        jsqOperationService.resetOperation();
    }

    public static IBluetoothClientPort getPort() {
        return jsqOperationService.getPort();
    }


    public static void scanBleDevice(final BluetoothAdapter.LeScanCallback callback, final ScanStopCallback scanStopCallback) {
        android.bluetooth.BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager) TestApplication.getInstance().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return;
        }
        final BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(callback);
            }
        }.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scanStopCallback != null) {
                    scanStopCallback.onScanStop();
                }
                stopScanBle(callback, scanStopCallback);
            }
        }, 1000 * 10);
    }

    public static void stopScanBle(final BluetoothAdapter.LeScanCallback callback, ScanStopCallback scanStopCallback) {
        android.bluetooth.BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager) TestApplication.getInstance().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return;
        }
        final BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
//        if (mBluetoothAdapter.isDiscovering()) {
        mBluetoothAdapter.stopLeScan(callback);
//        }

    }

    public interface ScanStopCallback {
        void onScanStop();
    }


}
