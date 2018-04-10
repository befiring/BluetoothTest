package cn.com.ut.protocol.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import cn.com.ut.protocol.model.MessageType;
import cn.com.ut.protocol.model.PortType;
import cn.com.ut.protocol.port.IBluetoothClientPort;
import cn.com.ut.protocol.port.IBluetoothDevice;
import cn.com.ut.protocol.port.PortBase;
import cn.com.ut.protocol.AppRuntime;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

/**
 * 传统蓝牙客户端端口
 * Created by zhangyihuang on 2016/12/23.
 */
public class BluetoothClientPort extends PortBase implements IBluetoothClientPort {
    private final String TAG = BluetoothClientPort.class.getName();

    /**
     * 蓝牙串口通用的UUID
     */
    public final UUID SERIAL_PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * 蓝牙设备地址集
     */
    private List<String> mBluetoothAddrs;

    /**
     * 当前连接解锁器
     */
    private BluetoothDevice mCurrentDevice = null;

    /**
     * 蓝牙适配器
     */
    protected BluetoothAdapter adapter;

    private final ReentrantLock connectLock = new ReentrantLock();

    public ReentrantLock getConnectLock() {
        return connectLock;
    }

    /**
     * Wait queue for waiting connect
     */
    private final Condition notConnect = connectLock.newCondition();

    /**
     * 退出
     */
    private boolean exit;

    /**
     * 默认蓝牙地址
     */
    private String mDefaultAddr;

    protected ConnectThread connectThread;
    private ConnectedThread connectedThread;

    public BluetoothClientPort() {
        super();
        portName = "BluetoothClientPort";
        buffersLength = 1024 * 1024 * 1; // 1M
        buffers = new byte[buffersLength];
        adapter = getDefaultAdapter();
    }

