package cn.stylefeng.guns.modular.vsshop.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.model.Article;
import cn.stylefeng.guns.modular.system.dao.ArticleMapper;
import cn.stylefeng.guns.modular.vsshop.common.VssCodeStatus;
import cn.stylefeng.guns.modular.vsshop.model.VipArticle;
import cn.stylefeng.guns.modular.vsshop.model.VssCode;
import cn.stylefeng.guns.modular.vsshop.service.IArticleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wm
 * @since 2019-08-28
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, VipArticle> implements IArticleService {

    @Override
    public void createCode(Integer num) {
        List<VipArticle> vssCodeList=new ArrayList<>();
        for(int i=0;i<num;i++){
            VipArticle vssCodeTemp=new VipArticle();
            vssCodeTemp.setUuid(RandomUtil.randomUUID().replaceAll("-",""));
            vssCodeTemp.setCode(RandomUtil.randomString(8));
            vssCodeTemp.setInsertTime(new Timestamp(System.currentTimeMillis()));
            vssCodeTemp.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            vssCodeTemp.setStatus(String.valueOf(VssCodeStatus.NORMAL.ordinal()));
            vssCodeTemp.setOwner(ShiroKit.getUser().getId()+"");
            vssCodeList.add(vssCodeTemp);
        }
        insertBatch(vssCodeList);
    }
}
