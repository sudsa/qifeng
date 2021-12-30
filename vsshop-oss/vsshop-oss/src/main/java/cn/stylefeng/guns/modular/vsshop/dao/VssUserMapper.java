package cn.stylefeng.guns.modular.vsshop.dao;

import cn.stylefeng.guns.modular.vsshop.model.VssUser;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jianyinlin
 * @since 2019-03-09
 */
public interface VssUserMapper extends BaseMapper<VssUser> {

    List<VssUser> getVipCoinUser(@Param("ip_uuid")String ip_uuid,@Param("siteUuid")String siteUuid);

    List<VssUser> getNormalCoinUser(@Param("ip_uuid")String ip_uuid,@Param("siteUuid") String siteUuid, @Param("coin")Integer coin);
}
