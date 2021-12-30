package cn.stylefeng.guns.modular.vsshop.controller;

import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.factory.PageFactory;
import cn.stylefeng.guns.core.common.page.PageInfoBT;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.model.VssCustomer;
import cn.stylefeng.guns.modular.system.model.VssCustomerSiteLog;
import cn.stylefeng.guns.modular.system.model.VssPlan;
import cn.stylefeng.guns.modular.vsshop.service.IVssPlanService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import org.springframework.web.bind.annotation.RequestParam;
import cn.stylefeng.guns.modular.system.model.VssPlanSite;
import cn.stylefeng.guns.modular.vsshop.service.IVssPlanSiteService;

/**
 * 计划网站控制器
 *
 * @author fengshuonan
 * @Date 2019-05-26 16:24:27
 */
@Controller
@RequestMapping("/vssPlanSite")
public class VssPlanSiteController extends BaseController {

    private String PREFIX = "/vsshop/vssPlanSite/";

    @Autowired
    private IVssPlanSiteService vssPlanSiteService;
    @Autowired
    private IVssPlanService vssPlanService;
    /**
     * 跳转到计划网站首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssPlanSite.html";
    }

    /**
     * 跳转到添加计划网站
     */
    @RequestMapping("/vssPlanSite_add")
    public String vssPlanSiteAdd() {
        return PREFIX + "vssPlanSite_add.html";
    }

    /**
     * 跳转到修改计划网站
     */
    @RequestMapping("/vssPlanSite_update/{vssPlanSiteId}")
    public String vssPlanSiteUpdate(@PathVariable String vssPlanSiteId, Model model) {
        VssPlanSite vssPlanSite = vssPlanSiteService.selectById(vssPlanSiteId);
        model.addAttribute("item",vssPlanSite);
        LogObjectHolder.me().set(vssPlanSite);
        return PREFIX + "vssPlanSite_edit.html";
    }

    /**
     * 获取计划网站列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @Permission
    public Object list(VssPlanSite vssPlanSite) {
        if(!ShiroKit.getUser().getRoleNames().contains("超级管理员")){
            String ownerUuid=String.valueOf(ShiroKit.getUser().getId());
            EntityWrapper entityWrapper=new EntityWrapper();
            entityWrapper.eq("create_uuid",ownerUuid);
            VssPlan vssPlan=vssPlanService.selectOne(entityWrapper);
            vssPlanSite.setPlanUuid(vssPlan.getCreateUuid());
        }
        Page<VssPlanSite> page = new PageFactory<VssPlanSite>().defaultPage();
        EntityWrapper entityWrapper=new EntityWrapper<>(vssPlanSite);
        entityWrapper.orderBy("insert_time",false);
        page.setRecords(vssPlanSiteService.selectPage(page,entityWrapper).getRecords());
        return new PageInfoBT<>(page);
    }

    /**
     * 新增计划网站
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    @Permission
    public Object add(VssPlanSite vssPlanSite) {
        vssPlanSiteService.insert(vssPlanSite);
        return SUCCESS_TIP;
    }

    /**
     * 删除计划网站
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @Permission
    public Object delete(@PathVariable String vssPlanSiteId) {
        vssPlanSiteService.deleteById(vssPlanSiteId);
        return SUCCESS_TIP;
    }

    /**
     * 修改计划网站
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    @Permission
    public Object update(VssPlanSite vssPlanSite) {
        vssPlanSiteService.updateById(vssPlanSite);
        return SUCCESS_TIP;
    }

    /**
     * 计划网站详情
     */
    @RequestMapping(value = "/detail/{vssPlanSiteId}")
    @ResponseBody
    @Permission
    public Object detail(@PathVariable("vssPlanSiteId") String vssPlanSiteId) {
        return vssPlanSiteService.selectById(vssPlanSiteId);
    }
}
