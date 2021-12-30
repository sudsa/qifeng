package cn.stylefeng.guns.modular.vsshop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.core.util.*;
import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.guns.modular.system.dao.VssCodeMapper;
import cn.stylefeng.guns.modular.system.model.*;
import cn.stylefeng.guns.modular.vsshop.common.*;
import cn.stylefeng.guns.modular.vsshop.controller.VssShopApiController;
import cn.stylefeng.guns.modular.vsshop.core.CsdnHandleDownload;
import cn.stylefeng.guns.modular.vsshop.core.HandleDownload;
import cn.stylefeng.guns.modular.vsshop.model.*;
import cn.stylefeng.guns.modular.vsshop.service.*;
import cn.stylefeng.roses.core.util.HttpContext;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jianyinlin
 * @since 2019-03-09
 */
@Service
public class VssCodeServiceImpl extends ServiceImpl<VssCodeMapper, VssCode> implements IVssCodeService {

    @Autowired
    IVssCodeService vssCodeService;
    @Autowired
    IVssLogService vssLogService;
    @Autowired
    IVssUserService vssUserService;
    @Autowired
    AliEmailHelper aliEmailHelper;
    @Autowired
    HttpVssUserHelper httpVssUserHelper;
    @Autowired
    IVssCustomerService vssCustomerService;
    @Autowired
    IVssSiteService vssSiteService;
    @Autowired
    IVssPlanSiteService vssPlanSiteService;
    @Autowired
    IVssCustomerSiteLogService vssCustomerSiteLogService;
    @Autowired
    IVssPlanService vssPlanService;
    @Autowired
    IVssUserSiteLogService vssUserSiteLogService;

    private Boolean sendEmail(VssCode vssCode, String mailAddress) {
        //邮件发送
        if (StrUtil.isNotEmpty(mailAddress)) {
            SingleSendMailRequest email = new SingleSendMailRequest();
            email.setToAddress(mailAddress.trim());
            URI uri = URLUtil.toURI(HttpContext.getRequest().getRequestURL().toString());
            String path = uri.toString().replaceAll(uri.getRawPath(), "");
            String name = StrUtil.subAfter(vssCode.getFilePath(), "/", true);
            email.setHtmlBody(String.format("你好，你的文件 %s 下载地址为：%s", name, path + "/" + vssCode.getFilePath()));
            email.setSubject(String.format("CSDN文件%s下载地址", name));
            return aliEmailHelper.sendDown(email);
        }
        return null;
    }

