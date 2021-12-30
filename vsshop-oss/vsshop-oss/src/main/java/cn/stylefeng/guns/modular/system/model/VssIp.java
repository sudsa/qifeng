package cn.stylefeng.guns.modular.system.model;

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
 * @since 2019-06-30
 */
@TableName("vss_ip")
public class VssIp extends Model<VssIp> {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.INPUT)
    private String uuid;
    /**
     * ip
     */
    private String ip;
    /**
     * 区域id
     */
    @TableField("region_uuid")
    private String regionUuid;
    /**
     * 是否主服务器
     */
    private String master;

    /**
     * 状态；1正常；2禁用
     */
    @TableField("status")
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRegionUuid() {
        return regionUuid;
    }

    public void setRegionUuid(String regionUuid) {
        this.regionUuid = regionUuid;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "VssIp{" +
        ", uuid=" + uuid +
        ", ip=" + ip +
        ", regionUuid=" + regionUuid +
        ", master=" + master +
        "}";
    }
}
