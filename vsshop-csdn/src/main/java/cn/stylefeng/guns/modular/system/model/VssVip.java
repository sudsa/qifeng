package cn.stylefeng.guns.modular.system.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.IdType;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.io.Serializable;

/**
 * <p>
 * 网站会员
 * </p>
 *
 * @author jianyinlin
 * @since 2019-04-12
 */
@TableName("vss_vip")
public class VssVip extends Model<VssVip> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 会员名称
     */
    private String name;
    /**
     * 描述
     */
    private String remark;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 库存
     */
    private Integer stock;
    /**
     * 链接
     */
    private String link;

    @TableField(exist = false)
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "VssVip{" +
        ", id=" + id +
        ", name=" + name +
        ", remark=" + remark +
        ", price=" + price +
        ", stock=" + stock +
        ", link=" + link +
        "}";
    }
}
