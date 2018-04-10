package cn.com.ut.protocol.entity;

/**
 * 解锁器应用程序下载结果上报
 * Created by zhangyihuang on 2017/4/24.
 */
public class EstJsqDownloadResultsReport extends DataEntity {

    /**
     * 操作标志字=0x01表示数据下载成功(即底层CPU接收的数据校验和PC下载的检验一样)，
     * 如果为标志字为：0xFF则表示下载失败，检验不一致
     */
    public byte operationMark;

    public EstJsqDownloadResultsReport() {
        super();
        dataEntityType[0] = 0x00;
        dataEntityType[1] = 0x0E;
        dataContentLength = 0x01;
    }
}