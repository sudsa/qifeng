package cn.stylefeng.guns.modular.vsshop.model;

import java.io.InputStream;
import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.date.DatePattern;
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
 * @author jianyinlin
 * @since 2019-03-13
 */
@TableName("vss_log")
public class VssLog extends Model<VssLog> {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.INPUT)
    @Excel(name = "uuid", needMerge = true)
    private String uuid;
    /**
     * 是否复用
     */
//    @Excel(name = "是否复用", needMerge = true)
    private String reuse;
    /**
     * 激活码
     */
    @Excel(name = "激活码", needMerge = true)
    private String code;
    /**
     * 需要积分
     */
    @Excel(name = "需要积分", needMerge = true)
    private Integer coin;
    /**
     * 网站
     */
    @Excel(name = "网站", needMerge = true)
    private String source;
    /**
     * 网站用户
     */
//    @Excel(name = "网站用户", needMerge = true)
    private String vssuser;

    /**
     * 拥有者
     */
    @Excel(name = "拥有者", needMerge = true)
    private String owner;
    /**
     * ip
     */
//    @Excel(name = "ip", needMerge = true)
    private String ip;


    private String region;
    /**
     * 下载地址
     */
    @TableField("down_url")
    @Excel(name = "下载地址", needMerge = true)
    private String downUrl;
    /**
     * 文件地址
     */
    @TableField("file_path")
    @Excel(name = "文件地址", needMerge = true)
    private String filePath;
    /**
     * 记录时间
     */
    @Excel(name = "下载时间", needMerge = true,exportFormat =DatePattern.NORM_DATETIME_PATTERN)
    @TableField("insert_time")
    private Date insertTime;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getReuse() {
        return reuse;
    }

    public void setReuse(String reuse) {
        this.reuse = reuse;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getVssuser() {
        return vssuser;
    }

    public void setVssuser(String vssuser) {
        this.vssuser = vssuser;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "VssLog{" +
        ", uuid=" + uuid +
        ", reuse=" + reuse +
        ", code=" + code +
        ", coin=" + coin +
        ", source=" + source +
        ", vssuser=" + vssuser +
        ", downUrl=" + downUrl +
        ", filePath=" + filePath +
        ", insertTime=" + insertTime +
        "}";
    }
}
