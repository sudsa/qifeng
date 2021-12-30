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
 * @author stylefeng
 * @since 2019-05-26
 */
@TableName("vss_log")
public class VssLog extends Model<VssLog> {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    private String uuid;
    /**
     * 是否复用
     */
    private String reuse;
    /**
     * 激活码
     */
    private String code;
    /**
     * 需要积分
     */
    private Integer coin;
    /**
     * 网站名称
     */
    @TableField("site_name")
    private String siteName;
    /**
     * 客户计划
     */
    @TableField("customer_plan_name")
    private String customerPlanName;
    /**
     * 网站计划
     */
    @TableField("user_plan_name")
    private String userPlanName;
    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customerName;
    /**
     * 网站用户
     */
    @TableField("user_name")
    private String userName;

    /**
     * 拥有者
     */
    private String owner;
    /**
     * 下载地址
     */
    @TableField("down_url")
    private String downUrl;
    /**
     * 文件地址
     */
    @TableField("file_path")
    private String filePath;
    /**
     * ip
     */
    private String ip;
    /**
     * 记录时间
     */
    @TableField("insert_time")
    private Date insertTime;
    /**
     * 邮件地址
     */
    private String email;
    /**
     * 资源标题
     */
    private String name;

    /**
     * 文件大小
     */
    @TableField("file_size")
    private Integer fileSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
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

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getCustomerPlanName() {
        return customerPlanName;
    }

    public void setCustomerPlanName(String customerPlanName) {
        this.customerPlanName = customerPlanName;
    }

    public String getUserPlanName() {
        return userPlanName;
    }

    public void setUserPlanName(String userPlanName) {
        this.userPlanName = userPlanName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        ", siteName=" + siteName +
        ", customerPlanName=" + customerPlanName +
        ", userPlanName=" + userPlanName +
        ", customerName=" + customerName +
        ", userName=" + userName +
        ", owner=" + owner +
        ", downUrl=" + downUrl +
        ", filePath=" + filePath +
        ", ip=" + ip +
        ", insertTime=" + insertTime +
        ", email=" + email +
        "}";
    }
}
