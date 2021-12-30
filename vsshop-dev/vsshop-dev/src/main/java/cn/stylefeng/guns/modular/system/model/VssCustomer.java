package cn.stylefeng.guns.modular.system.model;

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
 * @author stylefeng
 * @since 2019-05-26
 */
@TableName("vss_customer")
public class VssCustomer extends Model<VssCustomer> {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.INPUT)
    private String uuid;
    /**
     * 用户码
     */
    @TableField("customer_code")
    @Excel(name = "用户码", needMerge = true)
    private String customerCode;
    /**
     * 用户名
     */
    @Excel(name = "用户名", needMerge = true)
    private String name;
    /**
     * 密码
     */
    @Excel(name = "密码", needMerge = true)
    private String pwd;
    /**
     * 昵称
     */
    private String nick;
    /**
     * 邮箱
     */
    @Excel(name = "邮箱", needMerge = true)
    private String email;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 微信
     */
    private String wechat;
    /**
     * ip
     */
    private String ip;
    /**
     * 拥有者
     */
    @TableField("owner_name")
    @Excel(name = "拥有者", needMerge = true)
    private String ownerName;
    /**
     * 拥有者_uuid
     */
    @TableField("owner_uuid")
    private String ownerUuid;
    /**
     * 计划名称
     */
    @TableField("plan_name")
    @Excel(name = "计划名称", needMerge = true)
    private String planName;
    /**
     * 计划类型
     */
    @TableField("plan_uuid")
    private String planUuid;
    /**
     * 类型：1日；2月；3季度；4半年；5年；6终身；7次
     */
    @Excel(name = "计划类型", needMerge = true)
    private String type;
    /**
     * 状态：1:未使用，2已使用，3作废
     */
    @Excel(name = "状态", needMerge = true)
    private String status;
    /**
     * 创建时间
     */
    @TableField("insert_time")
    @Excel(name = "创建时间", needMerge = true,exportFormat = DatePattern.NORM_DATETIME_PATTERN )
    private Date insertTime;
    /**
     * 首次登陆时间
     */
    @TableField("first_time")
    @Excel(name = "用户激活时间", needMerge = true,exportFormat = DatePattern.NORM_DATETIME_PATTERN )
    private Date firstTime;
    /**
     * 失效时间
     */
    @TableField("expired_time")
    @Excel(name = "过期时间", needMerge = true,exportFormat = DatePattern.NORM_DATETIME_PATTERN )
    private Date expiredTime;
    /**
     * 最后时间
     */
    @TableField("last_time")
    @Excel(name = "最后更新时间", needMerge = true,exportFormat = DatePattern.NORM_DATETIME_PATTERN )
    private Date lastTime;
    /**
     * 最大下载量
     */
    @Excel(name = "最大下载量", needMerge = true)
    private Integer maxDown;
    /**
     * 已下载
     */
    @Excel(name = "已下载", needMerge = true )
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

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(String ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanUuid() {
        return planUuid;
    }

    public void setPlanUuid(String planUuid) {
        this.planUuid = planUuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Date firstTime) {
        this.firstTime = firstTime;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "VssCustomer{" +
        ", uuid=" + uuid +
        ", customerCode=" + customerCode +
        ", name=" + name +
        ", pwd=" + pwd +
        ", nick=" + nick +
        ", email=" + email +
        ", mobile=" + mobile +
        ", wechat=" + wechat +
        ", ip=" + ip +
        ", ownerName=" + ownerName +
        ", ownerUuid=" + ownerUuid +
        ", planName=" + planName +
        ", planUuid=" + planUuid +
        ", type=" + type +
        ", status=" + status +
        ", insertTime=" + insertTime +
        ", firstTime=" + firstTime +
        ", expiredTime=" + expiredTime +
        ", lastTime=" + lastTime +
        "}";
    }
}
