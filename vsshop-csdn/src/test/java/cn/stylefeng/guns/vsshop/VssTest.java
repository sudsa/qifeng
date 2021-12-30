package cn.stylefeng.guns.vsshop;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.stylefeng.guns.base.BaseJunit;
import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.guns.modular.vsshop.common.OKHttpHelper;
import cn.stylefeng.guns.modular.vsshop.core.CsdnHandleDownload;
import cn.stylefeng.guns.modular.vsshop.core.HandleDownload;
import cn.stylefeng.guns.modular.vsshop.model.ApiResponse;
import cn.stylefeng.guns.modular.vsshop.model.VssCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import okhttp3.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jianyinlin on 2019/6/25
 */
public class VssTest {
    public static void main(String[] args) {
        ApiResponse apiResponse=ApiResponse.success("");
        String apiReuseUrl = "http://152.136.109.183:8080/cosapi";
        String apiReuseUser = "ty1451";
        String apiReusePwd = "123456HXJ";
        Request.Builder builder = new Request.Builder();
        builder.url(apiReuseUrl);
        try {
            Map<String, String> params = new HashMap<>();
            params.put("downloadurl", "https://download.csdn.net/download/u012598782/7344059");
            params.put("verifyCode", apiReuseUser);
            params.put("password", apiReusePwd);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(params));
            Map<String, String> map = new HashMap<>();
            map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            map.put("Connection", "keep-alive");
            map.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16C104 CSDNApp/3.5.1(iOS)");
            map.put("Accept-Language", "zh-cn");
            map.put("Accept-Encoding", "br,deflate");
            Headers headers = Headers.of(map);
            builder.headers(headers);
            Request request = builder.post(requestBody).build();
            Response response = OKHttpHelper.buildOKHttpClient().followRedirects(true).build().newCall(request).execute();
            String result = response.body().string();
            JSONObject jsonObject = JSON.parseObject(result);
            String returnCode = jsonObject.getString("code");
            if ("5".equals(returnCode) || "a".equals(returnCode)) {
                apiResponse.setStatus(1);
                apiResponse.setMsg("success");
                apiResponse.setResult(jsonObject.getString("cos"));

                try {
                    builder = new Request.Builder();
                    request = builder.url((String) apiResponse.getResult()).build();
                    response = OKHttpHelper.buildOKHttpClient().followRedirects(true).build().newCall(request).execute();
                    //下载文件
                    String fileName = response.request().url().pathSegments().get(1);
                    if (StrUtil.isEmpty(fileName)) {

                    } else {
                        //文件名处理
                        fileName = URLUtil.decode(fileName);
                        fileName = fileName.replaceAll("#", "");
                        fileName = fileName.replaceAll("&", "AND");
                        fileName = fileName.replaceAll("\\?", "");
                        fileName = fileName.replaceAll("/", "");
                        fileName = fileName.replaceAll("\\\\", "");
                        fileName = fileName.replaceAll("\\+", "");
                        fileName = fileName.replaceAll(" ", "");
                        if (fileName.substring(0, 1).equals(".")) {
                            fileName = RandomUtil.randomString(6) + fileName;
                        }
                        if (fileName.length() > 100) {
                            fileName = RandomUtil.randomString(6) + "." + StrUtil.subAfter(fileName, ".", true);
                        }
                        Pattern pattern = Pattern.compile("https://download.csdn.net/download/([\\s\\S]*?)/([\\s\\S]*?)$");


                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } else {
                apiResponse.setStatus(-1);
                apiResponse.setMsg(jsonObject.toJSONString());
                apiResponse.setResult(null);
            }
        } catch (Exception e) {
            apiResponse.setStatus(-1);
            apiResponse.setMsg(e.getMessage());
        }
    }
}
