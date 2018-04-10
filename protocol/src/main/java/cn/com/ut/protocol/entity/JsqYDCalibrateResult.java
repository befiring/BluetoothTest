package cn.com.ut.protocol.entity;

/**
 * 解锁器验电标定结果 4.5.2.16
 * Created by zhangyihuang on 2018/3/12.
 */
public class JsqYDCalibrateResult {
    /**
     * 验电结果
     */
    private JsqYDResult jsqYDResult;

    /**
     * 标定结果
     */
    private byte calibratedResult;

    public JsqYDCalibrateResult() {
        this.jsqYDResult = new JsqYDResult();
    }

    public JsqYDResult getJsqYDResult() {
        return jsqYDResult;
    }

    public void setJsqYDResult(JsqYDResult jsqYDResult) {
        this.jsqYDResult = jsqYDResult;
    }

    public byte getCalibratedResult() {
        return calibratedResult;
    }

    public void setCalibratedResult(byte calibratedResult) {
        this.calibratedResult = calibratedResult;
    }
}