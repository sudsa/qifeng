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
@TableName("vss_site")
public class VssSite extends Model<VssSite> {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.INPUT)
    private String uuid;
    /**
     * 网站名称
     */
    private String name;
    /**
     * 缩写
     */
    private String abbr;
    /**
     * 域名
     */
    private String domain;
    /**
     * 1:正常；2禁用
     */
    private String status;
    /**
     * 每个ip每天下载次数
     */
    private Integer ipLimit;
    /**
     * 大积分界限
     */
    @TableField("big_coin_line")
    private Integer bigCoinLine;
    /**
     * 插入时间
     */
    @TableField("insert_time")
    private Date insertTime;

    public Integer getIpLimit() {
        return ipLimit;
    }

    public void setIpLimit(Integer ipLimit) {
        this.ipLimit = ipLimit;
    }

    public Integer getBigCoinLine() {
        return bigCoinLine;
    }

    public void setBigCoinLine(Integer bigCoinLine) {
        this.bigCoinLine = bigCoinLine;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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
        return "VssSite{" +
        ", uuid=" + uuid +
        ", name=" + name +
        ", abbr=" + abbr +
        ", domain=" + domain +
        ", status=" + status +
        ", insertTime=" + insertTime +
        "}";
    }
}
