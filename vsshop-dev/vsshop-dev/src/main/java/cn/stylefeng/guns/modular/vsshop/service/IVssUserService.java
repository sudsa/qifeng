package cn.stylefeng.guns.modular.vsshop.service;

import cn.stylefeng.guns.modular.system.model.VssUser;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jianyinlin
 * @since 2019-03-09
 */
public interface IVssUserService extends IService<VssUser> {
    /**
     * 获取大积分账号
     * @param siteUuid
     * @return
     */
    List<VssUser> getVipCoinUser(String siteUuid);
    /**
     * 获取普通积分账号
     * @param siteUuid
     * @return
     */
    List<VssUser> getNormalCoinUser(String siteUuid,Integer coin);
    /**
     * 获取普通积分账号
     * @param siteUuid
     * @return
     */
    List<VssUser> getDownCountUser(String siteUuid);
}
