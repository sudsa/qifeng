package cn.stylefeng.guns.modular.vsshop;

import cn.hutool.core.collection.CollUtil;
import cn.stylefeng.guns.InitSpringBoot;
import cn.stylefeng.guns.modular.system.dao.UserMapper;
import cn.stylefeng.guns.modular.system.model.User;
import cn.stylefeng.guns.modular.vsshop.model.VssUser;
import cn.stylefeng.guns.modular.vsshop.service.IVssUserService;
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
    UserMapper userMapper;


    @Override
    public String toString() {
        return super.toString();
    }

    //每1分钟执行一次
    @Scheduled(cron = "0 2 0 1/1 * ? ")
    public void updateDayDown(){
        logger.info("开始重置当天下载次数");
        try {

            List<VssUser> vssUserList=vssUserService.selectList(null);
            if(CollUtil.isNotEmpty(vssUserList)){
                vssUserList.forEach(vssUser -> {
                    vssUser.setDayDown(0);
                });
            }
            vssUserService.updateBatchById(vssUserList);

            List<User> userList=userMapper.selectList(null);
            if(CollUtil.isNotEmpty(userList)){
                userList.forEach(user -> {
                    user.setDaydown(0);
                    userMapper.updateById(user);
                });
            }

        }catch (Exception e){
            logger.info(String.format("重置当天下载次数出错；%s", e.getMessage()));
        }
        logger.info("开始重置当天下载次数完成！");
    }
}
