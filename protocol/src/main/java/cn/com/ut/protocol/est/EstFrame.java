package cn.com.ut.protocol.est;

import cn.com.ut.protocol.DataConvert;
import cn.com.ut.protocol.core.CRCCheck;
import cn.com.ut.protocol.core.Frame;

/**
 * E匙通帧
 * Created by zhangyihuang on 2017/3/28.
 */
public class EstFrame extends Frame {
    private final String TAG = EstFrame.class.getName();
    /**
     * 链路层命令
     */
    private byte cmdLink;

    public byte getCmdLink() {
        return cmdLink;
    }

    public void setCmdLink(byte cmdLink) {
        this.cmdLink = cmdLink;
    }

    /**
     * 应用层命令
     */
    private byte cmdCode;

    public byte getCmdCode() {
        return cmdCode;
    }

    /**
     * 设置应用层命令
     *
     * @param cmdCode
     */
    public void setCmdCode(byte cmdCode) {
        this.cmdCode = cmdCode;
    }

    /**
     * 构造函数，初始化帧结构以及CRC检验对象
     */
    public EstFrame() {
        initFrame();
    }

    @Override
    protected void initFrame() {
        byte[] head = {(byte) 0xA5, 0x5A, (byte) 0xA5, 0x5A};
        setHead(head);
        contentStart = 8; //正文（或帧内容）开始位
        crcCheck = new CRCCheck(16, 0x1021, false, 0, 0);
        crcCheck.setHeadLength(4);//设置同步头长度
        crcValue = new byte[crcCheck.getCrcLength()];
    }

    @Override
    public int getFrameLength(byte[] data, int offset) {
        //return DataConvert.bytesToInt(data, offset + 10);
        return DataConvert.bytesToInt16(data, offset + 5) + 10; //仅是数据正文长度，需加10
    }

    @Override
    public Frame handleFrame(byte[] data, int offset, int length) {
        EstFrame frame = new EstFrame();
        frame.cmdLink = data[offset + 4];
        frame.frameLength = DataConvert.bytesToInt16(data, offset + 5) + 10;
        frame.cmdCode = data[offset + 7];

        int contentLength = DataConvert.bytesToInt16(data, offset + 5);
        if (contentLength > 0) {
            frame.content = new byte[contentLength];
            System.arraycopy(data, offset + frame.contentStart, frame.content, 0, contentLength);
        }
        System.arraycopy(data, offset + frame.frameLength - 2, frame.crcValue, 0, 2);
        return frame;
    }

    @Override
    public byte[] getAllFrameBytes() {
        int contentLength = content == null ? 0 : content.length;
        int frameLength = 8 + contentLength + crcCheck.getCrcLength();
        byte[] data = new byte[frameLength];

        //同步头(4字节)
        System.arraycopy(head, 0, data, 0, head.length);
        data[4] = cmdLink;
        DataConvert.int16ToBytes(contentLength, data, 5);
        data[7] = cmdCode;

        //数据
        if (content != null) {
            System.arraycopy(content, 0, data, contentStart, content.length);
        }
        //校验码(2字节)
        crcCheck.getCheckCode(data, head.length);
        System.arraycopy(data, frameLength - 2, crcValue, 0, 2);
        return data;
    }

}