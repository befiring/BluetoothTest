package cn.com.ut.protocol.entity;

import cn.com.ut.protocol.DataConvert;

/**
 * 解锁器应用程序下载数据
 * Created by zhangyihuang on 2017/4/14.
 */
public class EstJsqProgramData {
    /**
     * 应用程序下载开始
     */
    public EstJsqDownloadStart jsqDownloadStart;

    /**
     * 应用程序下载数据
     */
    public EstJsqDownloadData jsqDownloadData;

    /**
     * 解锁器应用程序下载开始数据实体格式
     * Created by zhangyihuang on 2017/4/14.
     */
    public class EstJsqDownloadStart extends DataEntity {
        /**
         * 文件大小(4字节，高到低)
         */
        public int fileSize;

        /**
         * 检验和(4字节，高到低)
         */
        public int checkSum;

        /**
         * 时间对时（8字节）
         */
        public byte[] calibrationTime;

        /**
         * 下载数据类型(1字节)
         * 0x01表示应用程序，0x02表示图库
         */
        public byte downloadDataType;

        public EstJsqDownloadStart() {
            super();
            dataEntityType[0] = 0x00;
            dataEntityType[1] = 0x0B;
            dataContentLength = 0x11;
            downloadDataType = 0x01;//默认应用程序

            calibrationTime = new byte[8];
            calibrationTime[0] = 0x00;
            String nowStr = DataConvert.getDateTime("yyyyMMddHHmmss");
            byte[] syncTime = DataConvert.hexStringToBytes(nowStr);
            System.arraycopy(syncTime, 0, calibrationTime, 1, 7);//校准时间
        }
    }

    /**
     * 应用程序下载数据
     */
    public class EstJsqDownloadData extends DataEntity {
        /**
         * 数据
         */
        public byte[] data;

        public int curFrameNo = 1;
        public int totalFrames = 0;
        public int frameLength = 256;

        public byte[] getData(int frameNum) {
            byte[] sendData = new byte[frameLength + 4];  //帧长度
            sendData[0] = dataEntityType[0];
            sendData[1] = dataEntityType[1];
            sendData[2] = (byte) ((frameLength >> 8) & 0xFF);
            sendData[3] = (byte) (frameLength & 0xFF);

            int length = frameLength;
            int index = (frameNum - 1) * frameLength + frameLength;
            if (index > data.length) {
                length = data.length - (frameNum - 1) * frameLength;
            }
            System.arraycopy(data, (frameNum - 1) * frameLength, sendData, 4, length);

            if (length < frameLength) {
                index = length + 4;
                //补齐帧
                while (index < frameLength) {
                    sendData[index++] = 0x00;
                }
            }
            return sendData;
        }

        public EstJsqDownloadData() {
            super();
            dataEntityType[0] = 0x00;
            dataEntityType[1] = 0x0C;
        }
    }
}