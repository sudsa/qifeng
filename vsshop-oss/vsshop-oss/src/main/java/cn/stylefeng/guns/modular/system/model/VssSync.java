package cn.stylefeng.guns.modular.system.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author stylefeng
 * @since 2019-06-08
 */
@TableName("vss_sync")
public class VssSync extends Model<VssSync> {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.INPUT)
    private String uuid;
    /**
     * 下载地址
     */
    @TableField("down_url")
    private String downUrl;
    /**
     * 文件来源地址
     */
    @TableField("origin_file_path")
    private String originFilePath;
    /**
     * oss地址
     */
    @TableField("oss_file_path")
    private String ossFilePath;
    /**
     * 同步时间
     */
    @TableField("insert_time")
    private Date insertTime;
    /**
     * 同步来源
     */
    private String source;
    //1已同步到oss；2待同步；3失效
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getOriginFilePath() {
        return originFilePath;
    }

    public void setOriginFilePath(String originFilePath) {
        this.originFilePath = originFilePath;
    }

    public String getOssFilePath() {
        return ossFilePath;
    }

    public void setOssFilePath(String ossFilePath) {
        this.ossFilePath = ossFilePath;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "VssSync{" +
        ", uuid=" + uuid +
        ", downUrl=" + downUrl +
        ", originFilePath=" + originFilePath +
        ", ossFilePath=" + ossFilePath +
        ", insertTime=" + insertTime +
        ", source=" + source +
        "}";
    }
}
