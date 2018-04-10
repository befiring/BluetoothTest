package cn.com.ut.protocol.est;

import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import cn.com.ut.protocol.BoolResult;
import cn.com.ut.protocol.DataConvert;
import cn.com.ut.protocol.DataWriteUtils;
import cn.com.ut.protocol.MD5ComputeWrapper;
import cn.com.ut.protocol.PreferencesUtil;
import cn.com.ut.protocol.core.Frame;
import cn.com.ut.protocol.core.ProtocolBase;
import cn.com.ut.protocol.entity.EstDeleteTicket;
import cn.com.ut.protocol.entity.EstJsqDownloadResultsReport;
import cn.com.ut.protocol.entity.EstJsqDownloaded;
import cn.com.ut.protocol.entity.EstJsqProgramData;
import cn.com.ut.protocol.entity.EstJsqVersion;
import cn.com.ut.protocol.entity.EstKeyDataInfo;
import cn.com.ut.protocol.entity.EstNoNeedLearn;
import cn.com.ut.protocol.entity.EstRecallReturnData;
import cn.com.ut.protocol.entity.EstSelfCheckedCode;
import cn.com.ut.protocol.entity.EstSelfStudyAuthorize;
import cn.com.ut.protocol.entity.EstTaskTicket;
import cn.com.ut.protocol.service.IEstOperationService;
import cn.com.ut.protocol.service.IMultiFramesSendListener;
import cn.com.ut.protocol.service.IMultiReceiveInfoService;

import static cn.com.ut.protocol.DataConvert.intToBytes;

/**
 * IKeyE规约
 * Created by zhangyihuang on 2017/3/29.
 */
public class ProtocolEst extends ProtocolBase {

    /**
     * 发送超时
     */
    private int sendTimeOut = 1500;

    /**
     * 当前发送的帧
     */
    private EstFrame curSendFrame;

    /**
     * 当前发送的多帧处理
     */
    private MultiFramesSend curSendMultiFrame;

    /**
     * 多帧发送信息通知处理接口
     */
    private IMultiFramesSendListener mMultiFramesSend;

    /**
     * 当前发送钥匙自学数据md5校验码
     */
    private byte[] curMd5Buf;

    /**
     * 当前接收的多帧
     */
    private MultiFramesReceive curReceiveMultiFrame;

    /**
     * 多帧接收信息通知处理接口
     */
    private IMultiReceiveInfoService mMultiReceive;

    /**
     * 追忆回传数据
     */
    private EstRecallReturnData recallReturnData;

    /**
     * 延时3秒判断是否收到追忆回传数据
     */
    private boolean isReturnData = false;

    /**
     * 总帧数
     */
    private int totalFrames = -1;

    /**
     * 设备ID，或钥匙ID
     */
    public byte[] deviceId = {-1, -1, -1, -1, -1, -1, -1, -1};
    public byte[] retain = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    private final String TAG = ProtocolEst.class.getName();
    private IEstOperationService estOperation;
    private ExecutorService executorService;

    private static final String jsqDownloadDataInfo = "发送解锁器应用程序下载数据...";

