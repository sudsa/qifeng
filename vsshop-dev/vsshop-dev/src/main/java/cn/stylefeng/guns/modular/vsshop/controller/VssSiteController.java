package cn.stylefeng.guns.modular.vsshop.controller;

import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.factory.PageFactory;
import cn.stylefeng.guns.core.common.page.PageInfoBT;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.model.VssPlan;
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
import cn.stylefeng.guns.modular.system.model.VssSite;
import cn.stylefeng.guns.modular.vsshop.service.IVssSiteService;

/**
 * 网站管理控制器
 *
 * @author fengshuonan
 * @Date 2019-05-26 16:24:45
 */
@Controller
@RequestMapping("/vssSite")
public class VssSiteController extends BaseController {

    private String PREFIX = "/vsshop/vssSite/";

    @Autowired
    private IVssSiteService vssSiteService;

    /**
     * 跳转到网站管理首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssSite.html";
    }

    /**
     * 跳转到添加网站管理
     */
    @RequestMapping("/vssSite_add")
    public String vssSiteAdd() {
        return PREFIX + "vssSite_add.html";
    }

    /**
     * 跳转到修改网站管理
     */
    @RequestMapping("/vssSite_update/{vssSiteId}")
    public String vssSiteUpdate(@PathVariable String vssSiteId, Model model) {
        VssSite vssSite = vssSiteService.selectById(vssSiteId);
        model.addAttribute("item",vssSite);
        LogObjectHolder.me().set(vssSite);
        return PREFIX + "vssSite_edit.html";
    }

    /**
     * 获取网站管理列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @Permission
    public Object list(VssSite vssSite) {
        Page<VssSite> page = new PageFactory<VssSite>().defaultPage();
        EntityWrapper entityWrapper=new EntityWrapper<>(vssSite);
        entityWrapper.orderBy("insert_time",false);
        page.setRecords(vssSiteService.selectPage(page,entityWrapper).getRecords());
        return new PageInfoBT<>(page);
    }

    /**
     * 新增网站管理
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    @Permission
    public Object add(VssSite vssSite) {
        vssSiteService.insert(vssSite);
        return SUCCESS_TIP;
    }

    /**
     * 删除网站管理
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @Permission
    public Object delete(@PathVariable String vssSiteId) {
        vssSiteService.deleteById(vssSiteId);
        return SUCCESS_TIP;
    }

    /**
     * 修改网站管理
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    @Permission
    public Object update(VssSite vssSite) {
        vssSiteService.updateById(vssSite);
        return SUCCESS_TIP;
    }

    /**
     * 网站管理详情
     */
    @RequestMapping(value = "/detail/{vssSiteId}")
    @ResponseBody
    @Permission
    public Object detail(@PathVariable("vssSiteId") String vssSiteId) {
        return vssSiteService.selectById(vssSiteId);
    }
}
