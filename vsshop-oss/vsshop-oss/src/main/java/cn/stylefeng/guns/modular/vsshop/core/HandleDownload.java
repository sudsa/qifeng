package cn.stylefeng.guns.modular.vsshop.core;

import cn.stylefeng.guns.modular.system.model.VssIp;
import cn.stylefeng.guns.modular.vsshop.model.ApiResponse;
import cn.stylefeng.guns.modular.vsshop.model.DownloadInfo;
import cn.stylefeng.guns.modular.vsshop.model.HttpVssUser;
import cn.stylefeng.guns.modular.vsshop.model.VssCode;

public interface HandleDownload {
    public ApiResponse apiDownload(VssCode vssCode);
    public ApiResponse checkDownload(VssCode vssCode, VssIp vssIp) throws Exception;
    public HttpVssUser getHttpVssUser(VssCode vssCode);
    public ApiResponse apiReuseDownload(VssCode vssCode);
    public Boolean login(HttpVssUser httpVssUser);
    public ApiResponse download(HttpVssUser httpVssUser, String resouceUrl, String name);
    public DownloadInfo getDownloadInfo(VssCode vssCode);
    public Boolean keepActive(HttpVssUser httpVssUser);
    public Integer getDayDownLimit();
}
