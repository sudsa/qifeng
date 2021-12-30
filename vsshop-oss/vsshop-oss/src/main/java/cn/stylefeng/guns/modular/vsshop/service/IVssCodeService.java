package cn.stylefeng.guns.modular.vsshop.service;

import cn.stylefeng.guns.modular.system.model.VssIp;
import cn.stylefeng.guns.modular.vsshop.model.ApiResponse;
import cn.stylefeng.guns.modular.vsshop.model.VssCode;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jianyinlin
 * @since 2019-03-09
 */
public interface IVssCodeService extends IService<VssCode> {

    ApiResponse getFile(VssCode vssCode, VssIp vssIp) throws Exception;
    String downFile(VssCode vssCode, VssIp vssIp) throws Exception;

    void createCode(String source, String type, Integer num);
}
