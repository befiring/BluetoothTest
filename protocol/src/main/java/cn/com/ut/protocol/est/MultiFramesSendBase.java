package cn.com.ut.protocol.est;

/**
 * 多帧发送基类
 * Created by zhangyihuang on 2017/3/29.
 */
public class MultiFramesSendBase {

    /**
     * 信息帧数据
     */
    public byte[] info;

    /**
     * 数据帧数据
     */
    public byte[] data;

    /**
     * 帧长
     */
    public int frameLength;

    /**
     * 总帧数
     */
    public int totalFrames;

    /**
     * 得到数据帧数据
     *
     * @param frameNum 帧号从1开始
     * @return
     */
    public byte[] getData(int frameNum) {
        byte[] sendData = new byte[frameLength + 2];  //帧长度
        sendData[0] = (byte) ((frameNum >> 8) & 0xFF);
        sendData[1] = (byte) (frameNum & 0xFF);
        System.arraycopy(data, (frameNum - 1) * frameLength, sendData, 2, frameLength);
        return sendData;
    }

    /**
     * 获取帧数据长
     * @return
     */
    public int getFrameLength() {
        return 256;
    }
}