package cn.com.ut.protocol.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 自学数据
 * Created by zhangyihuang on 2017/3/30.
 */
public class EstKeyDataInfo {
    /**
     * 自学数据压缩包长度
     */
    private byte[] packageLength = {0x00, 0x00, 0x00};

    /**
     * 段标志 1字节
     */
    public byte partFlag;

    /**
     * 自学数据压缩算法 1字节
     */
    public byte compressAlgorithm;

    /**
     * 自学数据压缩包长度 3字节
     */
    public byte[] compressPackageLength;

    /**
     * 自学数据格式版本号 4字节
     */
    public int initDataFormatVersion;

    /**
     * 传出自学数据的设备ID 2字节
     */
    public short adapterId;

    /**
     * 传出自学数据设备类型描述 1字节
     */
    public byte deviceTypeDes;

    /**
     * 系统最大站号 1字节
     */
    public byte stationCount;

    /**
     * 系统最大设备号 2字节
     */
    public short maxDeviceId;

    /**
     * 锁编码个数 2字节
     */
    public short rfidCount;

    /**
     * 设备汉字编码表起始地址 2字节
     */
    public short deviceAddr;

    /**
     * 锁码区起始地址 4字节
     */
    public int rfidAddr;

    /**
     * 智能解锁关联表区起始地址 4字节
     */
    public int unlockAddr;

    /**
     * 设备所属站号表起始地址 4字节
     */
    public int belongAddr;

    /**
     * 锁编码所占的字节个数 1字节
     */
    public byte rfidBytes;

    /**
     * 确认人员身份编码表起始地址 4字节
     */
    public int personIdentityAddr;

    /**
     * 网络控制器地址描述表起始地址 4字节
     */
    public int networkAddr;

    /**
     * 偏移地址索引表起始地址 4字节
     */
    public int pyAddr;

    /////////////////////////////数据帧对象/////////////////////////////////////////
    /**
     * 站名描述区
     */
    public List<String> stationNameList;

    /**
     * 设备汉字编码表
     */
    public List<String> deviceCodeTable;

    /**
     * 锁码区(锁编码＋设备编号+属性类型)
     */
    public List<LockArea> lockAreas;

    /**
     * 设备所属站号表 1字节
     */
    public List<Byte> stationNoList;

    /**
     * 偏移地址索引表
     */
    public OffsetAddrTable offsetAddrTable;

    /**
     * 登陆人员信息描述表
     */
    public PersonInfoDesTable personInfoDesTable;

    /**
     * 人员与授权设备对应表
     */
    public PersonDevAuthorTable personDevAuthorTable;

    /**
     * 操作属性关联表
     * 操作属性低4位：0x00表示随意操作
     * 操作属性低4位：0x01表示授权操作
     * 操作属性低4位：0x02表示特许操作
     * 操作属性低4位：0x03表示强制验电闭锁操作
     * 操作属性高4位：0x00表示顺时针旋转开锁
     * 操作属性高4位：0x01表示逆时针旋转开锁
     */
    public List<Byte> operationPropertyTable;

    /**
     * 自学数据MD5码
     */
    public byte[] md5Buf;

    /**
     * 短信猫或短信平台号码
     */
    public String smsPhone;

    /*
    * 人员电话号码表
     */
    public UserPhoneTable userPhoneTable;

    /**
     * 强制验电闭锁前导项表
     */
    public ForcedLeadLockTable forcedLeadLockTable;

    /**
     * 远程输入输出装置配置表
     */
    public RemoteDeviceTable remoteDeviceTable;

    public EstKeyDataInfo() {
        partFlag = 0x00;//段标志
        compressAlgorithm = 0x00;//自学数据压缩算法
        compressPackageLength = packageLength;//自学数据压缩包长度
        rfidBytes = 0x06;//锁编码所占的字节个数
        personIdentityAddr = 0x00;//确认人员身份编码表起始地址
        networkAddr = 0x00;//网络控制器地址描述表起始地址
        offsetAddrTable = new OffsetAddrTable();
    }

    /**
     * 锁码区(锁编码＋设备编号+属性类型)
     * 按降序排列的锁编码
     */
    public class LockArea {
        public byte[] rfidCode;//锁编码 6字节
        public int matrix;//设备编号 2字节
        public byte rfidProperty;//属性类型 1字节
    }

    /**
     * 偏移地址索引表
     */
    public class OffsetAddrTable {
        public int deviceStatusTableAddr;//设备状态条目表起始地址（先高后低）
        public int lockTableTourAddr;//巡视锁码表起始地址（先高后低）
        public int selfTaughtDataVersionAddr;//自学数据版本起始地址（先高后低）
        public int repairLockTableAddr;//抢修锁码表起始地址（先高后低）
        public int landingPersonInfoDescTableAddr;//登陆人员信息描述表起始地址（先高后低）
        public int personAuthorizedDevicesTableAddr;// 人员与授权设备对应表起始地址（先高后低）
        public int operatePropertyAssociationTableAddr;//操作属性关联表起始地址（先高后低）
        public int selfLearnMD5Addr;//自学数据MD5码起始地址（先高后低）
        public int smsPlatformNumberAddr;//短信猫或短信平台号码起始地址（先高后低）
        public int personPhoneNumberAddr;//人员电话号码表起始地址（先高后低）
        public int mandatoryVerificationLockAddr;//强制验电闭锁前导项表起始地址(先高后低)
        //public int inputOutputDeviceConfigTableAddr;//输入输出装置配置表起始地址(先高后低)

