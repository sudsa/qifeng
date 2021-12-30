package cn.stylefeng.guns.modular.vsshop.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.core.util.*;
import cn.stylefeng.guns.InitSpringBoot;
import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.dao.UserMapper;
import cn.stylefeng.guns.modular.system.model.User;
import cn.stylefeng.guns.modular.system.model.VssIp;
import cn.stylefeng.guns.modular.vsshop.common.*;
import cn.stylefeng.guns.modular.vsshop.core.CsdnHandleDownload;
import cn.stylefeng.guns.modular.vsshop.core.HandleDownload;
import cn.stylefeng.guns.modular.vsshop.dao.VssLogMapper;
import cn.stylefeng.guns.modular.vsshop.dao.VssUserMapper;
import cn.stylefeng.guns.modular.vsshop.model.*;
import cn.stylefeng.guns.modular.vsshop.dao.VssCodeMapper;
import cn.stylefeng.guns.modular.vsshop.service.IVssCodeService;
import cn.stylefeng.roses.core.util.HttpContext;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.HttpUtil;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Struct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jianyinlin
 * @since 2019-03-09
 */
@Service
public class VssCodeServiceImpl extends ServiceImpl<VssCodeMapper, VssCode> implements IVssCodeService {

    private Logger logger=LoggerFactory.getLogger(this.getClass());
    @Autowired
    VssCodeMapper vssCodeMapper;

    @Autowired
    VssLogMapper vssLogMapper;

    @Autowired
    VssUserMapper vssUserMapper;

    @Autowired
    AliEmailHelper aliEmailHelper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    HandleDownload handleDownload;
    @Autowired
    OssHelper ossHelper;
    @Override
    public ApiResponse getFile(VssCode vssCodeReq,VssIp vssIp) throws Exception {

        EntityWrapper<VssCode> entityWrapper=new EntityWrapper<>();
        entityWrapper.eq("code",vssCodeReq.getCode());
        VssCode vssCode=selectOne(entityWrapper);
        if(vssCode!=null){
            //更新用户下载次数
            User user= userMapper.getByName(vssCode.getOwner());
            if(user!=null){
                if(user.getDaydown()>=user.getDaylimit()){
                    throw new Exception("超过每天下载限制");
                }
                if(user.getBalance()<user.getPrice()){
                    throw new Exception(String.format("余额不足，余额：%s", user.getBalance()));
                }
            }else{
                throw new Exception("激活码异常！无有效用户关联");
            }
            //重复下载
            boolean openReuse= "open".equals(ConstantFactory.me().getDictsByName("复用开关",1));
            if(openReuse && "1".equals(vssCode.getReuseEnable()) && StrUtil.isNotEmpty(vssCode.getFilePath()) && vssCodeReq.getDownUrl().equals(vssCode.getDownUrl())){
                ApiResponse apiResponse=ApiResponse.success(vssCode.getFilePath());
                vssCode.setEmail(vssCodeReq.getEmail());
                Boolean sendResult=sendEmail(vssCode);
                if(sendResult!=null){
                    if(sendResult){
                        apiResponse.setMsg("发送邮件成功");
                    }else {
                        apiResponse.setMsg("发送邮件失败！请从网页下载");
                    }
                }else{
                    apiResponse.setMsg("");
                }
                return apiResponse;
            }

            //检验code
            checkVssCodeStatus(vssCode);

            EntityWrapper<VssCode> tempEntityWrapper=new EntityWrapper<>();
            tempEntityWrapper.eq("down_url",vssCodeReq.getDownUrl());
//            tempEntityWrapper.ge("update_time", DateUtil.offsetMonth(new Date(),-1));
            //如果已经有下载过，则直接使用
            VssCode vssCodeTemp=selectOne(tempEntityWrapper);
            if(openReuse  && vssCodeTemp!=null  && "1".equals(vssCodeTemp.getReuseEnable()) && StrUtil.isNotEmpty(vssCodeTemp.getFilePath())){

                //请求属性赋值
                CommonUtils.copyProperties(vssCodeReq,vssCode);
                vssCode.setFilePath(vssCodeTemp.getFilePath());
                vssCode.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                vssCode.setSource(vssCode.getSource());
                vssCode.setUsedTime(new Timestamp(System.currentTimeMillis()));
                vssCode.setStatus(String.valueOf(VssCodeStatus.USED.ordinal()));
                vssCode.setCoin(vssCodeTemp.getCoin());
                updateById(vssCode);

                //记录日志
                //记录下载日志
                VssLog vssLog=new VssLog();
                vssLog.setUuid(RandomUtil.randomUUID().replaceAll("-",""));
                vssLog.setCode(vssCode.getCode());
                vssLog.setDownUrl(vssCode.getDownUrl());
                vssLog.setFilePath(vssCode.getFilePath());
                vssLog.setInsertTime(new Date());
                vssLog.setReuse("是");
                vssLog.setVssuser("系统");
                vssLog.setSource(vssCode.getSource());
                String coin=NumberUtil.isNumber(vssCodeTemp.getCoin())?vssCode.getCoin():"0";
                vssLog.setCoin(Integer.parseInt(coin));
                vssLog.setOwner(vssCode.getOwner());
                vssLog.setIp(vssCodeReq.getIp());
                vssLogMapper.insert(vssLog);

                ApiResponse apiResponse=ApiResponse.success(vssCodeTemp.getFilePath());
                Boolean sendResult=sendEmail(vssCode);
                if(sendResult!=null){
                    if(sendResult){
                        apiResponse.setMsg("发送邮件成功");
                    }else {
                        apiResponse.setMsg("发送邮件失败！请从网页下载");
                    }
                }else{
                    apiResponse.setMsg("");
                }
                user.setDaydown(user.getDaydown()+1);
                user.setBalance(user.getBalance()-user.getPrice());
                userMapper.updateById(user);
                return apiResponse;
            }else{

                //请求属性赋值
                CommonUtils.copyProperties(vssCodeReq,vssCode);

                //获取文件地址
                vssCode.setFilePath(downFile(vssCode,vssIp));
                vssCode.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                vssCode.setUsedTime(new Timestamp(System.currentTimeMillis()));
                vssCode.setStatus(String.valueOf(VssCodeStatus.USED.ordinal()));
                vssCode.setReuseEnable(openReuse?"1":"2");
                updateById(vssCode);

                ApiResponse apiResponse=ApiResponse.success(vssCode.getFilePath());
                Boolean sendResult=sendEmail(vssCode);
                if(sendResult!=null){
                    if(sendResult){
                        apiResponse.setMsg("发送邮件成功");
                    }else {
                        apiResponse.setMsg("发送邮件失败！请从网页下载");
                    }
                }else{
                    apiResponse.setMsg("");
                }
                Boolean isCrxFile= StrUtil.endWith(vssCode.getFilePath(),".crx");
                apiResponse.setMsg(apiResponse.getMsg()+ (isCrxFile==true?",插件请使用非谷歌浏览器下载！":""));
                user.setDaydown(user.getDaydown()+1);
                user.setBalance(user.getBalance()-user.getPrice());
                userMapper.updateById(user);
                return apiResponse;
            }
        }else{
            throw new Exception("不存在该激活码！");
        }
    }

