package cn.com.ut.protocol.service;

import cn.com.ut.protocol.entity.EstRecallReturnData;

/**
 * 追忆数据回传服务
 * Created by zhangyihuang on 2017/4/18.
 */
public class ReturnRecallDataService extends MultiReceiveInfoService implements OnReturnRecallDataListener {

    protected OnReturnRecallDataListener mOnReturnRecallDataListener;

    public ReturnRecallDataService(OnReturnRecallDataListener onReturnRecallDataListener) {
        this.mOnReturnRecallDataListener = onReturnRecallDataListener;
    }

    @Override
    public void onReturnData(Object data) {
        EstRecallReturnData returnData = (EstRecallReturnData) data;
        if (returnData != null) {
            onReturnRecallData(returnData);
        }
    }

    @Override
    public void onReturnNull() {
        if (mOnReturnRecallDataListener != null) {
            mOnReturnRecallDataListener.onReturnNull();
        }
    }

    public void onReturnRecallData(EstRecallReturnData returnData) {
        if (mOnReturnRecallDataListener != null) {
            mOnReturnRecallDataListener.onReturnRecallData(returnData);
        }
    }
}