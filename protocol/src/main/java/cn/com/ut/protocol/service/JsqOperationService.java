package cn.com.ut.protocol.service;

import android.content.Context;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cn.com.ut.protocol.BoolResult;
import cn.com.ut.protocol.Channel;
import cn.com.ut.protocol.DataConvert;
import cn.com.ut.protocol.DataWriteUtils;
import cn.com.ut.protocol.UTApplication;
import cn.com.ut.protocol.bluetooth.BluetoothCentralPort;
import cn.com.ut.protocol.bluetooth.BluetoothClientPort;
import cn.com.ut.protocol.entity.JsqBluetoothName;
import cn.com.ut.protocol.entity.JsqCalibrateResult;
import cn.com.ut.protocol.entity.JsqLockType;
import cn.com.ut.protocol.entity.JsqModifyBluetoothResult;
import cn.com.ut.protocol.entity.JsqOperateResult;
import cn.com.ut.protocol.entity.JsqOptCommand;
import cn.com.ut.protocol.entity.JsqVersion;
import cn.com.ut.protocol.entity.JsqWxsCalibrate;
import cn.com.ut.protocol.entity.JsqWxsInfo;
import cn.com.ut.protocol.entity.JsqWxsInfoResult;
import cn.com.ut.protocol.entity.JsqYDCalibrateResult;
import cn.com.ut.protocol.entity.JsqYDResult;
import cn.com.ut.protocol.jsq.ProtocolJsq;
import cn.com.ut.protocol.model.Gsn2LLock;
import cn.com.ut.protocol.model.ILockBase;
import cn.com.ut.protocol.model.LockBase;
import cn.com.ut.protocol.model.MessageType;
import cn.com.ut.protocol.model.OnMessageEventListener;
import cn.com.ut.protocol.model.ProtocolFeatureType;
import cn.com.ut.protocol.model.WxdbmLock;
import cn.com.ut.protocol.port.IBluetoothClientPort;
import cn.com.ut.protocol.port.IPortBase;

/**
 * 解锁器设备操作类,作为解锁器规约和1D规约的结合点
 * 增加解锁操作、增加跳步操作、增加电控锁解锁及异常状态处理、增加跳步未操作并返回的处理、修改复合属性F6/F7/F8的处理
 * Created by zhangyihuang on 2017/1/19.
 */
