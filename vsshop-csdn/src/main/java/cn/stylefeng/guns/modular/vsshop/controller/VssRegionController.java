package cn.stylefeng.guns.modular.vsshop.controller;

import cn.hutool.core.util.RandomUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import org.springframework.web.bind.annotation.RequestParam;
import cn.stylefeng.guns.modular.system.model.VssRegion;
import cn.stylefeng.guns.modular.vsshop.service.IVssRegionService;

/**
 * 区域管理控制器
 *
 * @author fengshuonan
 * @Date 2019-06-30 22:28:32
 */
@Controller
@RequestMapping("/vssRegion")
public class VssRegionController extends BaseController {

    private String PREFIX = "/vsshop/vssRegion/";

    @Autowired
    private IVssRegionService vssRegionService;

    /**
     * 跳转到区域管理首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssRegion.html";
    }

    /**
     * 跳转到添加区域管理
     */
    @RequestMapping("/vssRegion_add")
    public String vssRegionAdd() {
        return PREFIX + "vssRegion_add.html";
    }

    /**
     * 跳转到修改区域管理
     */
    @RequestMapping("/vssRegion_update/{vssRegionId}")
    public String vssRegionUpdate(@PathVariable String vssRegionId, Model model) {
        VssRegion vssRegion = vssRegionService.selectById(vssRegionId);
        model.addAttribute("item",vssRegion);
        LogObjectHolder.me().set(vssRegion);
        return PREFIX + "vssRegion_edit.html";
    }

    /**
     * 获取区域管理列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition) {
        return vssRegionService.selectList(null);
    }

    /**
     * 新增区域管理
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(VssRegion vssRegion) {
        vssRegion.setUuid(RandomUtil.randomUUID().replaceAll("-",""));
        vssRegionService.insert(vssRegion);
        return SUCCESS_TIP;
    }

    /**
     * 删除区域管理
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String vssRegionId) {
        vssRegionService.deleteById(vssRegionId);
        return SUCCESS_TIP;
    }

    /**
     * 修改区域管理
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(VssRegion vssRegion) {
        vssRegionService.updateById(vssRegion);
        return SUCCESS_TIP;
    }

    /**
     * 区域管理详情
     */
    @RequestMapping(value = "/detail/{vssRegionId}")
    @ResponseBody
    public Object detail(@PathVariable("vssRegionId") String vssRegionId) {
        return vssRegionService.selectById(vssRegionId);
    }
}
