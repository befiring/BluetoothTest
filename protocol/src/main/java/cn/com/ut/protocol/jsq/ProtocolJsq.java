package cn.com.ut.protocol.jsq;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cn.com.ut.protocol.BoolResult;
import cn.com.ut.protocol.DataConvert;
import cn.com.ut.protocol.core.Frame;
import cn.com.ut.protocol.core.ProtocolBase;
import cn.com.ut.protocol.entity.JsqBluetoothName;
import cn.com.ut.protocol.entity.JsqCalibrateResult;
import cn.com.ut.protocol.entity.JsqCalibratedType;
import cn.com.ut.protocol.entity.JsqModifyBluetoothResult;
import cn.com.ut.protocol.entity.JsqOperateResult;
import cn.com.ut.protocol.entity.JsqOptCommand;
import cn.com.ut.protocol.entity.JsqTicketData;
import cn.com.ut.protocol.entity.JsqTicketInfo;
import cn.com.ut.protocol.entity.JsqVersion;
import cn.com.ut.protocol.entity.JsqWxsCalibrate;
import cn.com.ut.protocol.entity.JsqWxsInfo;
import cn.com.ut.protocol.entity.JsqWxsInfoResult;
import cn.com.ut.protocol.entity.JsqYDCalibrateResult;
import cn.com.ut.protocol.entity.JsqYDResult;
import cn.com.ut.protocol.model.ProtocolFeatureType;
import cn.com.ut.protocol.service.IJsqOperationService;

/**
 * 解锁器规约
 * Created by zhangyihuang on 2016/12/26.
 */
public class ProtocolJsq extends ProtocolBase {
    private final String TAG = ProtocolJsq.class.getName();

    /**
     * 发送超时
     */
    private final int sendTimeOut = 3000;
    private ExecutorService executorService;

    /**
     * 当前发送的帧
     */
    private JsqFrame curSendFrame;

    private IJsqOperationService jsqOperation;

    public ProtocolJsq(IJsqOperationService jsqOperation) {
        super();
        setProtocolName("解锁器规约");
        this.protocolFeatureType = ProtocolFeatureType.Unlock;
        this.jsqOperation = jsqOperation;
        this.executorService = Executors.newCachedThreadPool();
    }

    protected Frame getFrame() {
        return new JsqFrame();
    }

    /**
     * 启用
     *
     * @param value
     * @return
     */
    public void setEnabled(boolean value) {
        if (!isEnabled && value) {
            isEnabled = true;
            enableSendQueueWorker(isEnabled);
        } else if (isEnabled && !value) {
            isEnabled = false;
            enableSendQueueWorker(isEnabled);
        }
    }

    /**
     * 校验错
     *
     * @param frame
     * @return
     */
    protected void crcError(Frame frame) {
        JsqFrame ackFrame = getErrorFrame(JsqErrorCode.CRCERROR, ((JsqFrame) frame).getNum());
        enqueueSendData(ackFrame);
    }

    /**
     * 得到错误帧
     *
     * @param errorCode 错误码
     * @param num       帧号
     * @return
     */
    private static JsqFrame getErrorFrame(byte errorCode, int num) {
        JsqFrame jsqFrame = new JsqFrame();
        jsqFrame.setCmdCode(0);
        jsqFrame.setNum(num);
        byte[] content = new byte[1];
        content[0] = errorCode;
        jsqFrame.setContent(content);
        return jsqFrame;
    }