    private Boolean sendEmail(VssCode vssCode) {
        //邮件发送
        if(StrUtil.isNotEmpty(vssCode.getEmail())){
            SingleSendMailRequest email=new SingleSendMailRequest();
            email.setToAddress(vssCode.getEmail().trim());
            String name=StrUtil.subAfter(vssCode.getFilePath(),"/",true);
            email.setHtmlBody(String.format("你好，你的文件 %s 下载地址为：%s",name, vssCode.getFilePath()));
            email.setSubject(String.format("CSDN文件%s下载地址", name));
            return aliEmailHelper.sendDown(email);
        }
        return null;
    }

    @Override
    public void createCode(String source, String type, Integer num) {
        List<VssCode> vssCodeList=new ArrayList<>();
        for(int i=0;i<num;i++){
            VssCode vssCodeTemp=new VssCode();
            vssCodeTemp.setUuid(RandomUtil.randomUUID().replaceAll("-",""));
            vssCodeTemp.setCode(RandomUtil.randomString(8));
            vssCodeTemp.setInsertTime(new Timestamp(System.currentTimeMillis()));
            vssCodeTemp.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            vssCodeTemp.setStatus(String.valueOf(VssCodeStatus.NORMAL.ordinal()));
            vssCodeTemp.setSource(source);
            vssCodeTemp.setType(type);
            vssCodeTemp.setOwner(ShiroKit.getUser().getName());
            vssCodeTemp.setReuseEnable("1");
            vssCodeList.add(vssCodeTemp);
        }
        insertBatch(vssCodeList);
    }

