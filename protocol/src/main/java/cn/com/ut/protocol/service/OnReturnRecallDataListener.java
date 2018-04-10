package cn.com.ut.protocol.service;

import cn.com.ut.protocol.entity.EstRecallReturnData;

/**
 * 返回追忆数据接口
 * Created by zhangyihuang on 2017/4/18.
 */
public interface OnReturnRecallDataListener {
    /**
     * 返回追忆数据实体类
     */
    public abstract void onReturnRecallData(EstRecallReturnData returnData);

    /**
     * 无追忆数据返回
     */
    public abstract void onReturnNull();
}