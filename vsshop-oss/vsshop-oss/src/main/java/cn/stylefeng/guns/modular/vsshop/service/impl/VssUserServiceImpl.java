package cn.stylefeng.guns.modular.vsshop.service.impl;

import cn.stylefeng.guns.modular.vsshop.model.VssUser;
import cn.stylefeng.guns.modular.vsshop.dao.VssUserMapper;
import cn.stylefeng.guns.modular.vsshop.service.IVssUserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jianyinlin
 * @since 2019-03-09
 */
@Service
public class VssUserServiceImpl extends ServiceImpl<VssUserMapper, VssUser> implements IVssUserService {

    @Autowired
    VssUserMapper vssUserMapper;

    @Override
    public List<VssUser> getVipCoinUser(String ip_uuid,String siteUuid) {
        return vssUserMapper.getVipCoinUser(ip_uuid,siteUuid);
    }

    @Override
    public List<VssUser> getNormalCoinUser(String ip_uuid,String siteUuid, Integer coin) {
        return vssUserMapper.getNormalCoinUser(ip_uuid,siteUuid,coin);
    }

}
