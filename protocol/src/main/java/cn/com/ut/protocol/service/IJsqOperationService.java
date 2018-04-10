package cn.com.ut.protocol.service;

import android.content.Context;

import java.util.concurrent.Future;

import cn.com.ut.protocol.BoolResult;
import cn.com.ut.protocol.entity.JsqYDCalibrateResult;
import cn.com.ut.protocol.model.ILockBase;
import cn.com.ut.protocol.entity.JsqBluetoothName;
import cn.com.ut.protocol.entity.JsqCalibrateResult;
import cn.com.ut.protocol.entity.JsqModifyBluetoothResult;
import cn.com.ut.protocol.entity.JsqOperateResult;
import cn.com.ut.protocol.entity.JsqOptCommand;
import cn.com.ut.protocol.entity.JsqVersion;
import cn.com.ut.protocol.entity.JsqWxsCalibrate;
import cn.com.ut.protocol.entity.JsqWxsInfo;
import cn.com.ut.protocol.entity.JsqWxsInfoResult;
import cn.com.ut.protocol.entity.JsqYDResult;
import cn.com.ut.protocol.model.OnMessageEventListener;
import cn.com.ut.protocol.port.IBluetoothClientPort;
import cn.com.ut.protocol.port.IPortBase;

/**
 * 解锁器操作接口
 * Created by zhangyihuang on 2016/12/26.
 */
public abstract class IJsqOperationService {

    /**
     * 打开
     */
    public abstract void open();

    /**
     * 关闭连接
     */
    public abstract void close();

    /**
     * 上一次的操作结果
     */
    public abstract JsqOperateResult getLastJsqOperateResult();

    /**
     * 设置上一次的操作结果
     */
    public abstract void setLastJsqOperateResult(JsqOperateResult result);

    /**
     * 解锁器拔出
     */
    public abstract void receivePullOut();

    /**
     * 2A解锁器插入
     *
     * @param rfid
     * @param lockType
     */
    public abstract void receivePlugIn2A(long rfid, byte lockType);

    /**
     * 解锁器插入
     *
     * @param rfid
     */
    public abstract void receivePlugIn(long rfid);

    /**
     * 设置当前锁具
     *
     * @param rfid
     * @param lockType
     */
    public abstract void setCurLockBase(long rfid, short lockType);

    /**
     * 收到操作结果
     *
     * @param result
     */
    public abstract void receiveOptResult(JsqOperateResult result);

    /**
     * 收到验电结果
     *
     * @param result
     */
    public abstract void receiveYDResult(JsqYDResult result);

    /**
     * 收到修改蓝牙名称结果
     *
     * @param result
     */
    public abstract void receiveModifyBluetoothNameResult(JsqModifyBluetoothResult result);

    /**
     * 收到验电标定结果
     *
     * @param result
     */
    public abstract void receiveYDCalibrateResult(JsqYDCalibrateResult result);

    /**
     * 接收到复位结果
     *
     * @param result
     */
    public abstract void receiveResetResult(byte result);

    /**
     * 接收到解锁器版本
     *
     * @param result
     */
    public abstract void receiveJSQVersion(JsqVersion result);

    /**
     * 收到无线锁标定结果
     *
     * @param result
     */
    public abstract void receiveCalibrateResult(JsqCalibrateResult result);

    /**
     * 收到无线锁信息
     *
     * @param result
     */
    public abstract void receiveWxsInfoResult(JsqWxsInfoResult result);

    /**
     * 操作解锁器
     *
     * @param optCommand 解锁器操作指令参数
     */
    public abstract Future<BoolResult> operationJsq(JsqOptCommand optCommand);

    /**
     * 下发复位指令
     */
    public abstract Future<BoolResult> sendResetCommand();

    /**
     * 下发操作指令
     *
     * @param command 命令
     */
    public abstract Future<BoolResult> sendOptCommand(JsqOptCommand command);

    /**
     * 下发RFID非法指令
     */
    public abstract Future<BoolResult> sendRfidErrorCommand();

    /**
     * 发送获取解锁器版本指令
     */
    public abstract Future<BoolResult> sendJSQVersionCommand();