        public OffsetAddrTable() {
            deviceStatusTableAddr = 0x00;//偏移地址索引表 设备状态条目表起始地址（先高后低）
            lockTableTourAddr = 0x00;//巡视锁码表起始地址（先高后低）
            selfTaughtDataVersionAddr = 0x00;//自学数据版本起始地址（先高后低）
            repairLockTableAddr = 0x00;//抢修锁码表起始地址（先高后低）
            landingPersonInfoDescTableAddr = 0x00;//登陆人员信息描述表起始地址（先高后低）
            personAuthorizedDevicesTableAddr = 0x00;//人员与授权设备对应表起始地址（先高后低）
            operatePropertyAssociationTableAddr = 0x00;//操作属性关联表起始地址（先高后低）
            selfLearnMD5Addr = 0x00;//自学数据MD5码起始地址（先高后低）
            smsPlatformNumberAddr = 0x00;//短信猫或短信平台号码起始地址（先高后低）
            personPhoneNumberAddr = 0x00;//人员电话号码表起始地址（先高后低）
            mandatoryVerificationLockAddr = 0x00;//强制验电闭锁前导项表起始地址（先高后低）
        }
    }

    /**
     * 登陆人员信息描述表
     * 每条登陆人员信息的格式如下：
     * 班组ID（4字节），班组名（16字节），姓名（12字节），密码（10字节），RFID码（12字节），权限（4字节），
     * 权限有效开始时间(12字节)，权限有效结束时间(12字节)，操作人ID（4个字节）0x0D 0x0A
     */
    public class PersonInfoDesTable {
        public int personInfoCount;//登陆人员信息条数N
        public List<PersonInfoDes> personInfoDesList;

        public PersonInfoDesTable() {
            personInfoDesList = new ArrayList<>();
        }

        public class PersonInfoDes {
            public int teamId; //班组ID （4字节）
            public String teamName; //班组名 （16字节）
            public String name; //姓名 （12字节）
            public String password; //密码 （10字节）
            public String rfidCode; //RFID码 （12字节）
            public String power; //权限 （4字节）
            public String validStartTime; //权限有效开始时间 (12字节)
            public String validEndTime; //权限有效结束时间 (12字节)
            public int operatorId; //操作人ID （4个字节）
        }
    }

    /**
     * 人员与授权设备对应表
     * 上表设备按编号升序排列，允许编号不连续
     */
    public class PersonDevAuthorTable {
        public int authorUserCount;//授权人员个数 2字节
        public int deviceCount;//设备个数 2字节
        public List<PersonDevAuthor> personDevAuthorList;

        public PersonDevAuthorTable() {
            personDevAuthorList = new ArrayList<>();
        }

        /**
         * 2字节设备编号＋n字节的授权人员矩阵码
         * 矩阵码长度=（授权人员个数+8-1）/8，每个bit
         * 表示一个人员是否对该设备授权，0表示未授权，
         * 1表示授权。不足8位的补0。
         * 这里的人员顺序按照人员信息描述表的顺序排列
         */
        public class PersonDevAuthor {
            public int deviceId;// 2字节设备编号
            public byte[] authorMatrixCode;//n字节的授权人员矩阵码
        }
    }

    /**
     * 人员电话号码表
     * 说明：电话号码按照人员信息描述表的顺序排列
     */
    public class UserPhoneTable {
        public int userPhoneCount;//人员电话号码条数N
        public List<PhoneExtend> phoneExtendList;

        /**
         * 电话号码及扩展属性信息格式：电话号码字符串，，，+“00H”
         */
        public class PhoneExtend {
            public String phone;//电话号码字符串：不定长的字符串
            public String extendProperty;//逗号：保留，以后用来扩展，两个逗号之间表示一个属性，共三个扩展属性

            public PhoneExtend() {
                extendProperty = ",,,";
            }
        }
    }

    /**
     * 强制验电闭锁前导项表
     * 按锁编码的降序排列
     * 同一锁编码允许有多个前导项锁编码，分别列出
     * 操作属性低四位为0x03表示强制验电闭锁操作的设备，必须在其所有前导项操作完毕的60秒内才可操作
     */
    public class ForcedLeadLockTable {
        public int preambleCount; //前导项表元素个数 没有前导项时个数为零 2字节
        public List<PreambleLock> preambleLockList;

        public ForcedLeadLockTable() {
            preambleLockList = new ArrayList<>();
        }

        /**
         * 锁编码＋其前导项锁编码
         */
        public class PreambleLock {
            public byte[] um;//锁编码
            public byte[] leadum;//其前导项锁编码
        }
    }

    /**
     * 远程输入输出装置配置表
     * 按设备编号的升序排列
     * 不同锁编码运行MAC地址相同，即一个控制器可以控制多个设备
     * 远程输入输出装置地址和MAC地址一一对应
     * 输出节点对应的动作：目前明确的有开、关、停三个动作，另外保留13个动作供以后使用。不用的输出节点默认为0x00
     * 输入节点依次对应：暂保留。不用的输入节点默认为0xff
     */
    public class RemoteDeviceTable {
        public byte[] sm4Buf; //SM4密钥数据 16字节
        public int remoteDeviceCount;//远程装置控制的设备个数 2字节
        public List<RemoteDevice> remoteDeviceList;

        /**
         * 控制器MAC如：8600510000000012
         * 远程输入输出装置的地址，当钥匙操作的装置地址与装置返回的装置地址不一致时，钥匙应给出提示
         * 输出节点对应的动作：开、关、停、另外13个字节保留，给其他动作使用
         * 输入节点依次对应：暂保留
         */
        public class RemoteDevice {
            public int deviceId;//设备编号（2Bytes）
            public long mac;//控制器MAC（8Bytes）
            public byte remoteAddr;//远程输入输出装置地址（1Bytes）
            public String outputNodeDefine;//输出节点定义（16Bytes）
            public String inputNodeDefine;//输入接点定义（16Bytes）
        }
    }
}