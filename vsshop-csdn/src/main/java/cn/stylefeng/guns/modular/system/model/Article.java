package cn.stylefeng.guns.modular.system.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wm
 * @since 2019-08-28
 */
@TableName("vip_article")
public class Article extends Model<Article> {

    private static final long serialVersionUID = 1L;

    private String uuid;
    /**
     * 激活码
     */
    private String code;
    /**
     * down_url，下载地址
     */
    @TableField("down_url")
    private String downUrl;
    /**
     * 1:未使用，2已使用，3作废
     */
    private String status;
    private String ip;
    private String email;
    /**
     * 1 正常，2，大积分
     */
    private String type;
    /**
     * 创建者
     */
    private String owner;
    /**
     * 创建时间
     */
    @TableField("insert_time")
    private Date insertTime;
    /**
     * 使用时间
     */
    @TableField("used_time")
    private Date usedTime;
    /**
     * 最后时间
     */
    @TableField("update_time")
    private Date updateTime;


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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "Article{" +
        ", uuid=" + uuid +
        ", code=" + code +
        ", downUrl=" + downUrl +
        ", status=" + status +
        ", ip=" + ip +
        ", email=" + email +
        ", type=" + type +
        ", owner=" + owner +
        ", insertTime=" + insertTime +
        ", usedTime=" + usedTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
