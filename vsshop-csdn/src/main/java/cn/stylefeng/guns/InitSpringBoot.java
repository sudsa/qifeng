package cn.stylefeng.guns;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.guns.modular.vsshop.common.IPHelper;
import cn.stylefeng.guns.modular.vsshop.common.OKHttpHelper;
import cn.stylefeng.guns.modular.vsshop.common.OssHelper;
import cn.stylefeng.guns.modular.vsshop.core.CsdnHandleDownload;
import cn.stylefeng.guns.modular.vsshop.model.HttpVssUser;
import cn.stylefeng.guns.modular.vsshop.model.VssCode;
import cn.stylefeng.guns.modular.vsshop.model.VssUser;
import cn.stylefeng.guns.modular.vsshop.service.IVssCodeService;
import cn.stylefeng.guns.modular.vsshop.service.IVssUserService;
import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <spring boot初始化>
 *
 * @author 2018年8月5日 下午1:48:44
 * @version 1.0
 */
@Component("InitSpringBoot")
public class InitSpringBoot {

    private Logger log = LoggerFactory.getLogger(InitSpringBoot.class);

    @Autowired
    IVssCodeService vssCodeService;
    @Autowired
    IVssUserService vssUserService;
    @Autowired
    OssHelper ossHelper;

    public static String LOCAL_IP;
    public static boolean SERVICE_ENABLE=true;

    @PostConstruct
    public void post(){
        LOCAL_IP=IPHelper.getOuterNetIp().trim();
        if(StrUtil.isEmpty(LOCAL_IP)){
            log.info("###################### Fail ! Fail! Fail!!!!!!!!!!!IP EMPTY ###############################");
        }else{
            log.info(String.format("###################### SUCCESS! IP: %s###############################", LOCAL_IP));
        }

    }
//    @PostConstruct
    public void run() {
        try {
            OSSClient ossClient=ossHelper.getOssClient();
            // 创建OSSClient实例。
            EntityWrapper entityWrapper = new EntityWrapper();
            entityWrapper.eq("status", "2");
            entityWrapper.ne("file_path", "");
            List<VssCode> vssCodeList = vssCodeService.selectList(entityWrapper);
            List<VssCode> listTemp=new ArrayList<>();
            for (int i = 0; i < vssCodeList.size(); i++) {
                VssCode vssCode = vssCodeList.get(i);
                boolean found = ossClient.doesObjectExist(ossHelper.getBucket(), vssCode.getFilePath().replaceAll(ossHelper.getResourceDomain(), ""));
                if (!found) {
                    vssCode.setFilePath("");
                    vssCode.setReuseEnable("2");
                } else {
                    vssCode.setReuseEnable("1");
                }
                listTemp.add(vssCode);
                System.out.println(i);
            }
            vssCodeService.updateBatchById(listTemp);
            ossClient.shutdown();
            log.info("###################### SUCCESS! ###############################");
        } catch (Exception e) {
            log.error("###################### ERROR! ###############################", e);
        }
    }

}
