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
import cn.stylefeng.guns.modular.system.model.VssApi;
import cn.stylefeng.guns.modular.vsshop.service.IVssApiService;

/**
 * 别家api控制器
 *
 * @author fengshuonan
 * @Date 2019-06-12 14:31:36
 */
@Controller
@RequestMapping("/vssApi")
public class VssApiController extends BaseController {

    private String PREFIX = "/vsshop/vssApi/";

    @Autowired
    private IVssApiService vssApiService;

    /**
     * 跳转到别家api首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssApi.html";
    }

    /**
     * 跳转到添加别家api
     */
    @RequestMapping("/vssApi_add")
    public String vssApiAdd() {
        return PREFIX + "vssApi_add.html";
    }

    /**
     * 跳转到修改别家api
     */
    @RequestMapping("/vssApi_update/{vssApiId}")
    public String vssApiUpdate(@PathVariable Integer vssApiId, Model model) {
        VssApi vssApi = vssApiService.selectById(vssApiId);
        model.addAttribute("item",vssApi);
        LogObjectHolder.me().set(vssApi);
        return PREFIX + "vssApi_edit.html";
    }

    /**
     * 获取别家api列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition) {
        return vssApiService.selectList(null);
    }

    /**
     * 新增别家api
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(VssApi vssApi) {
        vssApiService.insert(vssApi);
        return SUCCESS_TIP;
    }

    /**
     * 删除别家api
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam Integer vssApiId) {
        vssApiService.deleteById(vssApiId);
        return SUCCESS_TIP;
    }

    /**
     * 修改别家api
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(VssApi vssApi) {
        vssApiService.updateById(vssApi);
        return SUCCESS_TIP;
    }

    /**
     * 别家api详情
     */
    @RequestMapping(value = "/detail/{vssApiId}")
    @ResponseBody
    public Object detail(@PathVariable("vssApiId") Integer vssApiId) {
        return vssApiService.selectById(vssApiId);
    }
}
