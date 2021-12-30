package cn.stylefeng.guns.modular.vsshop.controller;

import cn.stylefeng.roses.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import org.springframework.web.bind.annotation.RequestParam;
import cn.stylefeng.guns.modular.system.model.VssVip;
import cn.stylefeng.guns.modular.vsshop.service.IVssVipService;

/**
 * 会员商品控制器
 *
 * @author fengshuonan
 * @Date 2019-04-12 18:10:18
 */
@Controller
@RequestMapping("/vssVip")
public class VssVipController extends BaseController {

    private String PREFIX = "/vsshop/vssVip/";

    @Autowired
    private IVssVipService vssVipService;

    /**
     * 跳转到会员商品首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssVip.html";
    }

    /**
     * 跳转到添加会员商品
     */
    @RequestMapping("/vssVip_add")
    public String vssVipAdd() {
        return PREFIX + "vssVip_add.html";
    }

    /**
     * 跳转到修改会员商品
     */
    @RequestMapping("/vssVip_update/{vssVipId}")
    public String vssVipUpdate(@PathVariable Integer vssVipId, Model model) {
        VssVip vssVip = vssVipService.selectById(vssVipId);
        model.addAttribute("item",vssVip);
        LogObjectHolder.me().set(vssVip);
        return PREFIX + "vssVip_edit.html";
    }

    /**
     * 获取会员商品列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition) {
        return vssVipService.selectList(null);
    }

    /**
     * 新增会员商品
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(VssVip vssVip) {
        vssVipService.insert(vssVip);
        return SUCCESS_TIP;
    }

    /**
     * 删除会员商品
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam Integer vssVipId) {
        vssVipService.deleteById(vssVipId);
        return SUCCESS_TIP;
    }

    /**
     * 修改会员商品
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(VssVip vssVip) {
        vssVipService.updateById(vssVip);
        return SUCCESS_TIP;
    }

    /**
     * 会员商品详情
     */
    @RequestMapping(value = "/detail/{vssVipId}")
    @ResponseBody
    public Object detail(@PathVariable("vssVipId") Integer vssVipId) {
        return vssVipService.selectById(vssVipId);
    }
}
