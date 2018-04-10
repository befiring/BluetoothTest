package cn.com.ut.protocol;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 钥匙数据转换类
 * Created by zhangyihuang on 2016/12/23.
 */
public class DataConvert {

    /**
     * String类型转成byte数组
     *
     * @param str 要转换的字符串
     * @return
     */
    public static byte[] stringToBytes(String str) {
        str = str.replace(" ", "");//去空格
        if (null == str || str.isEmpty())
            return null;

        int mod = str.length() % 2;
        if (mod > 0)
            str += "0";
        int length = str.length() / 2;

        byte[] buffer = new byte[length];
        for (int i = 0; i < length; i++) {
            int beginIndex = i * 2;
            int endIndex = beginIndex + 2;
            String strTemp = str.substring(beginIndex, endIndex);
            buffer[i] = (byte) Integer.parseInt(strTemp);
        }
        return buffer;
    }

    /**
     * 数组到整型，高位在前
     *
     * @param data   全帧数据
     * @param offset 偏移量
     * @return 全帧长度
     */
    public static int bytesToInt(byte[] data, int offset) {
        return ((data[0 + offset] & 0xff) << 0x18) + ((data[1 + offset] & 0xff) << 0x10) + ((data[2 + offset] & 0xff) << 8) + (data[3 + offset] & 0xff);
    }

    /**
     * 数组到整型，高位在前(左移8位)
     *
     * @param data   全帧数据
     * @param offset 偏移量
     * @return 全帧长度
     */
    public static int bytesToInt16(byte[] data, int offset) {
        return (data[0 + offset] << 8) + data[1 + offset];
    }

    /**
     * 整型到数组，高位在前(只留后两个字节)
     *
     * @param i
     * @param data   目标数组
     * @param offset 偏移量
     * @return 全帧长度
     */
    public static void int16ToBytes(int i, byte[] data, int offset) {
        data[0 + offset] = (byte) ((0xff00 & i) >> 8);
        data[1 + offset] = (byte) (0xff & i);
    }

    /**
     * 整型到数组，高位在前(只留后两个字节)
     *
     * @param i
     * @return
     */
    public static byte[] int16ToBytes(int i) {
        byte[] data = new byte[2];
        data[0] = (byte) ((0xff00 & i) >> 8);
        data[1] = (byte) (0xff & i);
        return data;
    }

    /**
     * 整型到数组，高位在前
     *
     * @param i
     * @return
     */
    public static byte[] intToBytes(int i) {
        byte[] data = new byte[4];
        data[0] = (byte) ((0xff000000 & i) >> 0x18);
        data[1] = (byte) ((0xff0000 & i) >> 0x10);
        data[2] = (byte) ((0xff00 & i) >> 0x08);
        data[3] = (byte) (0xff & i);
        return data;
    }

    /**
     * 整型到数组，高位在前
     *
     * @param i
     * @param data   目标数组
     * @param offset 偏移量
     */
    public static void intToBytes(int i, byte[] data, int offset) {
        data[0 + offset] = (byte) ((0xff000000 & i) >> 0x18);
        data[1 + offset] = (byte) ((0xff0000 & i) >> 0x10);
        data[2 + offset] = (byte) ((0xff00 & i) >> 8);
        data[3 + offset] = (byte) (0xff & i);
    }

    /**
     * byte数组，指定位长度转成16进制string类型
     *
     * @param bytes      byte数组
     * @param byteStart  指定数组起始位
     * @param byteLength 长度
     * @param split      分隔符
     * @return
     */
    public static String bytesToString(byte[] bytes, int byteStart, int byteLength, String split) {
        StringBuilder bytesString = new StringBuilder();
        bytesString.append("[");
        String s = "";

        if (byteLength > (bytes.length - byteStart)) {//长度超出数据的长度，防止数组越界
            byteLength = bytes.length - byteStart;
        }
        for (int i = 0; i < byteLength; i++) {
            s = byteToHexString(bytes[i + byteStart]);//转成十六进制
            bytesString.append(split + s);
        }
        bytesString.append("]");
        return bytesString.toString();
    }

    /**
     * 数组转long
     *
     * @param data
     * @param offset
     * @param count  几位
     * @return
     */
    public static long bytesToLong(byte[] data, int offset, int count) {
        long temp = 0L;
        for (int j = 0; j < count; j++) {
            temp <<= 8;
            temp |= (data[j + offset] & 0xff);
        }
        return temp;
    }

    public static long bytesToLong(byte[] data) {
        long num = 0;
        for (int j = 0; j < 8; ++j) {
            num <<= 8;
            num |= (data[j] & 0xff);
        }
        return num;
    }

    public static byte[] longToBytes(long num) {
        byte[] data = new byte[8];
        for (int j = 0; j < 8; ++j) {
            int offset = 64 - (j + 1) * 8;
            data[j] = (byte) ((num >> offset) & 0xff);
        }
        return data;
    }

    /**
     * 数组到整型，高位在前
     *
     * @param data
     * @return
     */
    public static int bytesToInt(byte[] data) {
        return bytesToInt(data, 0);
    }

    public static String byteToHexString(byte b) {
        String hs = "";
        String stmp = "";
        stmp = (Integer.toHexString(b & 0XFF));
        if (stmp.length() == 1)
            hs = "0" + stmp;
        else
            hs = stmp;
        return hs.toUpperCase();
    }

    /**
     * 码值转成字符串
     *
     * @param value
     * @return
     */
    public static String getRFIDString(long value) {
        //return Long.toHexString(value).toUpperCase();
        String rfidStr = Long.toHexString(value).toUpperCase();
        if (rfidStr.length() < 12) {
            int lenth = 12 - rfidStr.length();
            byte bytes[] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            String tem = bytesToHexString(bytes);
            tem = tem.substring(0, lenth);
            rfidStr = tem + rfidStr;
        }
        return rfidStr;
    }

    /**
     * 得到RFID
     *
     * @param rfid
     * @return
     */
    public static long getRfid(String rfid) {
        try {
            return Long.parseLong(rfid, 16);
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    /**
     * 得到6字节的RFID
     *
     * @param value rfid值
     * @return 6字节的RFID
     */
    public static byte[] getRfid(long value) {
        return getBytes(value, 6);
    }

    /**
     * long转byte[]
     *
     * @param value  值
     * @param length 要几位
     * @return
     */
    public static byte[] getBytes(long value, int length) {
        byte[] rfid = new byte[length];
        long temp = 0;
        for (int i = length - 1; i >= 0; i--) {
            temp = value >> ((length - 1 - i) * 8);
            rfid[i] = (byte) (0xff & temp);
        }
        return rfid;
    }


    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmssfffff");//设置日期格式
        return dateFormater.format(new Date());// 为获取当前系统时间
    }

    public static String getDateTime(String format) {
        SimpleDateFormat dateFormater = new SimpleDateFormat(format);
        return dateFormater.format(new Date());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;//有符号byte 转 无符号int
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

}