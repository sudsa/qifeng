package cn.stylefeng.guns.modular.vsshop.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.model.VssCustomer;
import cn.stylefeng.guns.modular.system.dao.VssCustomerMapper;
import cn.stylefeng.guns.modular.system.model.VssCustomerSiteLog;
import cn.stylefeng.guns.modular.system.model.VssPlan;
import cn.stylefeng.guns.modular.system.model.VssPlanSite;
import cn.stylefeng.guns.modular.vsshop.model.CreateCodeReq;
import cn.stylefeng.guns.modular.vsshop.model.UserType;
import cn.stylefeng.guns.modular.vsshop.service.IVssCustomerService;
import cn.stylefeng.guns.modular.vsshop.service.IVssCustomerSiteLogService;
import cn.stylefeng.guns.modular.vsshop.service.IVssPlanService;
import cn.stylefeng.guns.modular.vsshop.service.IVssPlanSiteService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2019-05-26
 */
@Service
public class VssCustomerServiceImpl extends ServiceImpl<VssCustomerMapper, VssCustomer> implements IVssCustomerService {


    @Autowired
    IVssPlanService vssPlanService;
    @Autowired
    IVssPlanSiteService vssPlanSiteService;
    @Autowired
    IVssCustomerSiteLogService vssCustomerSiteLogService;
    @Override
    public void createCode(CreateCodeReq createCodeReq) throws Exception {
        VssPlan vssPlan=vssPlanService.selectById(createCodeReq.getPlanUuid());
        EntityWrapper entityWrapperPlanSite=new EntityWrapper();
        entityWrapperPlanSite.eq("plan_uuid",vssPlan.getUuid());
        List<VssPlanSite> vssPlanSiteList=new ArrayList<>();
        vssPlanSiteList.addAll(createCodeReq.getCoinList());
        vssPlanSiteList.addAll(createCodeReq.getCountList());

        if(CollUtil.isEmpty(vssPlanSiteList)){
            throw new Exception("请先配置计划");
        }
        for(int i=0;i<createCodeReq.getNum();i++) {

            try {

                vssPlanSiteService.updateBatchById(vssPlanSiteList);
                VssCustomer vssCustomer = new VssCustomer();
                vssCustomer.setOwnerName(ShiroKit.getUser().getName());
                vssCustomer.setOwnerUuid(String.valueOf(ShiroKit.getUser().getId()));
                vssCustomer.setCustomerCode(RandomUtil.randomUUID().replaceAll("-", ""));
                vssCustomer.setDowned(0);
                vssCustomer.setInsertTime(new Date());
                vssCustomer.setMaxDown(1000);
                vssCustomer.setType(createCodeReq.getDateType());
                vssCustomer.setPlanUuid(createCodeReq.getPlanUuid());
                vssCustomer.setPlanName(vssPlan.getName());
                vssCustomer.setStatus("1");
                vssCustomer.setName(RandomUtil.randomString(10));
                vssCustomer.setPwd(RandomUtil.randomString(10));
                insert(vssCustomer);
                for (int m = 0; m < vssPlanSiteList.size(); m++) {
                    VssPlanSite vssPlanSite = vssPlanSiteList.get(m);
                    if (vssPlanSite.getIsCoin().equals("1")) {
                        //如果是积分类型
                        VssCustomerSiteLog vssCustomerSiteLog = new VssCustomerSiteLog();
                        vssCustomerSiteLog.setDayDown(vssPlanSite.getDayDown());
                        vssCustomerSiteLog.setDowned(0);
                        vssCustomerSiteLog.setTotalCoin(vssPlanSite.getCoin());
                        vssCustomerSiteLog.setInsertTime(new Date());
                        vssCustomerSiteLog.setLastTime(new Date());
                        vssCustomerSiteLog.setCustomerUuid(vssCustomer.getUuid());
                        vssCustomerSiteLog.setSiteUuid(vssPlanSite.getUuid());
                        vssCustomerSiteLog.setUsedCoin(0);
                        vssCustomerSiteLog.setValidCoin(vssPlanSite.getCoin());
                        vssCustomerSiteLog.setUuid(RandomUtil.randomUUID().replaceAll("-", ""));
                        vssCustomerSiteLogService.insert(vssCustomerSiteLog);
                    } else {
                        //不是积分类型
                        VssCustomerSiteLog vssCustomerSiteLog = new VssCustomerSiteLog();
                        vssCustomerSiteLog.setDayDown(vssPlanSite.getDayDown());
                        vssCustomerSiteLog.setDowned(0);
                        vssCustomerSiteLog.setTotalCoin(1000);
                        vssCustomerSiteLog.setInsertTime(new Date());
                        vssCustomerSiteLog.setLastTime(new Date());
                        vssCustomerSiteLog.setCustomerUuid(vssCustomer.getUuid());
                        vssCustomerSiteLog.setSiteUuid(vssPlanSite.getUuid());
                        vssCustomerSiteLog.setUsedCoin(0);
                        vssCustomerSiteLog.setValidCoin(1000);
                        vssCustomerSiteLog.setUuid(RandomUtil.randomUUID().replaceAll("-", ""));
                        vssCustomerSiteLogService.insert(vssCustomerSiteLog);
                    }

                }
            } catch (Exception e) {
                throw new Exception(String.format("生成失败！%s", e.getMessage()));
            }
        }
    }
}