    protected void processReceiveFrame(Frame frame) {
        JsqFrame jsqFrame = (JsqFrame) frame;
        if (null != jsqFrame) {
            switch (jsqFrame.getCmdCode()) {
                case CommandCode.FRAMEERROR:
                    //Log.w("processReceiveFrame", "2222222222222222222222222222222222222222222222");
                    break;
                case CommandCode.UPRFID:
                    enqueueSendData(getCommAckFrame(jsqFrame.getNum(), CommandCode.UPRFIDACK));
                    receiveRfid(jsqFrame);
                    break;
                case CommandCode.OPTCOMMANDACK:  //收到操作指令应答
                    if (CommandCode.OPTCOMMAND == curSendFrame.getCmdCode())
                        curSendFrame.sendSuccess("");
                    break;
                case CommandCode.DIRECTOPTCOMMANDACK:  //收到直接操作指令应答
                    if (CommandCode.DIRECTOPTCOMMAND == curSendFrame.getCmdCode())
                        curSendFrame.sendSuccess("");
                    break;
                case CommandCode.UPOPTRESULT:  //操作结果上传
                    enqueueSendData(getCommAckFrame(jsqFrame.getNum(), CommandCode.UPOPTRESULTACK));
                    JsqOperateResult operResult = setJsqOperateResult(jsqFrame);
                    jsqOperation.receiveOptResult(operResult);
                    break;
                case CommandCode.RESETACK:  //接收到解锁器复位应答
                    if (CommandCode.RESET == curSendFrame.getCmdCode()) {
                        curSendFrame.sendSuccess("");
                        byte resetResult = jsqFrame.getContent()[0];
                        jsqOperation.receiveResetResult(resetResult);
                    }
                    break;
                case CommandCode.JSQVERSIONACK:  //接收到解锁器软件版本号
                    if (CommandCode.JSQVERSION == curSendFrame.getCmdCode()) {
                        curSendFrame.sendSuccess("");
                        JsqVersion jsqVersion = setJsqVersion(jsqFrame.getContent());
                        jsqOperation.receiveJSQVersion(jsqVersion);
                    }
                    break;
                case CommandCode.RFIDERRORACK:
                    if (CommandCode.RFIDERROR == curSendFrame.getCmdCode())
                        curSendFrame.sendSuccess("");
                    break;
                case CommandCode.WXSCALIBRATEDRESULT:
                    enqueueSendData(getCommAckFrame(jsqFrame.getNum(), CommandCode.WXSCALIBRATEDRESULTACK));
                    JsqCalibrateResult calibrateResult = setJsqCalibrateResult(jsqFrame);
                    jsqOperation.receiveCalibrateResult(calibrateResult);
                    break;
                case CommandCode.WXSCALIBRATEACK:
                    if (CommandCode.WXSCALIBRATE == curSendFrame.getCmdCode())
                        curSendFrame.sendSuccess("");
                    break;
                case CommandCode.WXSINFORECEIVE:
                    enqueueSendData(getCommAckFrame(jsqFrame.getNum(), CommandCode.WXSINFORECEIVEACK));
                    JsqWxsInfoResult wxsInfoResult = setWxsInfoResult(jsqFrame);
                    jsqOperation.receiveWxsInfoResult(wxsInfoResult);
                    break;
                case CommandCode.WXSINFOACK:
                    if (CommandCode.WXSINFO == curSendFrame.getCmdCode())
                        curSendFrame.sendSuccess("");
                    break;
                case CommandCode.YDRESULTCOMMANDACK:
                    if (CommandCode.QUERYYDCOMMAND == curSendFrame.getCmdCode())
                        curSendFrame.sendSuccess("");
                    JsqYDResult ydResult = setJsqYDResult(jsqFrame);
                    jsqOperation.receiveYDResult(ydResult);
                    break;
                case CommandCode.MODIFYBLUETOOTHNAMEACK:
                    if (CommandCode.MODIFYBLUETOOTHNAME == curSendFrame.getCmdCode())
                        curSendFrame.sendSuccess("");
                    JsqModifyBluetoothResult result = setJsqModifyBluetoothResult(jsqFrame);
                    jsqOperation.receiveModifyBluetoothNameResult(result);
                    break;
                case CommandCode.YDCALIBRATECOMMANDACK:
                    //验电标定应答命令
                    if (CommandCode.YDCALIBRATECOMMAND == curSendFrame.getCmdCode())
                        curSendFrame.sendSuccess("");
                    JsqYDCalibrateResult ydCalibrateResult = setJsqYDCalibrateResult(jsqFrame);
                    jsqOperation.receiveYDCalibrateResult(ydCalibrateResult);
                    break;
            }
        }
    }

