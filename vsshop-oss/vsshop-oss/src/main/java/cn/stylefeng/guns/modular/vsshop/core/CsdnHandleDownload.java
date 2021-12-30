package cn.stylefeng.guns.modular.vsshop.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.*;
import cn.stylefeng.guns.InitSpringBoot;
import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.guns.modular.system.model.VssApi;
import cn.stylefeng.guns.modular.system.model.VssIp;
import cn.stylefeng.guns.modular.system.model.VssRegion;
import cn.stylefeng.guns.modular.vsshop.VssSchedule;
import cn.stylefeng.guns.modular.vsshop.common.*;
import cn.stylefeng.guns.modular.vsshop.model.*;
import cn.stylefeng.guns.modular.vsshop.service.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CsdnHandleDownload extends BasicHandleDownload implements HandleDownload {

    private static Logger logger = LoggerFactory.getLogger(CsdnHandleDownload.class);
    @Autowired
    AliEmailHelper aliEmailHelper;
    @Autowired
    IVssUserService vssUserService;
    @Autowired
    IVssApiService vssApiService;
    @Autowired
    IVssIpService vssIpService;
    @Autowired
    IVssRegionService vssRegionService;
    @Autowired
    IVssCodeService vssCodeService;
    @Autowired
    OssHelper ossHelper;
    private static String acw_sc__v2 = "";
    private static String IP_UUID;
    CsdnHandleDownload(){

    }
    @Override
    public ApiResponse apiDownload(VssCode vssCode) {
        ApiResponse apiResponse = ApiResponse.fail("");
        String apiOpen = StrUtil.emptyToDefault(ConstantFactory.me().getDictsByName("别家api", 1), "close");
        if (!"open".equals(apiOpen)) {
            return apiResponse;
        }
        String apiUrl = ConstantFactory.me().getDictsByName("别家api", 2);
        String apiCode = ConstantFactory.me().getDictsByName("别家api", 3);
        try {
            Request.Builder builder = new Request.Builder();
            builder.header("ali-cdn-real-ip",vssCode.getIp());
            builder.url(String.format("%s?code=%s&downUrl=%s", apiUrl, apiCode, URLUtil.encode(vssCode.getDownUrl())));
            Request request = builder.build();
            Response response = OKHttpHelper.buildOKHttpClient().build().newCall(request).execute();
            String result = response.body().string();
            apiResponse = JSON.parseObject(result, ApiResponse.class);
        } catch (Exception e) {
            logger.error("错误",e);
        }
        return apiResponse;
    }

    @Override
    public ApiResponse checkDownload(VssCode vssCode,VssIp vssIp) throws Exception {
        ApiResponse apiResponse = ApiResponse.fail("");

        IP_UUID=vssIp.getUuid();
        boolean isMaster="1".equals(vssIp.getMaster());
        if (isMaster) {
            //获取区域
            String region=checkBeiJing(vssCode);
            if(StrUtil.isEmpty(region)){
                logger.error("ip获取失败！");
                throw new Exception("服务器出错");
            }
            vssCode.setRegion(region);
            EntityWrapper regionEntity=new EntityWrapper();
            regionEntity.like("citys",region);
            //判断是否正常区域
            boolean isNormal=(vssRegionService.selectCount(regionEntity)==0);
            VssRegion vssRegion;
            if(!isNormal){
                regionEntity.eq("status","1");
                 vssRegion=vssRegionService.selectOne(regionEntity);
            }else{
                regionEntity=new EntityWrapper();
                regionEntity.eq("citys","normal");
                regionEntity.eq("status","1");
                vssRegion=vssRegionService.selectOne(regionEntity);
            }
            //获取共享区域
            if(vssRegion==null){
                regionEntity=new EntityWrapper();
                regionEntity.eq("status","1");
                regionEntity.eq("other_share","1");
                vssRegion=vssRegionService.selectOne(regionEntity);
                if(vssRegion==null){
                    logger.error(String.format("不存在该区域！%s: %s", region,vssCode.getIp()));
                    throw new Exception("服务器出错");
                }
            }
            try {
                Request.Builder builder = new Request.Builder();
                builder.header("ali-cdn-real-ip",vssCode.getIp());
                builder.url(String.format("http://%s/api/getDownloadFile?code=%s&downUrl=%s", vssRegion.getDomain(),vssCode.getCode(), URLUtil.encode(vssCode.getDownUrl())));
                Request request = builder.build();
                Response response = OKHttpHelper.buildOKHttpClient().build().newCall(request).execute();
                String result = response.body().string();
                apiResponse = JSON.parseObject(result, ApiResponse.class);
                if (apiResponse.getStatus() == 1) {
                    return apiResponse;
                } else {
                    logger.error((String) apiResponse.getResult());
                    SingleSendMailRequest email=new SingleSendMailRequest();
                    email.setToAddress(ossHelper.getEmail());
                    email.setHtmlBody(String.format("CSDN下载调用失败：%s,网址：%s",(String) apiResponse.getResult(),vssRegion.getDomain()));
                    email.setSubject("CSDN下载调用失败");
                    aliEmailHelper.sendDown(email);
                    throw new Exception("下载调用失败！");
                }
            } catch (Exception e) {
                logger.error("错误",e);
                SingleSendMailRequest email=new SingleSendMailRequest();
                email.setToAddress(ossHelper.getEmail());
                email.setHtmlBody(String.format("CSDN下载调用失败：%s,网址：%s",(String) apiResponse.getResult(),vssRegion.getDomain()));
                email.setSubject("CSDN下载调用失败");
                aliEmailHelper.sendDown(email);
                throw new Exception("下载调用失败！");
            }
        }
        return apiResponse;
    }

    private String checkBeiJing(VssCode vssCode) throws Exception {
        if (StrUtil.isEmpty(vssCode.getIp())) {
            return "柘荣";
        }
        try {
            Request.Builder builder = new Request.Builder();
            builder.header("Authorization","APPCODE 5ba0f0c2776e4662b24926ae46ac81b2");
            builder.url(String.format("http://ipquery.market.alicloudapi.com/query?ip=%s", vssCode.getIp()));
            Request request = builder.build();
            Response response = OKHttpHelper.buildOKHttpClient().build().newCall(request).execute();
            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
            String region= jsonObject.getJSONObject("data").getString("city");
            String prov= jsonObject.getJSONObject("data").getString("prov");
            region=StrUtil.isBlank(region)?prov:region;
            if(StrUtil.isNotBlank(region)){
                return region;
            }
        } catch (Exception e) {
            logger.error("错误",e);

        }
        return "柘荣";
    }

    /**
     * 随机获取下载用户
     * .
     *
     * @return
     */
    @Override
    public HttpVssUser getHttpVssUser(VssCode vssCode) {

        if (String.valueOf(VssUserType.NORMAL.ordinal()).equals(vssCode.getType())) {
            List<VssUser> vssUserList = vssUserService.getNormalCoinUser(IP_UUID, String.valueOf(UrlSource.CSDN.ordinal()), Integer.parseInt(vssCode.getCoin()));
            if (CollUtil.isEmpty(vssUserList)) {
                //如果普通用完，用vip
                vssCode.setType(String.valueOf(VssUserType.VIP.ordinal()));
                vssUserList = vssUserService.getVipCoinUser(IP_UUID, String.valueOf(UrlSource.CSDN.ordinal()));
                if (CollUtil.isNotEmpty(vssUserList)) {
                    return new HttpVssUser(OKHttpHelper.buildOKHttpClient().build(), vssUserList.get(RandomUtil.randomInt(vssUserList.size())));
                }
            } else {
                return new HttpVssUser(OKHttpHelper.buildOKHttpClient().build(), vssUserList.get(RandomUtil.randomInt(vssUserList.size())));

            }
        } else if (String.valueOf(VssUserType.VIP.ordinal()).equals(vssCode.getType())) {
            List<VssUser> vssUserList = vssUserService.getVipCoinUser(IP_UUID, String.valueOf(UrlSource.CSDN.ordinal()));
            if (CollUtil.isNotEmpty(vssUserList)) {
                return new HttpVssUser(OKHttpHelper.buildOKHttpClient().build(), vssUserList.get(RandomUtil.randomInt(vssUserList.size())));
            }
        }
        InitSpringBoot.SERVICE_ENABLE=false;
        return null;
    }

    @Override
    public ApiResponse apiReuseDownload(VssCode vssCode) {
//                String proxyHost = "127.0.0.1";
//        String proxyPort = "1080";
//
//        System.setProperty("http.proxyHost", proxyHost);
//        System.setProperty("http.proxyPort", proxyPort);
//
//// 对https也开启代理
//        System.setProperty("https.proxyHost", proxyHost);
//        System.setProperty("https.proxyPort", proxyPort);
        ApiResponse apiResponse = ApiResponse.fail("");
        String apiReuseUrl = StrUtil.emptyToDefault(ConstantFactory.me().getDictsByName("api复用", 1), "http://58.87.110.4/cosapi");
        String apiReuseUser = StrUtil.emptyToDefault(ConstantFactory.me().getDictsByName("api复用", 2), "test");
        String apiReusePwd = StrUtil.emptyToDefault(ConstantFactory.me().getDictsByName("api复用", 3), "123456");
        Request.Builder builder = new Request.Builder();
        builder.url(apiReuseUrl);
        try {
            Map<String, String> params = new HashMap<>();
            params.put("downloadurl", vssCode.getDownUrl());
            params.put("verifyCode", apiReuseUser);
            params.put("password", apiReusePwd);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(params));
            Map<String, String> map = new HashMap<>();
            map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            map.put("Connection", "keep-alive");
            map.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16C104 CSDNApp/3.5.1(iOS)");
            map.put("Accept-Language", "zh-cn");
            map.put("Accept-Encoding", "br,deflate");
            Headers headers = Headers.of(map);
            builder.headers(headers);
            Request request = builder.post(requestBody).build();
            Response response = OKHttpHelper.buildOKHttpClient().followRedirects(true).build().newCall(request).execute();
            String result = response.body().string();
            logger.info(result);
            JSONObject jsonObject = JSON.parseObject(result);
            String returnCode = jsonObject.getString("code");
            if ("5".equals(returnCode) || "a".equals(returnCode)) {
                apiResponse.setStatus(1);
                apiResponse.setMsg("success");
                apiResponse.setResult(jsonObject.getString("cos"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Request.Builder builder = new Request.Builder();
                            Request request = builder.url((String) apiResponse.getResult()).build();
                            Response response = OKHttpHelper.buildOKHttpClient().followRedirects(true).build().newCall(request).execute();
                            //下载文件
                            String fileName="";
                            List<String> urls=response.request().url().pathSegments();
                            if(urls.size()>1){
                                fileName=response.request().url().pathSegments().get(1);
                            }else{
                                fileName = StrUtil.subAfter(response.headers().get("Content-Disposition"), "filename=",false);
                            }

                            //本地文件
                            if("returnfile".equalsIgnoreCase(fileName)){
                                fileName = StrUtil.subAfter(response.headers().get("Content-Disposition"), "UTF-8''",false);
                            }
                            if (StrUtil.isEmpty(fileName)) {
                                SingleSendMailRequest email=new SingleSendMailRequest();
                                email.setToAddress(ossHelper.getEmail());
                                email.setHtmlBody(String.format("小助手api文件地址 %s 下载失败, 激活码：%s,下载源地址：%s,拥有者：%s",(String) apiResponse.getResult(),vssCode.getCode(),vssCode.getDownUrl(),vssCode.getOwner()));
                                email.setSubject("CSDN小助手调用失败");
                                aliEmailHelper.sendDown(email);
                                EntityWrapper entityWrapper=new EntityWrapper();
                                entityWrapper.eq("code",vssCode.getCode());
                                VssCode vssCodeTemp=vssCodeService.selectOne(entityWrapper);
                                vssCodeTemp.setStatus("1");
                                vssCodeTemp.setDownUrl("");
                                vssCodeTemp.setFilePath("");
                                vssCodeService.updateById(vssCodeTemp);
                            } else {
                                //文件名处理
                                fileName = URLUtil.decode(fileName);
                                fileName = fileName.replaceAll("#", "");
                                fileName = fileName.replaceAll("&", "AND");
                                fileName = fileName.replaceAll("\\?", "");
                                fileName = fileName.replaceAll("/", "");
                                fileName = fileName.replaceAll("\\\\", "");
                                fileName = fileName.replaceAll("\\+", "");
                                fileName = fileName.replaceAll(" ", "");
                                if (fileName.substring(0, 1).equals(".")) {
                                    fileName = RandomUtil.randomString(6) + fileName;
                                }
                                if (fileName.length() > 100) {
                                    fileName = RandomUtil.randomString(6) + "." + StrUtil.subAfter(fileName, ".", true);
                                }
                                logger.info(fileName);
                                Pattern pattern = Pattern.compile("https://download.csdn.net/download/([\\s\\S]*?)/([\\s\\S]*?)$");

                                Matcher matcher = pattern.matcher(vssCode.getDownUrl());
                                String uploadUser = "user";
                                String resourceId = "id";
                                if (matcher.find()) {
                                    uploadUser = matcher.group(1);
                                    resourceId = matcher.group(2);
                                }
                                String filePath = "files" + File.separator + "csdn/api" + File.separator + uploadUser + "_" + resourceId + File.separator + fileName;
                                InputStream inputStream = response.body().byteStream();
                                OSSClient ossClient=ossHelper.getOssClient();
                                ossClient.putObject(ossHelper.getBucket(), filePath, inputStream);
                                inputStream.close();
                                response.close();
                                ossClient.shutdown();
                                EntityWrapper entityWrapper=new EntityWrapper();
                                entityWrapper.eq("code",vssCode.getCode());
                                VssCode vssCodeTemp=vssCodeService.selectOne(entityWrapper);

                                vssCodeTemp.setDownUrl(vssCode.getDownUrl());
                                vssCodeTemp.setFilePath(String.format("http://down.%s/%s", ossHelper.getResourceDomain(),filePath));
                                vssCodeTemp.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                                vssCodeTemp.setUsedTime(new Timestamp(System.currentTimeMillis()));
                                vssCodeTemp.setStatus(String.valueOf(VssCodeStatus.USED.ordinal()));
                                vssCodeTemp.setReuseEnable("1");
                                vssCodeTemp.setEmail(vssCode.getEmail());
                                vssCodeService.updateById(vssCodeTemp);
                            }
                        } catch (Exception e) {
                            logger.error("小助手文件上传oss错误",e);
                            EntityWrapper entityWrapper=new EntityWrapper();
                            entityWrapper.eq("code",vssCode.getCode());
                            VssCode vssCodeTemp=vssCodeService.selectOne(entityWrapper);
                            vssCodeTemp.setStatus("1");
                            vssCodeTemp.setDownUrl("");
                            vssCodeTemp.setFilePath("");
                            vssCodeService.updateById(vssCodeTemp);
                        }
                    }
                }).start();
            } else {
                apiResponse.setStatus(-1);
                apiResponse.setMsg(jsonObject.toJSONString());
                apiResponse.setResult(null);
            }
        } catch (Exception e) {
            apiResponse.setStatus(-1);
            apiResponse.setMsg(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return apiResponse;
    }

    @Override
    public Boolean login(HttpVssUser httpVssUser) {

        return null;
    }

    @Override
    public ApiResponse download(HttpVssUser httpVssUser, String resouceUrl, String name) {
        ApiResponse apiResponse = ApiResponse.fail("");
        Request.Builder builder = new Request.Builder();
        builder.url(resouceUrl);
        //https://download.csdn.net/index.php/mobile/source/do_download/9881373/icaoweiwei
        Map<String, String> map = new HashMap<>();
        map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        map.put("Connection", "keep-alive");
        map.put("Cookie", cookieHandle(httpVssUser.getVssUser().getCookie()));
        map.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16C104 CSDNApp/3.5.1(iOS)");
        map.put("Accept-Language", "zh-cn");
        map.put("Referer", resouceUrl);
        map.put("Accept-Encoding", "br,deflate");
        Headers headers = Headers.of(map);
        builder.headers(headers);
        try {
            //获取文件地址
            Request request = builder.build();
            Response response = http(httpVssUser, request);

            //下载文件
            String fileName = StrUtil.subBetween(response.headers().get("Content-Disposition"), "filename=\"", "\"");
            Date date = new Date();
            String year = String.valueOf(DateUtil.year(date));
            String month = String.valueOf(DateUtil.month(date) + 1);
            String day = String.valueOf(DateUtil.dayOfMonth(date));

            if (StrUtil.isEmpty(fileName)) {
                String result = response.body().string();
                if (result.contains("每位用户每天最多可下载20个资源")) {
                    httpVssUser.getVssUser().setDayDown(20);
                    vssUserService.updateById(httpVssUser.getVssUser());
                }
                if (result.contains("window.location.href=\"https://passport.csdn.net/account/login?from=")) {
                    httpVssUser.getVssUser().setDayDown(20);
                    httpVssUser.getVssUser().setStatus("2");
                    vssUserService.updateById(httpVssUser.getVssUser());
                }
                if (result.contains("每个IP每天最多可下载200个资源")) {
                    InitSpringBoot.SERVICE_ENABLE=false;
                    SingleSendMailRequest email=new SingleSendMailRequest();
                    email.setToAddress(ossHelper.getEmail());
                    email.setHtmlBody(String.format("报警：ip: %s,下载达200次，已下线处理",InitSpringBoot.LOCAL_IP));
                    email.setSubject("CSDN服务器下载达到200次");
                    aliEmailHelper.sendDown(email);
                }
                logger.error(result);
                String txtcontent = result.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
                txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符
                apiResponse.setMsg(txtcontent);
                return apiResponse;
            }
            //文件名处理
            fileName = URLUtil.decode(fileName);
            fileName = fileName.replaceAll("#", "");
            fileName = fileName.replaceAll("&", "AND");
            fileName = fileName.replaceAll("\\?", "");
            fileName = fileName.replaceAll("/", "");
            fileName = fileName.replaceAll("\\\\", "");
            fileName = fileName.replaceAll("\\+", "");
            fileName = fileName.replaceAll(" ", "");
            if (fileName.substring(0, 1).equals(".")) {
                fileName = RandomUtil.randomString(6) + fileName;
            }
            if (fileName.length() > 100) {
                fileName = RandomUtil.randomString(6) + "." + StrUtil.subAfter(fileName, ".", true);
            }
            name = name.toLowerCase();
            OSSClient ossClient=ossHelper.getOssClient();
            String filePath = "files" + File.separator + name + File.separator + year + File.separator + month + File.separator + day + File.separator + fileName;
            ossClient.putObject(ossHelper.getBucket(), filePath, response.body().byteStream());
            // 关闭OSSClient。
            ossClient.shutdown();
            apiResponse.setResult(String.format("http://down.%s/%s", ossHelper.getResourceDomain(),filePath));
            apiResponse.setStatus(1);
            apiResponse.setMsg("success");
            return apiResponse;
        } catch (Exception e) {
            logger.error("错误",e);
            apiResponse.setMsg(e.getMessage());
            return apiResponse;
        }
    }

    @Override
    public DownloadInfo getDownloadInfo(VssCode vssCode) {
        String originUrl = vssCode.getDownUrl();
        VssUser vssUser = new VssUser();
        vssUser.setCookie("");
        HttpVssUser httpVssUser = new HttpVssUser(OKHttpHelper.buildOKHttpClient().build(), vssUser);
//        String proxyHost = "127.0.0.1";
//        String proxyPort = "8888";
//
//        System.setProperty("http.proxyHost", proxyHost);
//        System.setProperty("http.proxyPort", proxyPort);
//
//// 对https也开启代理
//        System.setProperty("https.proxyHost", proxyHost);
//        System.setProperty("https.proxyPort", proxyPort);
        try {
            if (ReUtil.isMatch("https://download.csdn.net/index.php/mobile/source/download_info/([\\s\\S]*?)", originUrl)) {
                DownloadInfo downloadInfo = new DownloadInfo();
                downloadInfo.setCoin("6");
                return downloadInfo;
            }
            Request.Builder builder = new Request.Builder();
            builder.url(originUrl);
            //https://download.csdn.net/index.php/mobile/source/do_download/9881373/icaoweiwei
            Map<String, String> map = new HashMap<>();
            map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            map.put("Connection", "keep-alive");
            map.put("Cookie", cookieHandle(httpVssUser.getVssUser().getCookie()));
            map.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            map.put("Accept-Language", "zh-cn");
            map.put("Referer", originUrl);
            map.put("Accept-Encoding", "gzip,deflate");
            Headers headers = Headers.of(map);

            builder.headers(headers);

            //获取文件地址
            Request request = builder.build();
            Response response = http(httpVssUser, request);
            byte[] bytes = responseHandle(response, httpVssUser);
            //获取文件地址
            String result = new String(ZipUtil.unGzip(bytes));
            //
            Document doc = Jsoup.parse(result);
            ;
            String coin = doc.select(".dl_download_box").select("label").select("em").text();
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setCoin(coin);
            return downloadInfo;
        } catch (Exception e) {
            logger.error("错误",e);
        }
        return null;
    }

    @Override
    public Boolean keepActive(HttpVssUser httpVssUser) {
        return null;
    }

    @Override
    public Integer getDayDownLimit() {
        return 19;
    }


    public String getACW2(String arg1) {
        String key = "3000176000856006061501533003690027800375";
        String _0x23a392 = unsbox(arg1);
        String arg2 = hexXor(key, _0x23a392);
        return arg2;
    }

    public String hexXor(String _0x4e08d8, String _0x23a392) {
        String _0x5a5d3b = "";
        int _0xe89588 = 0x0;
        while (_0xe89588 < _0x23a392.length() && _0xe89588 < _0x4e08d8.length()) {
            int _0x401af1 = Integer.parseInt(_0x23a392.substring(_0xe89588, _0xe89588 + 0x2), 16);
            int _0x105f59 = Integer.parseInt(_0x4e08d8.substring(_0xe89588, _0xe89588 + 0x2), 16);
            String _0x189e2c = Integer.toHexString(_0x401af1 ^ _0x105f59);
            if (_0x189e2c.length() == 1) {
                _0x189e2c = "0" + _0x189e2c;
            }
            _0x5a5d3b += _0x189e2c;
            _0xe89588 += 0x2;
        }
        return _0x5a5d3b;

    }

    public String unsbox(String arg) {
        Integer _0x4b082b[] = {0xf, 0x23, 0x1d, 0x18, 0x21, 0x10, 0x1, 0x26, 0xa, 0x9, 0x13, 0x1f, 0x28, 0x1b, 0x16, 0x17, 0x19, 0xd,
                0x6, 0xb, 0x27, 0x12, 0x14, 0x8, 0xe, 0x15, 0x20, 0x1a, 0x2, 0x1e, 0x7, 0x4, 0x11, 0x5, 0x3, 0x1c,
                0x22, 0x25, 0xc, 0x24};
        String _0x4da0dc[] = new String[40];
        String _0x12605e = "";

        for (int _0x20a7bf = 0; _0x20a7bf < arg.length(); _0x20a7bf++) {
            String _0x385ee3 = arg.substring(_0x20a7bf, _0x20a7bf + 1);
            for (int _0x217721 = 0; _0x217721 < arg.length(); _0x217721++) {
                if (_0x4b082b[_0x217721] == _0x20a7bf + 0x1) {
                    _0x4da0dc[_0x217721] = _0x385ee3;
                }
            }
        }
        _0x12605e = CollUtil.join(Arrays.asList(_0x4da0dc), "");

        return _0x12605e;
    }


    public static void main(String[] args) {
        ApiResponse apiResponse=new ApiResponse();
        new CsdnHandleDownload().getACW2("DAA67A71D3D12366C3F9DC6FA80CEFC788CB0FFE");
        VssCode vssCode=new VssCode();
        vssCode.setDownUrl("https://download.csdn.net/download/u013362100/8875487");
        String apiReuseUrl ="http://152.136.109.183:8080/cosapi" ;
        String apiReuseUser = "ty1451";
        String apiReusePwd = "123456HXJ";
        Request.Builder builder = new Request.Builder();
        builder.url(apiReuseUrl);
        try {
            Map<String, String> params = new HashMap<>();
            params.put("downloadurl", vssCode.getDownUrl());
            params.put("verifyCode", apiReuseUser);
            params.put("password", apiReusePwd);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(params));
            Map<String, String> map = new HashMap<>();
            map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            map.put("Connection", "keep-alive");
            map.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16C104 CSDNApp/3.5.1(iOS)");
            map.put("Accept-Language", "zh-cn");
            map.put("Accept-Encoding", "br,deflate");
            Headers headers = Headers.of(map);
            builder.headers(headers);
            Request request = builder.post(requestBody).build();
            Response response = OKHttpHelper.buildOKHttpClient().followRedirects(true).build().newCall(request).execute();
            String result = response.body().string();
            logger.info(result);
            JSONObject jsonObject = JSON.parseObject(result);
            String returnCode = jsonObject.getString("code");
            if ("5".equals(returnCode) || "a".equals(returnCode)) {
                apiResponse.setStatus(1);
                apiResponse.setMsg("success");
                apiResponse.setResult(jsonObject.getString("cos"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Request.Builder builder = new Request.Builder();
                            Request request = builder.url((String) apiResponse.getResult()).build();
                            Response response = OKHttpHelper.buildOKHttpClient().followRedirects(true).build().newCall(request).execute();
                            String fileName="";
                            List<String> urls=response.request().url().pathSegments();
                            if(urls.size()>1){
                                fileName=response.request().url().pathSegments().get(1);
                            }else{
                                fileName = StrUtil.subAfter(response.headers().get("Content-Disposition"), "filename=",false);
                            }
                        }catch (Exception e){
                            logger.error(e.getMessage());
                        }
                    }
                }).start();
            }
        }catch (Exception e){};
    }

    private String cookieHandle(String cookie) {

        if (!cookie.endsWith(";")) {
            cookie += ";";
        }
        int indexBegin = cookie.indexOf("acw_sc__v2");
        if (indexBegin > -1) {
            int indexEnd = cookie.indexOf(";", indexBegin);
            return StrUtil.replace(cookie, cookie.substring(indexBegin, indexEnd), "acw_sc__v2=" + getAcw_sc__v2());
        } else {
            cookie += "acw_sc__v2=" + getAcw_sc__v2();
        }
        return cookie;
    }

    private byte[] responseHandle(Response response, HttpVssUser httpVssUser) throws IOException {
        byte[] bytes = response.body().bytes();
        String result = new String(ZipUtil.unGzip(bytes));
        String prefix = "<html><script>\nvar arg1='";
        if (StrUtil.startWith(result, prefix)) {
            setAcw_sc__v2(getACW2(StrUtil.subBetween(result, prefix, "'")));
        } else {
            return bytes;
        }
        Request.Builder builder = new Request.Builder();
        builder.url(response.request().url());
        //https://download.csdn.net/index.php/mobile/source/do_download/9881373/icaoweiwei
        Map<String, String> map = new HashMap<>();
        map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        map.put("Connection", "keep-alive");
        map.put("Cookie", cookieHandle(httpVssUser.getVssUser().getCookie()));
        map.put("User-Agent", response.request().header("User-Agent"));
        map.put("Accept-Language", "zh-cn");
        map.put("Referer", "https://download.csdn.net");
        map.put("Accept-Encoding", "gzip,deflate");
        Headers headers = Headers.of(map);

        builder.headers(headers);

        //获取文件地址
        Request request = builder.build();
        response = http(httpVssUser, request);
        bytes = response.body().bytes();
        return bytes;
    }
    private String getAcw_sc__v2() {
        return acw_sc__v2;
    }

    private void setAcw_sc__v2(String acw_sc__v2) {
        this.acw_sc__v2 = acw_sc__v2;
    }
}
