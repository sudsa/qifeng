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
 * @since 2019-05-26
 */
@TableName("vss_user")
public class VssUser extends Model<VssUser> {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.INPUT)
    private String uuid;
    /**
     * 1.csdn
     */
    private String source;
    private String username;
    private String password;
    private String cookie;
    /**
     * 1登录成功，2登录失败
     */
    @TableField("login_status")
    private String loginStatus;
    /**
     * 1，正常，2禁用
     */
    private String status;
    /**
     * 计划uuid
     */
    @TableField("plan_uuid")
    private String planUuid;
    @TableField("last_login_time")
    private Date lastLoginTime;
    @TableField("insert_time")
    private Date insertTime;

    /**
     * 最大下载量
     */
    private Integer maxDown;
    /**
     * 已下载
     */
    private Integer downed;

    public Integer getMaxDown() {
        return maxDown;
    }

    public void setMaxDown(Integer maxDown) {
        this.maxDown = maxDown;
    }

    public Integer getDowned() {
        return downed;
    }

    public void setDowned(Integer downed) {
        this.downed = downed;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlanUuid() {
        return planUuid;
    }

    public void setPlanUuid(String planUuid) {
        this.planUuid = planUuid;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
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
        return "VssUser{" +
        ", uuid=" + uuid +
        ", source=" + source +
        ", username=" + username +
        ", password=" + password +
        ", cookie=" + cookie +
        ", loginStatus=" + loginStatus +
        ", status=" + status +
        ", planUuid=" + planUuid +
        ", lastLoginTime=" + lastLoginTime +
        ", insertTime=" + insertTime +
        "}";
    }
}