    /**
     * 通用回ACK帧
     *
     * @param num     帧号
     * @param cmdcode 命令
     * @return
     */
    private static JsqFrame getCommAckFrame(int num, int cmdcode) {
        JsqFrame jsqFrame = new JsqFrame();
        jsqFrame.setCmdCode(cmdcode);
        jsqFrame.setNum(num);
        return jsqFrame;
    }

    /**
     * 收到锁码
     *
     * @param jsqFrame
     * @return
     */
    private void receiveRfid(JsqFrame jsqFrame) {
        long rfid = DataConvert.bytesToLong(jsqFrame.getContent(), 0, 6);
        byte[] rfiddata = new byte[6];
        System.arraycopy(jsqFrame.getContent(), 0, rfiddata, 0, 6);

        if (rfid == 0) {
            jsqOperation.receivePullOut();
        } else if (jsqFrame.getContent().length > 6) {
            //如果有传锁具类型，说明为2A解锁器操作
            byte lockType = jsqFrame.getContent()[6];
            jsqOperation.receivePlugIn2A(rfid, lockType);
        } else {
            jsqOperation.receivePlugIn(rfid);
        }
    }

    /**
     * 设置操作结果
     *
     * @param jsqFrame
     * @return
     */
    private static JsqOperateResult setJsqOperateResult(JsqFrame jsqFrame) {
        JsqOperateResult operResult = new JsqOperateResult();
        operResult.setRfid(DataConvert.bytesToLong(jsqFrame.getContent(), 0, 6));
        operResult.setOperateTime(new byte[6]);
        System.arraycopy(jsqFrame.getContent(), 6, operResult.getOperateTime(), 0, 6);
        operResult.setOperateDeviceId(jsqFrame.getContent()[12]);
        operResult.setOperResultDesHigh((short) ((jsqFrame.getContent()[13] << 8) + jsqFrame.getContent()[14]));
        operResult.setOperResultDesLow(jsqFrame.getContent()[15]);
        if (jsqFrame.getContent().length > 16) {
            operResult.setAVoltagePercentage(jsqFrame.getContent()[16]);
            operResult.setBVoltagePercentage(jsqFrame.getContent()[17]);
            operResult.setCVoltagePercentage(jsqFrame.getContent()[18]);
            operResult.setVoltageCondition(jsqFrame.getContent()[19]);
            operResult.setCalibrationValue(jsqFrame.getContent()[20]);
            operResult.setDeviceType(jsqFrame.getContent()[21]);
        }

        if (jsqFrame.getExHeadData() != null) {
            byte[] serialNumBytes = new byte[4];
            System.arraycopy(jsqFrame.getExHeadData(), 0, serialNumBytes, 0, 4);
            operResult.setCurSerialNumber(DataConvert.bytesToInt(serialNumBytes));
        }
        return operResult;
    }

    /**
     * 设置解锁器软件版本对象
     *
     * @param content
     */
    private JsqVersion setJsqVersion(byte[] content) {
        JsqVersion jsqVersion = new JsqVersion();

        byte[] appVersion = new byte[5];
        System.arraycopy(content, 0, appVersion, 0, 5);

        String appVersionStr = new String(appVersion);
        String[] version = appVersionStr.split("");
        appVersionStr = "";
        for (int i = 1; i < 6; i++) {
            appVersionStr += version[i];
            if (i == 1 || i == 3) {
                appVersionStr += ".";
            }
        }
        jsqVersion.appVersion = appVersionStr;//new String(appVersion);
        return jsqVersion;
    }

