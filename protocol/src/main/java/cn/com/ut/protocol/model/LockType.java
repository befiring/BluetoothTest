package cn.com.ut.protocol.model;

/**
 * Created by wyman on 17/4/20.
 */
public class LockType {
    public int value;
    public String typeName;

    @Override
    public String toString() {
        return typeName;
    }
}