    @Override
    public ApiResponse getFile(DownloadReq downloadReq) throws Exception {
        //设置下载处理类
        HandleDownload handleDownload = getHandleDownload(downloadReq);

        handleDownload.checkUrl(downloadReq);

        //获取客户信息
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("customer_code", downloadReq.getCode());
        VssCustomer vssCustomer = vssCustomerService.selectOne(entityWrapper);
        if (vssCustomer != null) {
            //检查重复下载
            EntityWrapper entityWrapperCode = new EntityWrapper();
            entityWrapperCode.eq("down_url", downloadReq.getDownUrl());
            VssCode vssCode = selectOne(entityWrapperCode);
            boolean openReuse = "open".equals(ConstantFactory.me().getDictsByName("复用开关", 1));

            if (vssCode != null) {
                ApiResponse apiResponse = checkSelfDown(vssCode, downloadReq, openReuse);
                if (apiResponse != null) {
                    return apiResponse;
                }
            }

            //检验code状态
            checkCodeStatus(vssCustomer);

            //检查下载次数开始########################################
            if (vssCustomer.getDowned() >= vssCustomer.getMaxDown()) {
                throw new Exception(String.format("下载总次数%s次已用光！", vssCustomer.getMaxDown()));
            }
            //数据准备
            EntityWrapper entityWrapperSite = new EntityWrapper();
            entityWrapperSite.eq("abbr", downloadReq.getAbbr());
            VssSite vssSite = vssSiteService.selectOne(entityWrapperSite);

            VssPlan vssPlan = vssPlanService.selectById(vssCustomer.getPlanUuid());

            EntityWrapper entityWrapperPlanSite = new EntityWrapper();
            entityWrapperPlanSite.eq("plan_uuid", vssCustomer.getPlanUuid());
            entityWrapperPlanSite.eq("site_uuid", vssSite.getUuid());
            VssPlanSite vssPlanSite = vssPlanSiteService.selectOne(entityWrapperPlanSite);

            EntityWrapper entityWrapperCustomerSiteLog = new EntityWrapper();
            entityWrapperCustomerSiteLog.eq("customer_uuid", vssCustomer.getUuid());
            entityWrapperCustomerSiteLog.eq("site_uuid", vssSite.getUuid());
            VssCustomerSiteLog vssCustomerSiteLog = vssCustomerSiteLogService.selectOne(entityWrapperCustomerSiteLog);

            if (vssPlanSite.getDayDown() <= vssCustomerSiteLog.getDayDown()) {
                throw new Exception(String.format("超过当天%s下载次数%s次！", vssSite.getName(), vssPlanSite.getDayDown()));
            }


            //检查积分
            if (vssPlanSite.getIsCoin().equals("1")) {
                if(vssCustomerSiteLog.getValidCoin()<Integer.parseInt(vssCode.getCoin())){
                    throw new Exception(String.format("积分不足，需要%s积分", vssCode.getCoin()));
                }
                vssCustomerSiteLog.setUsedCoin(vssCustomerSiteLog.getUsedCoin() + Integer.parseInt(vssCode.getCoin()));
                vssCustomerSiteLog.setValidCoin(vssCustomerSiteLog.getTotalCoin() - vssCustomerSiteLog.getUsedCoin());
            } else {
                if(vssCustomerSiteLog.getValidCoin()<1){
                    throw new Exception("下载次数用完了");
                }
                vssCustomerSiteLog.setUsedCoin(vssCustomerSiteLog.getUsedCoin() + 1);
                vssCustomerSiteLog.setValidCoin(vssCustomerSiteLog.getTotalCoin() - vssCustomerSiteLog.getUsedCoin());
            }
            //开始下载
            if (vssCode == null) {
                //请求属性赋值
                vssCode = new VssCode();
                CommonUtils.copyProperties(downloadReq, vssCode);
                vssCode.setUuid(RandomUtil.randomString(32));
                vssCode.setCode(RandomUtil.randomString(32));
                vssCode.setCustomerUuid(vssCustomer.getUuid());
                vssCode.setOwner(vssCustomer.getOwnerName());
                vssCode.setReuseEnable(openReuse ? "1" : "2");
                vssCode.setSiteUuid(vssSite.getUuid());
                vssCode.setInsertTime(new Date());
                vssCode.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                vssCode.setUsedTime(new Timestamp(System.currentTimeMillis()));
                vssCode.setStatus(String.valueOf(VssCodeStatus.USED.ordinal()));
                //获取文件
                downFile(vssCode, downloadReq, vssPlanSite, vssCustomerSiteLog, vssCustomer, vssSite, handleDownload);
                insert(vssCode);
            }

            //更新日下载
            vssCustomerSiteLog.setDayDown(vssCustomerSiteLog.getDayDown() + 1);
            vssCustomerSiteLog.setDowned(vssCustomerSiteLog.getDowned() + 1);
            vssCustomerSiteLog.setLastTime(new Date());
            vssCustomerSiteLogService.updateById(vssCustomerSiteLog);

            //更新客户信息
            vssCustomer.setDowned(vssCustomer.getDowned() + 1);
            vssCustomerService.updateById(vssCustomer);

            //记录下载日志
            VssLog vssLog = new VssLog();
            BeanUtil.copyProperties(vssCode, vssLog);
            vssLog.setUuid(RandomUtil.randomUUID().replaceAll("-", ""));
            vssLog.setInsertTime(new Date());
            vssLog.setReuse("是");
            vssLog.setUserName("系统");
            vssLog.setSiteName(vssSite.getAbbr());
            vssLog.setOwner(vssCustomer.getOwnerName());
            vssLog.setCustomerName(vssCustomer.getName());
            vssLog.setCustomerPlanName(vssPlan.getName());
            vssLog.setEmail(downloadReq.getEmail());
//                vssLog.setIp();
            String coin = NumberUtil.isNumber(vssCode.getCoin()) ? vssCode.getCoin() : "0";
            vssLog.setCoin(Integer.parseInt(coin));
            vssLogService.insert(vssLog);

            ApiResponse apiResponse = ApiResponse.success(vssCode.getFilePath());
            Boolean sendResult = sendEmail(vssCode, downloadReq.getEmail());
            if (sendResult != null) {
                if (sendResult) {
                    apiResponse.setMsg("发送邮件成功");
                } else {
                    apiResponse.setMsg("发送邮件失败！请从网页下载");
                }
            } else {
                apiResponse.setMsg("");
            }
            Boolean isCrxFile = StrUtil.endWith(vssCode.getFilePath(), ".crx");
            apiResponse.setMsg(apiResponse.getMsg() + (isCrxFile == true ? ",插件请使用非谷歌浏览器下载！" : ""));
            return apiResponse;
        } else {
            throw new Exception("不存在该激活码！");
        }
    }

