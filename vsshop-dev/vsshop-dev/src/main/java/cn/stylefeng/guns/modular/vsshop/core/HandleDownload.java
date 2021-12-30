package cn.stylefeng.guns.modular.vsshop.core;

import cn.stylefeng.guns.modular.system.model.VssCode;
import cn.stylefeng.guns.modular.vsshop.model.DownloadInfo;
import cn.stylefeng.guns.modular.vsshop.model.DownloadReq;
import cn.stylefeng.guns.modular.vsshop.model.HttpVssUser;
import cn.stylefeng.guns.modular.vsshop.model.OperateResponse;

public interface HandleDownload {
    public Boolean login(HttpVssUser httpVssUser);
    public OperateResponse download(HttpVssUser httpVssUser, VssCode vssCode) throws Exception;
    public DownloadInfo getDownloadInfo(String originUrl);
    public Boolean keepActive(HttpVssUser httpVssUser);
    public void checkUrl(DownloadReq downloadReq) throws Exception;
    public String getActualDownUrl(VssCode vssCode) throws Exception;
}
