package cn.com.ut.protocol.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyihuang on 2017/1/16.
 */
public class LockInfo {

    /**
     * 钥匙属性名称字典
     */
    public static Map<Long, String> keyPropertyMap;

    /**
     * 开放锁具类型
     */
    public static Map<Long, String> keyOpenLockMap;

    private static List<LockType> estLockTypes;

    public static Map<Long, String> getPwMap() {
        if (keyPropertyMap == null) {
            initKeyPropertyMap();
        }
        return keyPropertyMap;
    }

    public static Map<Long, String> getOpenLockMap() {
        if (keyOpenLockMap == null) {
            initOpenLockMap();
        }
        return keyOpenLockMap;
    }

    /**
     * 钥匙属性名称字典初始化
     */
    private static void initKeyPropertyMap() {
        keyPropertyMap = new HashMap<>();
        keyPropertyMap.put(Long.valueOf(0x00), "");
        keyPropertyMap.put(Long.valueOf(0x01), "机械锁");
        keyPropertyMap.put(Long.valueOf(0x02), "电编码锁");
        keyPropertyMap.put(Long.valueOf(0x03), "电控锁");
        keyPropertyMap.put(Long.valueOf(0x04), "单向电控锁");
        keyPropertyMap.put(Long.valueOf(0x05), "GSN验电");
        keyPropertyMap.put(Long.valueOf(0x06), "无源验电");
        keyPropertyMap.put(Long.valueOf(0x07), "有源验电");
        keyPropertyMap.put(Long.valueOf(0x08), "超级防空锁");
        keyPropertyMap.put(Long.valueOf(0x09), "反馈式电编码锁");
        keyPropertyMap.put(Long.valueOf(0x0A), "智能锁具");
        keyPropertyMap.put(Long.valueOf(0x0B), "智能压板");
        keyPropertyMap.put(Long.valueOf(0x0C), "有源电位置检测");
        keyPropertyMap.put(Long.valueOf(0x0D), "无源电位置检测");
        keyPropertyMap.put(Long.valueOf(0x0E), "机械锁+电编码锁");
        keyPropertyMap.put(Long.valueOf(0x0F), "电编码锁2H");
        keyPropertyMap.put(Long.valueOf(0x10), "电编码锁3H");
        keyPropertyMap.put(Long.valueOf(0x11), "巡检锁");
        keyPropertyMap.put(Long.valueOf(0x12), "无线锁-电编码锁");
        keyPropertyMap.put(Long.valueOf(0x13), "无线锁-机械锁");
        keyPropertyMap.put(Long.valueOf(0x14), "无线锁-高压带电显示闭锁装置");
        keyPropertyMap.put(Long.valueOf(0x15), "无线智能面板锁");
        keyPropertyMap.put(Long.valueOf(0x16), "无线智能防火门锁");
        keyPropertyMap.put(Long.valueOf(0x17), "智能面板锁");
        keyPropertyMap.put(Long.valueOf(0x18), "智能防火门锁");
        keyPropertyMap.put(Long.valueOf(0x19), "GSN2验电器");
        keyPropertyMap.put(Long.valueOf(0x79), "跳步钥匙");
        keyPropertyMap.put(Long.valueOf(0x80), "锌合金挂锁");
        keyPropertyMap.put(Long.valueOf(0x81), "塑料挂锁");
        keyPropertyMap.put(Long.valueOf(0x82), "链条锁");
        keyPropertyMap.put(Long.valueOf(0x83), "把手球锁");
        keyPropertyMap.put(Long.valueOf(0x84), "把手锁");
        keyPropertyMap.put(Long.valueOf(0x85), "插芯锁");
        keyPropertyMap.put(Long.valueOf(0x86), "防盗盒锁");
        keyPropertyMap.put(Long.valueOf(0x87), "平面锁");
        keyPropertyMap.put(Long.valueOf(0x88), "连杆锁");
        keyPropertyMap.put(Long.valueOf(0x89), "防火门锁");
        keyPropertyMap.put(Long.valueOf(0x8A), "圆孔锁");
        keyPropertyMap.put(Long.valueOf(0x44), "无线-外装式智能门锁");
        keyPropertyMap.put(Long.valueOf(0x8c), "GYD1D验电器");
    }

    private static void initOpenLockMap() {
        keyOpenLockMap = new HashMap<Long, String>();
        keyOpenLockMap.put(Long.valueOf(0xB0), "地刀锁");
        keyOpenLockMap.put(Long.valueOf(0xB1), "网门锁");
        keyOpenLockMap.put(Long.valueOf(0xB2), "开关锁");
        keyOpenLockMap.put(Long.valueOf(0xB3), "门锁");
        keyOpenLockMap.put(Long.valueOf(0xB4), "带电指示器");
        keyOpenLockMap.put(Long.valueOf(0xB5), "盒锁");
    }

