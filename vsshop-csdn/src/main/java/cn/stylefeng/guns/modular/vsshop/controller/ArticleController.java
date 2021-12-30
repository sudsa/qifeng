package cn.stylefeng.guns.modular.vsshop.controller;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.view.PoiBaseView;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.guns.core.common.constant.factory.PageFactory;
import cn.stylefeng.guns.core.common.page.PageInfoBT;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.vsshop.common.OssHelper;
import cn.stylefeng.guns.modular.vsshop.model.ApiResponse;
import cn.stylefeng.guns.modular.vsshop.model.VipArticle;
import cn.stylefeng.guns.modular.vsshop.service.IArticleService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * VipArticle控制器
 *
 * @author fengshuonan
 * @Date 2019-08-28 17:43:18
 */
@Controller
@RequestMapping("/article")
public class ArticleController extends BaseController {

    private String PREFIX = "/vsshop/article/";

    @Autowired
    private IArticleService articleService;
    @Autowired
    OssHelper ossHelper;

    /**
     * 跳转到VipArticle首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "article.html";
    }

    /**
     * 跳转到添加VipArticle
     */
    @RequestMapping("/article_add")
    public String articleAdd() {
        return PREFIX + "article_add.html";
    }

    /**
     * 跳转到修改VipArticle
     */
    @RequestMapping("/article_update/{articleId}")
    public String articleUpdate(@PathVariable String articleId, Model model) {
        VipArticle article = articleService.selectById(articleId);
        model.addAttribute("item", article);
        LogObjectHolder.me().set(article);
        return PREFIX + "article_edit.html";
    }

    /**
     * 获取VipArticle列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(VipArticle article) {
        if (!ShiroKit.getUser().getRoleNames().contains("超级管理员")) {
            article.setOwner(ShiroKit.getUser().getName());
        }
        if (StrUtil.isEmpty(article.getCode())) {
            article.setCode(null);
        } else {
            article.setCode(article.getCode().trim());
        }
        Page<VipArticle> page = new PageFactory<VipArticle>().defaultPage();
        EntityWrapper entityWrapper = new EntityWrapper<>();
        if (StrUtil.isNotBlank(article.getBeginTime())) {
            entityWrapper.ge("update_time", article.getBeginTime());
        }
        if (StrUtil.isNotBlank(article.getEndTime())) {
            entityWrapper.le("update_time", article.getEndTime());
        }
        if (StrUtil.isNotBlank(article.getCode())) {
            entityWrapper.eq("code", article.getCode());
        }
        if (StrUtil.isNotBlank(article.getStatus()) && !article.getStatus().equals("999")) {
            entityWrapper.eq("status", article.getStatus());
        }
        if (StrUtil.isNotBlank(article.getOwner())) {
            entityWrapper.eq("owner", article.getOwner());
        }

        entityWrapper.orderBy("update_time", false);
        page.setRecords(articleService.selectPage(page, entityWrapper).getRecords());
        return new PageInfoBT<>(page);
    }

    /**
     * 新增VipArticle
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(@RequestParam Integer num) {
        if (num == null) {
            return ApiResponse.fail("数量不能为空");
        }

        if (num > 10000) {
            return ApiResponse.fail("一次最多生成10000");
        }
        articleService.createCode(num);
        return ApiResponse.success("生成成功！");
    }

    /**
     * 删除VipArticle
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String articleId) {
        articleService.deleteById(articleId);
        return SUCCESS_TIP;
    }

    /**
     * 修改VipArticle
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(VipArticle article) {
        articleService.updateById(article);
        return SUCCESS_TIP;
    }

    /**
     * VipArticle详情
     */
    @RequestMapping(value = "/detail/{articleId}")
    @ResponseBody
    public Object detail(@PathVariable("articleId") Integer articleId) {
        return articleService.selectById(articleId);
    }

    /**
     * 下载激活码
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void detailListExport(VipArticle vssCodeQuery, ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        ExportParams params = new ExportParams("文章激活码", "1", ExcelType.XSSF);
        params.setFreezeCol(2);
        try {
            if (!ShiroKit.getUser().getRoleNames().contains("超级管理员")) {
                vssCodeQuery.setOwner(ShiroKit.getUser().getName());
            }
            if (StrUtil.isEmpty(vssCodeQuery.getCode())) {
                vssCodeQuery.setCode(null);
            }
            EntityWrapper entityWrapper = new EntityWrapper<>();
            if (StrUtil.isNotBlank(vssCodeQuery.getBeginTime())) {
                entityWrapper.ge("update_time", vssCodeQuery.getBeginTime());
            }
            if (StrUtil.isNotBlank(vssCodeQuery.getEndTime())) {
                entityWrapper.le("update_time", vssCodeQuery.getEndTime());
            }
            if (StrUtil.isNotBlank(vssCodeQuery.getCode())) {
                entityWrapper.eq("code", vssCodeQuery.getCode());
            }
            if (StrUtil.isNotBlank(vssCodeQuery.getStatus()) && !vssCodeQuery.getStatus().equals("999")) {
                entityWrapper.eq("status", vssCodeQuery.getStatus());
            }
            if (StrUtil.isNotBlank(vssCodeQuery.getOwner())) {
                entityWrapper.eq("owner", vssCodeQuery.getOwner());
            }
            entityWrapper.orderBy("update_time", false);
            List<VipArticle> vssCodeListTemp = articleService.selectList(entityWrapper);
            vssCodeListTemp.forEach(vssCode -> {
                String status = vssCode.getStatus();
                if ("1".equals(status)) {
                    vssCode.setStatus("正常");
                } else if ("2".equals(status)) {
                    vssCode.setStatus("已使用");
                } else if ("3".equals(status)) {
                    vssCode.setStatus("废弃");
                }
//                vssCode.setCode(String.format("http://%s/downArticle?code=%s", ConstantFactory.me().getDictsByName("文章域名", 1), vssCode.getCode()));
                vssCode.setCode(String.format("http://%s/downArticle?code=%s", ossHelper.getWebDomain(), vssCode.getCode()));
            });
            map.put(NormalExcelConstants.DATA_LIST, vssCodeListTemp); // 数据集合
            map.put(NormalExcelConstants.CLASS, VipArticle.class);//导出实体
            map.put(NormalExcelConstants.PARAMS, params);//参数
            map.put(NormalExcelConstants.FILE_NAME, String.format("%s个-%s", vssCodeListTemp.size(), DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN)));//文件名称
        } catch (Exception e) {
            ApiResponse.fail("下载激活码失败！" + e.getMessage());
        }

        PoiBaseView.render(map, request, response, NormalExcelConstants.EASYPOI_EXCEL_VIEW);
        ;//View名称
    }
}
