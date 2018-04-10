package cn.com.ut.protocol;

import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 数据输入帮助类
 * Created by zhangyihuang on 2017/4/2.
 */
public class DataWriteUtils {
    public static final String APPNAME = "utznpw";
    public static final String LOGDIR = "log";

    /**
     * 写入字节
     *
     * @param outputStream
     * @param buf
     */
    public static void writeByte(ByteArrayOutputStream outputStream, byte buf) {
        outputStream.write(buf);
    }

    /**
     * 写入字节数组
     *
     * @param outputStream
     * @param buf
     */
    public static void writeBytes(ByteArrayOutputStream outputStream, byte[] buf) {
        try {
            outputStream.write(buf);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 写入字节数组
     *
     * @param outputStream
     * @param buf          the data.
     * @param off          the start offset in the data.
     * @param len          the number of bytes to write.
     */
    public static void writeBytes(ByteArrayOutputStream outputStream, byte[] buf, int off, int len) {
        try {
            outputStream.write(buf, off, len);
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 写入short类型值
     *
     * @param outputStream
     * @param val
     */
    public static void writeShort(ByteArrayOutputStream outputStream, short val) {
        writeByte(outputStream, (byte) ((0xff00 & val) >> 0x08));
        writeByte(outputStream, (byte) (0xff & val));
    }

    public static void writeShort(ByteArrayOutputStream outputStream, int val) {
        writeByte(outputStream, (byte) ((0xff00 & val) >> 0x08));
        writeByte(outputStream, (byte) (0xff & val));
    }

    /**
     * 写入Int类型值
     *
     * @param outputStream
     * @param val
     */
    public static void writeInt(ByteArrayOutputStream outputStream, int val) {
        writeByte(outputStream, (byte) ((0xff000000 & val) >> 0x18));
        writeByte(outputStream, (byte) ((0xff0000 & val) >> 0x10));
        writeByte(outputStream, (byte) ((0xff00 & val) >> 0x08));
        writeByte(outputStream, (byte) (0xff & val));
    }

    /**
     * 写入long类型值
     *
     * @param outputStream
     * @param val
     */
    public static void writeLong(ByteArrayOutputStream outputStream, long val) {
        writeByte(outputStream, (byte) ((val >> 0x38) & 0x0FF));
        writeByte(outputStream, (byte) ((val >> 0x30) & 0x0FF));
        writeByte(outputStream, (byte) ((val >> 0x28) & 0x0FF));
        writeByte(outputStream, (byte) ((val >> 0x20) & 0x0FF));
        writeByte(outputStream, (byte) ((val >> 0x18) & 0x0FF));
        writeByte(outputStream, (byte) ((val >> 0x10) & 0x0FF));
        writeByte(outputStream, (byte) ((val >> 0x08) & 0x0FF));
        writeByte(outputStream, (byte) (val & 0x0FF));
    }

    /**
     * 写入编码为gb2312的字符串
     *
     * @param outputStream 写入流
     * @param outputStr    写入字符串
     */
    public static void writeGb2312String(ByteArrayOutputStream outputStream, String outputStr, boolean appendZero) {
        try {
            if (outputStr == null || outputStr.isEmpty()) {
                return;
            }
            byte[] byteStationName = new String(outputStr).getBytes("gb2312");
            outputStream.write(byteStationName);
            if (appendZero) {
                outputStream.write(0x00);//结束符"00H"
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void writeString(ByteArrayOutputStream outputStream, String outputStr, boolean appendZero) {
        try {
            byte[] byteStationName = new String(outputStr).getBytes();
            outputStream.write(byteStationName);
            if (appendZero) {
                outputStream.write(0x00);//结束符"00H"
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 得到帧字节字符串
     *
     * @param data
     */
    public static String getFrameBytes(byte[] data) {
        int[] intData = new int[data.length];
        String bytesSrt = "[";
        for (int i = 0; i < data.length; i++) {
            if (data[i] >= 0) {
                intData[i] = data[i];
                if (intData[i] <= 0x0F) {
                    bytesSrt += String.format("0%s", Integer.toHexString(intData[i]).toUpperCase());
                } else {
                    bytesSrt += Integer.toHexString(intData[i]).toUpperCase();
                }
            } else {
                intData[i] = data[i] + 256;
                if (intData[i] <= 0x0F) {
                    bytesSrt += String.format("0%s", Integer.toHexString(intData[i]).toUpperCase());
                } else {
                    bytesSrt += Integer.toHexString(intData[i]).toUpperCase();
                }

            }
            bytesSrt += " ";
        }
        bytesSrt = bytesSrt.trim();
        bytesSrt += "]";
        return bytesSrt;
    }

    /**
     * 输出字节日志
     *
     * @param tag
     * @param bytesSrt
     */
    public static void printFrameBytes(String tag, String bytesSrt) {
        Log.w(tag, bytesSrt);
    }

    /**
     * 打印到日志文件
     *
     * @param dataStr
     */
    public static void writeBytesToFile(String dataStr) {
        String logPath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            logPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator
                    + APPNAME
                    + File.separator
                    + LOGDIR;

            File file = new File(logPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy_MM_dd");
                String date = dateFormater.format(new Date());
                String fileName = String.format("framebytes_%s.log", date);
                FileWriter fw = new FileWriter(logPath + File.separator + fileName, true);
                fw.write(new Date() + "\n");
                fw.write(dataStr + "\n");
                fw.write("\n");
                fw.close();
            } catch (IOException e) {
                Log.e("crash handler", "load file failed...", e.getCause());
            }
        }
    }
}