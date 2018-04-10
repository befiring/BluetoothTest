package cn.com.ut.protocol.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import cn.com.ut.protocol.DataWriteUtils;
import cn.com.ut.protocol.port.IBluetoothClientPort;
import cn.com.ut.protocol.port.IBluetoothDevice;
import cn.com.ut.protocol.port.PortBase;

/**
 * 蓝牙4.0客户端端口
 * Created by zhangyihuang on 2017/9/3.
 */
public class BluetoothCentralPort extends PortBase implements IBluetoothClientPort {
    private final String TAG = BluetoothCentralPort.class.getName();

    private static final int STATE_DISCONNECTED = 0;//设备无法连接
    private static final int STATE_CONNECTING = 1;//设备正在连接状态
    private static final int STATE_CONNECTED = 2;//设备连接完毕

    private static final String IO_SERVICE_UUID = "0000ff00-0000-1000-8000-00805f9b34fb";
    private static final String READ_CHARACTERISTIC_UUID = "0000ff01-0000-1000-8000-00805f9b34fb";
    private static final String READ_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    private static final String WRITE_CHARACTERISTIC_UUID = "0000ff02-0000-1000-8000-00805f9b34fb";

    private Context mContext;
    private BluetoothManager mBluetoothManager;//蓝牙管理器
    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器，处理系统蓝牙是否打开，搜索设备
    private String mBluetoothDeviceAddress;//蓝牙设备地址
    private BluetoothGatt mBluetoothGatt;//GATT客户端 发现蓝牙服务，根据特征值处理数据交互
    private BluetoothGattCallback mGattCallback;
    private int mConnectionState = STATE_DISCONNECTED;

    /**
     * 默认蓝牙地址
     */
    private String mDefaultAddr;
    /**
     * 当前连接解锁器
     */
    private BluetoothDevice mCurrentDevice = null;

    public BluetoothCentralPort(Context context) {
        super();
        portName = "BluetoothCentralPort";
        buffersLength = 1024 * 1024 * 1; // 1M
        buffers = new byte[buffersLength];
        mContext = context;
        initialize();
    }

    @Override
    public void setPortConfig(String defaultAddr) {
        mDefaultAddr = defaultAddr;
        mCurrentDevice = null;
    }

    @Override
    public void setPortConfig(String defaultAddr, boolean enableDatagram) {
        super.setWriteReport(enableDatagram);
        mDefaultAddr = defaultAddr;
    }

    /**
     * 当前连接解锁器
     */
    @Override
    public IBluetoothDevice getCurrentDevice() {
        if (null != mCurrentDevice)
            return new BluetoothDeviceWarpper(mCurrentDevice);
        return null;
    }

    /**
     * 切换解锁器并重新打开
     *
     * @param macAddress 物理地址
     */
    @Override
    public void switchDevice(String macAddress) {
        try {
            this.close();
            mDefaultAddr = macAddress;
            Thread.sleep(100);
            this.open();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(byte[] data) {
        BluetoothGattConnectCallback gattConnectCallback = (BluetoothGattConnectCallback) mGattCallback;
        if (gattConnectCallback != null) {
            gattConnectCallback.sendData(data);
            writeReportToFile(data, 0, data.length, true);
        }
    }

    @Override
    public void send(String cmdName, int frameNum, int totalFrames, byte[] data) {
        BluetoothGattConnectCallback gattConnectCallback = (BluetoothGattConnectCallback) mGattCallback;
        if (gattConnectCallback != null) {
            gattConnectCallback.sendData(data);
            writeReportToFile(cmdName, data, 0, data.length, frameNum, totalFrames, true);
        }
    }

    /**
     * 接收解锁器字节数组数据
     *
     * @param data
     */
    public void receiveData(byte[] data) {
        int index = offset;
        System.arraycopy(data, 0, buffers, offset, data.length);
        offset += data.length;
        int count = offset - index;
        if (count > 0) {
            reviceDataEvent();
        }
    }

    @Override
    public boolean open() {
        return connect(mDefaultAddr);
    }

    @Override
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        setState(false);
    }

    protected void setState(boolean isConnected) {
        this.isConnected = isConnected;
        onPortStatusChanged();
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {//连接远程设备
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        Log.i(TAG, "Address:" + address);
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        mGattCallback = new BluetoothGattConnectCallback(mContext, this);
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);//创建连接
        } else {
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);//创建连接
        }
        Log.i(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mCurrentDevice = device;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();//获得一个外设的所有服务
    }