    /**
     * 构造函数
     *
     * @param estOperation
     */
    public ProtocolEst(IEstOperationService estOperation) {
        this.protocolName = "IKeyE-1规约";
        this.estOperation = estOperation;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * @return
     */
    @Override
    protected Frame getFrame() {
        return new EstFrame();
    }

    /**
     * @param frame 收到的校验出错帧
     */
    @Override
    protected void crcError(Frame frame) {

    }

    /**
     * @param value
     */
    @Override
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
     * 处理接收到的数据帧
     *
     * @param frame
     */
    @Override
    protected void processReceiveFrame(Frame frame) {
        final EstFrame estFrame = (EstFrame) frame;
        if (null != estFrame) {
            int cmdCode = estFrame.getCmdCode();
            if (cmdCode < 0) {
                cmdCode += 256;
            }
            switch (estFrame.getCmdLink()) {
                case FunctionCode.ACK:
                    if (curSendFrame != null && (!curSendFrame.getDied()) && (curSendFrame.getCmdCode() == cmdCode)) {
                        if (cmdCode == 0x01 || cmdCode == 0x02) {
                            byte[] content = new byte[4];  //包数据长度
                            System.arraycopy(estFrame.getContent(), 6, content, 0, 4);
                            curSendFrame.setResult(content);
                            long frameNum = DataConvert.bytesToLong(content, 0, 4) + 1;
                            curSendFrame.sendSuccess(String.valueOf(frameNum));
                        } else {
                            curSendFrame.setResult(estFrame.getContent());
                            curSendFrame.sendSuccess("");
                        }
                    }
                    break;
                case FunctionCode.NAK:
                    curSendFrame.sendFailure("");
                    break;
                case FunctionCode.LINK:
                case FunctionCode.LINK1:
                    break;
                case FunctionCode.DNDATA:

                    break;
                case FunctionCode.UPINFO:

                    break;
                case FunctionCode.DNDATAINFO:
                    break;
            }
            switch (cmdCode) {
                case CommandCode.KEYSELFSTUDY:
                    break;
                case CommandCode.DOWNLOADSINGLEFRAMEDATA:
                    break;
                case CommandCode.UPLOADSINGLEFRAMEDATA:
                    sendAckReplyFrame((byte) CommandCode.UPLOADSINGLEFRAMEDATA);//回应ACK
                    byte[] content = new byte[2];  //包数据长度
                    System.arraycopy(estFrame.getContent(), 0, content, 0, 2);
                    int dataEntityType = DataConvert.bytesToInt16(content, 0);
                    switch (dataEntityType) {
                        case DataEntityType.SELFCHECKCODE:
                            EstSelfCheckedCode estSelfCheckedCode = setSelfCheckedCode(estFrame.getContent());//收到上传自学数据校验码md5码
                            estimateSelfStudy(estSelfCheckedCode);//判断是否需要自学
                            break;
                        case DataEntityType.KEYSNOLEARN:
                            break;
                        case DataEntityType.SENDSETKEY:
                            break;
                        case DataEntityType.UPLOADRFID:
                            receiveRfid(estFrame);
                            break;
                        case DataEntityType.GETJSQVERSION:

                            break;
                        case DataEntityType.JSQVERSIONREPORT:
                            asyncOnGetJsqVersion(estFrame.getContent());
                            break;
                        case DataEntityType.JSQSOFTWAREDOWNLOADSTART:
                            break;
                        case DataEntityType.JSQSOFTWAREDOWNLOADEND:

                            break;
                        case DataEntityType.JSQSOFTWAREDOWNLOADRESULTREPORT:
                            asyncOnUpgradeResultsReport(estFrame.getContent());
                            break;
                    }
                    break;
                case CommandCode.KEYRECALLDATA:
                    //电脑钥匙追忆回传数据
                    sendAckReplyFrame((byte) CommandCode.KEYRECALLDATA);//回应ACK
                    isReturnData = true;
                    handleInfo(estFrame);
                    break;
            }
        }
    }

    /**
     * 创建自学数据，并保存MD5码
     *
     * @param keyDataInfo
     */
    public void createStudyDataAndSaveMD5(EstKeyDataInfo keyDataInfo) {
        createKeyDataFrame(keyDataInfo);
    }

    /**
     * 发送电脑钥匙自学数据
     *
     * @param keyDataInfo
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public Future<BoolResult> sendSelfStudyDataInfo(final EstKeyDataInfo keyDataInfo, IMultiFramesSendListener multiFramesSend) throws InterruptedException, ExecutionException {
        this.mMultiFramesSend = multiFramesSend;
        byte[] content = getKeyDataInfoVersion();
        byte cmdCode = 0x03;
        Future<BoolResult> resultFuture = sendLinkFrame(cmdCode, content);
        BoolResult boolResult = (BoolResult) resultFuture.get();
        if (boolResult.getResult()) {//握手成功
            return sendKeyDataInfo(keyDataInfo);//发送钥匙自学数据
        }
        return null;
    }

    /**
     * 发送操作票数据信息
     *
     * @param taskParam
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public Future<BoolResult> sendEstTaskTicketData(final EstTaskTicket taskParam) throws InterruptedException, ExecutionException {
        byte[] content = getKeyTaskTicketVersion();
        byte cmdCode = 0x03;
        Future<BoolResult> resultFuture = sendLinkFrame(cmdCode, content);
        BoolResult boolResult = (BoolResult) resultFuture.get();
        if (boolResult.getResult()) { //握手成功
            return sendEstTaskTicket(taskParam);//发送操作票数据
        }
        return null;
    }

    /**
     * 请求追忆回传数据
     */
    public void requestRecallReturnData(IMultiReceiveInfoService multiReceive) {
        byte cmdCode = 0x00;
        this.isReturnData = false;
        this.mMultiReceive = multiReceive;
        curReceiveMultiFrame = new MultiFramesReceive(this, mMultiReceive);
        sendLinkFrame(cmdCode, getKeyTaskTicketVersion());

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!isReturnData) {
                    mMultiReceive.onReturnNull();
                }
            }
        }, 5000);
    }

    /**
     * 处理多帧数据
     *
     * @param multiFrame
     */
    public void processReceiveMultiFrame(MultiFramesReceive multiFrame) {
        if (multiFrame != null && multiFrame.appCommand == CommandCode.KEYRECALLDATA) {
            recallReturnData = new EstRecallReturnData();
            recallReturnData.partFlag = multiFrame.partFlag;
            recallReturnData.frameLength = multiFrame.curSectionTotalFrames;
            recallReturnData.totalFrames = multiFrame.totalFrames;

            byte[] selfStudyVersion = new byte[8];
            byte[] buf = multiFrame.mData.toByteArray();
            System.arraycopy(buf, 5, selfStudyVersion, 0, 8);
            recallReturnData.selfStudyVersion = selfStudyVersion;//自学版本号（8字节，先高后低）

            byte[] operatedItems = new byte[2];
            System.arraycopy(buf, 31, operatedItems, 0, 2);
            recallReturnData.operateItems = (short) DataConvert.bytesToInt16(operatedItems, 0);//电脑钥匙已操作项数（2字节先高后低）

            byte[] operatedSteps = new byte[2];
            System.arraycopy(buf, 33, operatedSteps, 0, 2);
            recallReturnData.operateSteps = (short) DataConvert.bytesToInt16(operatedSteps, 0);//电脑钥匙已操作步数（2字节先高后低）
            recallReturnData.recallVersion = buf[35];//追忆版本号(1个字节)

            if (buf.length >= 292) {//信息帧36+数据帧一帧256
                int framesNum = 36;
                int length = buf.length - framesNum;
                byte[] content = new byte[length];
                System.arraycopy(buf, framesNum, content, 0, length);
                EstRecallReturnData.RecallDataFrame recallDataFrame = null;

                int index = 0;
                while (index < content.length) {
                    int dataLength = 33;
                    byte[] dataFormat = new byte[dataLength];
                    if (index + dataLength > content.length) {
                        break;//补0不足33字节
                    }
                    System.arraycopy(content, index, dataFormat, 0, dataLength);
                    long rfidCode = DataConvert.bytesToLong(dataFormat, 0, 6);
                    if (rfidCode == 0) {
                        break;
                    }
                    recallDataFrame = recallReturnData.new RecallDataFrame();
                    recallDataFrame.rfidCode = rfidCode;//设备RFID码(6字节)
                    recallDataFrame.deviceId = (short) DataConvert.bytesToInt16(dataFormat, 6);//设备ID（2字节
                    recallDataFrame.operateResults = dataFormat[8];//操作结果(1字节)
                    byte[] operatedTime = new byte[6];
                    System.arraycopy(dataFormat, 9, operatedTime, 0, 6);
                    recallDataFrame.operateTime = getOperatedTime(operatedTime);//操作时间(6字节)
                    byte[] operatedName = new byte[12];
                    System.arraycopy(dataFormat, 15, operatedName, 0, 12);
                    try {
                        recallDataFrame.operatorName = new String(operatedName, "gb2312").trim();//操作人姓名(12字节)
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                    recallDataFrame.operatorId = (short) DataConvert.bytesToInt16(dataFormat, 27);//操作人员ID(2字节)
                    recallDataFrame.ticketNum = DataConvert.bytesToInt(dataFormat, 29);//操作票序号（4字节）
                    recallReturnData.recallDataFrames.add(recallDataFrame);
                    index += dataLength;
                }

                mMultiReceive.onReturnData(recallReturnData);
            }
        }
    }

    /**
     * 发送取消登陆命令(取消授权)
     *
     * @return
     */
    public Future<BoolResult> sendCancelLogin() {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                EstFrame frame = new EstFrame();
                frame.setCmdCode((byte) 0x6B);
                frame.setCmdLink((byte) 0x02);
                frame.setContent(new byte[9]);
                frame.setCmdName("取消登陆命令");
                System.arraycopy(getCancelLoginContent(), 0, frame.getContent(), 0, 9);
                curSendFrame = frame;
                final ReentrantLock sendLock = frame.getSendLock();
                sendLock.lockInterruptibly();
                try {
                    enqueueSendData(frame);
                    frame.await(sendTimeOut, TimeUnit.SECONDS);
                } finally {
                    sendLock.unlock();
                }
                return frame.getSendResult();
            }
        });
    }

    /**
     * 发送APP授权电脑钥匙命令
     *
     * @return
     */
    public Future<BoolResult> sendSelfStudyAuthorize(final EstSelfStudyAuthorize studyAuthorize) {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                EstFrame frame = new EstFrame();
                frame.setCmdCode((byte) 0x6B);
                frame.setCmdLink((byte) 0x02);
                frame.setContent(new byte[15]);
                frame.setCmdName("APP授权电脑钥匙命令");
                System.arraycopy(getAuthorizeContent(studyAuthorize), 0, frame.getContent(), 0, 15);
                curSendFrame = frame;
                final ReentrantLock sendLock = frame.getSendLock();
                sendLock.lockInterruptibly();
                try {
                    enqueueSendData(frame);
                    frame.await(sendTimeOut, TimeUnit.SECONDS);
                } finally {
                    sendLock.unlock();
                }
                return frame.getSendResult();
            }
        });
    }

    /**
     * 发送获取解锁器软件版本
     *
     * @return
     */
    public Future<BoolResult> sendGetJsqVersion() {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                EstFrame frame = new EstFrame();
                frame.setCmdCode((byte) 0x6B);
                frame.setCmdLink((byte) 0x02);
                frame.setContent(new byte[3]);
                frame.setCmdName("获取解锁器软件版本命令");
                System.arraycopy(getJsqVersionContent(), 0, frame.getContent(), 0, 3);
                curSendFrame = frame;
                final ReentrantLock sendLock = frame.getSendLock();
                sendLock.lockInterruptibly();
                try {
                    enqueueSendData(frame);
                    frame.await(sendTimeOut, TimeUnit.SECONDS);
                } finally {
                    sendLock.unlock();
                }
                return frame.getSendResult();
            }
        });
    }

    /**
     * 发送删除操作票命令
     *
     * @return
     */
    public Future<BoolResult> sendDeleteEstTaskTicket() {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                EstFrame frame = new EstFrame();
                frame.setCmdCode((byte) 0x6B);
                frame.setCmdLink((byte) 0x02);
                frame.setContent(new byte[7]);
                frame.setCmdName("删除操作票命令");
                System.arraycopy(getDeleteTicketContent(), 0, frame.getContent(), 0, 7);
                curSendFrame = frame;
                final ReentrantLock sendLock = frame.getSendLock();
                sendLock.lockInterruptibly();
                try {
                    enqueueSendData(frame);
                    frame.await(sendTimeOut, TimeUnit.SECONDS);
                } finally {
                    sendLock.unlock();
                }
                return frame.getSendResult();
            }
        });
    }

    /**
     * 发送解锁器升级命令
     *
     * @param jsqProgramData
     * @return
     */
    public Future<BoolResult> sendUpdateFileToJSQ(final EstJsqProgramData jsqProgramData, IMultiFramesSendListener multiFramesSend) throws InterruptedException, ExecutionException {
        this.mMultiFramesSend = multiFramesSend;
        totalFrames = jsqProgramData.jsqDownloadData.totalFrames;
        curSendFrame = createJsqDownloadStart(jsqProgramData);//应用程序下载开始
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                curSendFrame.setCmdName("解锁器应用程序下载开始命令");
                BoolResult br = sendJsqProgramData(jsqProgramData);//应用程序下载数据
                if (br.getResult()) {
                    return sendJsqDownloaded().get();
                }
                return null;
            }
        });
    }

    /**
     * 发送握手命令
     *
     * @return Future<BoolResult>
     */
    private Future<BoolResult> sendLinkFrame(final byte cmdCode, final byte[] content) {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                EstFrame frame = new EstFrame();
                frame.setCmdCode(cmdCode);
                frame.setCmdLink((byte) 0x11);
                frame.setContent(new byte[8]);
                frame.setCmdName("握手命令");
                System.arraycopy(content, 0, frame.getContent(), 0, 8);
                curSendFrame = frame;
                final ReentrantLock sendLock = frame.getSendLock();
                sendLock.lockInterruptibly();
                try {
                    enqueueSendData(frame);
                    frame.await(sendTimeOut, TimeUnit.SECONDS);
                } finally {
                    sendLock.unlock();
                }
                return frame.getSendResult();
            }
        });
    }

    /**
     * 回ACK
     *
     * @param cmdCode 应用层命令字,包括LINK的特性字
     */
    private Future<BoolResult> sendAckReplyFrame(byte cmdCode) {
        byte cmdLink = (byte) 0x7F;
        return sendReplyFrame(cmdCode, cmdLink);
    }

    /**
     * 回NAK
     *
     * @param cmdCode
     */
    private Future<BoolResult> sendNakReplyFrame(byte cmdCode) {
        byte cmdLink = 0x00;
        return sendReplyFrame(cmdCode, cmdLink);
    }

    /**
     * 发送应答命令
     *
     * @param cmdCode
     * @param cmdLink
     */
    private Future<BoolResult> sendReplyFrame(final byte cmdCode, final byte cmdLink) {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                EstFrame frame = new EstFrame();
                frame.setCmdCode(cmdCode);
                frame.setCmdLink(cmdLink);
                frame.setContent(new byte[8]);
                frame.setCmdName("应答命令");
                System.arraycopy(retain, 0, frame.getContent(), 0, 8);
                curSendFrame = frame;
                final ReentrantLock sendLock = frame.getSendLock();
                sendLock.lockInterruptibly();
                try {
                    enqueueSendData(frame);
                    frame.await(sendTimeOut, TimeUnit.SECONDS);
                } finally {
                    sendLock.unlock();
                }
                return frame.getSendResult();
            }
        });
    }

    /**
     * 发送不需要自学命令
     *
     * @return
     */
    private Future<BoolResult> sendNoNeedSelfStudy() {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                EstFrame frame = new EstFrame();
                frame.setCmdCode((byte) 0x6B);
                frame.setCmdLink((byte) 0x02);
                frame.setContent(new byte[11]);
                frame.setCmdName("不需要自学命令");
                System.arraycopy(getNoNeedLearnContent(), 0, frame.getContent(), 0, 11);
                curSendFrame = frame;
                final ReentrantLock sendLock = frame.getSendLock();
                sendLock.lockInterruptibly();
                try {
                    enqueueSendData(frame);
                    frame.await(sendTimeOut, TimeUnit.SECONDS);
                } finally {
                    sendLock.unlock();
                }
                return frame.getSendResult();
            }
        });
    }

    /**
     * 构建解锁器应用程序下载开始命令帧
     *
     * @param jsqProgramData
     * @return
     */
    private EstFrame createJsqDownloadStart(EstJsqProgramData jsqProgramData) {
        EstFrame frame = new EstFrame();
        frame.setCmdCode((byte) 0x6B);
        frame.setCmdLink((byte) 0x02);
        frame.setContent(new byte[20]);
        System.arraycopy(getJsqDownloadStartContent(jsqProgramData), 0, frame.getContent(), 0, 20);
        return frame;
    }

    /**
     * 解锁器应用程序下载开始数据实体内容
     *
     * @return
     */
    private byte[] getJsqDownloadStartContent(EstJsqProgramData jsqProgramData) {
        byte[] content = new byte[20];
        System.arraycopy(jsqProgramData.jsqDownloadStart.dataEntityType, 0, content, 0, 2);
        content[2] = jsqProgramData.jsqDownloadStart.dataContentLength;
        byte[] fileSize = DataConvert.intToBytes(jsqProgramData.jsqDownloadStart.fileSize);
        System.arraycopy(fileSize, 0, content, 3, 4);
        byte[] checkSum = DataConvert.intToBytes(jsqProgramData.jsqDownloadStart.checkSum);
        System.arraycopy(checkSum, 0, content, 7, 4);
        System.arraycopy(jsqProgramData.jsqDownloadStart.calibrationTime, 0, content, 11, 8);
        content[19] = jsqProgramData.jsqDownloadStart.downloadDataType;
        return content;
    }

    /**
     * 发送解锁器应用程序下载数据
     *
     * @param jsqProgramData
     * @return
     * @throws InterruptedException
     */
    private BoolResult sendJsqProgramData(final EstJsqProgramData jsqProgramData) throws InterruptedException {
        curSendFrame.setDied(false); //要连续发和收，所以Died要重置
        BoolResult br = sendThree(curSendFrame, sendTimeOut);
        if (br.getResult()) {
            //发送成功则会收到下一帧的帧号
            if (br.getMsg() != null) {
                curSendFrame.setCmdCode((byte) 0x6B);
                curSendFrame.setCmdLink((byte) 0x02);
                int totalFrames = jsqProgramData.jsqDownloadData.totalFrames;
                curSendFrame.setTotalFrames(totalFrames);
                for (int index = 1; index <= totalFrames; index++) {
                    byte[] data = jsqProgramData.jsqDownloadData.getData(index);  //获取下一发送帧
                    curSendFrame.setContent(new byte[data.length]);
                    System.arraycopy(data, 0, curSendFrame.getContent(), 0, data.length);
                    curSendFrame.setDied(false); //要连续发和收，所以Died要重置
                    asyncSetSendProgressEvent(index, totalFrames, jsqDownloadDataInfo);

                    curSendFrame.setFrameNum(index);
                    BoolResult boolResult = sendThree(curSendFrame, sendTimeOut);
                    if (!boolResult.getResult()) {
                        return boolResult;
                    }
                }
            }
        }
        return br;
    }

    /**
     * 发送解锁器应用程序下载完毕
     *
     * @return
     */
    private Future<BoolResult> sendJsqDownloaded() {
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                EstFrame frame = new EstFrame();
                frame.setCmdCode((byte) 0x6B);
                frame.setCmdLink((byte) 0x02);
                frame.setContent(new byte[4]);
                frame.setCmdName("解锁器应用程序下载完毕命令");
                System.arraycopy(getJsqDownloadedContent(), 0, frame.getContent(), 0, 4);
                curSendFrame = frame;
                final ReentrantLock sendLock = frame.getSendLock();
                sendLock.lockInterruptibly();
                try {
                    enqueueSendData(frame);
                    frame.await(sendTimeOut, TimeUnit.SECONDS);
                } finally {
                    sendLock.unlock();
                }
                return frame.getSendResult();
            }
        });
    }

    /**
     * 发送钥匙自学数据
     *
     * @param keyDataInfo
     */
    private Future<BoolResult> sendKeyDataInfo(EstKeyDataInfo keyDataInfo) {
        createKeyDataFrame(keyDataInfo);//先构建数据帧，再构建信息帧，这样才能知道帧数
        curSendFrame = createKeyInfoFrame(keyDataInfo);
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                asyncSetSendInfoEvent();
                curSendFrame.setCmdName("自学数据信息帧命令");

                byte cmdCode = (byte) 0x01;
                String tip = "自学数据帧报文";
                BoolResult br = returnCallback(cmdCode, tip);
                return br;
            }
        });
    }

    /**
     * 构建自学数据帧
     *
     * @param keyDataInfo
     */
    private void createKeyDataFrame(EstKeyDataInfo keyDataInfo) {
        try {
            curSendMultiFrame = new MultiFramesSend(this);
            curSendMultiFrame.frameLength = curSendMultiFrame.getFrameLength();
            //生成结果字节流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //站名描述区
            for (String stationName : keyDataInfo.stationNameList) {
                DataWriteUtils.writeGb2312String(outputStream, stationName, true);
            }
            //设备汉字编码表
            keyDataInfo.deviceAddr = (short) (outputStream.size());//设备汉字编码表起始地址 - 1
            for (String deviceCode : keyDataInfo.deviceCodeTable) {
                DataWriteUtils.writeGb2312String(outputStream, deviceCode, true);
            }
            //锁码区
            keyDataInfo.rfidAddr = outputStream.size();//锁码区起始地址
            for (EstKeyDataInfo.LockArea lockArea : keyDataInfo.lockAreas) {
                DataWriteUtils.writeBytes(outputStream, lockArea.rfidCode);
                DataWriteUtils.writeShort(outputStream, lockArea.matrix);
                DataWriteUtils.writeByte(outputStream, lockArea.rfidProperty);
            }
            //设备所属站号表
            keyDataInfo.unlockAddr = outputStream.size();//智能解锁关联表区起始地址
            keyDataInfo.belongAddr = outputStream.size();//设备所属站号表起始地址
            for (Byte stationNo : keyDataInfo.stationNoList) {
                DataWriteUtils.writeByte(outputStream, stationNo.byteValue());
            }
            //偏移地址索引表
            keyDataInfo.pyAddr = outputStream.size();//偏移地址索引表起始地址 高位 4字节
            DataWriteUtils.writeInt(outputStream, keyDataInfo.offsetAddrTable.deviceStatusTableAddr);
            DataWriteUtils.writeInt(outputStream, keyDataInfo.offsetAddrTable.lockTableTourAddr);
            DataWriteUtils.writeInt(outputStream, keyDataInfo.offsetAddrTable.selfTaughtDataVersionAddr);
            DataWriteUtils.writeInt(outputStream, keyDataInfo.offsetAddrTable.repairLockTableAddr);
            keyDataInfo.offsetAddrTable.landingPersonInfoDescTableAddr = outputStream.size() + 28;//登陆人员信息描述表起始地址（先高后低）
            DataWriteUtils.writeInt(outputStream, keyDataInfo.offsetAddrTable.landingPersonInfoDescTableAddr);

            //登陆人员信息描述表
            ByteArrayOutputStream personInfoMemo = new ByteArrayOutputStream();
            if (keyDataInfo.personInfoDesTable != null) {
                DataWriteUtils.writeShort(personInfoMemo, keyDataInfo.personInfoDesTable.personInfoCount);
                for (EstKeyDataInfo.PersonInfoDesTable.PersonInfoDes personInfoDes : keyDataInfo.personInfoDesTable.personInfoDesList) {
                    DataWriteUtils.writeString(personInfoMemo, String.valueOf(personInfoDes.teamId), false);//班组ID 4字节
                    DataWriteUtils.writeString(personInfoMemo, ",", false);//分隔符
                    DataWriteUtils.writeGb2312String(personInfoMemo, personInfoDes.teamName, false);//班组名 16
                    DataWriteUtils.writeString(personInfoMemo, ",", false);//分隔符
                    DataWriteUtils.writeGb2312String(personInfoMemo, personInfoDes.name, false);//姓名 12
                    DataWriteUtils.writeString(personInfoMemo, ",", false);//分隔符
                    DataWriteUtils.writeString(personInfoMemo, personInfoDes.password, false);//密码 10
                    DataWriteUtils.writeString(personInfoMemo, ",", false);//分隔符
                    //DataWriteUtils.writeString(personInfoMemo, personInfoDes.rfidCode, false);//RFID码 12
                    DataWriteUtils.writeString(personInfoMemo, ",", false);//分隔符
                    DataWriteUtils.writeString(personInfoMemo, personInfoDes.power, false);//权限 4
                    DataWriteUtils.writeString(personInfoMemo, ",", false);//分隔符
                    DataWriteUtils.writeString(personInfoMemo, personInfoDes.validStartTime, false);//权限有效开始时间 12
                    DataWriteUtils.writeString(personInfoMemo, ",", false);//分隔符
                    DataWriteUtils.writeString(personInfoMemo, personInfoDes.validEndTime, false);//权限有效结束时间 12
                    DataWriteUtils.writeString(personInfoMemo, ",", false);//分隔符
                    DataWriteUtils.writeString(personInfoMemo, String.valueOf(personInfoDes.operatorId), false);//操作人ID 4
                    DataWriteUtils.writeByte(personInfoMemo, (byte) 0x0D);
                    DataWriteUtils.writeByte(personInfoMemo, (byte) 0x0A);
                }
            }

            //人员与授权设备对应表
            keyDataInfo.offsetAddrTable.personAuthorizedDevicesTableAddr = outputStream.size() + personInfoMemo.size() + 24;//人员与授权设备对应表起始地址（先高后低）
            DataWriteUtils.writeInt(outputStream, keyDataInfo.offsetAddrTable.personAuthorizedDevicesTableAddr);
            ByteArrayOutputStream personDevAuthorMemo = new ByteArrayOutputStream();
            if (keyDataInfo.personDevAuthorTable != null) {
                DataWriteUtils.writeShort(personDevAuthorMemo, keyDataInfo.personDevAuthorTable.authorUserCount);//授权人员个数 2字节
                DataWriteUtils.writeShort(personDevAuthorMemo, keyDataInfo.personDevAuthorTable.deviceCount);//设备个数 2字节
                for (EstKeyDataInfo.PersonDevAuthorTable.PersonDevAuthor personDevAuthor : keyDataInfo.personDevAuthorTable.personDevAuthorList) {
                    DataWriteUtils.writeShort(personDevAuthorMemo, personDevAuthor.deviceId);//2字节设备编号
                    DataWriteUtils.writeBytes(personDevAuthorMemo, personDevAuthor.authorMatrixCode);//n字节的授权人员矩阵码
                }
            }

            //操作属性关联表
            keyDataInfo.offsetAddrTable.operatePropertyAssociationTableAddr = outputStream.size() + personInfoMemo.size() + personDevAuthorMemo.size() + 20;//操作属性关联表起始地址（先高后低）
            DataWriteUtils.writeInt(outputStream, keyDataInfo.offsetAddrTable.operatePropertyAssociationTableAddr);
            ByteArrayOutputStream opProperMemo = new ByteArrayOutputStream();
            for (Byte operationProperty : keyDataInfo.operationPropertyTable) {
                DataWriteUtils.writeByte(opProperMemo, operationProperty.byteValue());
            }

            //自学数据MD5码
            keyDataInfo.offsetAddrTable.selfLearnMD5Addr = outputStream.size() + personInfoMemo.size() + personDevAuthorMemo.size() + opProperMemo.size() + 16;//自学数据MD5码起始地址（先高后低） md5码是16字节固定长度的
            DataWriteUtils.writeInt(outputStream, keyDataInfo.offsetAddrTable.selfLearnMD5Addr);
            //短信猫或短信平台号码
            keyDataInfo.offsetAddrTable.smsPlatformNumberAddr = keyDataInfo.offsetAddrTable.selfLearnMD5Addr + 16;//短信猫或短信平台号码起始地址（先高后低）
            DataWriteUtils.writeInt(outputStream, keyDataInfo.offsetAddrTable.smsPlatformNumberAddr);
            ByteArrayOutputStream smsPhoneMemo = new ByteArrayOutputStream();
            DataWriteUtils.writeGb2312String(smsPhoneMemo, keyDataInfo.smsPhone, true);

            //人员电话号码表
            keyDataInfo.offsetAddrTable.personPhoneNumberAddr = keyDataInfo.offsetAddrTable.smsPlatformNumberAddr + smsPhoneMemo.size();//人员电话号码表起始地址（先高后低）
            DataWriteUtils.writeInt(outputStream, keyDataInfo.offsetAddrTable.personPhoneNumberAddr);
            ByteArrayOutputStream userPhoneMemo = new ByteArrayOutputStream();
            if (keyDataInfo.userPhoneTable != null) {
                DataWriteUtils.writeShort(userPhoneMemo, keyDataInfo.userPhoneTable.userPhoneCount);
                for (EstKeyDataInfo.UserPhoneTable.PhoneExtend phoneExtend : keyDataInfo.userPhoneTable.phoneExtendList) {
                    String phoneExStr = phoneExtend.phone + phoneExtend.extendProperty;
                    DataWriteUtils.writeGb2312String(userPhoneMemo, phoneExStr, true);
                }
            }
            //强制验电闭锁前导项表
            keyDataInfo.offsetAddrTable.mandatoryVerificationLockAddr = keyDataInfo.offsetAddrTable.personPhoneNumberAddr + userPhoneMemo.size();//强制验电闭锁前导项表地址（先高后低）
            DataWriteUtils.writeInt(outputStream, keyDataInfo.offsetAddrTable.mandatoryVerificationLockAddr);
            ByteArrayOutputStream ydLeadPhoneMemo = new ByteArrayOutputStream();
            if (keyDataInfo.forcedLeadLockTable != null) {
                DataWriteUtils.writeShort(ydLeadPhoneMemo, keyDataInfo.forcedLeadLockTable.preambleCount);
                for (EstKeyDataInfo.ForcedLeadLockTable.PreambleLock preambleLock : keyDataInfo.forcedLeadLockTable.preambleLockList) {
                    //锁编码＋其前导项锁编码
                    DataWriteUtils.writeBytes(ydLeadPhoneMemo, preambleLock.um);
                    DataWriteUtils.writeBytes(ydLeadPhoneMemo, preambleLock.leadum);
                }
            }

            personInfoMemo.writeTo(outputStream);//将登陆人员信息描述表的内存流写入到自学内存流中
            personDevAuthorMemo.writeTo(outputStream);//将人员与授权设备对应表的内存流写入到自学内存流中
            opProperMemo.writeTo(outputStream);//将操作属性关联表的内存流写入自学内存流中

            ByteArrayOutputStream dataWithoutMd5 = new ByteArrayOutputStream();
            outputStream.writeTo(dataWithoutMd5);
            smsPhoneMemo.writeTo(dataWithoutMd5);
            userPhoneMemo.writeTo(dataWithoutMd5);
            ydLeadPhoneMemo.writeTo(dataWithoutMd5);

            byte[] md5Buf = MD5ComputeWrapper.computeMD5(dataWithoutMd5.toByteArray());//将MD5码写入自学数据
            int md5Len = 16; //md5长度
            byte[] newMd5Buf = new byte[md5Len];
            if (md5Buf.length < md5Len) {
                System.arraycopy(md5Buf, 0, newMd5Buf, 0, md5Buf.length);
            } else if (md5Buf.length >= md5Len) {
                System.arraycopy(md5Buf, 0, newMd5Buf, 0, md5Len);
            }
            DataWriteUtils.writeBytes(outputStream, newMd5Buf);
            keyDataInfo.md5Buf = newMd5Buf;
            saveMd5(newMd5Buf);

            smsPhoneMemo.writeTo(outputStream);
            userPhoneMemo.writeTo(outputStream);
            ydLeadPhoneMemo.writeTo(outputStream);
            personInfoMemo.close();
            personDevAuthorMemo.close();
            opProperMemo.close();
            smsPhoneMemo.close();
            userPhoneMemo.close();
            ydLeadPhoneMemo.close();
            dataWithoutMd5.close();

            //补齐帧
            while (outputStream.size() % curSendMultiFrame.frameLength != 0) {
                DataWriteUtils.writeByte(outputStream, (byte) 0x00);
            }
            //添加一帧附加帧,其值全为0
            for (int i = 0; i < curSendMultiFrame.frameLength; i++) {
                DataWriteUtils.writeByte(outputStream, (byte) 0x00);
            }

            curSendMultiFrame.totalFrames = outputStream.size() / curSendMultiFrame.frameLength;
            curSendMultiFrame.data = new byte[outputStream.size()];
            System.arraycopy(outputStream.toByteArray(), 0, curSendMultiFrame.data, 0, outputStream.size());
            outputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 构建自学信息帧
     *
     * @param keyDataInfo
     * @return
     */
    private EstFrame createKeyInfoFrame(EstKeyDataInfo keyDataInfo) {
        EstFrame frame = new EstFrame();
        frame.setCmdCode((byte) 0x01);//命令码 自学
        frame.setCmdLink((byte) 0x06);//功能码 信息帧
        frame.setContent(new byte[48]);//报文长度
        frame.getContent()[0] = keyDataInfo.partFlag; //段标志
        frame.getContent()[1] = (byte) ((curSendMultiFrame.totalFrames & 0xFF00) >> 8); //本段自学数据帧数 高位
        frame.getContent()[2] = ((byte) (curSendMultiFrame.totalFrames & 0x00FF));       //本段自学数据帧数 低位
        frame.getContent()[3] = (byte) ((curSendMultiFrame.totalFrames & 0xFF00) >> 8); //自学数据的总帧数 高位
        frame.getContent()[4] = ((byte) (curSendMultiFrame.totalFrames & 0x00FF));       //自学数据的总帧数 低位
        frame.getContent()[5] = keyDataInfo.compressAlgorithm; //自学数据压缩算法
        frame.getContent()[6] = keyDataInfo.compressPackageLength[0]; //自学数据压缩包长度 *
        frame.getContent()[7] = keyDataInfo.compressPackageLength[1];
        frame.getContent()[8] = keyDataInfo.compressPackageLength[2];

        byte[] version = intToBytes(keyDataInfo.initDataFormatVersion);
        frame.getContent()[9] = version[0]; //自学数据格式版本号 4字节
        frame.getContent()[10] = version[1];
        frame.getContent()[11] = version[2];
        frame.getContent()[12] = version[3];
        frame.getContent()[13] = (byte) ((keyDataInfo.adapterId & 0xFF00) >> 8);  //传出自学数据的设备ID 高位 ?
        frame.getContent()[14] = ((byte) (keyDataInfo.adapterId & 0x00FF));       //传出自学数据的设备ID 低位
        frame.getContent()[15] = keyDataInfo.deviceTypeDes;       //传出自学数据设备类型描述
        frame.getContent()[16] = keyDataInfo.stationCount;        //系统最大站号
        frame.getContent()[17] = (byte) ((keyDataInfo.maxDeviceId & 0xFF00) >> 8);        //系统最大设备号 高位 ?
        frame.getContent()[18] = ((byte) (keyDataInfo.maxDeviceId & 0x00FF));             //系统最大设备号 低位
        frame.getContent()[19] = (byte) ((keyDataInfo.rfidCount & 0xFF00) >> 8);        //锁编码个数 高位 ?
        frame.getContent()[20] = ((byte) (keyDataInfo.rfidCount & 0x00FF));             //锁编码个数 低位
        frame.getContent()[21] = (byte) ((keyDataInfo.deviceAddr & 0xFF00) >> 8);        //设备汉字编码表起始地址 高位 ?
        frame.getContent()[22] = ((byte) (keyDataInfo.deviceAddr & 0x00FF));             //设备汉字编码表起始地址 低位

        byte[] rfidAddr = intToBytes(keyDataInfo.rfidAddr);//锁码区起始地址
        frame.getContent()[23] = rfidAddr[0];         // 高位 4字节
        frame.getContent()[24] = rfidAddr[1];
        frame.getContent()[25] = rfidAddr[2];
        frame.getContent()[26] = rfidAddr[3];
        byte[] unlockAddr = intToBytes(keyDataInfo.unlockAddr);//智能解锁关联表区起始地址
        frame.getContent()[27] = unlockAddr[0];        // 高位 4字节
        frame.getContent()[28] = unlockAddr[1];
        frame.getContent()[29] = unlockAddr[2];
        frame.getContent()[30] = unlockAddr[3];
        byte[] belongAddr = intToBytes(keyDataInfo.belongAddr);//设备所属站号表起始地址
        frame.getContent()[31] = belongAddr[0];        // 高位 4字节
        frame.getContent()[32] = belongAddr[1];
        frame.getContent()[33] = belongAddr[2];
        frame.getContent()[34] = belongAddr[3];

        frame.getContent()[35] = keyDataInfo.rfidBytes;        //锁编码所占的字节个数
        byte[] personIdentityAddr = intToBytes(keyDataInfo.personIdentityAddr);//确认人员身份编码表起始地址
        frame.getContent()[36] = personIdentityAddr[0];        //高位 4字节
        frame.getContent()[37] = personIdentityAddr[1];
        frame.getContent()[38] = personIdentityAddr[2];
        frame.getContent()[39] = personIdentityAddr[3];
        byte[] networkAddr = intToBytes(keyDataInfo.networkAddr);//网络控制器地址描述表起始地址
        frame.getContent()[40] = networkAddr[0];        // 高位 4字节
        frame.getContent()[41] = networkAddr[1];
        frame.getContent()[42] = networkAddr[2];
        frame.getContent()[43] = networkAddr[3];
        byte[] pyAddr = intToBytes(keyDataInfo.pyAddr);//偏移地址索引表起始地址
        frame.getContent()[44] = pyAddr[0];        // 高位 4字节
        frame.getContent()[45] = pyAddr[1];
        frame.getContent()[46] = pyAddr[2];
        frame.getContent()[47] = pyAddr[3];
        return frame;
    }

    /**
     * 发送操作票数据
     *
     * @param taskParam
     */
    private Future<BoolResult> sendEstTaskTicket(EstTaskTicket taskParam) {
        createEstTicketDataFrame(taskParam);//先构建数据帧，再构建信息帧，这样才能知道帧数
        curSendFrame = createEstTicketInfoFrame(taskParam);
        return executorService.submit(new Callable<BoolResult>() {
            @Override
            public BoolResult call() throws Exception {
                curSendFrame.setCmdName("操作票数据信息帧命令");

                byte cmdCode = (byte) 0x02;
                String tip = "操作票数据帧报文";
                BoolResult br = returnCallback(cmdCode, tip);
                return br;
            }
        });
    }

    /**
     * 构建临时授权票数据帧
     *
     * @param taskParam
     */
    private void createEstTicketDataFrame(final EstTaskTicket taskParam) {
        try {
            curSendMultiFrame = new MultiFramesSend(this);
            curSendMultiFrame.frameLength = curSendMultiFrame.getFrameLength();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  //生成结果字节流
            ByteArrayOutputStream hintData = new ByteArrayOutputStream();
            int ulen = (int) hintData.size();
            if (taskParam.ticketList != null && taskParam.ticketList.size() > 0) {
                for (EstTaskTicket.Ticket ticket : taskParam.ticketList) {
                    //操作票格式
                    DataWriteUtils.writeShort(outputStream, ticket.ticketFormat.deviceId);//设备ID（2字节，先高后低）
                    DataWriteUtils.writeShort(outputStream, ticket.ticketFormat.descInfo);//本项描述信息（2字节，先高后低）
                    DataWriteUtils.writeShort(outputStream, ticket.ticketFormat.contentPointer);//显示内容指针（2字节，先高后低）

                    //显示内容
                    DataWriteUtils.writeGb2312String(hintData, ticket.contents.opHint, true);//操作描述
                    //DataWriteUtils.writeGb2312String(hintData, ticket.contents.passwordArea, true);//密码区
                    DataWriteUtils.writeByte(hintData, (byte) 0x00);//以0x00结束
                    //第三个显示项为V2.0及以后版本操作票的RFID锁码信息，以“00h”为结束符
                    if (!ticket.contents.eighteenLengthRfidCode.isEmpty()) {
                        DataWriteUtils.writeByte(hintData, (byte) 0x01);//分段个数（1字节）
                        DataWriteUtils.writeByte(hintData, (byte) 0x07);//分段1起始符(1字节，0x07)
                        DataWriteUtils.writeByte(hintData, (byte) 0x01);//分段长度（锁码的个数）
                        DataWriteUtils.writeString(hintData, ticket.contents.eighteenLengthRfidCode, false);//18位锁码值 ASCII编码
                        DataWriteUtils.writeByte(hintData, (byte) 0x17);//分段1终止符
                    }
                    DataWriteUtils.writeByte(hintData, (byte) 0x00);//以0x00结束
                }
                ulen = (int) hintData.size() + taskParam.ticketList.size() * 6 + 6;
            }
            taskParam.authPersonTableAddr = ulen;//临时授权人员表起始地址

            //临时授权人员表
            DataWriteUtils.writeShort(hintData, taskParam.authPersonTable.personInfoCount);
            for (EstTaskTicket.AuthPersonTable.PersonInfoDes personInfoDes : taskParam.authPersonTable.personInfoDesList) {
                DataWriteUtils.writeString(hintData, String.valueOf(personInfoDes.teamId), false);//班组ID 4字节
                DataWriteUtils.writeString(hintData, ",", false);//分隔符
                DataWriteUtils.writeGb2312String(hintData, personInfoDes.teamName, false);//班组名 16
                DataWriteUtils.writeString(hintData, ",", false);//分隔符
                DataWriteUtils.writeGb2312String(hintData, personInfoDes.name, false);//姓名 12
                DataWriteUtils.writeString(hintData, ",", false);//分隔符
                DataWriteUtils.writeString(hintData, personInfoDes.password, false);//密码 10
                DataWriteUtils.writeString(hintData, ",", false);//分隔符
                //DataWriteUtils.writeString(hintData, personInfoDes.rfidCode, false);//RFID码 12
                DataWriteUtils.writeString(hintData, ",", false);//分隔符
                DataWriteUtils.writeString(hintData, personInfoDes.power, false);//权限 4
                DataWriteUtils.writeString(hintData, ",", false);//分隔符
                DataWriteUtils.writeString(hintData, personInfoDes.validStartTime, false);//权限有效开始时间 12
                DataWriteUtils.writeString(hintData, ",", false);//分隔符
                DataWriteUtils.writeString(hintData, personInfoDes.validEndTime, false);//权限有效结束时间 12
                DataWriteUtils.writeString(hintData, ",", false);//分隔符
                DataWriteUtils.writeString(hintData, String.valueOf(personInfoDes.operatorId), false);//操作人ID 4
                DataWriteUtils.writeByte(hintData, (byte) 0x0D);
                DataWriteUtils.writeByte(hintData, (byte) 0x0A);
            }

            if (taskParam.ticketList.size() > 0) {
                DataWriteUtils.writeByte(outputStream, (byte) 0xFF);//操作票结束标志
                DataWriteUtils.writeByte(outputStream, (byte) 0x06);
                DataWriteUtils.writeByte(outputStream, (byte) 0x00);
                DataWriteUtils.writeByte(outputStream, (byte) 0x00);
                DataWriteUtils.writeByte(outputStream, (byte) 0x00);
                DataWriteUtils.writeByte(outputStream, (byte) 0x00);

                //记录操作项数的长度，包含结束标志
                taskParam.eTicketLength = (short) outputStream.size();
                //将提示信息追加到操作票的后面
                hintData.writeTo(outputStream);
                hintData.close();

                //补齐帧
                while (outputStream.size() % curSendMultiFrame.frameLength != 0) {
                    DataWriteUtils.writeByte(outputStream, (byte) 0x00);
                }

                //添加一帧附加帧,其值全为0
                for (int i = 0; i < curSendMultiFrame.frameLength; i++) {
                    DataWriteUtils.writeByte(outputStream, (byte) 0x00);
                }
                //本张操作票帧数(减掉最后一垃圾帧) 2字节
                taskParam.eTicketFrames = (short) (calculateFrameCount((int) outputStream.size(), curSendMultiFrame.frameLength) - 1);
            }

            curSendMultiFrame.totalFrames = outputStream.size() / curSendMultiFrame.frameLength;
            curSendMultiFrame.data = new byte[outputStream.size()];
            System.arraycopy(outputStream.toByteArray(), 0, curSendMultiFrame.data, 0, outputStream.size());
            outputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 构建临时授权票信息帧
     *
     * @param taskParam
     */
    private EstFrame createEstTicketInfoFrame(final EstTaskTicket taskParam) {
        EstFrame frame = new EstFrame();
        frame.setCmdCode((byte) 0x02);//命令码 临时授权票
        frame.setCmdLink((byte) 0x06);//功能码 信息帧
        frame.setContent(new byte[47]);//报文长度
        frame.getContent()[0] = taskParam.partFlag; //段标志
        frame.getContent()[1] = (byte) ((curSendMultiFrame.totalFrames & 0xFF00) >> 8); //本段自学数据帧数 高位
        frame.getContent()[2] = ((byte) (curSendMultiFrame.totalFrames & 0x00FF));       //本段自学数据帧数 低位
        frame.getContent()[3] = (byte) ((curSendMultiFrame.totalFrames & 0xFF00) >> 8); //自学数据的总帧数 高位
        frame.getContent()[4] = ((byte) (curSendMultiFrame.totalFrames & 0x00FF));       //自学数据的总帧数 低位
        frame.getContent()[5] = (byte) ((taskParam.makeTicketDeviceId & 0xFF00) >> 8);  //开票设备ID 高位
        frame.getContent()[6] = ((byte) (taskParam.makeTicketDeviceId & 0x00FF));       //开票设备ID 低位
        frame.getContent()[7] = taskParam.makeTicketDeviceType;//开票设备类型描述
        frame.getContent()[8] = taskParam.eTicketTaskId;//操作票任务号
        byte[] eTicketSerial = intToBytes(taskParam.eTicketSerial);
        frame.getContent()[9] = eTicketSerial[0];        //操作票序号 高位 4字节
        frame.getContent()[10] = eTicketSerial[1];
        frame.getContent()[11] = eTicketSerial[2];
        frame.getContent()[12] = eTicketSerial[3];
        byte[] eTicketVersion = intToBytes(taskParam.eTicketVersion);
        frame.getContent()[13] = eTicketVersion[0];        //操作票版本号 高位 4字节
        frame.getContent()[14] = eTicketVersion[1];
        frame.getContent()[15] = eTicketVersion[2];
        frame.getContent()[16] = eTicketVersion[3];
        frame.getContent()[17] = (byte) ((taskParam.stationNo & 0xFF00) >> 8);  //本次操作的站号 高位
        frame.getContent()[18] = ((byte) (taskParam.stationNo & 0x00FF));       //本次操作的站号 低位
        frame.getContent()[19] = (byte) ((taskParam.eTicketProperty & 0xFF00) >> 8);  //操作票属性 高位
        frame.getContent()[20] = ((byte) (taskParam.eTicketProperty & 0x00FF));       //操作票属性 低位
        frame.getContent()[21] = (byte) ((taskParam.eTicketLength & 0xFF00) >> 8);  //操作票长度 高位
        frame.getContent()[22] = ((byte) (taskParam.eTicketLength & 0x00FF));       //操作票长度 低位
        frame.getContent()[23] = taskParam.eTicketAttribute;//操作票特性字节 1字节
        frame.getContent()[24] = (byte) ((taskParam.eTicketFrames & 0xFF00) >> 8);  //本张操作票帧数 高位
        frame.getContent()[25] = ((byte) (taskParam.eTicketFrames & 0x00FF));       //本张操作票帧数 低位
        frame.getContent()[26] = (byte) ((taskParam.eTicketTaskIDEx & 0xFF00) >> 8);  //操作票扩展任务号 高位
        frame.getContent()[27] = ((byte) (taskParam.eTicketTaskIDEx & 0x00FF));       //操作票扩展任务号 低位
        byte[] dxMatrixOffset = intToBytes(taskParam.dxMatrixOffset);
        frame.getContent()[28] = dxMatrixOffset[0];        //地线锁码表起始地址 高位 4字节
        frame.getContent()[29] = dxMatrixOffset[1];
        frame.getContent()[30] = dxMatrixOffset[2];
        frame.getContent()[31] = dxMatrixOffset[3];
        byte[] authPersonTableAddr = intToBytes(taskParam.authPersonTableAddr);
        frame.getContent()[32] = authPersonTableAddr[0];        //地线锁码表起始地址 高位 4字节
        frame.getContent()[33] = authPersonTableAddr[1];
        frame.getContent()[34] = authPersonTableAddr[2];
        frame.getContent()[35] = authPersonTableAddr[3];

        String calibrationTime = DataConvert.getDateTime("yyMMddHHmmss");
        taskParam.calibrationTime = DataConvert.stringToBytes(calibrationTime);
        frame.getContent()[36] = taskParam.calibrationTime[0];        // 校准时间  高位 6字节
        frame.getContent()[37] = taskParam.calibrationTime[1];
        frame.getContent()[38] = taskParam.calibrationTime[2];
        frame.getContent()[39] = taskParam.calibrationTime[3];
        frame.getContent()[40] = taskParam.calibrationTime[4];
        frame.getContent()[41] = taskParam.calibrationTime[5];

        byte[] effectiveTime = intToBytes(taskParam.effectiveTime);
        frame.getContent()[42] = effectiveTime[0];        //有效时间 高位 4字节
        frame.getContent()[43] = effectiveTime[1];
        frame.getContent()[44] = effectiveTime[2];
        frame.getContent()[45] = effectiveTime[3];
        frame.getContent()[46] = taskParam.isOrderTicket;//是否有序票 1字节
        return frame;
    }

    /**
     * 发送钥匙自学数据递归调用
     *
     * @param cmdCode
     * @return
     * @throws InterruptedException
     */
    private BoolResult returnCallback(byte cmdCode, String tip) throws InterruptedException {
        curSendFrame.setDied(false); //要连续发和收，所以Died要重置
        BoolResult br = sendThree(curSendFrame, sendTimeOut);
        if (br.getResult()) {
            //发送成功则会收到下一帧的帧号
            if (br.getMsg() != null && !br.getMsg().isEmpty()) {
                curSendFrame.setCmdCode(cmdCode);
                curSendFrame.setCmdLink((byte) 0x03);
                int frameNum = Integer.parseInt(br.getMsg());
                int totalFrames = curSendMultiFrame.totalFrames;

                curSendFrame.setCmdName(tip);
                curSendFrame.setTotalFrames(totalFrames);
                for (; frameNum <= totalFrames; frameNum++) {
                    byte[] data = curSendMultiFrame.getData(frameNum);  //获取下一发送帧
                    curSendFrame.setContent(new byte[data.length]);
                    System.arraycopy(data, 0, curSendFrame.getContent(), 0, data.length);
                    curSendFrame.setDied(false); //要连续发和收，所以Died要重置
                    asyncSetSendProgressEvent(frameNum, curSendMultiFrame.totalFrames, tip);

                    curSendFrame.setFrameNum(frameNum);
                    BoolResult boolResult = sendThree(curSendFrame, sendTimeOut);
                    if (!boolResult.getResult()) {
                        return boolResult;
                    }
                }
            }
        }
        return br;
    }

    /**
     * 获取授权命令数据正文
     *
     * @param studyAuthorize
     * @return
     */
    private byte[] getAuthorizeContent(EstSelfStudyAuthorize studyAuthorize) {
        byte[] content = new byte[15];
        System.arraycopy(studyAuthorize.dataEntityType, 0, content, 0, 2);
        content[2] = studyAuthorize.dataContentLength;
        byte[] loginId = DataConvert.int16ToBytes(studyAuthorize.loginId);
        System.arraycopy(loginId, 0, content, 3, 2);
        String calibrationTime = DataConvert.getDateTime("yyMMddHHmmss");
        studyAuthorize.calibrationTime = DataConvert.stringToBytes(calibrationTime);
        System.arraycopy(studyAuthorize.calibrationTime, 0, content, 5, 6);//校准时间
        byte[] effectiveTime = intToBytes(studyAuthorize.effectiveTime);
        System.arraycopy(effectiveTime, 0, content, 11, 4);//有效时间(4字节)
        return content;
    }

    /**
     * 不需要自学报文内容
     *
     * @return
     */
    private byte[] getNoNeedLearnContent() {
        byte[] content = new byte[11];
        EstNoNeedLearn estNoNeedLearn = new EstNoNeedLearn();
        System.arraycopy(estNoNeedLearn.dataEntityType, 0, content, 0, 2);
        content[2] = estNoNeedLearn.dataContentLength;
        System.arraycopy(estNoNeedLearn.retain, 0, content, 3, 8);
        return content;
    }

    /**
     * 设置自学数据校验码实体类对象
     *
     * @param content
     */
    private EstSelfCheckedCode setSelfCheckedCode(byte[] content) {
        EstSelfCheckedCode estSelfCheckedCode = new EstSelfCheckedCode();
        System.arraycopy(content, 0, estSelfCheckedCode.dataEntityType, 0, 2);
        estSelfCheckedCode.dataContentLength = content[2];
        System.arraycopy(content, 3, estSelfCheckedCode.md5, 0, 16);
        return estSelfCheckedCode;
    }

    /**
     * 设置解锁器应用程序下载结果上报
     *
     * @param content
     * @return
     */
    private EstJsqDownloadResultsReport setJsqDownloadResultsReport(byte[] content) {
        EstJsqDownloadResultsReport resultsReport = new EstJsqDownloadResultsReport();
        System.arraycopy(content, 0, resultsReport.dataEntityType, 0, 2);
        resultsReport.dataContentLength = content[2];
        resultsReport.operationMark = content[3];
        return resultsReport;
    }

    /**
     * 判断是否需要自学
     *
     * @param estSelfCheckedCode
     */
    private void estimateSelfStudy(EstSelfCheckedCode estSelfCheckedCode) {
        //throws InterruptedException, ExecutionException {
        if (equalMD5(estSelfCheckedCode.md5)) {
            sendNoNeedSelfStudy();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    estOperation.onAuthorizedSuccess();//授权成功
                    this.cancel();
                }
            }, 2000);//五百毫秒

//            Future<BoolResult> resultFuture = sendNoNeedSelfStudy();
//            BoolResult boolResult = (BoolResult) resultFuture.get();
//            if (boolResult.getResult()) {//成功
//                estOperation.onAuthorizedSuccess();//授权成功
//            }
        } else {
            estOperation.requestKeySelfStudy();//需要自学
        }
    }

    /**
     * 设置解锁器软件版本对象
     *
     * @param content
     */
    private EstJsqVersion setEstJsqVersion(byte[] content) {
        EstJsqVersion estJsqVersion = new EstJsqVersion();
        System.arraycopy(content, 0, estJsqVersion.dataEntityType, 0, 2);
        estJsqVersion.dataContentLength = content[2];
        byte[] appVersion = new byte[5];
        System.arraycopy(content, 3, appVersion, 0, 5);
        String appVersionStr = new String(appVersion);
        String[] version = appVersionStr.split("");
        appVersionStr = "";
        for (int i = 1; i < 6; i++) {
            appVersionStr += version[i];
            if (i == 1 || i == 3) {
                appVersionStr += ".";
            }
        }
        estJsqVersion.appVersion = appVersionStr;//new String(appVersion);

        byte[] galleryVersion = new byte[5];
        System.arraycopy(content, 8, galleryVersion, 0, 5);
        String galleryVersionStr = new String(galleryVersion);
        String[] version1 = galleryVersionStr.split("");
        galleryVersionStr = "";
        for (int i = 1; i < 6; i++) {
            galleryVersionStr += version1[i];
            if (i == 1 || i == 3) {
                galleryVersionStr += ".";
            }
        }
        estJsqVersion.galleryVersion = galleryVersionStr;
        return estJsqVersion;
    }

    /**
     * 收到锁码
     *
     * @param estFrame
     * @return
     */
    private void receiveRfid(EstFrame estFrame) {
        byte[] content = new byte[6];
        System.arraycopy(estFrame.getContent(), 3, content, 0, 6);
        long rfid = DataConvert.bytesToLong(content, 0, 6);
        if (rfid == 0) {
            estOperation.receivePullOut();
        } else {
            estOperation.receivePlugIn(rfid);
        }
    }

    /**
     * 通用处理信息0x07和数据0x05
     *
     * @param frame
     */
    private void handleInfo(EstFrame frame) {
        switch (frame.getCmdLink()) {
            case FunctionCode.UPDATAINFO:
                curReceiveMultiFrame.writInfoData(frame);
                break;
            case FunctionCode.UPDATA:
                curReceiveMultiFrame.writData(frame);
                break;
        }
    }

    /**
     * 计算发送已知长度的报文需要的帧数
     *
     * @param dataLength
     * @param bytesPerframe 每帧报文长度
     * @return
     */
    public int calculateFrameCount(int dataLength, int bytesPerframe) {
        return (dataLength + bytesPerframe - 1) / bytesPerframe;
    }

    /**
     * 钥匙自学版本号
     *
     * @return
     */
    private byte[] getKeyDataInfoVersion() {
        byte[] keyDataVersion = {0x08, 0x01, 0x0D, 0x1F, 0x11, 0x0E, 0x02, 0x11};
        return keyDataVersion;
    }

    /**
     * 传票握手钥匙版本号
     *
     * @return
     */
    private byte[] getKeyTaskTicketVersion() {
        byte[] keyDataVersion = {(byte) 0xCF, 0x03, 0x06, 0x00, 0x12, 0x09, 0x02, 0x11};
        return keyDataVersion;
    }

    /**
     * 获取取消登陆命令数据正文内容
     *
     * @return
     */
    private byte[] getCancelLoginContent() {
        byte[] cancelLoginContent = {0x00, 0x06, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        return cancelLoginContent;
    }

    /**
     * 获取解锁器软件版本命令数据正文内容
     *
     * @return
     */
    private byte[] getJsqVersionContent() {
        byte[] versionContent = {0x00, 0x09, 0x00};
        return versionContent;
    }

    /**
     * 获取删除操作票数据实体内容
     *
     * @return
     */
    private byte[] getDeleteTicketContent() {
        byte[] content = new byte[7];
        EstDeleteTicket deleteTicket = new EstDeleteTicket();
        System.arraycopy(deleteTicket.dataEntityType, 0, content, 0, 2);
        content[2] = deleteTicket.dataContentLength;
        System.arraycopy(deleteTicket.retain, 0, content, 3, 4);
        return content;
    }

    /**
     * 获取解锁器应用程序下载完毕数据实体内容
     *
     * @return
     */
    private byte[] getJsqDownloadedContent() {
        byte[] content = new byte[4];
        EstJsqDownloaded jsqDownloaded = new EstJsqDownloaded();
        System.arraycopy(jsqDownloaded.dataEntityType, 0, content, 0, 2);
        content[2] = jsqDownloaded.dataContentLength;
        content[3] = jsqDownloaded.operationMark;
        return content;
    }

    /**
     * 判断两次Md5值是否相等
     *
     * @param lastMd5
     * @return
     */
    private boolean equalMD5(byte[] lastMd5) {
        byte[] curMd5 = null;
        if (curMd5Buf == null) {
            curMd5 = (byte[]) readMd5();
        } else {
            curMd5 = curMd5Buf;
        }
        if (curMd5 == null) {
            return false;
        }
        if (lastMd5.length != curMd5.length) {
            return false;
        }
        for (int i = 0; i < curMd5.length; i++) {
            if (lastMd5[i] != curMd5[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 保存md5码
     *
     * @param md5
     */
    private void saveMd5(byte[] md5) {
        curMd5Buf = md5;
        PreferencesUtil.saveObject(PreferencesUtil.KEY_MD5, md5);
    }

    /**
     * 读md5码
     *
     * @return
     */
    private Object readMd5() {
        return PreferencesUtil.readObject(PreferencesUtil.KEY_MD5);
    }

    /**
     * 得到操作时间
     *
     * @param operatedTime
     * @return
     */
    private String getOperatedTime(byte[] operatedTime) {
        String operatedTimeStr = "";
        for (byte data : operatedTime) {
            operatedTimeStr += DataConvert.byteToHexString(data);
        }
        return operatedTimeStr;
    }

    /**
     * 异步设置发送消息
     */
    private void asyncSetSendInfoEvent() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (mMultiFramesSend != null) {
                    mMultiFramesSend.setSendInfoEvent("发送自学数据信息帧...");
                }
            }
        });
    }

    /**
     * 异步设置发送进度消息
     *
     * @param index
     * @param count
     */
    private void asyncSetSendProgressEvent(final int index, final int count, final String info) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (mMultiFramesSend != null) {
                    mMultiFramesSend.setSendProgressEvent(index, count, info);
                }
            }
        });
    }

    /**
     * 异步调用得到解锁器版本
     *
     * @param content
     */
    private void asyncOnGetJsqVersion(final byte[] content) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                EstJsqVersion estJsqVersion = setEstJsqVersion(content);
                estOperation.onGetJsqVersion(estJsqVersion);
            }
        });
    }

    /**
     * 异步调用得到解锁器升级结果
     *
     * @param content
     */
    private void asyncOnUpgradeResultsReport(final byte[] content) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                EstJsqDownloadResultsReport resultsReport = setJsqDownloadResultsReport(content);
                estOperation.onUpgradeResultsReport(resultsReport);
            }
        });
    }
}