    private void checkVssCodeStatus(VssCode vssCode) throws Exception {
        int status=NumberUtil.isNumber(vssCode.getStatus())?Integer.parseInt(vssCode.getStatus()):0;
        if(status==VssCodeStatus.NORMAL.ordinal()){
            return ;
        }
        if(status==VssCodeStatus.USED.ordinal()){
            throw new Exception("激活码已经使用！");
        }else if(status==VssCodeStatus.INVALID.ordinal()){
            throw new Exception("激活码不可用状态！");
        }else {
            throw new Exception("激活码未知状态！");
        }
    }

    public String downFile(VssCode vssCode, VssIp vssIp) throws Exception {
        boolean isMaster="1".equals(vssIp.getMaster());
        ApiResponse apiResponse;
        //获取来源
        UrlSource urlSource=CommonUtils.getSource(vssCode.getDownUrl());

        //设置下载处理类
        setHandleDownload(urlSource);

        //获取下载信息
        DownloadInfo downloadInfo=handleDownload.getDownloadInfo(vssCode);

        //判断积分是否足够
        String coin=downloadInfo.getCoin();
        if(!NumberUtil.isNumber(coin)){
            throw new Exception("网络不稳定，请等候5秒重试");
        }
        vssCode.setCoin(coin);
        int coinlimit=Integer.parseInt(StrUtil.emptyToDefault(ConstantFactory.me().getDictsByName("大积分分数", 1),"5"));
        if(Integer.parseInt(coin)>coinlimit){
            vssCode.setType(String.valueOf(VssUserType.VIP.ordinal()));
        }
        vssCode.setSource(String.valueOf(urlSource.ordinal()));

        String apiOpen=StrUtil.emptyToDefault(ConstantFactory.me().getDictsByName("api复用开关", 1),"close");
        if("open".equals(apiOpen) && isMaster){
            apiResponse=handleDownload.apiReuseDownload(vssCode);
            if(apiResponse.getStatus()==1){
                //记录下载日志
                VssLog vssLog=new VssLog();
                vssLog.setUuid(RandomUtil.randomUUID().replaceAll("-",""));
                vssLog.setCode(vssCode.getCode());
                vssLog.setDownUrl(vssCode.getDownUrl());
                vssLog.setFilePath((String)apiResponse.getResult());
                vssLog.setInsertTime(new Date());
                vssLog.setReuse("否");
                vssLog.setVssuser("小助手");
                vssLog.setSource(urlSource.name());
                vssLog.setCoin(Integer.parseInt(coin));
                vssLog.setOwner(vssCode.getOwner());
                vssLog.setIp(vssCode.getIp());
                vssLogMapper.insert(vssLog);
                return (String) apiResponse.getResult();
            }else{
                logger.warn("调用小助手失败："+apiResponse.getMsg());
            }
        }
        HttpVssUser httpVssUser=null;
        String internal = StrUtil.emptyToDefault(ConstantFactory.me().getDictsByName("内部调用开关", 1), "open");
        if ("open".equals(internal)) {
            //检验下载区域等，如果指定区域却调用api失败则抛异常
            apiResponse=handleDownload.checkDownload(vssCode,vssIp);
            if(apiResponse.getStatus()==1){
                EntityWrapper entityWrapper=new EntityWrapper();
                entityWrapper.eq("code",vssCode.getCode());
                List<VssLog> vssLogList= vssLogMapper.selectList(entityWrapper);
                if(CollUtil.isNotEmpty(vssLogList)){
                    VssLog vssLog=vssLogList.get(0);
                    vssLog.setIp(vssCode.getIp());
                    vssLog.setRegion(vssCode.getRegion());
                    vssLog.setOwner(vssCode.getOwner());
                    vssLogMapper.updateById(vssLog);
                }
                return (String) apiResponse.getResult();
            }
            //获取下载用户
            httpVssUser= handleDownload.getHttpVssUser(vssCode);
        }


        if(httpVssUser!=null){
            //获取实际下载地址
            String actualDownUrl=getActualDownUrl(vssCode,urlSource);
            //下载
            apiResponse= handleDownload.download(httpVssUser,actualDownUrl,urlSource.name());
            if(apiResponse.getStatus()!=1){
                SingleSendMailRequest email=new SingleSendMailRequest();
                email.setToAddress(ossHelper.getEmail());
                email.setHtmlBody(String.format("激活码：%s ;拥有者：%s ;下载地址：%s ;邮箱：%s;网站账号：%s; 返回内容：%s ;服务器ip：%s,客户ip：%s", vssCode.getCode(),vssCode.getOwner(),vssCode.getDownUrl(),StrUtil.nullToEmpty(vssCode.getEmail()),httpVssUser.getVssUser().getUsername(),apiResponse.getMsg(),InitSpringBoot.LOCAL_IP,vssCode.getIp()));
                email.setSubject(String.format("%s下载请求出错", urlSource.name()));
                aliEmailHelper.sendDown(email);
                User user= userMapper.getByName(vssCode.getOwner());
                if(user!=null && user.getId()!=1 && StrUtil.isNotBlank(user.getEmail())){
                    email.setToAddress(user.getEmail());
                    aliEmailHelper.sendDown(email);
                }
                throw new Exception("下载失败");
            }else{
                //记录下载日志
                VssLog vssLog=new VssLog();
                vssLog.setUuid(RandomUtil.randomUUID().replaceAll("-",""));
                vssLog.setCode(vssCode.getCode());
                vssLog.setDownUrl(vssCode.getDownUrl());
                vssLog.setFilePath((String)apiResponse.getResult());
                vssLog.setInsertTime(new Date());
                vssLog.setReuse("否");
                vssLog.setVssuser(httpVssUser.getVssUser().getUsername());
                vssLog.setSource(urlSource.name());
                vssLog.setCoin(Integer.parseInt(coin));
                vssLog.setOwner(vssCode.getOwner());
                vssLog.setIp(vssCode.getIp());
                vssLog.setRegion(vssCode.getRegion());
                vssLogMapper.insert(vssLog);

                //更新用户积分
                if(String.valueOf(VssUserType.VIP.ordinal()).equals(vssCode.getType())){
                    coin="1";
                }
                VssUser vssUser=httpVssUser.getVssUser();
                String tempCoin=NumberUtil.toStr(Integer.parseInt(vssUser.getCoin())-Integer.parseInt(coin),"0");
                int tempDayDown=httpVssUser.getVssUser().getDayDown()+1;
                vssUser.setCoin(tempCoin);
                vssUser.setLastLoginTime(new Date());
                vssUser.setDayDown(tempDayDown);
                vssUserMapper.updateById(vssUser);
                return (String)apiResponse.getResult();
            }
        }else{
            apiResponse= handleDownload.apiDownload(vssCode);
            if(apiResponse.getStatus()!=1){
                SingleSendMailRequest email=new SingleSendMailRequest();
                email.setToAddress(ossHelper.getEmail());
                email.setHtmlBody(String.format("%s无可用账号；需要积分：%s;激活码：%s ;拥有者：%s ;下载地址：%s ;邮箱：%s;服务器ip：%s,客户ip：%s", urlSource.name(),vssCode.getCoin(),vssCode.getCode(),vssCode.getOwner(),vssCode.getDownUrl(),StrUtil.nullToEmpty(vssCode.getEmail()),InitSpringBoot.LOCAL_IP,vssCode.getIp()));

                email.setSubject("自动发货警告");
                aliEmailHelper.sendDown(email);
                InitSpringBoot.SERVICE_ENABLE=false;
                throw new Exception(String.format("%s账号不足，联系店家", urlSource.name()));
            }else{
                //记录下载日志
                VssLog vssLog=new VssLog();
                vssLog.setUuid(RandomUtil.randomUUID().replaceAll("-",""));
                vssLog.setCode(vssCode.getCode());
                vssLog.setDownUrl(vssCode.getDownUrl());
                vssLog.setFilePath((String)apiResponse.getResult());
                vssLog.setInsertTime(new Date());
                vssLog.setReuse("否");
                vssLog.setVssuser("api");
                vssLog.setSource(urlSource.name());
                vssLog.setCoin(Integer.parseInt(coin));
                vssLog.setOwner(vssCode.getOwner());
                vssLog.setIp(vssCode.getIp());
                vssLog.setRegion(vssCode.getRegion());
                vssLogMapper.insert(vssLog);
            }
            return (String) apiResponse.getResult();
        }

    }

    private String getActualDownUrl(VssCode vssCode, UrlSource urlSource) throws Exception {
        if(urlSource.equals(UrlSource.CSDN) && vssCode.getType().equals(String.valueOf(VssUserType.NORMAL.ordinal()))){
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
        }else if(urlSource.equals(UrlSource.CSDN) && vssCode.getType().equals(String.valueOf(VssUserType.VIP.ordinal()))){

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


    public HandleDownload setHandleDownload(UrlSource urlSource) throws Exception {
        if(urlSource.equals(UrlSource.CSDN)){
            handleDownload=(HandleDownload) SpringUtil.getBean(CsdnHandleDownload.class);
        }else if(urlSource.equals(UrlSource.UNKNOW)){
            throw new Exception("暂不支持该网站下载或者输入正确下载地址重试！");
        }

        return null;
    }


    public static void main(String[] args) {

    }
}
