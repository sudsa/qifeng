package cn.stylefeng.guns.modular.vsshop.controller;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.view.PoiBaseView;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.factory.PageFactory;
import cn.stylefeng.guns.core.common.page.PageInfoBT;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.vsshop.common.UrlSource;
import cn.stylefeng.guns.modular.vsshop.common.VssUserType;
import cn.stylefeng.guns.modular.vsshop.model.ApiResponse;
import cn.stylefeng.guns.modular.vsshop.model.VssCode;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.modular.vsshop.model.VssLog;
import cn.stylefeng.guns.modular.vsshop.service.IVssLogService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 下载日志控制器
 *
 * @author fengshuonan
 * @Date 2019-03-13 22:54:27
 */
@Controller
@RequestMapping("/vssLog")
public class VssLogController extends BaseController {

    private String PREFIX = "/vsshop/vssLog/";

    @Autowired
    private IVssLogService vssLogService;

    /**
     * 跳转到下载日志首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "vssLog.html";
    }

    /**
     * 跳转到添加下载日志
     */
    @RequestMapping("/vssLog_add")
    public String vssLogAdd() {
        return PREFIX + "vssLog_add.html";
    }

    /**
     * 跳转到修改下载日志
     */
    @RequestMapping("/vssLog_update/{vssLogId}")
    public String vssLogUpdate(@PathVariable String vssLogId, Model model) {
        VssLog vssLog = vssLogService.selectById(vssLogId);
        model.addAttribute("item",vssLog);
        LogObjectHolder.me().set(vssLog);
        return PREFIX + "vssLog_edit.html";
    }

    /**
     * 获取下载日志列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @Permission
    public Object list(VssLog vssLog) {
        if(StrUtil.isEmpty(vssLog.getCode())){
            vssLog.setCode(null);
        }
        if(StrUtil.isEmpty(vssLog.getReuse())){
            vssLog.setReuse(null);
        }
        if(!ShiroKit.getUser().getRoleNames().contains("超级管理员")){
            vssLog.setOwner(ShiroKit.getUser().getName());
        }
        Page<VssLog> page = new PageFactory<VssLog>().defaultPage();
        EntityWrapper entityWrapper=new EntityWrapper<>(vssLog);

        entityWrapper.orderBy("insert_time",false);
        List<VssLog> vssLogList=vssLogService.selectPage(page,entityWrapper).getRecords();
        if(CollUtil.isNotEmpty(vssLogList) && !ShiroKit.getUser().getRoleNames().contains("超级管理员")){
            vssLogList.stream().forEach(vssLog1 -> {vssLog.setReuse("--");});
        }
        page.setRecords(vssLogList);
        return new PageInfoBT<>(page);
    }

    /**
     * 下载激活码
     */
    @RequestMapping(value = "/export",method = RequestMethod.GET)
    public void detailListExport(VssLog vssLogQuery, ModelMap map, HttpServletRequest request, HttpServletResponse response)  {
        ExportParams params = new ExportParams("下载记录", "1", ExcelType.XSSF);
        params.setFreezeCol(2);
        try {
            if(!ShiroKit.getUser().getRoleNames().contains("超级管理员")){
                vssLogQuery.setOwner(ShiroKit.getUser().getName());
            }
            if(StrUtil.isEmpty(vssLogQuery.getCode())){
                vssLogQuery.setCode(null);
            }
            if(StrUtil.isEmpty(vssLogQuery.getReuse())){
                vssLogQuery.setReuse(null);
            }
            EntityWrapper entityWrapper=new EntityWrapper<>(vssLogQuery);
            List<VssLog> vssLogListTemp=vssLogService.selectList(entityWrapper);
            map.put(NormalExcelConstants.DATA_LIST, vssLogListTemp); // 数据集合
            map.put(NormalExcelConstants.CLASS, VssLog.class);//导出实体
            map.put(NormalExcelConstants.PARAMS, params);//参数
            map.put(NormalExcelConstants.FILE_NAME, String.format("%s个-%s",vssLogListTemp.size(), DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN)));//文件名称
        } catch (Exception e) {
            ApiResponse.fail("导出下载记录失败！"+e.getMessage());
        }

        PoiBaseView.render(map,request,response,NormalExcelConstants.EASYPOI_EXCEL_VIEW); ;//View名称
    }
    /**
     * 下载日志详情
     */
    @RequestMapping(value = "/detail/{vssLogId}")
    @ResponseBody
    @Permission
    public Object detail(@PathVariable("vssLogId") String vssLogId) {
        return vssLogService.selectById(vssLogId);
    }
}
