package cn.stylefeng.guns.modular.vsshop.model;

import javax.validation.constraints.NotBlank;

public class ReuseRequest {
    @NotBlank(message = "请输入下载地址")
    private String downloadurl;
    @NotBlank(message = "请输入账号")
    private String verifyCode;
    @NotBlank(message = "请输入密码")
    private String password;

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
