package cn.stylefeng.guns.modular.vsshop.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.stylefeng.guns.modular.api.ApiController;
import cn.stylefeng.guns.modular.system.model.VssCode;
import cn.stylefeng.guns.modular.system.model.VssCustomer;
import cn.stylefeng.guns.modular.system.model.VssVip;
import cn.stylefeng.guns.modular.vsshop.model.ApiResponse;
import cn.stylefeng.guns.modular.vsshop.model.DownloadReq;
import cn.stylefeng.guns.modular.vsshop.service.IVssCodeService;
import cn.stylefeng.guns.modular.vsshop.service.IVssCustomerService;
import cn.stylefeng.guns.modular.vsshop.service.IVssVipService;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.HttpContext;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class VssShopApiController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    IVssCodeService vssCodeService;
    @Autowired
    IVssVipService vssVipService;
    @Autowired
    IVssCustomerService vssCustomerService;
    @RequestMapping(value = "/getDownloadFile", method = RequestMethod.GET)
    public ApiResponse getFile(@Valid DownloadReq downloadReq, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            StringBuffer sb=new StringBuffer();
            for(ObjectError objectError : bindingResult.getAllErrors()){
                sb.append(objectError.getDefaultMessage() +" , ");
            }
            return ApiResponse.fail(sb.toString());
        }

        //预处理
        downloadReq.setCode(downloadReq.getCode().trim());
        downloadReq.setDownUrl(downloadReq.getDownUrl().trim().replaceAll(" ",""));
        String check = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        if(StrUtil.isNotBlank(downloadReq.getEmail())){
            downloadReq.setEmail(downloadReq.getEmail().replaceAll(" ",""));
            if(!ReUtil.isMatch(check,downloadReq.getEmail())){
                return ApiResponse.fail("邮件地址错误！");
            }
        }

        try{
            //code限流30秒5次

            RateLimiter limiter = caches.get(downloadReq.getCode());
            if(limiter!=null && !limiter.tryAcquire()){
               return ApiResponse.fail("请求太过频繁，请等5s重试！！");
            }
            return vssCodeService.getFile(downloadReq);
        }catch (Exception e){
            logger.error(e.getMessage());
            return ApiResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "/getVipList", method = RequestMethod.GET)
    public ApiResponse getVipList() {
        try {
            EntityWrapper entityWrapper=new EntityWrapper();
            entityWrapper.orderBy("name",false);
            List<VssVip> vssVipList= vssVipService.selectList(entityWrapper);
            if(CollUtil.isNotEmpty(vssVipList)){
                for(int i=0;i<vssVipList.size();i++){
                    vssVipList.get(i).setIndex(i+1);
                }
            }
            ApiResponse apiResponse=ApiResponse.success(vssVipList);
            apiResponse.setStatus(0);
            return apiResponse;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ApiResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "/getInfoList", method = RequestMethod.GET)
    public ApiResponse getInfoList() {
        try {
            ApiResponse apiResponse=ApiResponse.success(null);
            apiResponse.setStatus(0);
            return apiResponse;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ApiResponse.fail(e.getMessage());
        }
    }

    // 根据IP分不同的令牌桶, 每天自动清理缓存
    public static LoadingCache<String, RateLimiter> caches = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<String, RateLimiter>() {
                @Override
                public RateLimiter load(String key) throws Exception {
                // 新的IP初始化 (限流每秒两个令牌响应)
                    return RateLimiter.create(0.2);
                }
     });

}
