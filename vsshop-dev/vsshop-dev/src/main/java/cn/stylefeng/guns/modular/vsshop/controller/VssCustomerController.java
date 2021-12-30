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
import cn.stylefeng.guns.modular.system.model.VssCode;
import cn.stylefeng.guns.modular.vsshop.common.UrlSource;
import cn.stylefeng.guns.modular.vsshop.common.VssUserType;
import cn.stylefeng.guns.modular.vsshop.model.ApiResponse;
import cn.stylefeng.guns.modular.vsshop.model.CreateCodeReq;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.system.model.VssCustomer;
import cn.stylefeng.guns.modular.vsshop.service.IVssCustomerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * 客户管理控制器
 *
 * @author fengshuonan
 * @Date 2019-05-26 16:19:56
 */
@Controller
@RequestMapping("/vssCustomer")
public class VssCustomerController extends BaseController {

    private String PREFIX = "/vsshop/vssCustomer/";

    @Autowired
    private IVssCustomerService vssCustomerService;

    /**
     * 跳转到客户管理首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssCustomer.html";
    }

    /**
     * 跳转到添加客户管理
     */
    @RequestMapping("/vssCustomer_add")
    public String vssCustomerAdd() {
        return PREFIX + "vssCustomer_add.html";
    }

    /**
     * 跳转到修改客户管理
     */
    @RequestMapping("/vssCustomer_update/{vssCustomerId}")
    public String vssCustomerUpdate(@PathVariable String vssCustomerId, Model model) {
        VssCustomer vssCustomer = vssCustomerService.selectById(vssCustomerId);
        model.addAttribute("item",vssCustomer);
        LogObjectHolder.me().set(vssCustomer);
        return PREFIX + "vssCustomer_edit.html";
    }

    /**
     * 获取客户管理列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @Permission
    public Object list(VssCustomer vssCustomer) {
        if(!ShiroKit.getUser().getRoleNames().contains("超级管理员")){
            vssCustomer.setOwnerName(ShiroKit.getUser().getName());
        }
        if(StrUtil.isEmpty(vssCustomer.getCustomerCode())){
            vssCustomer.setCustomerCode(null);
        }else {
            vssCustomer.setCustomerCode(vssCustomer.getCustomerCode().trim());
        }
        Page<VssCustomer> page = new PageFactory<VssCustomer>().defaultPage();
        EntityWrapper entityWrapper=new EntityWrapper<>(vssCustomer);
        entityWrapper.orderBy("last_time",false);
        page.setRecords(vssCustomerService.selectPage(page,entityWrapper).getRecords());
        return new PageInfoBT<>(page);
    }

    /**
     * 新增客户
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    @Permission
    public ApiResponse add(@Valid CreateCodeReq createCodeReq, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            StringBuffer sb=new StringBuffer();
            for(ObjectError objectError : bindingResult.getAllErrors()){
                sb.append(objectError.getDefaultMessage() +" , ");
            }
            return ApiResponse.fail(sb.toString());
        }

        if(createCodeReq.getNum()>10000){
            return ApiResponse.fail("一次最多生成10000个");
        }
        try {
            vssCustomerService.createCode(createCodeReq);
        }catch (Exception e){
            return ApiResponse.fail(String.format("生成失败！%s", e.getMessage()));
        }
        return ApiResponse.success("生成成功！");
    }

    /**
     * 删除客户管理
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @Permission
    public Object delete(@PathVariable String vssCustomerId) {
        vssCustomerService.deleteById(vssCustomerId);
        return SUCCESS_TIP;
    }

    /**
     * 修改客户管理
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    @Permission
    public Object update(VssCustomer vssCustomer) {
        vssCustomerService.updateById(vssCustomer);
        return SUCCESS_TIP;
    }

    /**
     * 客户管理详情
     */
    @RequestMapping(value = "/detail/{vssCustomerId}")
    @ResponseBody
    @Permission
    public Object detail(@PathVariable("vssCustomerId") String vssCustomerId) {
        return vssCustomerService.selectById(vssCustomerId);
    }

    /**
     * 下载用户
     */
    @RequestMapping(value = "/export",method = RequestMethod.GET)
    public void detailListExport(VssCustomer customerReq, ModelMap map, HttpServletRequest request, HttpServletResponse response)  {
        ExportParams params = new ExportParams("激活码", "1", ExcelType.XSSF);
        params.setFreezeCol(2);
        try {
            if(!ShiroKit.getUser().getRoleNames().contains("超级管理员")){
                customerReq.setOwnerName(ShiroKit.getUser().getName());
            }
            if(StrUtil.isEmpty(customerReq.getCustomerCode())){
                customerReq.setCustomerCode(null);
            }
            EntityWrapper entityWrapper=new EntityWrapper<>(customerReq);
            entityWrapper.orderBy("last_time",false);
            List<VssCustomer> vssCustomerList=vssCustomerService.selectList(entityWrapper);
            vssCustomerList.forEach(vssCustomer -> {

                String status=vssCustomer.getStatus();
                if("1".equals(status)){
                    vssCustomer.setStatus("正常");
                }else if("2".equals(status)){
                    vssCustomer.setStatus("已使用");
                }else if("3".equals(status)){
                    vssCustomer.setStatus("废弃");
                }
                vssCustomer.setCustomerCode(String.format("http://www.sheepsun.com/down?code=%s", vssCustomer.getCustomerCode()));
            });
            map.put(NormalExcelConstants.DATA_LIST, vssCustomerList); // 数据集合
            map.put(NormalExcelConstants.CLASS, VssCode.class);//导出实体
            map.put(NormalExcelConstants.PARAMS, params);//参数
            map.put(NormalExcelConstants.FILE_NAME, String.format("%s个-%s",vssCustomerList.size(), DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN)));//文件名称
        } catch (Exception e) {
            ApiResponse.fail("下载用户码失败！"+e.getMessage());
        }

        PoiBaseView.render(map,request,response,NormalExcelConstants.EASYPOI_EXCEL_VIEW); ;//View名称
    }
}
