package cn.com.ut.protocol.entity;

/**
 * 删除操作票命令数据实体
 * Created by zhangyihuang on 2017/4/24.
 */
public class EstDeleteTicket extends DataEntity {

    /**
     * 保留（8字节）
     */
    public byte[] retain = {0x00, 0x00, 0x00, 0x00};

    public EstDeleteTicket() {
        super();
        dataEntityType[0] = 0x00;
        dataEntityType[1] = 0x11;
        dataContentLength = 0x04;
    }
}