    /**
     * 自己重复下载自己的资源
     *
     * @param vssCode
     * @param downloadReq
     * @param openReuse
     * @return
     */
    private ApiResponse checkSelfDown(VssCode vssCode, DownloadReq downloadReq, boolean openReuse) {
        ApiResponse apiResponse = null;
        if (openReuse && "1".equals(vssCode.getReuseEnable()) && StrUtil.isNotEmpty(vssCode.getFilePath())) {
            if (downloadReq.getCode().equals(vssCode.getCustomerUuid())) {
                apiResponse = ApiResponse.success(vssCode.getFilePath());
                Boolean sendResult = sendEmail(vssCode, downloadReq.getEmail());
                if (sendResult != null) {
                    if (sendResult) {
                        apiResponse.setMsg("发送邮件成功");
                    } else {
                        apiResponse.setMsg("发送邮件失败！请从网页下载");
                    }
                } else {
                    apiResponse.setMsg("");
                }
            } else {
                EntityWrapper entityWrapper = new EntityWrapper();
                entityWrapper.eq("code", downloadReq.getCode());
                entityWrapper.eq("down_url", downloadReq.getDownUrl());
                VssLog vssLog = vssLogService.selectOne(entityWrapper);
                if (vssLog != null) {
                    apiResponse = ApiResponse.success(vssCode.getFilePath());
                    Boolean sendResult = sendEmail(vssCode, downloadReq.getEmail());
                    if (sendResult != null) {
                        if (sendResult) {
                            apiResponse.setMsg("发送邮件成功");
                        } else {
                            apiResponse.setMsg("发送邮件失败！请从网页下载");
                        }
                    } else {
                        apiResponse.setMsg("");
                    }
                }
            }

        }
        return apiResponse;
    }

    private void checkCodeStatus(VssCustomer vssCustomer) throws Exception {
        int status = NumberUtil.isNumber(vssCustomer.getStatus()) ? Integer.parseInt(vssCustomer.getStatus()) : 0;
        if (status == VssCodeStatus.NORMAL.ordinal()) {
            checkExpired(vssCustomer);
        }
        if (status == VssCodeStatus.USED.ordinal()) {
            throw new Exception("激活码已经使用！");
        } else if (status == VssCodeStatus.INVALID.ordinal()) {
            throw new Exception("激活码不可用状态！");
        } else {
            throw new Exception("激活码未知状态！");
        }


    }


    private void checkExpired(VssCustomer vssCustomer) throws Exception {
        if (vssCustomer.getExpiredTime() != null) {
            if (vssCustomer.getExpiredTime().getTime() < new Date().getTime()) {
                throw new Exception(String.format("激活码已过期！截止：%s", DateUtil.formatDate(vssCustomer.getExpiredTime())));
            }
        } else {
            String type = vssCustomer.getType();
            vssCustomer.setFirstTime(new DateTime());
            vssCustomer.setLastTime(new Date());
            if (String.valueOf(VssCustomerType.UNKNOW.ordinal()).equals(type)) {
                throw new Exception("激活码未知时间类型！");
            } else if (String.valueOf(VssCustomerType.DAY.ordinal()).equals(type)) {
                vssCustomer.setExpiredTime(DateUtil.offsetDay(vssCustomer.getFirstTime(), 1));
            } else if (String.valueOf(VssCustomerType.WEEK.ordinal()).equals(type)) {
                vssCustomer.setExpiredTime(DateUtil.offsetWeek(vssCustomer.getFirstTime(), 1));
            } else if (String.valueOf(VssCustomerType.MONTH.ordinal()).equals(type)) {
                vssCustomer.setExpiredTime(DateUtil.offsetMonth(vssCustomer.getFirstTime(), 1));
            } else if (String.valueOf(VssCustomerType.QUARTER.ordinal()).equals(type)) {
                vssCustomer.setExpiredTime(DateUtil.offsetMonth(vssCustomer.getFirstTime(), 3));
            } else if (String.valueOf(VssCustomerType.HAFLYEAR.ordinal()).equals(type)) {
                vssCustomer.setExpiredTime(DateUtil.offsetMonth(vssCustomer.getFirstTime(), 6));
            } else if (String.valueOf(VssCustomerType.YEAR.ordinal()).equals(type)) {
                vssCustomer.setExpiredTime(DateUtil.offsetMonth(vssCustomer.getFirstTime(), 12));
            } else if (String.valueOf(VssCustomerType.MANYYEAR.ordinal()).equals(type)) {
                vssCustomer.setExpiredTime(DateUtil.offsetMonth(vssCustomer.getFirstTime(), 120));
            } else if (String.valueOf(VssCustomerType.COUNT.ordinal()).equals(type)) {
                vssCustomer.setExpiredTime(DateUtil.offsetMonth(vssCustomer.getFirstTime(), 120));
            }
        }
    }

