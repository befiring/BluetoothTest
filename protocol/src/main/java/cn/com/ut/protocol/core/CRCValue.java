package cn.com.ut.protocol.core;

/**
 * CRC校验通用计算类，可计算8位、16位或32位CRC校验值
 * Created by zhangyihuang on 2016/12/22.
 */
public class CRCValue {
    final int MAXBYTEVALUES = 256;
    final int BITSPERBYTE = 8;

    int BITCOUNT;
    long POLYNOMINAL;
    boolean FREVERSE;
    long INITIAL;
    long FINALMASK;
    long mask;
    long crc_register;
    long[] values = new long[MAXBYTEVALUES];
    public final long[] bits = {
            0x00000001, 0x00000002, 0x00000004, 0x00000008,
            0x00000010, 0x00000020, 0x00000040, 0x00000080,
            0x00000100, 0x00000200, 0x00000400, 0x00000800,
            0x00001000, 0x00002000, 0x00004000, 0x00008000,
            0x00010000, 0x00020000, 0x00040000, 0x00080000,
            0x00100000, 0x00200000, 0x00400000, 0x00800000,
            0x01000000, 0x02000000, 0x04000000, 0x08000000,
            0x10000000, 0x20000000, 0x40000000, 0x80000000};

    /**
     * CRC校验计算
     *
     * @param bitCount    位数(8、16、32)
     * @param polynominal 多项式
     * @param shiftRight  是否右移
     * @param initial     初始值
     * @param finalMask   最终掩码
     */
    public CRCValue(int bitCount, int polynominal, boolean shiftRight,
                    int initial, int finalMask) {
        BITCOUNT = bitCount;
        POLYNOMINAL = polynominal;
        INITIAL = initial;
        FINALMASK = finalMask;
        // Mask needed to mask off redundent bits
        int l = 1;
        mask = ((l << (BITCOUNT - 1)) - 1) | (l << (BITCOUNT - 1));
        setREVERSE(shiftRight);        // Before set this property
        // mask must be calculated
    }

    boolean getREVERSE() {
        return FREVERSE;
    }

    void setREVERSE(boolean shiftRight) {
        FREVERSE = shiftRight;
        if (FREVERSE) {
            for (int ii = 0; ii < MAXBYTEVALUES; ++ii)
                values[ii] = reverseTableEntry(ii);
        } else {
            for (int ii = 0; ii < MAXBYTEVALUES; ++ii)
                values[ii] = forwardTableEntry(ii);
        }
    }

    /**
     * This function creates a CRC table entry for a reversed CRC function.
     *
     * @param entryindex entryindex: The index of the CRC table entry.
     * @return The value for the specified CRC table entry.
     */
    long reverseTableEntry(int entryindex) {
        long result = entryindex;
        for (int ii = 0; ii < BITSPERBYTE; ++ii) {
            if ((result & 1) == 0)
                result >>= 1;
            else
                result = (result >> 1) ^ reverse(POLYNOMINAL);
        }
        result = result & mask;
        return result;
    }

    /**
     * This function returns the reversed bit patter from its input.
     * For example, 1010 becomes 0101.
     *
     * @param value The value to reverse
     */
    long reverse(long value) {
        int result = 0;

        for (int jj = 0; jj < BITCOUNT; ++jj) {
            if ((value & bits[jj]) != 0)
                result |= bits[BITCOUNT - jj - 1];
        }
        return result;
    }

    /**
     * This function creates a CRC table entry for a non-reversed CRC function.
     *
     * @param entryindex entryindex: The index of the CRC table entry.
     * @return The value for the specified CRC table entry.
     */
    long forwardTableEntry(int entryindex) {
        long result = entryindex << (BITCOUNT - BITSPERBYTE);
        for (int ii = 0; ii < BITSPERBYTE; ++ii) {
            if ((result & bits[BITCOUNT - 1]) == 0)
                result <<= 1;
            else
                result = (result << 1) ^ POLYNOMINAL;
        }
        result = result & mask;
        return result;
    }

    void reset() {
        crc_register = INITIAL;
    }

    long value() {
        long result = crc_register ^ FINALMASK;
        result = result & mask;// result &= mask;
        return result;
    }

    void update(byte[] buffer, int length)//增加字节偏移量
    {
        update(buffer, 0, length);
    }

    /**
     * This function updates the value of the CRC register based upon
     * the contents of a buffer.
     *
     * @param buffer The input buffer
     * @param offset
     * @param length The length of the input buffer.
     *               The process for updating depends upon whether or not we are using
     *               the reversed CRC form.
     */
    void update(byte[] buffer, int offset, int length)//增加字节偏移量函数
    {
        if (getREVERSE()) {
            for (int ii = 0; ii < length; ++ii) {
                crc_register = values[(int) ((crc_register ^ buffer[ii + offset]) & 0xFF)]
                        ^ (crc_register >> 8);
            }
        } else {
            for (int ii = 0; ii < length; ++ii) {
                long index = ((crc_register >> (BITCOUNT - BITSPERBYTE)) ^ buffer[ii + offset]);
                crc_register = values[(int) (index & 0xFF)] ^ (crc_register << BITSPERBYTE);
            }
        }
    }

    public long getCrc32(byte[] buffer, int length) {
        reset();
        update(buffer, length);
        return value();
    }

    public long getCrc32(byte[] buffer, int offset, int length) {
        reset();
        update(buffer, offset, length);
        return value();
    }

    public long getCrc(byte[] buffer, int length) {
        return getCrc32(buffer, length);
    }

    public long getCrc(byte[] buffer, int offset, int length) {
        return getCrc32(buffer, offset, length);
    }

    public short getCrc16(byte[] buffer, int length) {
        return (short) getCrc32(buffer, length);
    }

    public byte getCrc8(byte[] buffer, int length) {
        return (byte) getCrc32(buffer, length);
    }
}