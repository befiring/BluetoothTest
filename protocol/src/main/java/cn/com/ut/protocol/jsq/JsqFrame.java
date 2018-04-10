package cn.com.ut.protocol.jsq;

import java.io.UnsupportedEncodingException;

import cn.com.ut.protocol.DataConvert;
import cn.com.ut.protocol.core.CRCCheck;
import cn.com.ut.protocol.core.Frame;

/**
 * 解锁器帧
 * Created by zhangyihuang on 2016/12/23.
 */
public class JsqFrame extends Frame {

    /**
     * 版本号
     */
    private byte[] version;

    /**
     * 流水号
     */
    private static short _sn = 0x0000;

    /**
     * 流水号
     */
    private int num = -1;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    /**
     * 解锁器命令字,第14位
     */
    private int cmdCode;

    public int getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(int cmdCode) {
        this.cmdCode = cmdCode;
    }

    /**
     * 扩展首部数据长度
     */
    private int exHeadDataLength;

    public int getExHeadDataLength() {
        return exHeadDataLength;
    }

    public void setExHeadDataLength(int exHeadDataLength) {
        this.exHeadDataLength = exHeadDataLength;
    }

    /**
     * 扩展首部数据
     */
    private byte[] exHeadData;

    public byte[] getExHeadData() {
        return exHeadData;
    }

    public void setExHeadData(byte[] exHeadData) {
        this.exHeadData = exHeadData;
    }

    /**
     * 流水号
     */
    public static short getSn() {
        _sn++;
        return _sn;
    }

    /**
     * 构造函数，初始化帧结构以及CRC检验对象
     */
    public JsqFrame() {
        initFrame();
    }

    protected final void initFrame() {
        try {
            String head = "JSQCOM";
            setHead(head.getBytes("UTF-8"));
            String versionStr = "1.00";
            version = versionStr.getBytes("UTF-8");
            contentStart = 20; //正文（或帧内容）开始位
            crcCheck = new CRCCheck(16, 0x1021, false, 0, 0);
            crcCheck.setHeadLength(6);//设置同步头长度
            crcValue = new byte[crcCheck.getCrcLength()];
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到数据正文长度
     *
     * @param data   全帧数据,偏移后的第一位为同步头开始位
     * @param offset 偏移量
     * @return 全帧长度
     */
    public int getFrameLength(byte[] data, int offset) {
        return DataConvert.bytesToInt(data, offset + 10);
    }

    /**
     * 处理收到的帧信息
     *
     * @param data   数据
     * @param offset 完整帧在数据中的偏移量，从同步头开始
     * @param length 数据长度,去除同步头含校验码本身
     * @return 全帧长度
     */
    public Frame handleFrame(byte[] data, int offset, int length) {
        JsqFrame frame = new JsqFrame();
        System.arraycopy(data, offset + 6, version, 0, 4);

        frame.frameLength = DataConvert.bytesToInt(data, offset + 10);
        frame.cmdCode = DataConvert.bytesToInt16(data, offset + 14);
        frame.num = DataConvert.bytesToInt16(data, offset + 16);
        frame.exHeadDataLength = DataConvert.bytesToInt16(data, offset + 18);
        if (frame.exHeadDataLength > 0) {
            frame.exHeadData = new byte[frame.exHeadDataLength];
            System.arraycopy(data, offset + frame.contentStart, frame.exHeadData, 0, frame.exHeadDataLength);
        }
        int contentLength = length - contentStart - frame.exHeadDataLength - this.crcCheck.getCrcLength();
        if (contentLength > 0) {
            frame.content = new byte[contentLength];
            System.arraycopy(data, offset + frame.contentStart + frame.exHeadDataLength, frame.content, 0, contentLength);
        }
        System.arraycopy(data, frame.frameLength - 2, frame.crcValue, 0, 2);
        return frame;
    }

    /**
     * 获取全帧数据
     *
     * @return 全帧数据
     */
    @Override
    public byte[] getAllFrameBytes() {
        int contentLength = content == null ? 0 : content.length;
        int frameLength = 20 + exHeadDataLength + contentLength + crcCheck.getCrcLength();
        byte[] data = new byte[frameLength];

        //同步头(6字节)
        System.arraycopy(head, 0, data, 0, head.length);
        //版本号(4字节)
        System.arraycopy(version, 0, data, 6, version.length);
        //帧长度(4字节)
        DataConvert.intToBytes(frameLength, data, 10);

        //命令码(2字节)
        data[14] = (byte) ((cmdCode & 0xFF00) >> 8);
        data[14 + 1] = (byte) (cmdCode & 0xFF);
        if (num == -1) { //流水号(2字节)
            num = getSn();
        }
        data[16] = (byte) ((num & 0xFF00) >> 8);
        data[16 + 1] = (byte) (num & 0xFF);

        //扩展首部长度(2字节)
        data[18] = (byte) ((exHeadDataLength & 0xFF00) >> 8);
        data[18 + 1] = (byte) (exHeadDataLength & 0xFF);
        if (exHeadDataLength > 0 && exHeadData != null) {
            System.arraycopy(exHeadData, 0, data, 20, exHeadDataLength);
        }
        //数据
        if (content != null) {
            System.arraycopy(content, 0, data, 20 + exHeadDataLength, content.length);
        }
        //校验码(2字节)
        crcCheck.getCheckCode(data, head.length);
        System.arraycopy(data, frameLength - 2, crcValue, 0, 2);
        return data;
    }
}