    private String downFile(VssCode vssCode, DownloadReq downloadReq, VssPlanSite customerPlanSite, VssCustomerSiteLog vssCustomerSiteLog, VssCustomer vssCustomer, VssSite vsssite, HandleDownload handleDownload) throws Exception {

        //获取下载信息
        DownloadInfo downloadInfo = handleDownload.getDownloadInfo(vssCode.getDownUrl());

        //判断积分是否足够
        String coinStr = downloadInfo.getCoin();

        if (!NumberUtil.isNumber(coinStr)) {
            throw new Exception("网络不稳定，请重试");
        } else {
            vssCode.setCoin(coinStr);
            vssCode.setName(downloadInfo.getTitle());
        }

        //检查积分
        Integer coin = Integer.parseInt(coinStr);
        if (vsssite.getBigCoinLine() < 0 || vsssite.getBigCoinLine() >= coin) {
            //大积分或者按此下载
            vssCode.setType(String.valueOf(VssUserType.VIP.ordinal()));
            coin = 1;
        } else {
            vssCode.setType(String.valueOf(VssUserType.NORMAL.ordinal()));
        }

        //获取下载用户
        HttpVssUser httpVssUser = httpVssUserHelper.getHttpVssUser(vsssite, vssCode, coin);

        //下载
        OperateResponse operateResponse = handleDownload.download(httpVssUser, vssCode);
        if (!operateResponse.isSuccess()) {
            SingleSendMailRequest email = new SingleSendMailRequest();
            email.setToAddress("805014823@qq.com");
            email.setHtmlBody(String.format("用户码: %s,激活码：%s ;拥有者：%s ;下载地址：%s ;邮箱：%s;网站账号：%s; 返回内容：%s", vssCustomer.getCustomerCode(), vssCode.getCode(), vssCode.getOwner(), vssCode.getDownUrl(), StrUtil.nullToEmpty(downloadReq.getEmail()), httpVssUser.getVssUser().getUsername(), operateResponse.getOutMessage().toString()));
            email.setSubject(String.format("%s下载请求出错", vsssite.getName()));
            aliEmailHelper.sendDown(email);
            throw new Exception("下载失败");
        } else {
            //记录下载日志
            //更新用户积分

            EntityWrapper entityWrapperUserSiteLog = new EntityWrapper();
            entityWrapperUserSiteLog.eq("user_uuid", httpVssUser.getVssUser().getUuid());
            entityWrapperUserSiteLog.eq("site_uuid", vsssite.getUuid());
            VssUserSiteLog vssUserSiteLog = vssUserSiteLogService.selectOne(entityWrapperUserSiteLog);

            EntityWrapper entityWrapperPlanSite= new EntityWrapper();
            entityWrapperPlanSite.eq("site_uuid", vsssite.getUuid());
            entityWrapperPlanSite.eq("plan_uuid", httpVssUser.getVssUser().getPlanUuid());
            VssPlanSite vssPlanSite=vssPlanSiteService.selectOne(entityWrapperPlanSite);

            vssUserSiteLog.setLastTime(new Date());
            vssUserSiteLog.setDayDown(vssUserSiteLog.getDayDown() + 1);
            vssUserSiteLog.setDowned(vssUserSiteLog.getDowned() + 1);
            if (!vssPlanSite.getIsCoin().equals("1")) {
                vssUserSiteLog.setUsedCoin(vssCustomerSiteLog.getUsedCoin() + 1);
                vssUserSiteLog.setValidCoin(vssUserSiteLog.getTotalCoin() - vssUserSiteLog.getUsedCoin());
            } else {
                vssUserSiteLog.setUsedCoin(vssCustomerSiteLog.getUsedCoin() + Integer.parseInt(vssCode.getCoin()));
                vssUserSiteLog.setValidCoin(vssUserSiteLog.getTotalCoin() - vssUserSiteLog.getUsedCoin());
            }
            vssUserSiteLogService.updateById(vssUserSiteLog);

            VssUser vssUser = httpVssUser.getVssUser();
            vssUser.setDowned(vssUser.getDowned() + 1);
            vssUser.setLastLoginTime(new Date());
            vssUserService.updateById(vssUser);
            return operateResponse.getOutMessage().toString();
        }
    }

    public HandleDownload getHandleDownload(DownloadReq downloadReq) throws Exception {
        if (UrlSource.CSDN.name().equals(downloadReq.getAbbr())) {
            return SpringUtil.getBean(CsdnHandleDownload.class);
        } else {
            throw new Exception("暂不支持该网站下载或者输入正确下载地址重试！");
        }
    }

}
