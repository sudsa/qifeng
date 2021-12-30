package cn.stylefeng.guns.modular.vsshop.controller;

import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.factory.PageFactory;
import cn.stylefeng.guns.core.common.page.PageInfoBT;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.model.VssCustomer;
import cn.stylefeng.guns.modular.system.model.VssCustomerSiteLog;
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
import cn.stylefeng.guns.modular.system.model.VssPlan;
import cn.stylefeng.guns.modular.vsshop.service.IVssPlanService;

/**
 * 计划管理控制器
 *
 * @author fengshuonan
 * @Date 2019-05-26 16:23:30
 */
@Controller
@RequestMapping("/vssPlan")
public class VssPlanController extends BaseController {

    private String PREFIX = "/vsshop/vssPlan/";

    @Autowired
    private IVssPlanService vssPlanService;

    /**
     * 跳转到计划管理首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssPlan.html";
    }

    /**
     * 跳转到添加计划管理
     */
    @RequestMapping("/vssPlan_add")
    public String vssPlanAdd() {
        return PREFIX + "vssPlan_add.html";
    }

    /**
     * 跳转到修改计划管理
     */
    @RequestMapping("/vssPlan_update/{vssPlanId}")
    public String vssPlanUpdate(@PathVariable String vssPlanId, Model model) {
        VssPlan vssPlan = vssPlanService.selectById(vssPlanId);
        model.addAttribute("item",vssPlan);
        LogObjectHolder.me().set(vssPlan);
        return PREFIX + "vssPlan_edit.html";
    }

    /**
     * 获取计划管理列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @Permission
    public Object list(VssPlan vssPlan) {
        if(!ShiroKit.getUser().getRoleNames().contains("超级管理员")){
            String ownerUuid=String.valueOf(ShiroKit.getUser().getId());
           vssPlan.setCreateUuid(ownerUuid);
        }
        Page<VssPlan> page = new PageFactory<VssPlan>().defaultPage();
        EntityWrapper entityWrapper=new EntityWrapper<>(vssPlan);
        entityWrapper.orderBy("insert_time",false);
        page.setRecords(vssPlanService.selectPage(page,entityWrapper).getRecords());
        return new PageInfoBT<>(page);
    }

    /**
     * 新增计划管理
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    @Permission
    public Object add(VssPlan vssPlan) {
        vssPlanService.insert(vssPlan);
        return SUCCESS_TIP;
    }

    /**
     * 删除计划管理
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @Permission
    public Object delete(@PathVariable String vssPlanId) {
        vssPlanService.deleteById(vssPlanId);
        return SUCCESS_TIP;
    }

    /**
     * 修改计划管理
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    @Permission
    public Object update(VssPlan vssPlan) {
        vssPlanService.updateById(vssPlan);
        return SUCCESS_TIP;
    }

    /**
     * 计划管理详情
     */
    @RequestMapping(value = "/detail/{vssPlanId}")
    @ResponseBody
    @Permission
    public Object detail(@PathVariable("vssPlanId") String vssPlanId) {
        return vssPlanService.selectById(vssPlanId);
    }
}
