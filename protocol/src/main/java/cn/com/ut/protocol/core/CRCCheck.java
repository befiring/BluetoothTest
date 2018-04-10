package cn.com.ut.protocol.core;

/**
 * CRC校验类，获得或检查CRC的校验值
 * Created by zhangyihuang on 2016/12/22.
 */
public class CRCCheck {

    int bitCount = 0;
    int polynominal = 0;
    int initial = 0;
    int finalMask = 0x0;
    int crcLength = 2;
    int _headLength = 4;
    boolean shiftRight = false;
    protected CRCValue _crc = null;

    /**
     * 获取Crc字节长度
     */
    public int getCrcLength() {
        return crcLength;
    }

    /**
     * 设置Crc字节长度
     */
    public void setCrcLength(int crcLength) {
        this.crcLength = crcLength;
    }

    /**
     * 获取同步头长度
     */
    public int getHeadLength() {
        return _headLength;
    }

    /**
     * 设置同步头长度
     */
    public void setHeadLength(int headLength) {
        this._headLength = headLength;
    }

    /**
     * 实例化一个CRC校验对象,参数不能错误。
     *
     * @param bitCount    位长
     * @param polynominal 多项式
     * @param shiftRight  是否右移
     * @param initial     初始值
     * @param finalMask   掩码
     */
    public CRCCheck(int bitCount, int polynominal, boolean shiftRight, int initial, int finalMask) {
        this.bitCount = bitCount;
        this.polynominal = polynominal;
        this.shiftRight = shiftRight;
        this.initial = initial;
        this.finalMask = finalMask;

        _crc = new CRCValue(this.bitCount, this.polynominal, this.shiftRight, this.initial, this.finalMask);
    }

    /**
     * 对数据进行校验
     *
     * @param checkData 被校验的一个完整的帧
     * @param offset    完整帧在数据中的偏移量,去除同步头
     * @param length    长度,去除同步头含校验码本身
     */
    public boolean check(byte[] checkData, int offset, int length) {
        byte low, high;
        long ret32bit = _crc.getCrc(checkData, offset, length - 2);
        // 萃取CRC高、低位校验码
        low = (byte) (0xFF & (byte) ret32bit);
        high = (byte) (0xFF & (byte) (ret32bit >> 8));
        byte dLow = checkData[offset + length - 1];
        byte dHigh = checkData[offset + length - 2];
        return ((low == dLow) && (high == dHigh));
    }

    public void getCheckCode(byte[] checkData, int offset) {
        int length = checkData.length;
        HightLow hl = countCheckCode(checkData, offset);
        checkData[length - 2] = hl.getHi();
        checkData[length - 1] = hl.getLow();
    }

    private HightLow countCheckCode(byte[] checkData, int offset) {
        long ret32bit = _crc.getCrc(checkData, offset, (checkData.length - offset) - 2);
        // 萃取CRC高、低位校验码
        byte low = (byte) (0xFF & (byte) ret32bit);
        byte high = (byte) (0xFF & (byte) (ret32bit >> 8));

        return new HightLow(high, low);
    }

    public class HightLow {
        byte hi;
        byte low;

        public HightLow(byte h, byte l) {
            this.hi = h;
            this.low = l;
        }

        public byte getHi() {
            return hi;
        }

        public byte getLow() {
            return low;
        }
    }
}