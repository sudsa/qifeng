package cn.stylefeng.guns.modular.vsshop.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.stylefeng.guns.InitSpringBoot;
import cn.stylefeng.guns.modular.vsshop.service.IVssRegionService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import org.springframework.web.bind.annotation.RequestParam;
import cn.stylefeng.guns.modular.system.model.VssIp;
import cn.stylefeng.guns.modular.vsshop.service.IVssIpService;

import java.util.List;

/**
 * 服务器管理控制器
 *
 * @author fengshuonan
 * @Date 2019-06-30 22:29:00
 */
@Controller
@RequestMapping("/vssIp")
public class VssIpController extends BaseController {

    private String PREFIX = "/vsshop/vssIp/";

    @Autowired
    private IVssIpService vssIpService;
    @Autowired
    private IVssRegionService vssRegionService;
    /**
     * 跳转到服务器管理首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssIp.html";
    }

    /**
     * 跳转到添加服务器管理
     */
    @RequestMapping("/vssIp_add")
    public String vssIpAdd() {
        return PREFIX + "vssIp_add.html";
    }

    /**
     * 跳转到修改服务器管理
     */
    @RequestMapping("/vssIp_update/{vssIpId}")
    public String vssIpUpdate(@PathVariable String vssIpId, Model model) {
        VssIp vssIp = vssIpService.selectById(vssIpId);
        model.addAttribute("item",vssIp);
        LogObjectHolder.me().set(vssIp);
        return PREFIX + "vssIp_edit.html";
    }

    /**
     * 获取服务器管理列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition) {
        List<VssIp> vssIpList=vssIpService.selectList(null);
        if(CollUtil.isNotEmpty(vssIpList)){
            vssIpList.forEach(vssIp -> {
                vssIp.setRegionUuid(vssRegionService.selectById(vssIp.getRegionUuid()).getName());
            });
        }
        return vssIpList;
    }
    @RequestMapping(value = "/getRegions")
    @ResponseBody
    public Object getRegions() {
        return vssRegionService.selectList(null);
    }


    /**
     * 新增服务器管理
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(VssIp vssIp) {
        vssIp.setUuid(RandomUtil.randomUUID().replaceAll("-",""));
        vssIpService.insert(vssIp);
        InitSpringBoot.SERVICE_ENABLE=true;
        return SUCCESS_TIP;
    }

    /**
     * 删除服务器管理
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String vssIpId) {
        vssIpService.deleteById(vssIpId);
        return SUCCESS_TIP;
    }

    /**
     * 修改服务器管理
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(VssIp vssIp) {
        vssIpService.updateById(vssIp);
        InitSpringBoot.SERVICE_ENABLE=true;
        return SUCCESS_TIP;
    }

    /**
     * 服务器管理详情
     */
    @RequestMapping(value = "/detail/{vssIpId}")
    @ResponseBody
    public Object detail(@PathVariable("vssIpId") String vssIpId) {
        return vssIpService.selectById(vssIpId);
    }
}
