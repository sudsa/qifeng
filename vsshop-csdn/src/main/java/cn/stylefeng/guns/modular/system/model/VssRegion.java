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
@TableName("vss_region")
public class VssRegion extends Model<VssRegion> {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.INPUT)
    private String uuid;
    /**
     * 名称
     */
    private String name;
    /**
     * 城市列表，用逗号分隔。normal表示除特定外的城市
     */
    private String citys;
    /**
     * 域名
     */
    private String domain;
    /**
     * 状态；1正常；2禁用
     */
    @TableField("status")
    private String status;
    /**
     * 他域共享：不同区域共享;状态；1正常；2禁用
     */
    @TableField("other_share")
    private String otherShare;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCitys() {
        return citys;
    }

    public void setCitys(String citys) {
        this.citys = citys;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    public String getOtherShare() {
        return otherShare;
    }

    public void setOtherShare(String otherShare) {
        this.otherShare = otherShare;
    }

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "VssRegion{" +
        ", uuid=" + uuid +
        ", name=" + name +
        ", citys=" + citys +
        ", domain=" + domain +
        ", status=" + status +
        ", otherShare=" + otherShare +
        "}";
    }
}
