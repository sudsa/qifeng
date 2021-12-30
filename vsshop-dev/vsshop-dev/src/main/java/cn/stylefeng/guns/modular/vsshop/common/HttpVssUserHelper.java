package cn.stylefeng.guns.modular.vsshop.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.guns.modular.system.model.VssCode;
import cn.stylefeng.guns.modular.system.model.VssSite;
import cn.stylefeng.guns.modular.system.model.VssUser;
import cn.stylefeng.guns.modular.vsshop.model.HttpVssUser;
import cn.stylefeng.guns.modular.vsshop.service.IVssUserService;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class HttpVssUserHelper {
    public Logger log=LoggerFactory.getLogger(this.getClass());

    @Autowired
    IVssUserService vssUserService;
    @Autowired
    AliEmailHelper aliEmailHelper;
    /**
     * 随机获取下载用户
     *.
     * @return
     */
    public HttpVssUser getHttpVssUser(VssSite vsssite, VssCode vssCode,Integer coin) throws Exception {

        if(String.valueOf(VssUserType.NORMAL.ordinal()).equals(vssCode.getType())){
            List<VssUser> vssUserList= vssUserService.getNormalCoinUser(vsssite.getUuid(),coin);
            if(CollUtil.isEmpty(vssUserList)){
                //如果普通用完，用vip
                vssCode.setType(String.valueOf(VssUserType.VIP.ordinal()));
                vssUserList=vssUserService.getVipCoinUser(vsssite.getUuid());
                if(CollUtil.isNotEmpty(vssUserList)){
                    return new HttpVssUser(OKHttpHelper.buildOKHttpClient().build(),vssUserList.get(RandomUtil.randomInt(vssUserList.size())));
                }
            }else {
                return new HttpVssUser(OKHttpHelper.buildOKHttpClient().build(),vssUserList.get(RandomUtil.randomInt(vssUserList.size())));

            }
        }else if(String.valueOf(VssUserType.VIP.ordinal()).equals(vssCode.getType())){
            List<VssUser> vssUserList=vssUserService.getVipCoinUser(vsssite.getUuid());
            if(CollUtil.isNotEmpty(vssUserList)){
                return new HttpVssUser(OKHttpHelper.buildOKHttpClient().build(),vssUserList.get(RandomUtil.randomInt(vssUserList.size())));
            }
        }

        SingleSendMailRequest email=new SingleSendMailRequest();
        email.setToAddress("805014823@qq.com");
        email.setHtmlBody(String.format("%s无可用账号；需要积分：%s",vsssite.getName(),vssCode.getCoin()));
        email.setSubject("自动发货警告");
        aliEmailHelper.sendDown(email);
        throw new Exception(String.format("%s账号不足，联系店家", vsssite.getName()));
    }

}