    public PortType getPortType() {
        return PortType.BluetoothClient;
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

    private void signalNotWait() {
        final ReentrantLock sendLock = this.connectLock;
        sendLock.lock();
        try {
            notConnect.signal();
        } finally {
            sendLock.unlock();
        }
    }

    public void start() {
        // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        setState(false);
        doConnect(1000);
    }

    /**
     * [MethodImpl(MethodImplOptions.Synchronized)]
     */
    @Override
    public boolean open() {
        Log.w(TAG, "open");
        exit = false;
        getAdapterDevice();
        if (mCurrentDevice == null) {
            addMessage("蓝牙未配对或无默认连接", MessageType.Error);
            return false;
        } else {
            adapter.cancelDiscovery();
            doConnect(0);
            return true;
        }
    }

    @Override
    public void close() {
        exit = true;
        signalNotWait();
        Log.w(TAG, "closing");
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
//        setState(false);
    }

    @Override
    public void send(byte[] data) {
        send("", -1, -1, data);
    }

//    @Override
//    public void send(String cmdName, byte[] data) {
//        send(cmdName, -1, -1, data);
//    }

    @Override
    public void send(String cmdName, int frameNum, int totalFrames, byte[] data) {
        if (isConnected()) {
            // Create temporary object
            ConnectedThread r;
            // Synchronize a copy of the ConnectedThread
            final ReentrantLock sendLock = this.connectLock;
            sendLock.lock();
            try {
                if (!isConnected())
                    return;
                r = connectedThread;
            } finally {
                sendLock.unlock();
            }
            // Perform the write unsynchronized
            r.write(cmdName, frameNum, totalFrames, data);
        } else {
            addMessage("无蓝牙设备连接", MessageType.Exception);
            //throw new Exception("无蓝牙设备连接");
        }
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made.
     * @param device The BluetoothDevice that has been connected.
     * @return [MethodImpl(MethodImplOptions.Synchronized)]
     */
    public void connected(BluetoothSocket socket, BluetoothDevice device) {
        // Cancel the thread that completed the connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(socket, this);
        connectedThread.start();
        setState(true);
    }

    /**
     * 线程执行连接
     *
     * @param sleepTime 自动重连等待时间
     * @return [MethodImpl(MethodImplOptions.Synchronized)]
     */
    private void doConnect(final long sleepTime) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    startConnect(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startConnect(long sleepTime) throws InterruptedException {
        final ReentrantLock connectLock = getConnectLock();
        connectLock.lockInterruptibly();
        try {
            while (AppRuntime.getIsBusinessCommunicationBusy()) {
                notConnect.await(sleepTime, TimeUnit.SECONDS);
                if (exit) return;
            }
            //主动关闭
            if (exit) return;
            notConnect.await(sleepTime, TimeUnit.SECONDS);

            //主动关闭
            if (exit) return;
            connectThread = new ConnectThread(mCurrentDevice, BluetoothClientPort.this);
            connectThread.setPriority(1);
            connectThread.start();
        } finally {
            connectLock.unlock();
        }
    }

    protected void setState(boolean isConnected) {
        this.isConnected = isConnected;
        onPortStatusChanged();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    public void connectionLost() {
        setState(false);
        if (!exit) {
            doConnect(2500);
        }
    }

    /**
     * 获取配置中已适配的设备
     */
    private void getAdapterDevice() {
        if (null != mDefaultAddr && !"".equals(mDefaultAddr)) {
            Set<BluetoothDevice> bluetoothDevices = getDefaultAdapter().getBondedDevices();
            if (bluetoothDevices.size() > 0) {
                for (BluetoothDevice device : bluetoothDevices) {
                    if (device.getAddress().equals(mDefaultAddr)) {
                        mCurrentDevice = device;
                        break;
                    }
                }
            }
            if (mCurrentDevice == null) {
                //验证蓝牙设备MAC地址是否有效
                if (BluetoothAdapter.checkBluetoothAddress(mDefaultAddr)) {
                    mCurrentDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mDefaultAddr);
                }
            }
        }
    }

    /**
     * 切换默认解锁器地址
     */
    public void switchMacAddress(String macAddress) {
        this.close();
        mDefaultAddr = macAddress;
    }

    /**
     * 切换解锁器并重新打开
     *
     * @param macAddress 物理地址
     */
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

    protected class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;
        private BluetoothClientPort mPort;
        private final ReentrantLock lock = new ReentrantLock();

        public ConnectThread(BluetoothDevice device, BluetoothClientPort port) {
            mmDevice = device;
            mPort = port;
            BluetoothSocket tmp = null;

            // 获得串口连接
            try {
                tmp = device.createRfcommSocketToServiceRecord(port.SERIAL_PORT_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            setName("ConnectThread");
            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                if (mmSocket != null) {
                    mmSocket.connect();
                }
                // throw new Java.IO.IOException("my throw Exception");
            } catch (IOException e) {
                // Close the socket
                try {
                    if (mmSocket != null) {
                        mmSocket.close();
                    }
                } catch (IOException e2) {
                    //Log.Error("PCSW", "unable to close() socket during connection failure", e2.Message);
                    e.printStackTrace();
                }
                // Start the service over to restart listening mode
                mPort.start();
                return;
            }
            // Reset the ConnectThread because we're done
            lock.lock();
            try {
                mPort.connectThread = null;
            } finally {
                lock.unlock();
            }
            // Start the connected thread
            if (mmSocket != null) {
                mPort.connected(mmSocket, mmDevice);
            }
        }

        public void cancel() {
            try {
                if (mmSocket != null) {
                    mmSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    protected class ConnectedThread extends Thread {
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private BluetoothClientPort mPort;
        private final String TAG = ConnectedThread.class.getName();

        public ConnectedThread(BluetoothSocket socket, BluetoothClientPort port) {
            mmSocket = socket;
            mPort = port;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            while (true) {
                int index = mPort.offset;
                if (mPort.buffers.length < (mPort.offset + 1024)) {
                    Arrays.copyOf(mPort.buffers, mPort.buffers.length + (1024 * 1024));
                }
                try {
                    // Read from the InputStream
                    mPort.offset += mmInStream.read(mPort.buffers, mPort.offset, mPort.buffers.length - mPort.offset);
                } catch (IOException e) {
                    mPort.connectionLost();
                    break;
                }
                int count = mPort.offset - index;
                if (count > 0) {
                    mPort.reviceDataEvent();
                }
            }
        }

        /**
         * `
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         * @return
         */
        public void write(byte[] buffer) {
            write("", -1, -1, buffer);
        }

        /**
         * @param cmdName
         * @param frameNum
         * @param totalFrames
         * @param buffer
         */
        public void write(String cmdName, int frameNum, int totalFrames, byte[] buffer) {
            try {
                mmOutStream.write(buffer, 0, buffer.length);
                mPort.writeReportToFile(cmdName, buffer, 0, buffer.length, frameNum, totalFrames, true);
            } catch (IOException e) {
                mPort.addMessage("蓝牙发送失败", MessageType.Exception);
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
                setState(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}