    /**
     * 处理无线锁标定结果
     *
     * @param frame
     * @return
     */
    private JsqCalibrateResult setJsqCalibrateResult(JsqFrame frame) {
        JsqCalibrateResult result = new JsqCalibrateResult();
        result.setLockTypeId((short) ((frame.getContent()[0] << 8) + frame.getContent()[1]));
        result.setCalibratedType(frame.getContent()[2]);
        result.setCalibratedResult(frame.getContent()[3]);
        if (result.getCalibratedType() == JsqCalibratedType.DBMS) {
            result.setCalibratedValue(new byte[2]);
            System.arraycopy(frame.getContent(), 4, result.getCalibratedValue(), 0, 2);
        }
        return result;
    }

    /**
     * 收到无线锁信息
     *
     * @param frame
     * @return
     */
    private JsqWxsInfoResult setWxsInfoResult(JsqFrame frame) {
        JsqWxsInfoResult result = new JsqWxsInfoResult();
        result.setLockTypeId((short) ((frame.getContent()[0] << 8) + frame.getContent()[1]));
        result.setRfid(DataConvert.bytesToLong(frame.getContent(), 2, 6));
        result.setWxsInfo(new byte[frame.getContent().length - 8]);
        System.arraycopy(frame.getContent(), 8, result.getWxsInfo(), 0, frame.getContent().length - 8);
        return result;
    }

