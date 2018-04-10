package cn.com.ut.protocol.model;

/**
 * Created by zhangyihuang on 2017/10/23.
 */
public class DNBS {
    /**
     * 1.定位  0xf6
     */
    public static final String DW = "246";

    /**
     * 2.GSN验电 0x51
     */
    public static final String YD_GSN = "81";

    /**
     * 3.GSN2验电 0x53
     */
    public static final String YD_GSN2 = "83";

    /**
     * 4.打开腕臂线地线 0x34
     */
    public static final String DKWBX = "52";

    /**
     * 5.检查腕臂线地线确已打开 0x35
     */
    public static final String JCWBX = "53";

    /**
     * 6.打开保护线地线 0x44
     */
    public static final String DKBHX = "68";

    /**
     * 7.检查保护线地线确已打开 0x45
     */
    public static final String JCBHX = "69";

    /**
     * 8.打开正馈线地线 0x54
     */
    public static final String DKZKX = "84";

    /**
     * 9.检查正馈线地线确已打开 0x55
     */
    public static final String JCZKX = "85";

    /**
     * 10.检查腕臂线地线确已拆下 0x37
     */
    public static final String JCWBX_C = "55";

    /**
     * 11.检查保护线地线确已拆下 0x47
     */
    public static final String JCBHX_C = "71";

    /**
     * 12.检查正馈线线地线确已拆下 0x57
     */
    public static final String JCZKX_C = "87";

    /**
     * 13.提示项 0x10
     */
    public static final String TSX = "16";

    /**
     * 14.GYD1D验电 0x56
     */
    public static final String GYD1D = "86";
}