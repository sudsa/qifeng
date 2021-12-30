package cn.stylefeng.guns.modular.vsshop.controller;

import cn.hutool.core.util.StrUtil;
import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.factory.PageFactory;
import cn.stylefeng.guns.core.common.page.PageInfoBT;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.model.VssCustomer;
import cn.stylefeng.guns.modular.vsshop.service.IVssCustomerService;
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
import cn.stylefeng.guns.modular.system.model.VssCustomerSiteLog;
import cn.stylefeng.guns.modular.vsshop.service.IVssCustomerSiteLogService;

/**
 * 客户网站日志控制器
 *
 * @author fengshuonan
 * @Date 2019-05-26 16:22:02
 */
@Controller
@RequestMapping("/vssCustomerSiteLog")
public class VssCustomerSiteLogController extends BaseController {

    private String PREFIX = "/vsshop/vssCustomerSiteLog/";

    @Autowired
    private IVssCustomerSiteLogService vssCustomerSiteLogService;
    @Autowired
    private IVssCustomerService vssCustomerService;

    /**
     * 跳转到客户网站日志首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssCustomerSiteLog.html";
    }

    /**
     * 跳转到添加客户网站日志
     */
    @RequestMapping("/vssCustomerSiteLog_add")
    public String vssCustomerSiteLogAdd() {
        return PREFIX + "vssCustomerSiteLog_add.html";
    }

    /**
     * 跳转到修改客户网站日志
     */
    @RequestMapping("/vssCustomerSiteLog_update/{vssCustomerSiteLogId}")
    public String vssCustomerSiteLogUpdate(@PathVariable String vssCustomerSiteLogId, Model model) {
        VssCustomerSiteLog vssCustomerSiteLog = vssCustomerSiteLogService.selectById(vssCustomerSiteLogId);
        model.addAttribute("item",vssCustomerSiteLog);
        LogObjectHolder.me().set(vssCustomerSiteLog);
        return PREFIX + "vssCustomerSiteLog_edit.html";
    }

    /**
     * 获取客户网站日志列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @Permission
    public Object list(VssCustomerSiteLog vssCustomerSiteLog) {
        if(!ShiroKit.getUser().getRoleNames().contains("超级管理员")){
            String ownerUuid=String.valueOf(ShiroKit.getUser().getId());
            EntityWrapper entityWrapper=new EntityWrapper();
            entityWrapper.eq("owner_uuid",ownerUuid);
            VssCustomer vssCustomer=vssCustomerService.selectOne(entityWrapper);
            vssCustomerSiteLog.setCustomerUuid(vssCustomer.getUuid());
        }
        Page<VssCustomerSiteLog> page = new PageFactory<VssCustomerSiteLog>().defaultPage();
        EntityWrapper entityWrapper=new EntityWrapper<>(vssCustomerSiteLog);
        entityWrapper.orderBy("last_time",false);
        page.setRecords(vssCustomerSiteLogService.selectPage(page,entityWrapper).getRecords());
        return new PageInfoBT<>(page);
    }

    /**
     * 新增客户网站日志
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    @Permission
    public Object add(VssCustomerSiteLog vssCustomerSiteLog) {
        vssCustomerSiteLogService.insert(vssCustomerSiteLog);
        return SUCCESS_TIP;
    }

    /**
     * 删除客户网站日志
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @Permission
    public Object delete(@PathVariable String vssCustomerSiteLogId) {
        vssCustomerSiteLogService.deleteById(vssCustomerSiteLogId);
        return SUCCESS_TIP;
    }

    /**
     * 修改客户网站日志
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    @Permission
    public Object update(VssCustomerSiteLog vssCustomerSiteLog) {
        vssCustomerSiteLogService.updateById(vssCustomerSiteLog);
        return SUCCESS_TIP;
    }

    /**
     * 客户网站日志详情
     */
    @RequestMapping(value = "/detail/{vssCustomerSiteLogId}")
    @ResponseBody
    @Permission
    public Object detail(@PathVariable("vssCustomerSiteLogId") String vssCustomerSiteLogId) {
        return vssCustomerSiteLogService.selectById(vssCustomerSiteLogId);
    }
}
