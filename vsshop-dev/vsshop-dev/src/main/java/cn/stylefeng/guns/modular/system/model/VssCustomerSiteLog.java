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
@TableName("vss_customer_site_log")
public class VssCustomerSiteLog extends Model<VssCustomerSiteLog> {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.INPUT)
    private String uuid;
    /**
     * 客户uuid
     */
    @TableField("customer_uuid")
    private String customerUuid;
    /**
     * 网站uuid
     */
    @TableField("site_uuid")
    private String siteUuid;
    /**
     * 当天下载次数
     */
    private Integer dayDown;
    /**
     * 累计已使用积分
     */
    private Integer usedCoin;
    /**
     * 总积分
     */
    private Integer totalCoin;
    /**
     * 累计下载次数
     */
    private Integer downed;
    /**
     * 可用积分
     */
    private Integer validCoin;

    /**
     * 创建时间
     */
    @TableField("insert_time")
    private Date insertTime;
    /**
     * 最后时间
     */
    @TableField("last_time")
    private Date lastTime;

    public Integer getDayDown() {
        return dayDown;
    }

    public void setDayDown(Integer dayDown) {
        this.dayDown = dayDown;
    }

    public Integer getUsedCoin() {
        return usedCoin;
    }

    public void setUsedCoin(Integer usedCoin) {
        this.usedCoin = usedCoin;
    }

    public Integer getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(Integer totalCoin) {
        this.totalCoin = totalCoin;
    }

    public Integer getDowned() {
        return downed;
    }

    public void setDowned(Integer downed) {
        this.downed = downed;
    }

    public Integer getValidCoin() {
        return validCoin;
    }

    public void setValidCoin(Integer validCoin) {
        this.validCoin = validCoin;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCustomerUuid() {
        return customerUuid;
    }

    public void setCustomerUuid(String customerUuid) {
        this.customerUuid = customerUuid;
    }

    public String getSiteUuid() {
        return siteUuid;
    }

    public void setSiteUuid(String siteUuid) {
        this.siteUuid = siteUuid;
    }


    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
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
        return "VssCustomerSiteLog{" +
        ", uuid=" + uuid +
        ", customerUuid=" + customerUuid +
        ", siteUuid=" + siteUuid +
        ", insertTime=" + insertTime +
        ", lastTime=" + lastTime +
        "}";
    }
}