    /**
     * 下发直接操作指令
     *
     * @param command 命令
     */
    public abstract Future<BoolResult> sendDirectOptCommand(JsqOptCommand command);

    /**
     * 发送无线锁标定指令
     *
     * @param command 命令
     */
    public abstract Future<BoolResult> sendCalibrateCommand(JsqWxsCalibrate command);

    /**
     * 发送获取无线锁信息指令
     *
     * @param command 命令
     */
    public abstract Future<BoolResult> sendWxsInfoCommand(JsqWxsInfo command);

    /**
     * 复位操作
     */
    public abstract void resetOperation();

    /**
     * 获取端口
     */
    public abstract IBluetoothClientPort getPort();

    /**
     * 设置是否启用报文日志
     *
     * @param enableDatagram
     */
    public abstract void setEnableDatagram(boolean enableDatagram);

    /**
     * 重置蓝牙连接端口
     *
     * @return
     */
    public abstract void resetBluetoothPort(Context context, String defaultAddr, boolean enableDatagram, boolean isBluetoothClient);

    /**
     * 获取端口连接状态
     */
    public abstract boolean isConnected();

    /**
     * 解锁当前锁
     */
    public abstract boolean unlockCurLock();

    /**
     * 解锁时向解锁器发送票id
     */
    public abstract boolean unlockCurLock(int historyId);

    /**
     * 打开连接
     */
    public abstract void openChannel();

    /**
     * 发送查询验电命令
     */
    public abstract Future<BoolResult> sendQueryElectricityCommand();

    /**
     * 发送修改设备蓝牙名称命令
     */
    public abstract Future<BoolResult> sendModifyBluetoothNameCommand(JsqBluetoothName command);

    /**
     * 发送验电标定命令
     */
    public abstract Future<BoolResult> sendYDCalibrateCommand();

    /**
     * 超时
     */
    public abstract void timeOut();

    /**
     * 收到操作结果事件
     */
    protected OnOptResultListener mOnOptResultListener;

    public void setOnOptResultListener(OnOptResultListener onOptResultListener) {
        this.mOnOptResultListener = onOptResultListener;
    }

    /**
     * 收到无线锁信息
     */
    protected OnWxsInfoResultListener mOnWxsInfoResultListener;

    public void setOnWxsInfoResultListener(OnWxsInfoResultListener onWxsInfoResultListener) {
        this.mOnWxsInfoResultListener = onWxsInfoResultListener;
    }

    /**
     * 收到无线锁标定结果
     */
    protected OnJsqCalibrateResultListener mOnJsqCalibrateResultListener;

    public void setOnJsqCalibrateResultListener(OnJsqCalibrateResultListener onJsqCalibrateResultListener) {
        this.mOnJsqCalibrateResultListener = onJsqCalibrateResultListener;
    }

    /**
     * 蓝牙重连后收到操作结果事件
     */
    protected OnOperationEventListener mOnOperationEventListener;

    public void setOnOperationEventListener(OnOperationEventListener onOperationEventListener) {
        this.mOnOperationEventListener = onOperationEventListener;
    }

    /**
     * 下发操作指令事件
     */
    protected OnOptCommandListener mOnOptCommandListener;

    public void setOnOptCommandListener(OnOptCommandListener onOptCommandListener) {
        this.mOnOptCommandListener = onOptCommandListener;
    }

    /**
     * 下发复位指令
     */
    protected OnResetCommandListener mOnResetCommandListener;

    public void setOnResetCommandListener(OnResetCommandListener onResetCommandListener) {
        this.mOnResetCommandListener = onResetCommandListener;
    }

    /**
     * 接收到解锁器版本
     */
    protected OnJSQVersionResultListener mOnJSQVersionResultListener;

    public void setOnJSQVersionResultListener(OnJSQVersionResultListener onJSQVersionResultListener) {
        this.mOnJSQVersionResultListener = onJSQVersionResultListener;
    }

    /**
     * 接收到复位结果
     */
    protected OnResetResultListener mOnResetResultListener;

    public void setOnResetResultListener(OnResetResultListener onResetResultListener) {
        this.mOnResetResultListener = onResetResultListener;
    }

