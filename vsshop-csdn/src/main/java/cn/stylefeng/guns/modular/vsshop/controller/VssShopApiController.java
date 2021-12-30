package cn.stylefeng.guns.modular.vsshop.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.*;
import cn.stylefeng.guns.InitSpringBoot;
import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.api.ApiController;
import cn.stylefeng.guns.modular.system.dao.UserMapper;
import cn.stylefeng.guns.modular.system.model.User;
import cn.stylefeng.guns.modular.system.model.VssIp;
import cn.stylefeng.guns.modular.system.model.VssVip;
import cn.stylefeng.guns.modular.vsshop.common.*;
import cn.stylefeng.guns.modular.vsshop.model.*;
import cn.stylefeng.guns.modular.vsshop.service.*;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/api")
public class VssShopApiController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    IVssCodeService vssCodeService;
    @Autowired
    IVssVipService vssVipService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisHelper redisHelper;
    @Autowired
    OssHelper ossHelper;
    @Autowired
    IVssIpService vssIpService;
    @Autowired
    AliEmailHelper aliEmailHelper;
    @Autowired
    IVssLogService vssLogService;
    @Autowired
    private IArticleService articleService;
    @RequestMapping(value = "/getDownloadFile", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse getFile(@Valid VssCode vssCode, BindingResult bindingResult) {
        if(bindingResult!=null && bindingResult.hasErrors()){
            StringBuffer sb=new StringBuffer();
            for(ObjectError objectError : bindingResult.getAllErrors()){
                sb.append(objectError.getDefaultMessage() +" , ");
            }
            return ApiResponse.fail(sb.toString());
        }

        //判断北京
        EntityWrapper ipEntity=new EntityWrapper();
        ipEntity.eq("ip",InitSpringBoot.LOCAL_IP);
        ipEntity.eq("status","1");
        VssIp vssIp =vssIpService.selectOne(ipEntity);
        if(vssIp==null){
            logger.error("不存在该服务器！");
            InitSpringBoot.SERVICE_ENABLE=false;
            return ApiResponse.fail("服务器出错");
        }
        boolean isMaster="1".equals(vssIp.getMaster());
        vssCode.setIp(getHttpServletRequest().getHeader("ali-cdn-real-ip"));
        if(isMaster){

            //预处理
            vssCode.setCode(vssCode.getCode().trim());
            String url=vssCode.getDownUrl().trim();
            url=StrUtil.subBefore(url,"?",false);
            if(ReUtil.isMatch("https://download.csdn.net/index.php/mobile/source/download/([\\s\\S]*?)",url)){
                url=url.replaceAll("index.php/mobile/source/","");
            }
            vssCode.setDownUrl(url);

            String check = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
            if(StrUtil.isNotBlank(vssCode.getEmail())){
                vssCode.setEmail(vssCode.getEmail().trim());
                if(!ReUtil.isMatch(check,vssCode.getEmail())){
                    return ApiResponse.fail("邮件地址错误！");
                }
            }

            try{
                //code限流30秒5次
                String ip=vssCode.getIp();
                String ipWhite=StrUtil.emptyToDefault(ConstantFactory.me().getDictsByName("ip白名单", 1),"202.105.96.83");
                if(ipWhite.contains(StrUtil.emptyToDefault(ip,""))){
                    return vssCodeService.getFile(vssCode,vssIp);
                }

                RateLimiter limiter = caches.get(vssCode.getCode());
                if(limiter!=null && !limiter.tryAcquire()){
                    return ApiResponse.fail("请求太过频繁，请稍候重试！！");
                }
                Integer ipCount = (Integer) redisHelper.get(ip);
                String ipCountStr=StrUtil.emptyToDefault(ConstantFactory.me().getDictsByName("ip限制", 1),"30");
                if (ipCount == null) {
                    redisHelper.set(ip, 1, 86400);
                    return vssCodeService.getFile(vssCode,vssIp);
                } else if (ipCount < Integer.parseInt(ipCountStr)) {
                    redisHelper.set(ip, ++ipCount);
                    return vssCodeService.getFile(vssCode,vssIp);
                }
                SingleSendMailRequest email=new SingleSendMailRequest();
                email.setToAddress(ossHelper.getEmail());
                email.setHtmlBody(String.format("报警：ip: %s,下载触发阀值,已禁止下载",vssCode.getIp()));
                email.setSubject("CSDN文件ip下载频繁触发阀值，已禁止下载");
                aliEmailHelper.sendDown(email);
                return ApiResponse.fail("请求频繁！！！！");
            }catch (Exception e){
                logger.error("错误",e);
                return ApiResponse.fail(e.getMessage());
            }
        }else {
            try{
                EntityWrapper<VssCode> entityWrapper=new EntityWrapper<>();
                entityWrapper.eq("code",vssCode.getCode());
                VssCode vssCodeTmep=vssCodeService.selectOne(entityWrapper);
                if(vssCodeTmep==null){
                    throw new Exception("不存在该激活码！");
                }
                vssCodeTmep.setIp(vssCode.getIp());
                vssCodeTmep.setDownUrl(vssCode.getDownUrl());

                EntityWrapper ipEntityTemp=new EntityWrapper();
                ipEntityTemp.eq("ip",vssCode.getIp());
                ipEntityTemp.eq("status","1");
                VssIp vssIpTemp =vssIpService.selectOne(ipEntityTemp);
                if(vssIpTemp==null || !"1".equals(vssIpTemp.getMaster())){
                    throw new Exception("非法来源！");
                }
                return ApiResponse.success(vssCodeService.downFile(vssCodeTmep,vssIp));
            }catch (Exception e){
                logger.error("错误",e);
                return ApiResponse.fail(e.getMessage());
            }
        }

    }

    /**
     * 根据用户调用
     * @param email
     * @param downUrl
     * @return
     */
    @RequestMapping(value = "/down", method = RequestMethod.GET)
    @ResponseBody
    @Permission
    public ApiResponse down(@RequestParam String email,@RequestParam String downUrl) {
        VssCode vssCode=new VssCode();
        vssCode.setDownUrl(downUrl);
        vssCode.setEmail(email);
        vssCode.setIp(getHttpServletRequest().getHeader("ali-cdn-real-ip"));
        try {
            if(ShiroKit.getUser()==null){
                return ApiResponse.fail("登陆失效，请重新登陆！");
            }else {
                User user=userMapper.getByAccount(ShiroKit.getUser().getAccount());
                VssCode vssCodeTemp=new VssCode();
                vssCodeTemp.setUuid(RandomUtil.randomUUID().replaceAll("-",""));
                vssCodeTemp.setCode(RandomUtil.randomString(8));
                vssCodeTemp.setInsertTime(new Timestamp(System.currentTimeMillis()));
                vssCodeTemp.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                vssCodeTemp.setStatus(String.valueOf(VssCodeStatus.NORMAL.ordinal()));
                vssCodeTemp.setSource(String.valueOf(UrlSource.CSDN.ordinal()));
                vssCodeTemp.setType("1");
                vssCodeTemp.setOwner(user.getName());
                vssCodeTemp.setReuseEnable("1");
                vssCodeService.insert(vssCodeTemp);
                vssCode.setCode(vssCodeTemp.getCode());
                return getFile(vssCode,null);
            }
        }catch (Exception e){
            return ApiResponse.fail(e.getMessage());
        }
    }
    @RequestMapping(value = "/reuse", method = RequestMethod.POST)
    @ResponseBody
    public ReuseResponse reuse(@RequestBody @Valid ReuseRequest reuseRequest, BindingResult bindingResult){
        ReuseResponse reuseResponse=new ReuseResponse();
        if(bindingResult.hasErrors()){
            StringBuffer sb=new StringBuffer();
            for(ObjectError objectError : bindingResult.getAllErrors()){
                sb.append(objectError.getDefaultMessage() +" , ");
            }
            reuseResponse.setCode("-1");
            reuseResponse.setMsg(sb.toString());
            return reuseResponse;
        }
        String ip=getHttpServletRequest().getHeader("ali-cdn-real-ip");
        EntityWrapper entityWrapper=new EntityWrapper();
        entityWrapper.eq("account",reuseRequest.getVerifyCode());
        entityWrapper.eq("pwd",reuseRequest.getPassword());
        entityWrapper.eq("status",1);
        List<User> userList=userMapper.selectList(entityWrapper);
        if(CollUtil.isNotEmpty(userList)){
            User user=userList.get(0);
            if(user.getBalance()<user.getPrice()){
                reuseResponse.setCode("-1");
                reuseResponse.setMsg("余额不足");
                return reuseResponse;
            }
            if(user.getDaydown()>=user.getDaylimit()){
                reuseResponse.setCode("-1");
                reuseResponse.setMsg("达到每天下载限制！");
                return reuseResponse;
            }

            boolean openReuse= "open".equals(ConstantFactory.me().getDictsByName("复用开关",1));
            if(openReuse) {
                EntityWrapper<VssCode> tempEntityWrapper = new EntityWrapper<>();
                tempEntityWrapper.eq("down_url", reuseRequest.getDownloadurl());
//            tempEntityWrapper.ge("update_time", DateUtil.offsetMonth(new Date(),-1));
                //如果已经有下载过，则直接使用
                VssCode vssCodeTemp = vssCodeService.selectOne(tempEntityWrapper);
                if (openReuse && vssCodeTemp != null && "1".equals(vssCodeTemp.getReuseEnable()) && StrUtil.isNotEmpty(vssCodeTemp.getFilePath())) {

                    //记录下载日志
                    VssLog vssLog=new VssLog();
                    vssLog.setUuid(RandomUtil.randomUUID().replaceAll("-",""));
                    vssLog.setCode(vssCodeTemp.getCode());
                    vssLog.setDownUrl(vssCodeTemp.getDownUrl());
                    vssLog.setFilePath(vssCodeTemp.getFilePath());
                    vssLog.setInsertTime(new Date());
                    vssLog.setReuse("是");
                    vssLog.setVssuser("reuse-api");
                    vssLog.setSource(UrlSource.CSDN.name());
                    String coin=NumberUtil.isNumber(vssCodeTemp.getCoin())?vssCodeTemp.getCoin():"0";
                    vssLog.setCoin(Integer.parseInt(coin));
                    vssLog.setOwner(user.getName());
                    vssLog.setIp(ip);
                    vssLogService.insert(vssLog);

                    user.setBalance(user.getBalance()-user.getPrice());
                    user.setDaydown(user.getDaydown()+1);
                    userMapper.updateById(user);

                    reuseResponse.setCode("a");
                    reuseResponse.setMsg("成功");
                    reuseResponse.setCost(user.getPrice());
                    reuseResponse.setCos(vssCodeTemp.getFilePath());
                    reuseResponse.setUser(user.getAccount());
                    reuseResponse.setBalance(user.getBalance());
                    return reuseResponse;
                }
            }
        }else{
            reuseResponse.setCode("-1");
            reuseResponse.setMsg("账号密码错误");
            return reuseResponse;
        }
        reuseResponse.setCode("-1");
        reuseResponse.setMsg("不存在资源");
        return reuseResponse;

    }

    @RequestMapping(value = "/getVipList", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse getVipList() {
        try {
            EntityWrapper entityWrapper=new EntityWrapper();
            entityWrapper.orderBy("name",false);
            List<VssVip> vssVipList= vssVipService.selectList(entityWrapper);
            if(CollUtil.isNotEmpty(vssVipList)){
                for(int i=0;i<vssVipList.size();i++){
                    vssVipList.get(i).setIndex(i+1);
                }
            }
            ApiResponse apiResponse=ApiResponse.success(vssVipList);
            apiResponse.setStatus(0);
            return apiResponse;
        } catch (Exception e) {
            logger.error("错误",e);
            return ApiResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "/getInfoList", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse getInfoList() {
        try {
            ApiResponse apiResponse=ApiResponse.success(null);
            apiResponse.setStatus(0);
            return apiResponse;
        } catch (Exception e) {
            logger.error("错误",e);
            return ApiResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse history(String code) {
        try {
            ApiResponse apiResponse=new ApiResponse();
            EntityWrapper entityWrapper=new EntityWrapper();
            entityWrapper.eq("code",code);
            apiResponse.setResult(vssCodeService.selectOne(entityWrapper));
            return apiResponse;
        } catch (Exception e) {
            logger.error("错误",e);
            return ApiResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "/aliMns", method = RequestMethod.POST)
    @ResponseBody
    public Object handleAliMns() {
        try {
            String jsonStr = IoUtil.read(super.getHttpServletRequest().getInputStream(), Charset.defaultCharset());
            jsonStr = new String(java.util.Base64.getDecoder().decode(jsonStr));
            JSONArray jsonArray = JSON.parseObject(jsonStr).getJSONArray("events");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String filePath = jsonObject.getJSONObject("oss").getJSONObject("object").getString("key");
            String bucket = jsonObject.getJSONObject("oss").getJSONObject("bucket").getString("name");
            String requestId = jsonObject.getJSONObject("responseElements").getString("requestId");
            if (redisHelper.get(requestId) != null) {
                return ApiResponse.success("success");
            } else {
                redisHelper.set(requestId, 1, 86400);
            }
            Integer fileCount = (Integer) redisHelper.get(filePath);
            String fileCountStr=StrUtil.emptyToDefault(ConstantFactory.me().getDictsByName("oss限制", 1),"20");

            if (fileCount == null) {
                redisHelper.set(filePath, 1, 86400);
            } else if (fileCount < Integer.parseInt(fileCountStr)) {
                redisHelper.set(filePath, ++fileCount);
            } else {
                OSSClient ossClient = ossHelper.getOssClient();
                ossClient.deleteObject(bucket, filePath);
                EntityWrapper entityWrapper = new EntityWrapper();
                entityWrapper.like("file_path", filePath);
                List<VssCode> vssCodeList = vssCodeService.selectList(entityWrapper);
                if (CollUtil.isNotEmpty(vssCodeList)) {
                    vssCodeList.forEach(vssCode -> {
                        vssCode.setReuseEnable("2");
                    });
                }
                vssCodeService.updateBatchById(vssCodeList);
                SingleSendMailRequest email=new SingleSendMailRequest();
                email.setToAddress(ossHelper.getEmail());
                email.setHtmlBody(String.format("报警：你的文件 %s被恶意下载，已经删除处理",filePath));
                email.setSubject("CSDN文件恶意下载报警");
                aliEmailHelper.sendDown(email);
            }

            return ApiResponse.success("success");
        } catch (Exception e) {
            logger.error("错误",e);
            return ApiResponse.fail(e.getMessage());
        }
    }

    // 根据IP分不同的令牌桶, 每天自动清理缓存
    public static LoadingCache<String, RateLimiter> caches = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<String, RateLimiter>() {
                @Override
                public RateLimiter load(String key) throws Exception {
                // 新的IP初始化 (限流每秒两个令牌响应)
                    return RateLimiter.create(0.2);
                }
     });


    private Boolean authenticate(String method, String uri, Map<String, String> headers, String cert) {
        String str2sign = this.getSignStr(method, uri, headers);
        String signature = (String)headers.get("Authorization");
        byte[] decodedSign = Base64.decodeBase64(signature);

        try {
            URL url = new URL(cert);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            DataInputStream in = new DataInputStream(conn.getInputStream());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate c = cf.generateCertificate(in);
            PublicKey pk = c.getPublicKey();
            Signature signetcheck = Signature.getInstance("SHA1withRSA");
            signetcheck.initVerify(pk);
            signetcheck.update(str2sign.getBytes());
            Boolean res = signetcheck.verify(decodedSign);
            return res;
        } catch (Exception var16) {
            var16.printStackTrace();
            logger.warn("authenticate fail, " + var16.getMessage());
            return false;
        }
    }

    private String getSignStr(String method, String uri, Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        sb.append(method);
        sb.append("\n");
        sb.append(this.safeGetHeader(headers, "Content-md5"));
        sb.append("\n");
        sb.append(this.safeGetHeader(headers, "Content-Type"));
        sb.append("\n");
        sb.append(this.safeGetHeader(headers, "Date"));
        sb.append("\n");
        List<String> tmp = new ArrayList();
        Iterator i$ = headers.entrySet().iterator();

        while(i$.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)i$.next();
            if (((String)entry.getKey()).startsWith("x-mns-")) {
                tmp.add((String)entry.getKey() + ":" + (String)entry.getValue());
            }
        }

        Collections.sort(tmp);
        i$ = tmp.iterator();

        while(i$.hasNext()) {
            String kv = (String)i$.next();
            sb.append(kv);
            sb.append("\n");
        }

        sb.append(uri);
        return sb.toString();
    }

    private String safeGetHeader(Map<String, String> headers, String name) {
        return headers.containsKey(name) ? (String)headers.get(name) : "";
    }


    @RequestMapping(value = "/getVipArticleFile")
    @ResponseBody
    public ApiResponse getVipArticleFile(@Valid VipArticle vipArticle, BindingResult bindingResult, Model model) throws Exception {
        ApiResponse apiResponse = ApiResponse.fail("");
        Map<String,Object> htmlcontent = new HashMap<String,Object>();
        if(bindingResult!=null && bindingResult.hasErrors()){
            StringBuffer sb=new StringBuffer();
            for(ObjectError objectError : bindingResult.getAllErrors()){
                sb.append(objectError.getDefaultMessage() +" , ");
            }
                return ApiResponse.fail("邮件地址错误！");
        }
        vipArticle.setIp(getHttpServletRequest().getHeader("ali-cdn-real-ip"));
            //预处理
        vipArticle.setCode(vipArticle.getCode().trim());
            String url=vipArticle.getDownUrl().trim();
            url=StrUtil.subBefore(url,"?",false);
        vipArticle.setDownUrl(url);
            String check = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
            if(StrUtil.isNotBlank(vipArticle.getEmail())){
                vipArticle.setEmail(vipArticle.getEmail().trim());
                if(!ReUtil.isMatch(check,vipArticle.getEmail())){
                    return ApiResponse.fail("邮件地址错误！");
                }
            }
            try{
                //code限流30秒5次
                String ip=vipArticle.getIp();
                RateLimiter limiter = caches.get(vipArticle.getCode());
                if(limiter!=null && !limiter.tryAcquire()){
                    return ApiResponse.fail("请求太过频繁，请稍候重试！！");
                }
                Integer ipCount = (Integer) redisHelper.get(ip);
                String ipCountStr=StrUtil.emptyToDefault(ConstantFactory.me().getDictsByName("ip限制", 1),"30");
                if (ipCount == null) {
                    redisHelper.set(ip, 1, 86400);
                    htmlcontent = vssCodeService.getVipArticleFile(vipArticle);
                } else if (ipCount < Integer.parseInt(ipCountStr)) {
                    redisHelper.set(ip, ++ipCount);
                    htmlcontent = vssCodeService.getVipArticleFile(vipArticle);
                }
                updateArticleSuccess(vipArticle);
                apiResponse.setStatus(1);
                apiResponse = ApiResponse.success(htmlcontent.get("article"));
                return apiResponse;
            }catch (Exception e){
                logger.error("错误",e);
                return ApiResponse.fail(e.getMessage());
            }
    }

    public void updateArticleSuccess(VipArticle article){
        EntityWrapper<VipArticle> entityWrapper=new EntityWrapper<>();
        entityWrapper.eq("code",article.getCode());
        VipArticle articleforUpdate = articleService.selectOne(entityWrapper);
        articleforUpdate.setDownUrl(article.getDownUrl());
        articleforUpdate.setEmail(StrUtil.isNotBlank(article.getEmail())?article.getEmail():"");
        articleforUpdate.setUpdateTime(new Date());
        articleforUpdate.setStatus("2");
        articleforUpdate.setIp(article.getIp());
        articleforUpdate.setUsedTime(new Date());
        articleService.updateById(articleforUpdate);

    }

    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
