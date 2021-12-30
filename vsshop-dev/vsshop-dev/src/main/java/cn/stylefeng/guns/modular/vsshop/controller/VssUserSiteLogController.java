package cn.stylefeng.guns.modular.vsshop.controller;

import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.factory.PageFactory;
import cn.stylefeng.guns.core.common.page.PageInfoBT;
import cn.stylefeng.guns.modular.system.model.VssUser;
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
import cn.stylefeng.guns.modular.system.model.VssUserSiteLog;
import cn.stylefeng.guns.modular.vsshop.service.IVssUserSiteLogService;

/**
 * 计划网站日志控制器
 *
 * @author fengshuonan
 * @Date 2019-05-26 16:25:35
 */
@Controller
@RequestMapping("/vssUserSiteLog")
public class VssUserSiteLogController extends BaseController {

    private String PREFIX = "/vsshop/vssUserSiteLog/";

    @Autowired
    private IVssUserSiteLogService vssUserSiteLogService;

    /**
     * 跳转到计划网站日志首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssUserSiteLog.html";
    }

    /**
     * 跳转到添加计划网站日志
     */
    @RequestMapping("/vssUserSiteLog_add")
    public String vssUserSiteLogAdd() {
        return PREFIX + "vssUserSiteLog_add.html";
    }

    /**
     * 跳转到修改计划网站日志
     */
    @RequestMapping("/vssUserSiteLog_update/{vssUserSiteLogId}")
    public String vssUserSiteLogUpdate(@PathVariable String vssUserSiteLogId, Model model) {
        VssUserSiteLog vssUserSiteLog = vssUserSiteLogService.selectById(vssUserSiteLogId);
        model.addAttribute("item",vssUserSiteLog);
        LogObjectHolder.me().set(vssUserSiteLog);
        return PREFIX + "vssUserSiteLog_edit.html";
    }

    /**
     * 获取计划网站日志列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @Permission
    public Object list(VssUserSiteLog vssUserSiteLog) {
        Page<VssUserSiteLog> page = new PageFactory<VssUserSiteLog>().defaultPage();
        EntityWrapper entityWrapper=new EntityWrapper<>(vssUserSiteLog);
        entityWrapper.orderBy("last_time",false);
        page.setRecords(vssUserSiteLogService.selectPage(page,entityWrapper).getRecords());
        return new PageInfoBT<>(page);
    }

    /**
     * 新增计划网站日志
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    @Permission
    public Object add(VssUserSiteLog vssUserSiteLog) {
        vssUserSiteLogService.insert(vssUserSiteLog);
        return SUCCESS_TIP;
    }

    /**
     * 删除计划网站日志
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @Permission
    public Object delete(@PathVariable String vssUserSiteLogId) {
        vssUserSiteLogService.deleteById(vssUserSiteLogId);
        return SUCCESS_TIP;
    }

    /**
     * 修改计划网站日志
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    @Permission
    public Object update(VssUserSiteLog vssUserSiteLog) {
        vssUserSiteLogService.updateById(vssUserSiteLog);
        return SUCCESS_TIP;
    }

    /**
     * 计划网站日志详情
     */
    @RequestMapping(value = "/detail/{vssUserSiteLogId}")
    @ResponseBody
    @Permission
    public Object detail(@PathVariable("vssUserSiteLogId") String vssUserSiteLogId) {
        return vssUserSiteLogService.selectById(vssUserSiteLogId);
    }
}