    /**
     * 解锁器拔出,收到0码
     */
    protected OnPullOutListener mOnPullOutListener;

    public void setOnPullOutListener(OnPullOutListener onPullOutListener) {
        this.mOnPullOutListener = onPullOutListener;
    }

    /**
     * 下发RFID非法事件
     */
    protected OnRfidErrorCommandListener mOnRfidErrorCommandListener;

    public void setOnRfidErrorCommandListener(OnRfidErrorCommandListener onRfidErrorCommandListener) {
        this.mOnRfidErrorCommandListener = onRfidErrorCommandListener;
    }

    /**
     * 发送获取解锁器版本指令
     */
    protected OnJSQVersionCommandListener mOnJSQVersionCommandListener;

    public void setOnJSQVersionCommandListener(OnJSQVersionCommandListener onJSQVersionCommandListener) {
        this.mOnJSQVersionCommandListener = onJSQVersionCommandListener;
    }

    /**
     * 下发直接操作指令
     */
    protected OnDirectOptCommandListener mOnDirectOptCommandListener;

    public void setOnDirectOptCommandListener(OnDirectOptCommandListener onDirectOptCommandListener) {
        this.mOnDirectOptCommandListener = onDirectOptCommandListener;
    }

    /**
     * 发送无线锁标定指令
     */
    protected OnCalibrateCommandListener mOnCalibrateCommandListener;

    public void setOnCalibrateCommandListener(OnCalibrateCommandListener onCalibrateCommandListener) {
        this.mOnCalibrateCommandListener = onCalibrateCommandListener;
    }

    /**
     * 发送获取无线锁信息指令
     */
    protected OnWxsInfoCommandListener mOnWxsInfoCommandListener;

    public void setOnWxsInfoCommandListener(OnWxsInfoCommandListener onWxsInfoCommandListener) {
        this.mOnWxsInfoCommandListener = onWxsInfoCommandListener;
    }

    protected OnPlugInLockActionListener mOnPlugInLockActionListener;

    public void setOnPlugInLockActionListener(OnPlugInLockActionListener onPlugInLockActionListener) {
        this.mOnPlugInLockActionListener = onPlugInLockActionListener;
    }

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
     * 端口状态改变事件,是否连接上
     */
    public IPortBase.OnPortStatusChangedListener mOnPortStatusChangedListener;

    public void setOnPortStatusChangedListener(IPortBase.OnPortStatusChangedListener onPortStatusChangedListener) {
        this.mOnPortStatusChangedListener = onPortStatusChangedListener;
    }

    /**
     * 得到需要解锁的锁,由应用指定（由锁码得到锁）
     */
    protected OnGetLockFuncListener mOnGetLockFuncListener;

    public void setOnGetLockFuncListener(OnGetLockFuncListener onGetLockFuncListener) {
        this.mOnGetLockFuncListener = onGetLockFuncListener;
    }

    /**
     * 电压值汇报事件
     */

    protected ILockBase.OnVoltageReportListener mOnVoltageReportListener;

    public void setOnVoltageReportListener(ILockBase.OnVoltageReportListener onVoltageReportListener) {
        this.mOnVoltageReportListener = onVoltageReportListener;
    }

    /**
     * 验电结果监听事件
     */
    protected OnYDResultReportListener mOnYDResultReportListener;

    /**
     * 设置验电结果监听
     *
     * @param onYDResultReportListener
     */
    public void setOnYDResultReportListener(OnYDResultReportListener onYDResultReportListener) {
        this.mOnYDResultReportListener = onYDResultReportListener;
    }

    /**
     * 修改设备蓝牙名称结果监听事件
     */
    protected OnModifyBluetoothResultListener mOnModifyBluetoothResultListener;

    /**
     * 设置修改设备蓝牙名称结果监听
     *
     * @param onModifyBluetoothResultListener
     */
    public void setOnModifyBluetoothResultListener(OnModifyBluetoothResultListener onModifyBluetoothResultListener) {
        this.mOnModifyBluetoothResultListener = onModifyBluetoothResultListener;
    }

    /**
     * 验电标定结果监听事件
     */
    protected OnYDCalibrateResultReportListener mOnYDCalibrateResultReportListener;

