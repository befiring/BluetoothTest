package cn.com.ut.protocol.service;

import android.content.Context;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cn.com.ut.protocol.BoolResult;
import cn.com.ut.protocol.Channel;
import cn.com.ut.protocol.DataWriteUtils;
import cn.com.ut.protocol.UTApplication;
import cn.com.ut.protocol.bluetooth.BluetoothCentralPort;
import cn.com.ut.protocol.bluetooth.BluetoothClientPort;
import cn.com.ut.protocol.entity.EstJsqDownloadResultsReport;
import cn.com.ut.protocol.entity.EstJsqProgramData;
import cn.com.ut.protocol.entity.EstJsqVersion;
import cn.com.ut.protocol.entity.EstKeyDataInfo;
import cn.com.ut.protocol.entity.EstSelfStudyAuthorize;
import cn.com.ut.protocol.entity.EstTaskTicket;
import cn.com.ut.protocol.model.ILockBase;
import cn.com.ut.protocol.model.LockBase;
import cn.com.ut.protocol.est.ProtocolEst;
import cn.com.ut.protocol.model.MessageType;
import cn.com.ut.protocol.model.OnMessageEventListener;
import cn.com.ut.protocol.model.ProtocolFeatureType;
import cn.com.ut.protocol.port.IBluetoothClientPort;
import cn.com.ut.protocol.port.IPortBase;

/**
 * E匙通操作服务类
 * Created by zhangyihuang on 2017/3/30.
 */
