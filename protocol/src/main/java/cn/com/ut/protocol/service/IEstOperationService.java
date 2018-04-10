package cn.com.ut.protocol.service;

import android.content.Context;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cn.com.ut.protocol.BoolResult;
import cn.com.ut.protocol.entity.EstJsqDownloadResultsReport;
import cn.com.ut.protocol.entity.EstJsqProgramData;
import cn.com.ut.protocol.entity.EstJsqVersion;
import cn.com.ut.protocol.entity.EstKeyDataInfo;
import cn.com.ut.protocol.entity.EstSelfStudyAuthorize;
import cn.com.ut.protocol.entity.EstTaskTicket;
import cn.com.ut.protocol.model.ILockBase;
import cn.com.ut.protocol.model.OnMessageEventListener;
import cn.com.ut.protocol.port.IBluetoothClientPort;
import cn.com.ut.protocol.port.IPortBase;

/**
 * E匙通操作服务接口
 * Created by zhangyihuang on 2017/3/30.
 */
public abstract class IEstOperationService {

    /**
     * 打开
     */
    public abstract void open();

    /**
     * 关闭连接
     */
    public abstract void close();

    /**
     * 获取端口连接状态
     */
    public abstract boolean isConnected();

    /**
     * 获取端口
     */
    public abstract IBluetoothClientPort getPort();

    /**
     * 重置蓝牙连接端口
     *
     * @return
     */
    public abstract void resetBluetoothPort(Context context, String defaultAddr, boolean enableDatagram, boolean isBluetoothClient);

    /**
     * 打开连接
     */
    public abstract void openChannel();

    /**
     * 重新设置蓝牙连接默认地址
     *
     * @param defaultAddr
     */
    public abstract void resetPortAddr(String defaultAddr);

    /**
     * 解锁器拔出
     */
    public abstract void receivePullOut();

    /**
     * 解锁器插入
     *
     * @param rfid
     */
    public abstract void receivePlugIn(long rfid);

    /**
     * 创建自学数据，并保存MD5码
     *
     * @param keyDataInfo
     */
    public abstract void createStudyDataAndSaveMD5(EstKeyDataInfo keyDataInfo);

    /**
     * 发送钥匙自学数据
     */
    public abstract Future<BoolResult> sendSelfStudyDataInfo(EstKeyDataInfo keyDataInfo, IMultiFramesSendListener multiFramesSend) throws InterruptedException, ExecutionException;

    /**
     * 发送APP授权电脑钥匙命令
     *
     * @param studyAuthorize
     * @return
     */
    public abstract Future<BoolResult> sendSelfStudyAuthorize(EstSelfStudyAuthorize studyAuthorize, OnKeySelfStudyListener onKeySelfStudyListener);

    /**
     * 发送取消登陆命令
     *
     * @return
     */
    public abstract Future<BoolResult> sendCancelLogin();

    /**
     * 发送操作票数据命令
     *
     * @param taskTicket
     * @return
     */
    public abstract Future<BoolResult> sendEstTaskTicketData(EstTaskTicket taskTicket) throws InterruptedException, ExecutionException;

    /**
     * 请求重新对钥匙自学
     */
    public abstract void requestKeySelfStudy();

    /**
     * 授权成功
     */
    public abstract void onAuthorizedSuccess();

    /**
     * 得到解锁器版本回调
     */
    public abstract void onGetJsqVersion(EstJsqVersion jsqVersion);

    /**
     * 请求追忆回传数据
     */
    public abstract void requestRecallReturnData(OnReturnRecallDataListener onReturnRecallDataListener);

    /**
     * 发送获取解锁器软件版本命令
     */
    public abstract void sendGetJsqVersion(OnGetJsqVersionListener onGetJsqVersionListener);

    /**
     * 发送解锁器升级命令
     */
    public abstract void sendUpdateFileToJSQ(EstJsqProgramData jsqProgramData, IMultiFramesSendListener multiFramesSend,
                                             OnUpgradeResultsReportListener onUpgradeResultsReportListener) throws InterruptedException, ExecutionException;

    /**
     * 发送删除操作票命令
     */
    public abstract Future<BoolResult> sendDeleteEstTaskTicket();

    /**
     * 得到解锁器升级结果回调
     *
     * @param resultsReport
     */
    public abstract void onUpgradeResultsReport(EstJsqDownloadResultsReport resultsReport);

