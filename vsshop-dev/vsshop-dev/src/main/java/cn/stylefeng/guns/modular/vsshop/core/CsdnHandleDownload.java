package cn.stylefeng.guns.modular.vsshop.core;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.stylefeng.guns.modular.system.model.VssBlack;
import cn.stylefeng.guns.modular.system.model.VssCode;
import cn.stylefeng.guns.modular.vsshop.common.AliEmailHelper;
import cn.stylefeng.guns.modular.vsshop.common.UrlSource;
import cn.stylefeng.guns.modular.vsshop.common.VssUserType;
import cn.stylefeng.guns.modular.vsshop.model.*;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.google.common.primitives.Bytes;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.coyote.http2.ByteUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CsdnHandleDownload extends BasicHandleDownload implements HandleDownload{
    @Autowired
    AliEmailHelper aliEmailHelper;

    private static Logger logger=LoggerFactory.getLogger(CsdnHandleDownload.class);
    public static final String siteAbbr= UrlSource.CSDN.name();

    private Integer iplimit=195;
    private Integer ipDownloadNormal=0;
    private Integer ipDownloadVip=0;
    private Date lastUpdate=new Date();



    @Override
    public Boolean login(HttpVssUser httpVssUser) {
        return null;
    }

    @Override
    public OperateResponse download(HttpVssUser httpVssUser, VssCode vssCode) throws Exception {
        if(DateUtil.beginOfDay(new Date()).getTime()>lastUpdate.getTime()){
            lastUpdate=new Date();
            ipDownloadVip=0;
            ipDownloadNormal=0;
        }
        if(ipDownloadNormal>=iplimit || ipDownloadVip>=iplimit){
            throw new Exception("服务器ip下载限制！");
        }

        OperateResponse result=new OperateResponse();
        String resouceUrl= getActualDownUrl(vssCode);
        Request.Builder builder=new Request.Builder();
        builder.url(resouceUrl);
        //https://download.csdn.net/index.php/mobile/source/do_download/9881373/icaoweiwei
        Map<String,String> map=new HashMap<>();
        map.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        map.put("Connection","keep-alive");
        map.put("Cookie",httpVssUser.getVssUser().getCookie());
        map.put("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16C104 CSDNApp/3.5.1(iOS)");
        map.put("Accept-Language","zh-cn");
        map.put("Referer",resouceUrl);
        map.put("Accept-Encoding","br,deflate");
        Headers headers=Headers.of(map);
        builder.headers(headers);
        byte[] bytes=null;
        try {
            //获取文件地址
            Request request=builder.build();
            Response response=http(httpVssUser,request);

            //下载文件
            String fileName=StrUtil.subBetween(response.headers().get("Content-Disposition"),"filename=\"","\"");
            Date date=new Date();
            String year=String.valueOf(DateUtil.year(date));
            String month=String.valueOf(DateUtil.month(date)+1);
            String day=String.valueOf(DateUtil.dayOfMonth(date));
            bytes=response.body().bytes();
            if(StrUtil.isEmpty(fileName)){
                String txtcontent = new String(bytes).replaceAll("</?[^>]+>", ""); //剔出<html>的标签
                txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符
                if(StrUtil.isBlank(txtcontent)){
                    throw new Exception("网络不佳，请等待30s后重试");
                }else if(txtcontent.contains("NoSuchKey The specified key does not exist")){
                    VssBlack vssBlack=new VssBlack();
                    vssBlack.setUuid(RandomUtil.randomString(32));
                    vssBlack.setDownUrl(vssCode.getDownUrl());
                    vssBlack.setInsertTime(new Date());
                    vssBlack.setReason(txtcontent);
                    vssBlackService.insert(vssBlack);
                    throw new Exception("该资源无法下载，请联系csdn官方人员！");
                }
                result.setSuccess(false);
                result.appendOutMessage(txtcontent);
                return result;
            }
            //文件名处理
            fileName= URLUtil.decode(fileName);
            fileName=fileName.replaceAll("#","");
            fileName=fileName.replaceAll("&","AND");
            fileName=fileName.replaceAll("\\?","");
            fileName=fileName.replaceAll("/","");
            fileName=fileName.replaceAll("\\\\","");
            fileName.replaceAll(" ","");
            if(fileName.substring(0,1).equals(".")){
                fileName=RandomUtil.randomString(6)+fileName;
            }
            if(fileName.length()>100){
                fileName=RandomUtil.randomString(6)+"."+StrUtil.subAfter(fileName,".",true);
            }
            FileUtil.writeBytes(bytes,rootPath+File.separator+"files"+File.separator+year+File.separator+month+File.separator+day+File.separator+fileName);
            result.setSuccess(true);
            result.appendOutMessage("files/"+siteAbbr+"/"+year+"/"+month+"/"+day+"/"+fileName);
            if(String.valueOf(VssUserType.NORMAL.ordinal()).equals(vssCode.getType())){
                ipDownloadNormal+=1;
            }else{
                ipDownloadVip+=1;
            }
            vssCode.setFileSize(response.body().contentLength());
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.appendOutMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DownloadInfo getDownloadInfo(String originUrl) {
        try {
            if(ReUtil.isMatch("https://download.csdn.net/index.php/mobile/source/download_info/([\\s\\S]*?)",originUrl)){
                DownloadInfo downloadInfo=new DownloadInfo();
                downloadInfo.setCoin("6");
                return downloadInfo;
            }
            //获取文件地址
            Document doc = Jsoup.connect(originUrl).get();
            String coin= doc.select(".dl_download_box").select("label").select("em").text();
            String title= doc.select(".download_dl").select("dd").select("h3").attr("title");
            DownloadInfo downloadInfo=new DownloadInfo();
            downloadInfo.setTitle(title);
            downloadInfo.setCoin(coin);
            return downloadInfo;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Boolean keepActive(HttpVssUser httpVssUser) {
        return null;
    }

    @Override
    public void checkUrl(DownloadReq downloadReq) throws Exception {

        String url=downloadReq.getDownUrl().trim();
        url=StrUtil.subBefore(url,"?",false);
        if(ReUtil.isMatch("https://download.csdn.net/index.php/mobile/source/download/([\\s\\S]*?)",url)){
            url=url.replaceAll("index.php/mobile/source/","");
        }
        downloadReq.setDownUrl(url);
        super.checkBlack(downloadReq.getDownUrl());
    }
    @Override
    public String getActualDownUrl(VssCode vssCode) throws Exception {
        if(vssCode.getType().equals(String.valueOf(VssUserType.NORMAL.ordinal()))){
            if(!vssCode.getDownUrl().matches("https://download.csdn.net/download/([\\s\\S]*?)/([\\s\\S]*?)$")){
                throw new Exception("请输入正确下载地址！如https://download.csdn.net/download/xx/xx");
            }
            Pattern pattern=Pattern.compile("https://download.csdn.net/download/([\\s\\S]*?)/([\\s\\S]*?)$");

            Matcher matcher=pattern.matcher(vssCode.getDownUrl());
            String uploadUser="";
            String resourceId="";
            if(matcher.find()){
                uploadUser=matcher.group(1);
                resourceId=matcher.group(2);
            }

            return String.format("https://download.csdn.net/index.php/mobile/source/do_download/%s/%s", resourceId,uploadUser);
        }else if(vssCode.getType().equals(String.valueOf(VssUserType.VIP.ordinal()))){

            if(ReUtil.isMatch("https://download.csdn.net/index.php/mobile/source/download_info/([\\s\\S]*?)",vssCode.getDownUrl())){
                String id=StrUtil.subAfter(vssCode.getDownUrl(),"https://download.csdn.net/index.php/mobile/source/download_info/",false);
                return String.format("https://download.csdn.net/index.php/mobile/source/download_client/%s", id);
            }
            if(!vssCode.getDownUrl().matches("https://download.csdn.net/download/([\\s\\S]*?)/([\\s\\S]*?)$")){
                throw new Exception("请输入正确下载地址！如https://download.csdn.net/download/xx/xx");
            }
            Pattern pattern=Pattern.compile("https://download.csdn.net/download/([\\s\\S]*?)/([\\s\\S]*?)$");

            Matcher matcher=pattern.matcher(vssCode.getDownUrl());
            String uploadUser="";
            String resourceId="";
            if(matcher.find()){
                uploadUser=matcher.group(1);
                resourceId=matcher.group(2);
            }

            return String.format("https://download.csdn.net/index.php/mobile/source/download_client/%s", resourceId);
        }else {
            throw new Exception("请输入正确下载地址！");
        }
    }

}