public class EstOperationService extends IEstOperationService implements IPortBase.OnPortStatusChangedListener,
        IPortBase.OnPortReportListener, OnMessageEventListener {

    private final String TAG = EstOperationService.class.getName();
    private Channel channel;
    private ProtocolEst protocolEst;
    private IBluetoothClientPort mBluetoothPort;

    /**
     * 解锁器是否在插入状态
     */
    private boolean isPlugIn;

    /**
     * 当前锁具
     */
    private ILockBase curLock;

    public EstOperationService(String defaultAddr, boolean enableDatagram) {
        mBluetoothPort = new BluetoothClientPort();
        mBluetoothPort.setPortConfig(defaultAddr, enableDatagram);
        mBluetoothPort.setOnPortStatusChangedListener(this);
        mBluetoothPort.setOnMessageEventListener(this);
        mBluetoothPort.setOnPortReportListener(this);
        protocolEst = new ProtocolEst(this);
        protocolEst.setProtocolFeatureType(ProtocolFeatureType.Unlock);
        protocolEst.setPort(mBluetoothPort);
        channel = new Channel();
        channel.setPort(mBluetoothPort);
        channel.setProtocol(protocolEst);
    }

    /**
     * 构造函数
     *
     * @param context           上下文
     * @param defaultAddr       蓝牙连接地址
     * @param enableDatagram    是否开启报文日志
     * @param isBluetoothClient 是否传统蓝牙
     */
    public EstOperationService(Context context, String defaultAddr, boolean enableDatagram, boolean isBluetoothClient) {
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
        protocolEst = new ProtocolEst(this);
        protocolEst.setProtocolFeatureType(ProtocolFeatureType.Unlock);
        protocolEst.setPort(mBluetoothPort);
        channel = new Channel();
        channel.setPort(mBluetoothPort);
        channel.setProtocol(protocolEst);
    }

    /**
     * 重置蓝牙端口
     *
     * @param context           上下文
     * @param defaultAddr       蓝牙连接地址
     * @param enableDatagram    是否开启报文日志
     * @param isBluetoothClient 是否传统蓝牙
     * @return
     */
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
        if (protocolEst != null) {
            protocolEst.setPort(mBluetoothPort);
        }
        if (channel != null) {
            channel.setPort(mBluetoothPort);
        }
    }

    @Override
    public void resetPortAddr(String defaultAddr) {
        mBluetoothPort.setPortConfig(defaultAddr);
    }

    @Override
    public void open() {
        if (channel != null) {
            channel.open();
        }
    }

    @Override
    public void close() {
        if (channel != null)
            channel.close();
    }

    @Override
    public boolean isConnected() {
        if (channel != null)
            return channel.isOpen();
        return false;
    }

    @Override
    public IBluetoothClientPort getPort() {
        return mBluetoothPort;
    }

    @Override
    public void openChannel() {
        if (channel != null && !channel.isOpen()) {
            channel.open();
        }
    }

    @Override
    public void receivePullOut() {

    }

    @Override
    public void receivePlugIn(long rfid) {
        onPlugIn(rfid);
    }

    /**
     * 创建自学数据，并保存MD5码
     *
     * @param keyDataInfo
     */
    @Override
    public void createStudyDataAndSaveMD5(EstKeyDataInfo keyDataInfo) {
        protocolEst.createStudyDataAndSaveMD5(keyDataInfo);
    }

    /**
     * 发送钥匙自学数据
     *
     * @param keyDataInfo
     * @return
     */
    @Override
    public Future<BoolResult> sendSelfStudyDataInfo(EstKeyDataInfo keyDataInfo, IMultiFramesSendListener multiFramesSend) throws InterruptedException, ExecutionException {
        return protocolEst.sendSelfStudyDataInfo(keyDataInfo, multiFramesSend);
    }

    /**
     * 发送APP授权电脑钥匙命令
     *
     * @param studyAuthorize
     * @return
     */
    @Override
    public Future<BoolResult> sendSelfStudyAuthorize(EstSelfStudyAuthorize studyAuthorize, OnKeySelfStudyListener onKeySelfStudyListener) {
        setOnKeySelfStudyListener(onKeySelfStudyListener);
        return protocolEst.sendSelfStudyAuthorize(studyAuthorize);
    }

    /**
     * 发送取消登陆命令
     *
     * @return
     */
    @Override
    public Future<BoolResult> sendCancelLogin() {
        return protocolEst.sendCancelLogin();
    }

    /**
     * 发送操作票数据命令
     *
     * @param taskTicket
     * @return
     */
    @Override
    public Future<BoolResult> sendEstTaskTicketData(EstTaskTicket taskTicket) throws InterruptedException, ExecutionException {
        return protocolEst.sendEstTaskTicketData(taskTicket);
    }

    /**
     * 请求追忆回传数据
     */
    @Override
    public void requestRecallReturnData(OnReturnRecallDataListener onReturnRecallDataListener) {
        IMultiReceiveInfoService multiReceive;
        if (onReturnRecallDataListener instanceof IMultiReceiveInfoService) {
            multiReceive = (IMultiReceiveInfoService) onReturnRecallDataListener;
        } else {
            multiReceive = new ReturnRecallDataService(onReturnRecallDataListener);
        }
        protocolEst.requestRecallReturnData(multiReceive);
    }

    /**
     * 发送获取解锁器软件版本命令
     *
     * @param onGetJsqVersionListener
     */
    @Override
    public void sendGetJsqVersion(OnGetJsqVersionListener onGetJsqVersionListener) {
        setOnGetJsqVersionListener(onGetJsqVersionListener);
        protocolEst.sendGetJsqVersion();
    }

    /**
     * 发送解锁器升级命令
     *
     * @param jsqProgramData
     */
    @Override
    public void sendUpdateFileToJSQ(EstJsqProgramData jsqProgramData, IMultiFramesSendListener multiFramesSend, OnUpgradeResultsReportListener onUpgradeResultsReportListener) throws InterruptedException, ExecutionException {
        setOnUpgradeResultsReportListener(onUpgradeResultsReportListener);
        protocolEst.sendUpdateFileToJSQ(jsqProgramData, multiFramesSend);
    }

    /**
     * 发送删除操作票命令
     *
     * @return
     */
    @Override
    public Future<BoolResult> sendDeleteEstTaskTicket() {
        return protocolEst.sendDeleteEstTaskTicket();
    }

    @Override
    public void onUpgradeResultsReport(EstJsqDownloadResultsReport resultsReport) {
        if (mOnUpgradeResultsReportListener != null) {
            mOnUpgradeResultsReportListener.onUpgradeResultsReport(resultsReport);
        }
    }

    /**
     * @param msg
     * @param msgType
     */
    @Override
    public void onMessageEvent(String msg, MessageType msgType) {

    }

    /**
     * 端口状态改变事件,是否连接上
     */
    @Override
    public void onPortStatusChanged(boolean bState) {
        if (mOnPortStatusChangedListener != null)
            mOnPortStatusChangedListener.onPortStatusChanged(bState);
    }

    /**
     * 插入
     *
     * @param rfid 锁码
     */
    public void onPlugIn(long rfid) {
        isPlugIn = true;
        curLock = new LockBase((short) 0x13);
        curLock.setOnMessageActionListener(mOnMessageActionListener);
        curLock.setOnCompleteActionListener(mOnCompleteActionListener);
        curLock.setOnJsqResetActionListener(mOnJsqResetActionListener);
        curLock.setRfid(rfid);
        if (mOnPlugInLockActionListener != null) {
            mOnPlugInLockActionListener.onPlugInLockAction(curLock, true);
        }
    }

    private void onMessageAction(String msg, MessageType msgType) {
        if (mOnMessageActionListener != null) {
            mOnMessageActionListener.onMessageEvent(msg, msgType);
        }
    }

    /**
     * 请求对钥匙进行自学
     */
    @Override
    public void requestKeySelfStudy() {
        if (mOnKeySelfStudyListener != null) {
            mOnKeySelfStudyListener.onKeySelfStudy();
        }
    }

    /**
     * 授权成功
     */
    @Override
    public void onAuthorizedSuccess() {
        if (mOnKeySelfStudyListener != null) {
            mOnKeySelfStudyListener.onAuthorizedSuccess();
        }
    }

    /**
     * 得到解锁器版本回调
     *
     * @param jsqVersion
     */
    @Override
    public void onGetJsqVersion(EstJsqVersion jsqVersion) {
        if (mOnGetJsqVersionListener != null) {
            mOnGetJsqVersionListener.onGetJsqVersion(jsqVersion);
        }
    }

    @Override
    public void onPortReport(String report) {
        DataWriteUtils.writeBytesToFile(report);
        DataWriteUtils.printFrameBytes(TAG, report);
    }
}