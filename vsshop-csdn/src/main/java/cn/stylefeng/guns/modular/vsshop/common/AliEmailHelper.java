package cn.stylefeng.guns.modular.vsshop.common;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.stylefeng.roses.core.util.HttpContext;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class AliEmailHelper {

    private static Logger logger=LoggerFactory.getLogger(AliEmailHelper.class);
    @Autowired
    OssHelper ossHelper;
    public Boolean sendDown(SingleSendMailRequest request){
        try {
            request.setAccountName(String.format("down@mail.%s",ossHelper.getResourceDomain() ));
            request.setFromAlias("文件自助下载");
            request.setTagName("down");
            return send(request);
        } catch (Exception e) {
            logger.error("邮件发送失败: "+e.getMessage());
            return false;
        }

    }

    //import com.aliyuncs.http.MethodType;
    private Boolean send(SingleSendMailRequest request) throws ClientException {
        // 如果是除杭州region外的其它region（如新加坡、澳洲Region），需要将下面的"cn-hangzhou"替换为"ap-southeast-1"、或"ap-southeast-2"。
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIDkIkE98VqmUj", "r8YoGye0BzzJRDyivufxk6uRjz0H8H");
        //使用https加密连接
        //profile.getHttpClientConfig().setProtocolType(com.aliyuncs.http.ProtocolType.HTTPS);
        // 如果是除杭州region外的其它region（如新加坡region）， 需要做如下处理
        //try {
        //DefaultProfile.addEndpoint("dm.ap-southeast-1.aliyuncs.com", "ap-southeast-1", "Dm",  "dm.ap-southeast-1.aliyuncs.com");
        //} catch (ClientException e) {
        //e.printStackTrace();
        //}
        IAcsClient client = new DefaultAcsClient(profile);
        try {
            //request.setVersion("2017-06-22");// 如果是除杭州region外的其它region（如新加坡region）,必须指定为2017-06-22
            request.setAddressType(1);
            request.setReplyToAddress(true);
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);
            logger.info(String.format("邮件发送成功%s: %s", request.getToAddress(),request.getSubject()));
            return true;
        } catch (ServerException e) {
            throw e;
        } catch (ClientException e) {
            throw e;
        }
    }

}
