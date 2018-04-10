package cn.com.ut.protocol.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 追忆回传数据
 * Created by zhangyihuang on 2017/4/15.
 */
public class EstRecallReturnData {
    /**
     * 段标志 1字节
     */
    public byte partFlag;

    /**
     * 本段数据帧数
     */
    public int frameLength;

    /**
     * 追忆数据的总帧数
     */
    public int totalFrames;

    /**
     * 自学版本号（8字节，先高后低）
     */
    public byte[] selfStudyVersion;

    /**
     * 电脑钥匙已操作项数（2字节先高后低）
     */
    public short operateItems;

    /**
     * 电脑钥匙已操作步数（2字节先高后低）
     */
    public short operateSteps;

    /**
     * 追忆版本号(1个字节)
     */
    public byte recallVersion;

    //////////////////////////////////////////追忆数据实体帧/////////////////////////////////////////

    public EstRecallReturnData() {
        recallDataFrames = new ArrayList<>();
    }

    public List<RecallDataFrame> recallDataFrames;

    /**
     * 追忆数据实体帧
     * 追忆数据每步操作格式：(31字节)
     * 设备RFID码(6字节)+设备ID（2字节）+操作结果(1字节)+ 操作时间(6字节)+
     * 操作人姓名(12字节)+操作人员ID(2字节)+ 操作票序号（4字节）
     */
    public class RecallDataFrame {
            public long rfidCode; //设备RFID码(6字节)
            public short deviceId;//设备ID（2字节
            public byte operateResults;//操作结果(1字节)
            public String operateTime;//操作时间(6字节)
            public String operatorName;//操作人姓名(12字节)
            public short operatorId;//操作人员ID(2字节)
            public int ticketNum;//操作票序号（4字节）
    }
}