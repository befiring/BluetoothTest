package cn.com.ut.protocol.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import cn.com.ut.protocol.BoolResult;

/**
 * 帧基础类
 * Created by zhangyihuang on 2016/12/22.
 */
public abstract class Frame {

    /**
     * 帧头（同步头）
     */
    protected byte[] head;

    public byte[] getHead() {
        return head;
    }

    protected void setHead(byte[] head) {
        this.head = head;
    }

    /**
     * 正文（或帧内容）开始位
     */
    protected int contentStart;

    public int getContentStart() {
        return contentStart;
    }

    /**
     * crc校验
     */
    protected CRCCheck crcCheck = null;

    /**
     * 死帧
     */
    private boolean died;

    public boolean getDied() {
        return died;
    }

    public void setDied(boolean died) {
        this.died = died;
    }

    /**
     * 正文（或帧内容）
     */
    protected byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * Crc校验内容
     */
    protected byte[] crcValue;

    /**
     * 帧长度
     */
    protected int frameLength;

    /**
     * 通信结果
     */
    private byte[] result;

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    /**
     * 应用层命名名
     */
    protected String cmdName;

    public String getCmdName() {
        return cmdName;
    }

    public void setCmdName(String cmdName) {
        this.cmdName = cmdName;
    }

    /**
     * 当前帧号
     */
    protected int frameNum;

    public int getFrameNum() {
        return frameNum;
    }

    public void setFrameNum(int frameNum) {
        this.frameNum = frameNum;
    }

    /**
     * 总帧数
     */
    protected int totalFrames;

    public int getTotalFrames() {
        return totalFrames;
    }

    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
    }

    public Frame() {
        cmdName = "";
        frameNum = 1;
        totalFrames = 1;
    }

    /**
     * 初始化帧结构
     */
    protected abstract void initFrame();

    /**
     * 对数据进行校验
     *
     * @param checkData 被校验的一个完整的帧
     * @param offset    完整帧在数据中的偏移量,去除同步头
     * @param length    长度,去除同步头含校验码本身
     * @return 校验是否成功
     */
    public boolean checkCrc(byte[] checkData, int offset, int length) {
        return crcCheck.check(checkData, offset, length);
    }

    /**
     * 得到数据正文长度
     *
     * @param data   全帧数据,偏移后的第一位为同步头开始位
     * @param offset 偏移量
     * @return 全帧长度
     */
    public abstract int getFrameLength(byte[] data, int offset);

    /**
     * 处理收到的帧信息
     *
     * @param data   数据
     * @param offset 完整帧在数据中的偏移量，从同步头开始
     * @param length 长度,去除同步头含校验码本身
     * @return 全帧长度
     */
    public abstract Frame handleFrame(byte[] data, int offset, int length);

    /**
     * 得到全帧数据
     *
     * @return 全帧数据
     */
    public abstract byte[] getAllFrameBytes();

    /**
     * Lock held by send
     */
    private final ReentrantLock sendLock = new ReentrantLock();

    public ReentrantLock getSendLock() {
        return sendLock;
    }

    /**
     * Wait queue for waiting send
     */
    private final Condition notWait = sendLock.newCondition();

    public Condition getNotWait() {
        return notWait;
    }

    private boolean signalFlag = false;

    public boolean await(long timeOut, TimeUnit unit) throws InterruptedException {
        signalFlag = false;
        notWait.await(timeOut, unit);
        return signalFlag;
    }

    /**
     * 发送结果
     */
    public BoolResult sendResult;

    public BoolResult getSendResult() {
        return sendResult;
    }

    private void signalNotWait() {
        final ReentrantLock sendLock = this.sendLock;
        sendLock.lock();
        try {
            signalFlag = true;
            notWait.signal();
        } finally {
            sendLock.unlock();
        }
    }

    /**
     * 发送失败
     *
     * @param mes
     */
    public void sendFailure(String mes) {
        setDied(true);
        sendResult = new BoolResult(false, mes);
        signalNotWait();
    }

    /**
     * 发送成功
     *
     * @param mes
     */
    public void sendSuccess(String mes) {
        setDied(true);
        sendResult = new BoolResult(true, mes);
        signalNotWait();
    }

    /**
     * 发送成功
     *
     * @param br 处理结果
     */
    public void sendSuccess(BoolResult br) {
        setDied(true);
        sendResult = br;
        signalNotWait();
    }
}