    /**
     * 设置验电标定结果监听
     *
     * @param onYDCalibrateResultReportListener
     */
    public void setOnYDCalibrateResultReportListener(OnYDCalibrateResultReportListener onYDCalibrateResultReportListener) {
        this.mOnYDCalibrateResultReportListener = onYDCalibrateResultReportListener;
    }

    /**
     * 超时监听事件
     */
    protected OnTimeOutListener mOnTimeOutListener;

    /**
     * 设置超时监听
     * @param mOnTimeOutListener
     */
    public void setmOnTimeOutListener(OnTimeOutListener mOnTimeOutListener) {
        this.mOnTimeOutListener = mOnTimeOutListener;
    }

    public interface OnResetResultListener {
        /**
         * 接收到复位结果事件
         */
        void onResetResult(byte result);
    }

    public interface OnOptResultListener {
        /**
         * 收到操作结果事件
         */
        void onOptResult(JsqOperateResult result);
    }

    public interface OnWxsInfoResultListener {
        /**
         * 收到无线锁信息
         */
        void onWxsInfoResult(JsqWxsInfoResult result);
    }

    public interface OnJsqCalibrateResultListener {
        /**
         * 收到无线锁标定结果
         */
        void onJsqCalibrateResult(JsqCalibrateResult result);
    }

    public interface OnOptCommandListener {
        /**
         * 下发操作指令事件
         */
        Future<BoolResult> onOptCommand(JsqOptCommand command);
    }

    public interface OnResetCommandListener {
        /**
         * 下发复位指令
         */
        Future<BoolResult> onResetCommand();
    }

    public interface OnJSQVersionResultListener {
        /**
         * 接收到解锁器版本
         */
        void onJSQVersionResult(JsqVersion result);
    }

    public interface OnPullOutListener {
        /**
         * 解锁器拔出,收到0码
         */
        void onPullOut();
    }

    public interface OnRfidErrorCommandListener {
        /**
         * 下发RFID非法事件
         */
        Future<BoolResult> onRfidErrorCommand();
    }

    public interface OnJSQVersionCommandListener {
        /**
         * 发送获取解锁器版本指令
         */
        Future<BoolResult> onJSQVersionCommand();
    }

    public interface OnDirectOptCommandListener {
        /**
         * 下发直接操作指令
         */
        Future<BoolResult> onDirectOptCommand(JsqOptCommand command);
    }

    public interface OnCalibrateCommandListener {
        /**
         * 发送无线锁标定指令
         */
        Future<BoolResult> onCalibrateCommand(JsqWxsCalibrate command);
    }

    public interface OnWxsInfoCommandListener {
        /**
         * 发送获取无线锁信息指令
         */
        Future<BoolResult> onWxsInfoCommand(JsqWxsInfo command);
    }

    public interface OnPlugInLockActionListener {
        /**
         * 当前插入的锁具，解锁器2A插入，便知道当前的锁具
         *
         * @param lockBase
         * @param isWxsLock 是否无线锁
         */
        void onPlugInLockAction(ILockBase lockBase, boolean isWxsLock);
    }

    public interface OnGetLockFuncListener {
        /**
         * 得到需要解锁的锁,由应用指定（由锁码得到锁）
         */
        ILockBase onGetLockFunc(long rfid);
    }

    public interface OnOperationEventListener {
        void onOperationEvent(JsqOperateResult jsqOperateResult);
    }

    public interface OnYDResultReportListener {
        /**
         * 验电结果上报
         *
         * @param ydResult
         */
        void onYDResultReport(JsqYDResult ydResult);
    }

    public interface OnModifyBluetoothResultListener {
        /**
         * 修改设备蓝牙名称结果上报
         *
         * @param result
         */
        void onModifyBluetoothResult(JsqModifyBluetoothResult result);
    }

    public interface OnYDCalibrateResultReportListener {
        /**
         * 验电标定结果上报
         *
         * @param ydCalibrateResult
         */
        void onYDCalibrateResult(JsqYDCalibrateResult ydCalibrateResult);
    }

    public interface OnTimeOutListener{
        /**
         * 所有命令超时监听
         */
        void onTimeOut();
    }
}