    /**
     * 提示消息事件
     */
    protected OnMessageEventListener mOnMessageActionListener;

    public void setOnMessageActionListener(OnMessageEventListener onMessageEventListener) {
        this.mOnMessageActionListener = onMessageEventListener;
    }

    /**
     * 操作完成事件
     */
    protected ILockBase.OnCompleteActionListener mOnCompleteActionListener;

    public void setOnCompleteActionListener(ILockBase.OnCompleteActionListener onCompleteActionListener) {
        this.mOnCompleteActionListener = onCompleteActionListener;
    }

    /**
     * 解锁器复位事件
     */
    protected ILockBase.OnJsqResetActionListener mOnJsqResetActionListener;

    public void setOnJsqResetActionListener(ILockBase.OnJsqResetActionListener onJsqResetActionListener) {
        this.mOnJsqResetActionListener = onJsqResetActionListener;
    }

    /**
     * 请求自学监听接口
     */
    protected OnKeySelfStudyListener mOnKeySelfStudyListener;

    /**
     * 设置监听请求自学接口
     *
     * @param onKeySelfStudyListener
     */
    public void setOnKeySelfStudyListener(OnKeySelfStudyListener onKeySelfStudyListener) {
        this.mOnKeySelfStudyListener = onKeySelfStudyListener;
    }

    /**
     * 解锁器版本回调监听接口
     */
    protected OnGetJsqVersionListener mOnGetJsqVersionListener;

    /**
     * 设置监听获取解锁器版本回调接口
     *
     * @param onGetJsqVersionListener
     */
    public void setOnGetJsqVersionListener(OnGetJsqVersionListener onGetJsqVersionListener) {
        this.mOnGetJsqVersionListener = onGetJsqVersionListener;
    }

    /**
     * 解锁器升级结果回调接口
     */
    protected OnUpgradeResultsReportListener mOnUpgradeResultsReportListener;

    /**
     * 设置解锁器升级结果回调接口
     *
     * @param onUpgradeResultsReportListener
     */
    public void setOnUpgradeResultsReportListener(OnUpgradeResultsReportListener onUpgradeResultsReportListener) {
        this.mOnUpgradeResultsReportListener = onUpgradeResultsReportListener;
    }

    protected OnPlugInLockActionListener mOnPlugInLockActionListener;

    public void setOnPlugInLockActionListener(OnPlugInLockActionListener onPlugInLockActionListener) {
        this.mOnPlugInLockActionListener = onPlugInLockActionListener;
    }

    /**
     * 得到需要解锁的锁,由应用指定（由锁码得到锁）
     */
    protected OnGetLockFuncListener mOnGetLockFuncListener;

    public void setOnGetLockFuncListener(OnGetLockFuncListener onGetLockFuncListener) {
        this.mOnGetLockFuncListener = onGetLockFuncListener;
    }

    /**
     * 端口状态改变事件,是否连接上
     */
    public IPortBase.OnPortStatusChangedListener mOnPortStatusChangedListener;

    public void setOnPortStatusChangedListener(IPortBase.OnPortStatusChangedListener onPortStatusChangedListener) {
        this.mOnPortStatusChangedListener = onPortStatusChangedListener;
    }

    public interface OnKeySelfStudyListener {
        /**
         * 授权成功
         */
        void onAuthorizedSuccess();

        /**
         * 请求对钥匙自学
         */
        void onKeySelfStudy();
    }

    public interface OnGetLockFuncListener {
        /**
         * 得到需要解锁的锁,由应用指定（由锁码得到锁）
         */
        ILockBase onGetLockFunc(long rfid);
    }

    public interface OnPlugInLockActionListener {
        /**
         * 当前插入的锁具，解锁器2A插入，便知道当前的锁具
         */
        void onPlugInLockAction(ILockBase lockBase, boolean isWxsLock);
    }

    public interface OnGetJsqVersionListener {
        /**
         * 得到解锁器版本回调
         *
         * @param jsqVersion
         */
        void onGetJsqVersion(EstJsqVersion jsqVersion);
    }

    public interface OnUpgradeResultsReportListener {
        /**
         * 解锁器升级结果回调
         *
         * @param resultsReport
         */
        void onUpgradeResultsReport(EstJsqDownloadResultsReport resultsReport);
    }
}