package cn.stylefeng.guns.modular.vsshop.service;

import cn.stylefeng.guns.modular.system.model.VssCustomer;
import cn.stylefeng.guns.modular.vsshop.model.CreateCodeReq;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author stylefeng
 * @since 2019-05-26
 */
public interface IVssCustomerService extends IService<VssCustomer> {

    void createCode(CreateCodeReq createCodeReq) throws Exception;
}
