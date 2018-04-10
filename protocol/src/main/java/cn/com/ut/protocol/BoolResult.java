package cn.com.ut.protocol;

/**
 * Created by zhangyihuang on 2016/12/22.
 */
public class BoolResult {
    private boolean result;

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    private String msg = "";

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BoolResult(boolean r) {
        result = r;
    }

    public BoolResult(boolean r, String m) {
        result = r;
        msg = m;
    }
}