    /**
     * 获取E匙通锁类型
     *
     * @return
     */
    public static List<LockType> getEstLockType() {
        if (estLockTypes == null) {
            estLockTypes = new ArrayList<>();
            LockType lockType1 = new LockType();
            lockType1.value = 0x28;
            lockType1.typeName = "锌合金挂锁";
            LockType lockType2 = new LockType();
            lockType2.value = 0x29;
            lockType2.typeName = "塑料挂锁";
            LockType lockType3 = new LockType();
            lockType3.value = 0x2A;
            lockType3.typeName = "链条锁";
            LockType lockType4 = new LockType();
            lockType4.value = 0x2B;
            lockType4.typeName = "把手球锁";
            LockType lockType5 = new LockType();
            lockType5.value = 0x2C;
            lockType5.typeName = "把手锁";
            LockType lockType6 = new LockType();
            lockType6.value = 0x2D;
            lockType6.typeName = "插芯锁";
            LockType lockType7 = new LockType();
            lockType7.value = 0x2E;
            lockType7.typeName = "防盗盒锁";
            LockType lockType8 = new LockType();
            lockType8.value = 0x2F;
            lockType8.typeName = "平面锁";
            LockType lockType9 = new LockType();
            lockType9.value = 0x30;
            lockType9.typeName = "连杆锁";
            LockType lockType10 = new LockType();
            lockType10.value = 0x31;
            lockType10.typeName = "防火门锁";
            LockType lockType11 = new LockType();
            lockType11.value = 0x32;
            lockType11.typeName = "圆孔锁";
            LockType lockType12 = new LockType();
            lockType12.value = 0x33;
            lockType12.typeName = "户外柜门锁";
            LockType lockType13 = new LockType();
            lockType13.value = 0x34;
            lockType13.typeName = "无线锁-机械锁";
            LockType lockType14 = new LockType();
            lockType14.value = 0x35;
            lockType14.typeName = "无线锁-电编码锁";
            LockType lockType15 = new LockType();
            lockType15.value = 0x36;
            lockType15.typeName = "无线锁-高压带电显示闭锁装置（GSN2L）";
            LockType lockType16 = new LockType();
            lockType16.value = 0x37;
            lockType16.typeName = "无线智能面板锁";
            LockType lockType17 = new LockType();
            lockType17.value = 0x38;
            lockType17.typeName = "无线智能防火门锁";
            LockType lockType18 = new LockType();
            lockType18.value = 0x39;
            lockType18.typeName = "无线-双孔机械锁";
            LockType lockType19 = new LockType();
            lockType19.value = 0x3A;
            lockType19.typeName = "GSN2验电器";
            LockType lockType20 = new LockType();
            lockType20.value = 0x3B;
            lockType20.typeName = "巡检";
            LockType lockType21 = new LockType();
            lockType21.value = 0x40;
            lockType21.typeName = "GSN2验电器(带加密)";
            LockType lockType22 = new LockType();
            lockType22.value = 0x41;
            lockType22.typeName = "隔离点标牌";
            LockType lockType23 = new LockType();
            lockType23.value = 0x42;
            lockType23.typeName = "隔离唯一标识";
            LockType lockType24 = new LockType();
            lockType24.value = 0x43;
            lockType24.typeName = "临时隔离挂锁";
            LockType lockType25 = new LockType();
            lockType25.value = 0x44;
            lockType25.typeName = "无线-外装式智能门锁";
            LockType lockType26 = new LockType();
            lockType26.value = 0x8c;
            lockType26.typeName = "GYD1D验电器";
            estLockTypes.add(lockType1);
            estLockTypes.add(lockType2);
            estLockTypes.add(lockType3);
            estLockTypes.add(lockType4);
            estLockTypes.add(lockType5);
            estLockTypes.add(lockType6);
            estLockTypes.add(lockType7);
            estLockTypes.add(lockType8);
            estLockTypes.add(lockType9);
            estLockTypes.add(lockType10);
            estLockTypes.add(lockType11);
            estLockTypes.add(lockType12);
            estLockTypes.add(lockType13);
            estLockTypes.add(lockType14);
            estLockTypes.add(lockType15);
            estLockTypes.add(lockType16);
            estLockTypes.add(lockType17);
            estLockTypes.add(lockType18);
            estLockTypes.add(lockType19);
            estLockTypes.add(lockType20);
            estLockTypes.add(lockType21);
            estLockTypes.add(lockType22);
            estLockTypes.add(lockType23);
            estLockTypes.add(lockType24);
            estLockTypes.add(lockType25);
            estLockTypes.add(lockType26);
        }
        return estLockTypes;
    }

    /**
     * 根据钥匙属性得到五防锁的名称
     *
     * @param keyProperty 钥匙属性
     * @return 五防锁的名称
     */
    public static String getLockName(Long keyProperty) {
        if (keyPropertyMap == null)
            initKeyPropertyMap();

        if (keyPropertyMap.containsKey(keyProperty))
            return keyPropertyMap.get(keyProperty);
        else
            return null;
    }

    public static String getLockNameEst(long lockType) {
        if (estLockTypes == null) {
            getEstLockType();
        }
        for (LockType lockTypeObj : estLockTypes) {
            if (lockTypeObj.value == lockType) {
                return lockTypeObj.typeName;
            }
        }
        return null;
    }

    /**
     * 根据钥匙属性得到五防锁的名称
     *
     * @param typeName 属性名称
     * @return keyProperty 钥匙属性
     */
    public static Long getLockType(String typeName) {
        if (keyPropertyMap == null)
            initKeyPropertyMap();

        if (keyPropertyMap.containsValue(typeName)) {
            for (Map.Entry entry : keyPropertyMap.entrySet()) {
                if (typeName.equals(entry.getValue())) {
                    return (Long) entry.getKey();
                }
            }
        }
        return 0l;
    }

    /**
     * 获取所有的value值
     *
     * @return
     */
    public static List<String> getAllTypeName() {
        if (keyPropertyMap == null)
            initKeyPropertyMap();

        List<String> types = new ArrayList<>();
        for (Map.Entry entry : keyPropertyMap.entrySet()) {
            types.add((String) entry.getValue());

        }
        return types;
    }
}