    /**
     * Implements callback methods for GATT events that the app cares about.  For example,
     * connection change and services discovered.
     */
    private class BluetoothGattConnectCallback extends BluetoothGattCallback {
        int packLength = 18;//每包长度
        Context mContext;
        BluetoothGatt curGatt;
        BluetoothCentralPort mPort;
        BluetoothGattCharacteristic readCharacteristic;
        BluetoothGattCharacteristic writeCharacteristic;
        BluetoothGattDescriptor readDescriptor;
        BondStateChangedReceiver mBondingBroadcastReceiver;

        public BluetoothGattConnectCallback(Context context, BluetoothCentralPort port) {
            this.mContext = context;
            this.mPort = port;
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //当连接状态发生改变
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    setState(true);//连接成功
                    mConnectionState = STATE_CONNECTED;
                    Log.i(TAG, "Connected to GATT server.");
                    // Attempts to discover services after successful connection.
                    boolean result = discoverServices();//开始搜索服务，一定要调用此方法，否则获取不到服务
                    Log.i(TAG, "Attempting to start service discovery:" + result);//获得某个Gatt外设提供的服务
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    //当设备无法连接
                    setState(false);
                    mConnectionState = STATE_DISCONNECTED;
                    refreshDeviceCache(mBluetoothGatt);
                    Log.i(TAG, "Disconnected from GATT server.");
                    break;
            }
        }

        /**
         * 清理本地的BluetoothGatt 的缓存，以保证在蓝牙连接设备的时候，设备的服务、特征是最新的
         *
         * @param gatt
         * @return
         */
        public boolean refreshDeviceCache(BluetoothGatt gatt) {
            if (null != gatt) {
                try {
                    BluetoothGatt localBluetoothGatt = gatt;
                    Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
                    if (localMethod != null) {
                        boolean result = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                        Log.i(TAG, "refresh BluetoothGatt:" + result);
                        return result;
                    }
                } catch (Exception localException) {
                    localException.printStackTrace();
                }
            }
            return false;
        }

        /**
         * 搜索连接设备所支持的服务
         *
         * @return
         */
        private boolean discoverServices() {
            if (mBluetoothGatt != null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                return mBluetoothGatt.discoverServices();
            }
            return false;
        }

