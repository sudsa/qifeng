package cn.stylefeng.guns.modular.vsshop.service;

import cn.stylefeng.guns.modular.system.model.VssCode;
import cn.stylefeng.guns.modular.vsshop.model.ApiResponse;
import cn.stylefeng.guns.modular.vsshop.model.DownloadReq;
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
    ApiResponse getFile(DownloadReq downloadReq) throws Exception;
}
