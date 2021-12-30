package cn.stylefeng.guns.modular.system.model;

import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author stylefeng
 * @since 2019-05-26
 */
@TableName("vss_code")
public class VssCode extends Model<VssCode> {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.INPUT)
    @Excel(name = "uuid", needMerge = true)
    private String uuid;

    @NotBlank(message = "请输入激活码")
    @Excel(name = "激活码", needMerge = true)
    private String code;
    /**
     * down_url，下载地址
     */
    @NotBlank(message = "请输入下载地址")
    @TableField("down_url")
    @Excel(name = "下载地址", needMerge = true)
    private String downUrl;

    @TableField("file_path")
    @Excel(name = "文件地址", needMerge = true)
    private String filePath;

    @Excel(name = "积分", needMerge = true)
    private String coin;
    /**
     * 1:未使用，2已使用，3作废
     */
    @Excel(name = "状态", needMerge = true)
    private String status;
    private String name;
    /**
     * 1、normal,2,大积分
     */
    @Excel(name = "类型", needMerge = true)
    private String type;
    private String owner;
    /**
     * 1可用 2不可
     */
    private String reuseEnable;
    @Excel(name = "生成时间", needMerge = true,exportFormat =DatePattern.NORM_DATETIME_PATTERN )
    @TableField("insert_time")
    private Date insertTime;
    @Excel(name = "使用时间", needMerge = true,exportFormat =DatePattern.NORM_DATETIME_PATTERN)
    @TableField("used_time")
    private Date usedTime;
    @Excel(name = "更新时间", needMerge = true,exportFormat =DatePattern.NORM_DATETIME_PATTERN)
    @TableField("update_time")
    private Date updateTime;

    /**
     * 文件大小
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 用户uuid
     */
    @TableField("customer_uuid")
    private String customerUuid;

    /**
     * 网站uuid
     */
    @TableField("site_uuid")
    @Excel(name = "网站", needMerge = true)
    private String siteUuid;
    /**
     * 网站用户uuid
     */
    @TableField("user_uuid")
    private String userUuid;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCustomerUuid() {
        return customerUuid;
    }

    public void setCustomerUuid(String customerUuid) {
        this.customerUuid = customerUuid;
    }

    public String getReuseEnable() {
        return reuseEnable;
    }

    public void setReuseEnable(String reuseEnable) {
        this.reuseEnable = reuseEnable;
    }

    public String getSiteUuid() {
        return siteUuid;
    }

    public void setSiteUuid(String siteUuid) {
        this.siteUuid = siteUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Date usedTime) {
        this.usedTime = usedTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "VssCode{" +
        ", uuid=" + uuid +
        ", code=" + code +
        ", downUrl=" + downUrl +
        ", coin=" + coin +
        ", filePath=" + filePath +
        ", fileSize=" + fileSize +
        ", status=" + status +
        ", name=" + name +
        ", type=" + type +
        ", owner=" + owner +
        ", customerUuid=" + customerUuid +
        ", reuseEnable=" + reuseEnable +
        ", siteUuid=" + siteUuid +
        ", userUuid=" + userUuid +
        ", insertTime=" + insertTime +
        ", usedTime=" + usedTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
