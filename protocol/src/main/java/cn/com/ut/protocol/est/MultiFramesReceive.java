package cn.com.ut.protocol.est;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.ut.protocol.DataConvert;
import cn.com.ut.protocol.DataWriteUtils;
import cn.com.ut.protocol.EstUtil;
import cn.com.ut.protocol.service.IMultiReceiveInfoService;

/**
 * 多帧数据接收处理
 * Created by zhangyihuang on 2017/4/15.
 */
public class MultiFramesReceive {

    /**
     * 初始段长度
     */
    public int firstSectionInfoLen = 0;

    /**
     * 数据据帧长
     */
    public int dataframLen = 0;

    /**
     * 应用层命令字
     */
    public int appCommand;

    /**
     * 应用层命令中文名
     */
    public String commandName;

    /**
     * 总帧数
     */
    protected int totalFrames;

    /**
     * 当前段数据帧数
     */
    protected int curSectionTotalFrames = 0;

    /**
     * 总段数
     */
    protected int totalSection = 0;

    /**
     * 当前帧号
     */
    protected int curFrameNum = 0;

    /**
     * 信息帧号，为段标志字节的前三位
     */
    protected int infoNum = 0;

    /**
     * 当前段号,为段标志字节的后五位
     */
    protected int curSectionNum = 0;

    /**
     * 信息帧段标志 1字节
     */
    public byte partFlag;

    /**
     * 正文数据
     */
    public ByteArrayOutputStream mData;

    private IMultiReceiveInfoService multiReceive;

    private ProtocolEst protocol;

    /**
     * 构造函数
     *
     * @param protocol     规约
     * @param multiReceive 多帧接收端口
     */
    public MultiFramesReceive(ProtocolEst protocol, IMultiReceiveInfoService multiReceive) {
        this.multiReceive = multiReceive;
        this.protocol = protocol;
    }

    /**
     * 创建内存区
     */
    private void createMemoryData() {
        if (mData != null) {
            try {
                mData.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            mData = null;
        }
        mData = new ByteArrayOutputStream();
    }

    /**
     * 写信息帧数据
     *
     * @param frame
     */
    public void writInfoData(EstFrame frame) {
        int receivedDataInfoNo = (frame.getContent()[0] & 0xE0) >> 5;//信息帧帧号，为段标志字节的前三位,UT-Net接票信息帧要分帧
        int receivedSectionNum = frame.getContent()[0] & 0x1F;//段号
        if (receivedDataInfoNo == 0) {
            if (curSectionNum != receivedSectionNum) return;
            curSectionNum++;
            partFlag = (byte) receivedDataInfoNo;
        }
        if (receivedSectionNum == 0) //初始段
        {
            if (infoNum != receivedDataInfoNo) return;//信息帧帧号不同
            infoNum++;
            if (receivedDataInfoNo == 0)//第一个信息帧
            {
                curFrameNum = 1;//帧号为1，等收数据的了
                createMemoryData();
                curSectionTotalFrames = DataConvert.bytesToInt16(frame.getContent(), 1);
                appCommand = frame.getCmdCode();
                if (appCommand < 0) {
                    appCommand += 256;
                }
                commandName = EstUtil.getCommandName(appCommand);
                totalFrames = DataConvert.bytesToInt16(frame.getContent(), 3);
                firstSectionInfoLen = frame.getContent().length;
                DataWriteUtils.writeBytes(mData, frame.getContent());
                multiReceive.setReceiveIndexEvent(0, totalFrames, commandName);
            } else {
                int length = frame.getContent().length - 1;
                firstSectionInfoLen += length;
                byte[] content = new byte[length];
                System.arraycopy(frame.getContent(), 1, content, 0, length);
                DataWriteUtils.writeBytes(mData, content);//去掉段标志
            }
        } else {
            curSectionTotalFrames += DataConvert.bytesToInt16(frame.getContent(), 1);
        }
    }

    public void writData(EstFrame frame) {
        if (curSectionNum < 1) return;//信息帧还没收到
        int frameNum = DataConvert.bytesToInt16(frame.getContent(), 0);
        multiReceive.setReceiveIndexEvent(frameNum, totalFrames, commandName);
        if (frameNum == curSectionTotalFrames) {
            multiReceive.setInfoEvent("等待下一段数据");
        }
        if (frameNum == totalFrames)//最后一帧不要,接收完成
        {
            dataframLen = frame.getContent().length - 2;
            byte[] content = new byte[dataframLen];
            System.arraycopy(frame.getContent(), 2, content, 0, dataframLen);
            DataWriteUtils.writeBytes(mData, content);
            multiReceive.setInfoEvent(String.format("%s接收完成", commandName));
            protocol.processReceiveMultiFrame(this);
            this.totalFrames = 0;//重新置0，避免重发引起的事件重复
            this.curFrameNum = 0;
            this.curSectionNum = 0;
            this.infoNum = 0;
        } else {
            if (curFrameNum == frameNum) {
                curFrameNum++;
                if (frameNum > 1) {//丢弃第一帧垃圾帧
                    dataframLen = frame.getContent().length - 2;
                    byte[] content = new byte[dataframLen];
                    System.arraycopy(frame.getContent(), 2, content, 0, dataframLen);
                    DataWriteUtils.writeBytes(mData, content);//去掉帧号
                }
            }
        }
    }

}