public class JsqOperationService extends IJsqOperationService implements IPortBase.OnPortStatusChangedListener,
        IPortBase.OnPortReportListener, OnMessageEventListener {

    private final String TAG = JsqOperationService.class.getName();
    /**
     * 解锁器是否在插入状态
     */
    private boolean isPlugIn;
    /**
     * 当前锁具
     */
    private ILockBase curLock;
    private ProtocolJsq protocolJsq;
    private IBluetoothClientPort mBluetoothPort;
    private Channel channel;

    public JsqOperationService(String defaultAddr, boolean enableDatagram) {
        mBluetoothPort = new BluetoothClientPort();
        mBluetoothPort.setPortConfig(defaultAddr, enableDatagram);
        mBluetoothPort.setOnPortStatusChangedListener(this);
        mBluetoothPort.setOnMessageEventListener(this);
        protocolJsq = new ProtocolJsq(this);
        protocolJsq.setProtocolFeatureType(ProtocolFeatureType.Unlock);
        protocolJsq.setPort(mBluetoothPort);
        channel = new Channel();
        channel.setPort(mBluetoothPort);
        channel.setProtocol(protocolJsq);
    }

    public JsqOperationService(Context context, String defaultAddr, boolean enableDatagram, boolean isBluetoothClient) {
        UTApplication.setInstance(context);//保存全局上下文对象
        if (isBluetoothClient) {
            mBluetoothPort = new BluetoothClientPort();
        } else {
            mBluetoothPort = new BluetoothCentralPort(context);
        }
        mBluetoothPort.setPortConfig(defaultAddr, enableDatagram);
        mBluetoothPort.setOnPortStatusChangedListener(this);
        mBluetoothPort.setOnMessageEventListener(this);
        mBluetoothPort.setOnPortReportListener(this);
        protocolJsq = new ProtocolJsq(this);
        protocolJsq.setProtocolFeatureType(ProtocolFeatureType.Unlock);
        protocolJsq.setPort(mBluetoothPort);
        channel = new Channel();
        channel.setPort(mBluetoothPort);
        channel.setProtocol(protocolJsq);
    }

    /**
     * 打开
     */
    public void open() {
        if (channel != null) {
            channel.open();
        }
    }

    @Override
    public void openChannel() {
        if (channel != null && !channel.isOpen()) {
            channel.open();
        }
    }

    /**
     * 关闭
     */
    @Override
    public void close() {
        if (channel != null)
            channel.close();
    }

    @Override
    public IBluetoothClientPort getPort() {
        return mBluetoothPort;
    }

    /**
     * 设置是否启用报文日志
     *
     * @param enableDatagram
     */
    @Override
    public void setEnableDatagram(boolean enableDatagram) {
        mBluetoothPort.setEnableDatagram(enableDatagram);
    }

    @Override
    public void resetBluetoothPort(Context context, String defaultAddr, boolean enableDatagram, boolean isBluetoothClient) {
        if (isBluetoothClient) {
            mBluetoothPort = new BluetoothClientPort();
        } else {
            mBluetoothPort = new BluetoothCentralPort(context);
        }
        mBluetoothPort.setPortConfig(defaultAddr, enableDatagram);
        mBluetoothPort.setOnPortStatusChangedListener(this);
        mBluetoothPort.setOnMessageEventListener(this);
        mBluetoothPort.setOnPortReportListener(this);
        if (protocolJsq != null) {
            protocolJsq.setPort(mBluetoothPort);
        }
        if (channel != null) {
            channel.setPort(mBluetoothPort);
        }
    }

    @Override
    public boolean isConnected() {
        if (channel != null)
            return channel.isOpen();
        return false;
    }

    /**
     * 上一次的操作结果
     */
    private JsqOperateResult lastJsqOperateResult;

    @Override
    public JsqOperateResult getLastJsqOperateResult() {
        return lastJsqOperateResult;
    }

    @Override
    public void setLastJsqOperateResult(JsqOperateResult result) {
        this.lastJsqOperateResult = result;
    }

    /**
     * 解锁器拔出
     */
    @Override
    public void receivePullOut() {
        if (mOnPullOutListener != null)
            mOnPullOutListener.onPullOut();
    }

    /**
     * 2A解锁器插入
     *
     * @param rfid
     * @param lockType
     */
    @Override
    public void receivePlugIn2A(long rfid, byte lockType) {
        onPlugIn2A(rfid, lockType);
    }

    /**
     * 解锁器插入
     *
     * @param rfid
     */
    @Override
    public void receivePlugIn(long rfid) {
        onPlugIn(rfid);
    }

    /**
     * 设置当前锁具
     *
     * @param rfid
     * @param lockType
     */
    @Override
    public void setCurLockBase(long rfid, short lockType) {
        curLock = new LockBase(lockType);
        if (curLock != null) {
            curLock.setOnMessageActionListener(mOnMessageActionListener);
            curLock.setOnCompleteActionListener(mOnCompleteActionListener);
            curLock.setOnJsqResetActionListener(mOnJsqResetActionListener);
            curLock.setOnVoltageReportListener(mOnVoltageReportListener);
            curLock.setRfid(rfid);
        }
    }

    /**
     * 收到操作结果
     *
     * @param result
     */
    @Override
    public void receiveOptResult(JsqOperateResult result) {
        setLastJsqOperateResult(result);
        onOptResult(result);
    }

    /**
     * 收到验电结果
     *
     * @param result
     */
    @Override
    public void receiveYDResult(JsqYDResult result) {
        onYDResult(result);
    }

    /**
     * 收到修改蓝牙名称结果
     *
     * @param result
     */
    @Override
    public void receiveModifyBluetoothNameResult(JsqModifyBluetoothResult result) {
        onModifyBluetoothNameResult(result);
    }

    /**
     * 收到验电标定结果
     *
     * @param result
     */
    @Override
    public void receiveYDCalibrateResult(JsqYDCalibrateResult result) {
        onYDCalibrateResult(result);
    }

    /**
     * 接收到复位结果
     *
     * @param result
     */
    @Override
    public void receiveResetResult(byte result) {
        onResetResult(result);
    }

    /**
     * 接收到解锁器版本
     *
     * @param result
     */
    @Override
    public void receiveJSQVersion(JsqVersion result) {
        onJSQVersionResult(result);
    }

    /**
     * 收到无线锁标定结果
     *
     * @param result
     */
    @Override
    public void receiveCalibrateResult(JsqCalibrateResult result) {
        onJsqCalibrateResult(result);
    }

    /**
     * 收到无线锁信息
     *
     * @param result
     */
    @Override
    public void receiveWxsInfoResult(JsqWxsInfoResult result) {
        onWxsInfoResult(result);
    }

    /**
     * 操作解锁器(带操作附加信息)
     *
     * @param optCommand 解锁器操作指令参数
     */
    @Override
    public Future<BoolResult> operationJsq(JsqOptCommand optCommand) {
        return sendOptCommand(optCommand);
    }

    /**
     * 下发操作指令
     *
     * @param command 命令
     */
    @Override
    public Future<BoolResult> sendOptCommand(JsqOptCommand command) {
        if (protocolJsq != null) {
            return protocolJsq.onOptCommand(command);
        }
        return null;
    }

    /**
     * 下发复位指令
     */
    @Override
    public Future<BoolResult> sendResetCommand() {
        if (protocolJsq != null) {
            return protocolJsq.onResetCommand();
        }
        return null;
    }

    /**
     * 下发RFID非法指令
     */
    @Override
    public Future<BoolResult> sendRfidErrorCommand() {
        if (protocolJsq != null) {
            return protocolJsq.onRfidErrorCommand();
        }
        return null;
    }

    /**
     * 发送获取解锁器版本指令
     */
    @Override
    public Future<BoolResult> sendJSQVersionCommand() {
        if (protocolJsq != null) {
            return protocolJsq.onJSQVersionCommand();
        }
        return null;
    }

    /**
     * 下发直接操作指令
     *
     * @param command 命令
     */
    @Override
    public Future<BoolResult> sendDirectOptCommand(JsqOptCommand command) {
        if (protocolJsq != null) {
            return protocolJsq.onDirectOptCommand(command);
        }
        return null;
    }

    /**
     * 发送无线锁标定指令
     *
     * @param command 命令
     */
    @Override
    public Future<BoolResult> sendCalibrateCommand(JsqWxsCalibrate command) {
        if (protocolJsq != null) {
            return protocolJsq.onCalibrateCommand(command);
        }
        return null;
    }

    /**
     * 发送获取无线锁信息指令
     *
     * @param command 命令
     */
    @Override
    public Future<BoolResult> sendWxsInfoCommand(JsqWxsInfo command) {
        if (protocolJsq != null) {
            return protocolJsq.onWxsInfoCommand(command);
        }
        return null;
    }

    public void onPlugIn2A(long rfid, byte lockType) {
        isPlugIn = true;
        curLock = null;
        switch (lockType) {
            case JsqLockType.WXSGSN2L:
                curLock = new Gsn2LLock(lockType);
                break;
            case JsqLockType.WXSDBMS:
                curLock = new WxdbmLock(lockType);
                break;
            default:
                curLock = new LockBase(lockType);
                break;
        }
        curLock.setOnMessageActionListener(mOnMessageActionListener);
        curLock.setOnCompleteActionListener(mOnCompleteActionListener);
        curLock.setOnJsqResetActionListener(mOnJsqResetActionListener);
        curLock.setOnVoltageReportListener(mOnVoltageReportListener);
        curLock.setRfid(rfid);
        if (mOnPlugInLockActionListener != null) {
            mOnPlugInLockActionListener.onPlugInLockAction(curLock, true);
        }
    }

    /**
     * 插入
     *
     * @param rfid 锁码
     */
    public void onPlugIn(long rfid) {
        isPlugIn = true;
        if (mOnGetLockFuncListener != null) {
            curLock = mOnGetLockFuncListener.onGetLockFunc(rfid);
            if (curLock != null) {
                curLock.setOnMessageActionListener(mOnMessageActionListener);
                curLock.setOnCompleteActionListener(mOnCompleteActionListener);
                curLock.setOnJsqResetActionListener(mOnJsqResetActionListener);
                curLock.setOnVoltageReportListener(mOnVoltageReportListener);
                curLock.setRfid(rfid);
                if (mOnPlugInLockActionListener != null) {
                    mOnPlugInLockActionListener.onPlugInLockAction(curLock, false);
                }
            }
        } else {
            onMessageAction("无法获得锁具类型！", MessageType.Info);
        }
    }

    private void onMessageAction(String msg, MessageType msgType) {
        if (mOnMessageActionListener != null) {
            mOnMessageActionListener.onMessageEvent(msg, msgType);
        }
    }

    public void onOptResult(JsqOperateResult result) {
        if (curLock != null) {
            curLock.receiveOptResult(result);
        } else {//不关联锁具，直接发送操作结果
            if (mOnOperationEventListener != null) {
                mOnOperationEventListener.onOperationEvent(result);
            }
        }
    }

    public void onYDResult(JsqYDResult result) {
        if (mOnYDResultReportListener != null) {
            mOnYDResultReportListener.onYDResultReport(result);
        }
    }

    public void onModifyBluetoothNameResult(JsqModifyBluetoothResult result) {
        if (mOnModifyBluetoothResultListener != null) {
            mOnModifyBluetoothResultListener.onModifyBluetoothResult(result);
        }
    }

    public void onYDCalibrateResult(JsqYDCalibrateResult result) {
        if (mOnYDCalibrateResultReportListener != null) {
            mOnYDCalibrateResultReportListener.onYDCalibrateResult(result);
        }
    }

    /**
     * 处理解锁器复位结果
     *
     * @param result
     */
    public void onResetResult(byte result) {
        if (mOnResetResultListener != null) {
            mOnResetResultListener.onResetResult(result);
        }
    }

    /**
     * 解锁当前锁
     */
    @Override
    public boolean unlockCurLock() {
        if (curLock == null) {
            onMessageAction("请指定操作锁具！", MessageType.Info);
            return false;
        }

        JsqOptCommand optCommand = new JsqOptCommand();
        optCommand.setLockTypeId(curLock.getLockType());
        optCommand.setRfid(curLock.getRfid());
        optCommand.setJsqOptProperty(curLock.getDescription());
        optCommand.setOperateExtraInfo(curLock.getExtraInfo());
        Future<BoolResult> result = operationJsq(optCommand);
        if (result != null) {
            try {
                BoolResult boolResult = result.get();
                if (boolResult.getResult()) {
                    if (isPlugIn) {
                        String msg = curLock.getLockName() + "，请操作！";
                        onMessageEvent(msg, MessageType.Info);
                    }
                } else {
                    onMessageAction("下发解锁器操作指令失败！", MessageType.Error);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 解锁时向解锁器发送票id
     *
     * @param historyId
     * @return
     */
    @Override
    public boolean unlockCurLock(int historyId) {
        if (curLock == null) {
            onMessageAction("请指定操作锁具！", MessageType.Info);
            return false;
        }
        JsqOptCommand optCommand = new JsqOptCommand();
        optCommand.setLockTypeId(curLock.getLockType());
        optCommand.setRfid(curLock.getRfid());
        optCommand.setJsqOptProperty(curLock.getDescription());
        optCommand.setOperateExtraInfo(curLock.getExtraInfo());
        optCommand.setSerialNumber(DataConvert.intToBytes(historyId));
        Future<BoolResult> result = operationJsq(optCommand);
        if (result != null) {
            try {
                BoolResult boolResult = result.get();
                if (boolResult.getResult()) {
                    if (isPlugIn) {
                        String msg = curLock.getLockName() + "，请操作！";
                        onMessageEvent(msg, MessageType.Info);
                    }
                } else {
                    onMessageAction("下发解锁器操作指令失败！", MessageType.Error);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 发送查询验电命令
     */
    @Override
    public Future<BoolResult> sendQueryElectricityCommand() {
        if (protocolJsq != null) {
            return protocolJsq.onQueryYDCommand();
        }
        return null;
    }

    /**
     * 发送修改设备蓝牙名称命令
     *
     * @param command
     * @return
     */
    @Override
    public Future<BoolResult> sendModifyBluetoothNameCommand(JsqBluetoothName command) {
        if (protocolJsq != null) {
            return protocolJsq.onModifyBluetoothNameCommand(command);
        }
        return null;
    }

    /**
     * 发送验电标定命令
     */
    @Override
    public Future<BoolResult> sendYDCalibrateCommand() {
        if (protocolJsq != null) {
            return protocolJsq.onYDCalibrateCommand();
        }
        return null;
    }

    /**
     * 端口产生的消息事件
     */
    protected OnMessageEventListener mOnMessageEventListener;

    public void setOnMessageEventListener(OnMessageEventListener onMessageEventListener) {
        this.mOnMessageEventListener = onMessageEventListener;
    }

    public void onMessageEvent(String msg, MessageType msgType) {
        if (mOnMessageEventListener != null)
            mOnMessageEventListener.onMessageEvent(msg, msgType);
    }

    /**
     * 复位操作
     */
    @Override
    public void resetOperation() {
        Future<BoolResult> result = sendResetCommand();
        if (result != null) {
            try {
                BoolResult boolResult = result.get();
                if (boolResult.getResult()) {
                    onMessageAction("下发解锁器复位指令成功！", MessageType.Info);
                } else {
                    onMessageAction("下发解锁器复位指令失败！", MessageType.Error);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onPortStatusChanged(boolean bState) {
        if (mOnPortStatusChangedListener != null)
            mOnPortStatusChangedListener.onPortStatusChanged(bState);
    }

    public void onJSQVersionResult(JsqVersion result) {
        if (mOnJSQVersionResultListener != null)
            mOnJSQVersionResultListener.onJSQVersionResult(result);
    }

    /**
     * 收到无线锁标定结果
     */
    public void onJsqCalibrateResult(JsqCalibrateResult result) {

    }

    /**
     * 收到无线锁信息
     */
    public void onWxsInfoResult(JsqWxsInfoResult result) {

    }

    /**
     * 下发复位指令
     */
    public Future<BoolResult> onResetCommand() {
        return null;
    }

    @Override
    public void onPortReport(String report) {
        DataWriteUtils.writeBytesToFile(report);
        DataWriteUtils.printFrameBytes(TAG, report);
    }

    /**
     * 超时
     */
    public void timeOut(){
        if (mOnTimeOutListener != null) {
            mOnTimeOutListener.onTimeOut();
        }
    }
}