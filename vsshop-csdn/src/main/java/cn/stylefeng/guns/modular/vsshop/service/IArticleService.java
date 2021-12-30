package cn.stylefeng.guns.modular.vsshop.service;

import cn.stylefeng.guns.modular.system.model.Article;
import cn.stylefeng.guns.modular.vsshop.model.VipArticle;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wm
 * @since 2019-08-28
 */
public interface IArticleService extends IService<VipArticle> {

    void createCode(Integer num);
}
