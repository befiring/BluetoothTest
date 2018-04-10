package cn.com.ut.protocol.model;

/**
 * Created by zhangyihuang on 2017/10/23.
 */
public class SkipLock extends LockBase implements ILockBase {

    public SkipLock(short lockType) {
        super(lockType);
    }
}