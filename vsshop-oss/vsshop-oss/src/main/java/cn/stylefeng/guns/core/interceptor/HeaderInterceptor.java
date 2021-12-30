package cn.stylefeng.guns.core.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.stylefeng.guns.InitSpringBoot;
import cn.stylefeng.guns.modular.vsshop.VssSchedule;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 拦截恶意请求
 */
@Component
public class HeaderInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        if("/api/reuse".equals(request.getRequestURI()) || "/api/down".equals(request.getRequestURI())){
            return true;
        }
        if(StrUtil.isEmpty(request.getHeader("ali-cdn-real-ip")) || !InitSpringBoot.SERVICE_ENABLE ){
            response.setStatus(404);
            return false;
        }
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}