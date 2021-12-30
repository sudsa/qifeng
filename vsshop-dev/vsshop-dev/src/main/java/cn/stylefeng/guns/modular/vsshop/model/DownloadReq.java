package cn.stylefeng.guns.modular.vsshop.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotations.TableField;

import javax.validation.constraints.NotBlank;

public class DownloadReq {

    @NotBlank(message = "请输入激活码")
    private String code;
    /**
     * down_url，下载地址
     */
    @NotBlank(message = "请输入下载地址")
    private String downUrl;

    private String email;

    @NotBlank(message = "网站来源不能为空")
    private String abbr;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }
}
