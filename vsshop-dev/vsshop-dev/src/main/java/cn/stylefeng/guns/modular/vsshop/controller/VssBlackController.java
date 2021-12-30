package cn.stylefeng.guns.modular.vsshop.controller;

import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.roses.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import org.springframework.web.bind.annotation.RequestParam;
import cn.stylefeng.guns.modular.system.model.VssBlack;
import cn.stylefeng.guns.modular.vsshop.service.IVssBlackService;

/**
 * 网址黑名单控制器
 *
 * @author fengshuonan
 * @Date 2019-05-28 01:03:28
 */
@Controller
@RequestMapping("/vssBlack")
public class VssBlackController extends BaseController {

    private String PREFIX = "/vsshop/vssBlack/";

    @Autowired
    private IVssBlackService vssBlackService;

    /**
     * 跳转到网址黑名单首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssBlack.html";
    }

    /**
     * 跳转到添加网址黑名单
     */
    @RequestMapping("/vssBlack_add")
    public String vssBlackAdd() {
        return PREFIX + "vssBlack_add.html";
    }

    /**
     * 跳转到修改网址黑名单
     */
    @RequestMapping("/vssBlack_update/{vssBlackId}")
    public String vssBlackUpdate(@PathVariable String vssBlackId, Model model) {
        VssBlack vssBlack = vssBlackService.selectById(vssBlackId);
        model.addAttribute("item",vssBlack);
        LogObjectHolder.me().set(vssBlack);
        return PREFIX + "vssBlack_edit.html";
    }

    /**
     * 获取网址黑名单列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition) {
        return vssBlackService.selectList(null);
    }

    /**
     * 新增网址黑名单
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    @Permission
    public Object add(VssBlack vssBlack) {
        vssBlackService.insert(vssBlack);
        return SUCCESS_TIP;
    }

    /**
     * 删除网址黑名单
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @Permission
    public Object delete(@PathVariable String vssBlackId) {
        vssBlackService.deleteById(vssBlackId);
        return SUCCESS_TIP;
    }

    /**
     * 修改网址黑名单
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    @Permission
    public Object update(VssBlack vssBlack) {
        vssBlackService.updateById(vssBlack);
        return SUCCESS_TIP;
    }

    /**
     * 网址黑名单详情
     */
    @RequestMapping(value = "/detail/{vssBlackId}")
    @ResponseBody
    public Object detail(@PathVariable("vssBlackId") String vssBlackId) {
        return vssBlackService.selectById(vssBlackId);
    }
}
