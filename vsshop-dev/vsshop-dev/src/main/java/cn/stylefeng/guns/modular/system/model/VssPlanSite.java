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
@TableName("vss_plan_site")
public class VssPlanSite extends Model<VssPlanSite> {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.INPUT)
    private String uuid;
    /**
     * 客户uuid
     */
    @TableField("plan_uuid")
    private String planUuid;
    /**
     * 网站uuid
     */

    private String siteUuid;
    /**
     * 每天下载次数限制
     */

    @TableField("day_down")
    private Integer dayDown;

    /**
     * 1;正常；2禁用
     */
    private String status;
    /**
     * 创建时间
     */
    @TableField("insert_time")
    private Date insertTime;

    //是否使用积分类型：1:是；2否
    private String isCoin;

    @TableField(exist = false)
    private Integer coin;

    /**
     * 是否大积分账号
     */
    @TableField(exist = false)
    private String isVip;

    public String getIsVip() {
        return isVip;
    }

    public void setIsVip(String isVip) {
        this.isVip = isVip;
    }

    //如果是积分类型：积分值
    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public String getIsCoin() {
        return isCoin;
    }

    public void setIsCoin(String isCoin) {
        this.isCoin = isCoin;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPlanUuid() {
        return planUuid;
    }

    public void setPlanUuid(String planUuid) {
        this.planUuid = planUuid;
    }

    public String getSiteUuid() {
        return siteUuid;
    }

    public void setSiteUuid(String siteUuid) {
        this.siteUuid = siteUuid;
    }

    public Integer getDayDown() {
        return dayDown;
    }

    public void setDayDown(Integer dayDown) {
        this.dayDown = dayDown;
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

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "VssPlanSite{" +
        ", uuid=" + uuid +
        ", planUuid=" + planUuid +
        ", siteUuid=" + siteUuid +
        ", status=" + status +
        ", insertTime=" + insertTime +
        "}";
    }
}
