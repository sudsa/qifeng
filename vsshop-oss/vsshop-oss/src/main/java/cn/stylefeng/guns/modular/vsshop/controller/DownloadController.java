package cn.stylefeng.guns.modular.vsshop.controller;

import cn.stylefeng.guns.core.common.annotion.Permission;
import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.modular.system.dao.UserMapper;
import cn.stylefeng.guns.modular.system.model.User;
import cn.stylefeng.roses.core.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DownloadController extends BaseController {

    @Autowired
    UserMapper userMapper;

    @RequestMapping("/down")
    public String down(Model model){
        String requestHeader = getHttpServletRequest().getHeader("user-agent");
        if(isMobileDevice(requestHeader)){
            model.addAttribute("ispc",false);
        }else{
            model.addAttribute("ispc",true);
        }
        model.addAttribute("url", ConstantFactory.me().getDictsByName("购买地址",1));
        return "/down.html";
    }
//    @RequestMapping({"/home","/"})
//    public String home(Model model){
//        String requestHeader = getHttpServletRequest().getHeader("user-agent");
//        if(isMobileDevice(requestHeader)){
//            model.addAttribute("ispc",false);
//        }else{
//            model.addAttribute("ispc",true);
//        }
//        return "/home.html";
//    }

    @RequestMapping("/vip")
    public String vip(Model model){
        String requestHeader = getHttpServletRequest().getHeader("user-agent");
        if(isMobileDevice(requestHeader)){
            model.addAttribute("ispc",false);
        }else{
            model.addAttribute("ispc",true);
        }
        return "/vip.html";
    }

    @RequestMapping("/sheep")
    public String sheep(Model model){
        String requestHeader = getHttpServletRequest().getHeader("user-agent");
        if(isMobileDevice(requestHeader)){
            model.addAttribute("ispc",false);
        }else{
            model.addAttribute("ispc",true);
        }
        return "/sheep.html";
    }

    @RequestMapping("/vssAgentGetFile")
    public String vssAgentGetFile(Model model){
        return "vsshop/vssAgent/vssAgentGetFile.html";
    }

    @RequestMapping("/vssAgentInfo")
    @Permission
    public String vssAgentInfo(Model model){
        User user=userMapper.getByName(ShiroKit.getUser().getName());
        model.addAttribute("vssAgent",user);
        return "vsshop/vssAgent/vssAgentInfo.html";
    }


    public static boolean  isMobileDevice(String requestHeader){
        /**
         * android : 所有android设备
         * mac os : iphone ipad
         * windows phone:Nokia等windows系统的手机
         */
        String[] deviceArray = new String[]{"android","mac os","windows phone"};
        if(requestHeader == null)
            return false;
        requestHeader = requestHeader.toLowerCase();
        for(int i=0;i<deviceArray.length;i++){
            if(requestHeader.indexOf(deviceArray[i])>0){
                return true;
            }
        }
        return false;
    }


}
