package cn.stylefeng.guns.modular.vsshop.controller;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.view.PoiBaseView;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.factory.PageFactory;
import cn.stylefeng.guns.core.common.page.PageInfoBT;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.model.LoginLog;
import cn.stylefeng.guns.modular.system.model.VssCode;
import cn.stylefeng.guns.modular.system.model.VssCustomer;
import cn.stylefeng.guns.modular.vsshop.common.UrlSource;
import cn.stylefeng.guns.modular.vsshop.common.VssUserType;
import cn.stylefeng.guns.modular.vsshop.model.ApiResponse;
import cn.stylefeng.guns.modular.vsshop.service.IVssCustomerService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.vsshop.service.IVssCodeService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * 激活码控制器
 *
 * @author fengshuonan
 * @Date 2019-03-09 21:54:32
 */
@Controller
@RequestMapping("/vssCode")
public class VssCodeController extends BaseController {

    private String PREFIX = "/vsshop/vssCode/";

    @Autowired
    private IVssCodeService vssCodeService;
    @Autowired
    private IVssCustomerService vssCustomerService;

    /**
     * 跳转到激活码首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssCode.html";
    }

    /**
     * 跳转到添加激活码
     */
    @RequestMapping("/vssCode_add")
    public String vssCodeAdd() {
        return PREFIX + "vssCode_add.html";
    }

    /**
     * 跳转到修改激活码
     */
    @RequestMapping("/vssCode_update/{vssCodeId}")
    public String vssCodeUpdate(@PathVariable String vssCodeId, Model model) {
        VssCode vssCode = vssCodeService.selectById(vssCodeId);
        model.addAttribute("item",vssCode);
        LogObjectHolder.me().set(vssCode);
        return PREFIX + "vssCode_edit.html";
    }

    /**
     * 获取激活码列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @Permission
    public Object list(VssCode vssCode) {
        if(!ShiroKit.getUser().getRoleNames().contains("超级管理员")){
            vssCode.setOwner(ShiroKit.getUser().getName());
        }
        if(StrUtil.isEmpty(vssCode.getCode())){
            vssCode.setCode(null);
        }else {
            vssCode.setCode(vssCode.getCode().trim());
        }
        Page<VssCode> page = new PageFactory<VssCode>().defaultPage();
        EntityWrapper entityWrapper=new EntityWrapper<>(vssCode);
        entityWrapper.orderBy("update_time",false);
        page.setRecords(vssCodeService.selectPage(page,entityWrapper).getRecords());
        return new PageInfoBT<>(page);
    }



    /**
     * 删除激活码
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @Permission
    public Object delete(@RequestParam String vssCodeId) {
        vssCodeService.deleteById(vssCodeId);
        return SUCCESS_TIP;
    }

    /**
     * 修改激活码
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    @Permission
    public Object update(VssCode vssCode) {
        vssCodeService.updateById(vssCode);
        return SUCCESS_TIP;
    }

    /**
     * 激活码详情
     */
    @RequestMapping(value = "/detail/{vssCodeId}")
    @ResponseBody
    @Permission
    public Object detail(@PathVariable("vssCodeId") String vssCodeId) {
        return vssCodeService.selectById(vssCodeId);
    }

}
