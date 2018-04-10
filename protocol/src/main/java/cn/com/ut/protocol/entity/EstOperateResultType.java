package cn.com.ut.protocol.entity;

import android.util.SparseArray;

/**
 * E匙通操作结果类型
 * Created by huangweiwen on 2017/4/18.
 */
public class EstOperateResultType {

    private static SparseArray<String> estResultTypes;

    static {
        initEstOperateResultType();
    }

    /**
     * 获取E匙通操作结果类型
     * @return
     */
    public static SparseArray<String> initEstOperateResultType() {
        if (estResultTypes == null) {
            estResultTypes = new SparseArray<>();
            estResultTypes.put(0x00, "合上锁");
            estResultTypes.put(0x01, "打开锁");
            estResultTypes.put(0x02, "开锁正常完成");
            estResultTypes.put(0x03, "验电通过");
            estResultTypes.put(0x04, "无权限");
            estResultTypes.put(0x05, "有电,禁止操作");
            estResultTypes.put(0x06, "验电无应答");
            estResultTypes.put(0x07, "走错间隔");
            estResultTypes.put(0x08, "巡视完成");
        }
        return estResultTypes;
    }

    public static class ResultType {
        public ResultType(int value, String name) {
            this.value = value;
            this.name = name;
        }
        public long value;
        public String name;
    }

    public static String getOperateResultName(int result) {
        return estResultTypes.get(result);
    }
}