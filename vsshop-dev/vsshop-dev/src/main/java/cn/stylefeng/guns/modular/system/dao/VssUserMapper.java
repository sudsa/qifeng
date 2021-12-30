package cn.stylefeng.guns.modular.system.dao;

import cn.stylefeng.guns.modular.system.model.VssUser;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-05-26
 */
public interface VssUserMapper extends BaseMapper<VssUser> {
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
    List<VssUser> getNormalCoinUser(@Param("siteUuid") String siteUuid,@Param("coin") Integer coin);
    /**
     * 获取普通积分账号
     * @param siteUuid
     * @return
     */
    List<VssUser> getDownCountUser(String siteUuid);
}
