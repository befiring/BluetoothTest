package cn.com.ut.protocol.port;

/**
 * 处理结果
 * Created by zhangyihuang on 2016/12/23.
 */
public enum ProcessResult {
    /**
     * 未处理
     */
    Null,

    /**
     * 部份同步头
     */
    PartHead,

    /**
     * 全部同步头
     */
    FullHead,

    /**
     * 等数据
     */
    WaitingData,

    /**
     * 处理完成
     */
    Finished
}