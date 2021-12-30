package cn.stylefeng.guns.modular.vsshop.service.impl;

import cn.stylefeng.guns.modular.system.dao.VssUserMapper;
import cn.stylefeng.guns.modular.system.model.VssUser;
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
    public List<VssUser> getVipCoinUser(String siteUuid) {
        return vssUserMapper.getVipCoinUser(siteUuid);
    }

    @Override
    public List<VssUser> getNormalCoinUser(String siteUuid, Integer coin) {
        return vssUserMapper.getNormalCoinUser(siteUuid,coin);
    }

    @Override
    public List<VssUser> getDownCountUser(String siteUuid) {
        return vssUserMapper.getDownCountUser(siteUuid);
    }
}
