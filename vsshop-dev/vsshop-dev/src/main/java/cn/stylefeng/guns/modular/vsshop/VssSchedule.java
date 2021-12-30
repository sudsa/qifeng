package cn.stylefeng.guns.modular.vsshop;

import cn.hutool.core.collection.CollUtil;
import cn.stylefeng.guns.modular.system.model.VssCustomerSiteLog;
import cn.stylefeng.guns.modular.system.model.VssUser;
import cn.stylefeng.guns.modular.system.model.VssUserSiteLog;
import cn.stylefeng.guns.modular.vsshop.common.HttpVssUserHelper;
import cn.stylefeng.guns.modular.vsshop.service.IVssCustomerSiteLogService;
import cn.stylefeng.guns.modular.vsshop.service.IVssUserService;
import cn.stylefeng.guns.modular.vsshop.service.IVssUserSiteLogService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Configurable
@EnableScheduling
public class VssSchedule {
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    IVssUserService vssUserService;
    @Autowired
    IVssUserSiteLogService vssUserSiteLogService;
    @Autowired
    IVssCustomerSiteLogService vssCustomerSiteLogService;

    //每1分钟执行一次
    @Scheduled(cron = "0 2 0 1/1 * ? ")
    public void updateDayDown(){
        logger.info("开始重置当天下载次数");
        try {
            VssUserSiteLog vssUserSiteLog=new VssUserSiteLog();
            vssUserSiteLog.setDayDown(0);
            vssUserSiteLogService.update(vssUserSiteLog,new EntityWrapper<>());

            VssCustomerSiteLog vssCustomerSiteLog=new VssCustomerSiteLog();
            vssUserSiteLog.setDayDown(0);
            vssCustomerSiteLogService.update(vssCustomerSiteLog,new EntityWrapper<>());

            logger.info("更新数据库当天下载次数完成！");
        }catch (Exception e){
            logger.info(String.format("重置当天下载次数出错；%s", e.getMessage()));
        }
        logger.info("开始重置当天下载次数完成！");
    }
}