        /**
         * 信号强度
         *
         * @param gatt
         * @param rssi
         * @param status
         */
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.w(TAG, " OnReadRemoteRssi staus=" + status);
            gatt.discoverServices();
        }

        /**
         * 发现新服务端
         * 当设备找到服务时，会回调该函数
         *
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                mPort.disconnect();
                return;
            }
            BluetoothGattService ioService = gatt.getService(UUID.fromString(IO_SERVICE_UUID));
            if (ioService == null) {
                mPort.disconnect();
                return;
            }
            readCharacteristic = ioService.getCharacteristic(UUID.fromString(READ_CHARACTERISTIC_UUID));
            if (readCharacteristic == null) {
                mPort.disconnect();
                return;
            }
            readDescriptor = readCharacteristic.getDescriptor(UUID.fromString(READ_DESCRIPTOR_UUID));
            if (readDescriptor == null) {
                mPort.disconnect();
                return;
            }
            writeCharacteristic = ioService.getCharacteristic(UUID.fromString(WRITE_CHARACTERISTIC_UUID));
            if (writeCharacteristic == null) {
                mPort.disconnect();
                return;
            }
            curGatt = gatt;
            if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_BONDED) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                mBondingBroadcastReceiver = new BondStateChangedReceiver(gatt.getDevice().getAddress(), this);
                mContext.registerReceiver(mBondingBroadcastReceiver, filter);
                if (gatt.getDevice().getBondState() == BluetoothDevice.BOND_NONE) {
                    // 没有绑定
                    Log.w(TAG, "BluetoothDevice.BOND_NONE");
                    setCharacteristicNotification();
                }
            } else {
                Log.i(TAG, "onServicesDiscovered.22222222222");
                setCharacteristicNotification();
            }
            writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        }

        /**
         * 读操作回调，当读取设备时会回调该函数
         *
         * @param gatt
         * @param characteristic
         * @param status
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //读取到的数据存在characteristic当中，可以通过characteristic.getValue();函数取出。然后再进行解析操作。
                // 对于所有其他的配置文件，用十六进制格式写数据
                // For all other profiles, writes the data formatted in HEX.
//                final byte[] data = characteristic.getValue();
//                if (data != null && data.length > 0) {
//                    final StringBuilder stringBuilder = new StringBuilder(data.length);
//                    for (byte byteChar : data)
//                        stringBuilder.append(String.format("%02X ", byteChar));
//                }
            }
        }

        /**
         * 接收钥匙设备发送数据回调函数
         *
         * @param gatt
         * @param characteristic
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                mPort.receiveData(data);
            }
        }

        /**
         * 写函数成功回调
         * 指本机写数据指令已经成功发送出去，并且智能硬件已经处理完回应回来
         *
         * @param gatt
         * @param characteristic
         * @param status
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            final byte[] data = characteristic.getValue();
//            Log.w("onCharacteristicWrite", String.valueOf(status));
//            Log.w("onCharacteristicWrite", DataWriteUtils.getFrameBytes(data));
            if (status == BluetoothGatt.GATT_SUCCESS) {//写入成功

            } else if (status == BluetoothGatt.GATT_FAILURE) {//写入失败

            } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) { //没有写入的权限

            }
        }

        /**
         * 设置当指定characteristic特征端口值变化时，发出通知返回
         */
        public void setCharacteristicNotification() {
            if (curGatt.setCharacteristicNotification(readCharacteristic, true)
                    && readDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
                if (!curGatt.writeDescriptor(readDescriptor)) {
                    Log.i(TAG, "WriteDescriptor false");
                    mPort.disconnect();
                    return;
                }
            }
        }

        /**
         * 发送字节数组数据
         *
         * @param buffer
         * @return
         */
        public boolean sendData(byte[] buffer) {
            //这里试试分包发送，因为Gatt最大报文长度为18个字节左右，按18个字节一包分开来发送试试
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {

            }
            return sendDataToCharacteristic(buffer);

            //分包
//            boolean result = false;
//            int packNum = buffer.length / packLength + 1;
//            int lastPackLength = buffer.length % packLength;
//            int packIndex = 1;
//            while (packIndex <= packNum) {
//                byte[] sendData;  //包数据长度
//                if (packIndex == packNum) {
//                    if (lastPackLength == 0) {
//                        return result;
//                    }
//                    sendData = new byte[lastPackLength];  //包数据长度
//                    System.arraycopy(buffer, (packIndex - 1) * packLength, sendData, 0, lastPackLength);
//                } else {
//                    sendData = new byte[packLength];  //包数据长度
//                    System.arraycopy(buffer, (packIndex - 1) * packLength, sendData, 0, packLength);
//                }
//
//                Log.d(TAG, sendData.toString());
//                packIndex++;
//                result = sendDataToCharacteristic(sendData);
//                if (!result) {
//                    return false;
//                }
//                //_mmOutStream.write(sendData, 0, sendData.length);
//            }
//            return result;
        }

        /**
         * 往设备中写入数据
         *
         * @param data
         * @return
         */
        private boolean sendDataToCharacteristic(byte[] data) {
            if (null == data) {
                return false;
            }
            if (writeCharacteristic != null && curGatt != null) {
                Log.w("BluetoothCentralPort", DataWriteUtils.getFrameBytes(data));
                if (writeCharacteristic.setValue(data) && curGatt.writeCharacteristic(writeCharacteristic)) {
                    return true;
                }
            }
            return false;
        }

        public void unRegisterBondReceiver() {
            if (mBondingBroadcastReceiver != null) {
                mContext.unregisterReceiver(mBondingBroadcastReceiver);
                mBondingBroadcastReceiver = null;
            }
        }
    }

    private class BondStateChangedReceiver extends BroadcastReceiver {
        String mAddress;
        BluetoothGattConnectCallback mGattConnectCallback;

        public BondStateChangedReceiver(String address, BluetoothGattConnectCallback gattConnectCallback) {
            this.mAddress = address;
            this.mGattConnectCallback = gattConnectCallback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
            if (!device.getAddress().equals(mAddress)) {
                return;
            }
            if (bondState == (int) BluetoothDevice.BOND_BONDED) {
                //表明蓝牙已经绑定
                // Continue to do what you've started before
                //Application.Context.UnregisterReceiver(this);
                mGattConnectCallback.unRegisterBondReceiver();
                //再次订阅
                mGattConnectCallback.setCharacteristicNotification();
                //mCallbacks.onBonded();
            }
        }
    }
}