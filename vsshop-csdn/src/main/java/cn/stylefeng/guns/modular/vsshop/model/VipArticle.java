package cn.stylefeng.guns.modular.vsshop.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@TableName("vip_article")
@Data
public class VipArticle extends Model<VipArticle> {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.INPUT)
    @Excel(name = "uuid", needMerge = true)
    private String uuid;

    @NotBlank(message = "请输入激活码")
    @Excel(name = "激活码", needMerge = true)
    private String code;
    /**
     * down_url，下载地址
     */
    @NotBlank(message = "请输入下载地址")
    @TableField("down_url")
    @Excel(name = "下载地址", needMerge = true)
    private String downUrl;
    /**
     * 1:未使用，2已使用，3作废
     */
    @Excel(name = "状态", needMerge = true)
    private String status;
    private String ip;
    private String email;
    private String owner;
    @Excel(name = "生成时间", needMerge = true,exportFormat = DatePattern.NORM_DATETIME_PATTERN )
    @TableField("insert_time")
    private Date insertTime;
    @Excel(name = "使用时间", needMerge = true,exportFormat =DatePattern.NORM_DATETIME_PATTERN)
    @TableField("used_time")
    private Date usedTime;
    @Excel(name = "更新时间", needMerge = true,exportFormat =DatePattern.NORM_DATETIME_PATTERN)
    @TableField("update_time")
    private Date updateTime;

    @TableField(exist = false)
    private String beginTime;

    @TableField(exist = false)
    private String endTime;

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }
}