    /**
     * 下发操作指令
     *
     * @param command 命令
     * @return
     */
    public Future<BoolResult> onOptCommand(final JsqOptCommand command) {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                int length = 16;
                if (command.getOperateExtraInfo() != null) {
                    length += command.getOperateExtraInfo().length;//操作附加信息
                }
                byte[] content = new byte[length];
                String time = DataConvert.getDateTime("yyMMddHHmmss");
                System.arraycopy(DataConvert.stringToBytes(time), 0, content, 0, 6);//校准时间
                DataConvert.int16ToBytes(command.getLockTypeId(), content, 6);//锁具类型ID码
                long rfid = command.getRfid();//DataConvert.getRfid();
                System.arraycopy(DataConvert.getRfid(rfid), 0, content, 8, 6);

                DataConvert.int16ToBytes(command.getJsqOptProperty(), content, 14);//锁具操作描述
                if (command.getOperateExtraInfo() != null && command.getOperateExtraInfo().length > 0) {
                    System.arraycopy(command.getOperateExtraInfo(), 0, content, 16, command.getOperateExtraInfo().length);
                }
                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(3);
                jsqFrame.setContent(content);
                jsqFrame.setExHeadData(command.getSerialNumber());

                if (command.getSerialNumber() != null) {
                    jsqFrame.setExHeadDataLength(jsqFrame.getExHeadDataLength() + 4);
                }
                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 发送复位指令
     *
     * @return
     */
    public Future<BoolResult> onResetCommand() {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(CommandCode.RESET);
                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 下发RFID非法指令
     *
     * @return
     */
    public Future<BoolResult> onRfidErrorCommand() {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(CommandCode.RFIDERROR);
                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 发送获取解锁器版本指令
     *
     * @return
     */
    public Future<BoolResult> onJSQVersionCommand() {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(CommandCode.JSQVERSION);
                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 下发直接操作指令
     *
     * @param command 命令
     * @return
     */
    public Future<BoolResult> onDirectOptCommand(final JsqOptCommand command) {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                int length = 16;
                if (command.getOperateExtraInfo() != null) {
                    length += command.getOperateExtraInfo().length;//操作附加信息
                }
                byte[] content = new byte[length];
                String time = DataConvert.getDateTime("yyMMddHHmmss");
                System.arraycopy(DataConvert.stringToBytes(time), 0, content, 0, 6);//校准时间
                DataConvert.int16ToBytes(command.getLockTypeId(), content, 6);//锁具类型ID码
                System.arraycopy(DataConvert.getRfid(command.getRfid()), 0, content, 8, 6);

                DataConvert.int16ToBytes(command.getJsqOptProperty(), content, 14);//锁具操作描述
                if (command.getOperateExtraInfo() != null && command.getOperateExtraInfo().length > 0) {
                    System.arraycopy(command.getOperateExtraInfo(), 0, content, 16, command.getOperateExtraInfo().length);
                }
                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(CommandCode.DIRECTOPTCOMMAND);
                jsqFrame.setContent(content);
                jsqFrame.setExHeadData(command.getSerialNumber());

                if (command.getSerialNumber() != null) {
                    jsqFrame.setExHeadDataLength(jsqFrame.getExHeadDataLength() + 4);
                }
                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 发送无线锁标定命令
     *
     * @param command 命令
     * @return
     */
    public Future<BoolResult> onCalibrateCommand(final JsqWxsCalibrate command) {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                int length = 3;
                if (command.getCalibratedType() == JsqCalibratedType.DBMS) {
                    length = command.getCalibratedValue().length + 3;
                }
                byte[] content = new byte[length];
                DataConvert.int16ToBytes(command.getLockTypeId(), content, 0);
                content[2] = command.getCalibratedType();
                if (command.getCalibratedType() == JsqCalibratedType.DBMS) {
                    System.arraycopy(command.getCalibratedValue(), 0, content, 3, command.getCalibratedValue().length);
                }
                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(CommandCode.WXSCALIBRATE);
                jsqFrame.setContent(content);
                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 获取无线锁信息
     *
     * @param command 命令
     * @return
     */
    public Future<BoolResult> onWxsInfoCommand(final JsqWxsInfo command) {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                byte[] content = new byte[3];
                DataConvert.int16ToBytes(command.getLockTypeId(), content, 0);
                content[2] = command.getCalibratedType();
                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(CommandCode.WXSINFO);
                jsqFrame.setContent(content);
                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 下发操作票数据帧
     *
     * @param command 命令
     * @return
     */
    private Future<BoolResult> jsqTicketInfoFrame(final JsqTicketInfo command) {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                int length = 29;
                byte[] content = new byte[length];
                System.arraycopy(command.getSerialNumber(), 0, content, 0, 4);//当前操作票序号
                String time = DataConvert.getDateTime("yyMMddHHmmss");
                System.arraycopy(DataConvert.stringToBytes(time), 0, content, 4, 6);//校准时间
                System.arraycopy(DataConvert.stringToBytes(command.getStartTime()), 0, content, 10, 6);//有效起始时间
                System.arraycopy(DataConvert.stringToBytes(command.getFinishTime()), 0, content, 16, 6);//有效结束时间
                DataConvert.intToBytes(command.getTicketLength(), content, 22);//操作票数据总字节长度
                DataConvert.int16ToBytes(command.getTicketFrames(), content, 26);//操作票数据总帧数
                content[28] = command.getOptDateVersion();//操作票数据版本号

                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(CommandCode.TICKETINFOFRAME);
                jsqFrame.setContent(content);
                jsqFrame.setExHeadData(command.getSerialNumber());

                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 下发操作票数据帧
     *
     * @param command 命令
     * @return
     */
    private Future<BoolResult> jsqTicketDataFrame(final JsqTicketData command) {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                int length = 4;
                if (command.getTicketData() != null) {
                    length += command.getTicketData().length;//操作票数据
                }

                byte[] content = new byte[length];
                DataConvert.int16ToBytes(command.getTicketFrames(), content, 0);//操作票数据总帧数
                DataConvert.int16ToBytes(command.getCurFrame(), content, 2);//当前数据帧帧号

                if (command.getTicketData() != null && command.getTicketData().length > 0) {
                    System.arraycopy(command.getTicketData(), 0, content, 4, command.getTicketData().length);
                }

                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(CommandCode.TICKETDATAFRAME);
                jsqFrame.setContent(content);
                jsqFrame.setExHeadData(command.getSerialNumber());

                if (command.getSerialNumber() != null) {
                    jsqFrame.setExHeadDataLength(jsqFrame.getExHeadDataLength() + 4);
                }
                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 发送查询验电命令
     *
     * @return
     */
    public Future<BoolResult> onQueryYDCommand() {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(CommandCode.QUERYYDCOMMAND);
                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 发送修改设备蓝牙名称命令
     *
     * @return
     */
    public Future<BoolResult> onModifyBluetoothNameCommand(final JsqBluetoothName command) {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                int length = 1;
                if (command.getBluetoothName() != null) {
                    length += command.getBluetoothName().length;//设备蓝牙名称
                }

                byte[] content = new byte[length];
                if (command.getBluetoothName() != null && command.getBluetoothName().length > 0) {
                    System.arraycopy(command.getBluetoothName(), 0, content, 0, command.getBluetoothName().length);
                }
                content[length - 1] = 0x00;//蓝牙名称是字符串，以0x00结束
                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(CommandCode.MODIFYBLUETOOTHNAME);
                jsqFrame.setContent(content);
                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 发送验电标定命令
     *
     * @return
     */
    public Future<BoolResult> onYDCalibrateCommand() {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                JsqFrame jsqFrame = new JsqFrame();
                jsqFrame.setCmdCode(CommandCode.YDCALIBRATECOMMAND);
                curSendFrame = jsqFrame;
                return sendThree(jsqFrame, sendTimeOut);
            }
        });
    }

    /**
     * 设置验电结果
     *
     * @param jsqFrame
     * @return
     */
    private static JsqYDResult setJsqYDResult(JsqFrame jsqFrame) {
        JsqYDResult ydResult = new JsqYDResult();
        ydResult.setAVoltagePercentage(jsqFrame.getContent()[0]);
        ydResult.setBVoltagePercentage(jsqFrame.getContent()[1]);
        ydResult.setCVoltagePercentage(jsqFrame.getContent()[2]);
        ydResult.setVoltageCondition(jsqFrame.getContent()[3]);
        ydResult.setCalibrationValue(jsqFrame.getContent()[4]);
        ydResult.setDeviceType(jsqFrame.getContent()[5]);
        return ydResult;
    }

    private static JsqModifyBluetoothResult setJsqModifyBluetoothResult(JsqFrame jsqFrame) {
        JsqModifyBluetoothResult result = new JsqModifyBluetoothResult();
        result.setModifyResult(jsqFrame.getContent()[0]);
        return result;
    }

    /**
     * 设置验电标定结果
     *
     * @param jsqFrame
     * @return
     */
    private static JsqYDCalibrateResult setJsqYDCalibrateResult(JsqFrame jsqFrame) {
        JsqYDCalibrateResult jsqYDCalibrateResult = new JsqYDCalibrateResult();
        jsqYDCalibrateResult.getJsqYDResult().setAVoltagePercentage(jsqFrame.getContent()[0]);
        jsqYDCalibrateResult.getJsqYDResult().setBVoltagePercentage(jsqFrame.getContent()[1]);
        jsqYDCalibrateResult.getJsqYDResult().setCVoltagePercentage(jsqFrame.getContent()[2]);
        jsqYDCalibrateResult.getJsqYDResult().setVoltageCondition(jsqFrame.getContent()[3]);
        jsqYDCalibrateResult.getJsqYDResult().setCalibrationValue(jsqFrame.getContent()[4]);
        jsqYDCalibrateResult.getJsqYDResult().setDeviceType(jsqFrame.getContent()[5]);
        jsqYDCalibrateResult.setCalibratedResult(jsqFrame.getContent()[6]);
        return jsqYDCalibrateResult;
    }

    @Override
    public void onTimeOut() {
        super.onTimeOut();
        jsqOperation.timeOut();
    }
}