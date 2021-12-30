package cn.stylefeng.guns.config.oss;

import cn.stylefeng.guns.modular.vsshop.common.OssHelper;
import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

@Repository
@Configuration
@PropertySource("classpath:ali.properties")
public class OssConfig {
   @Value("${oss.endpoint}")
   private String endpoint;
    @Value("${oss.accessKeyId}")
   private String accessKeyId;
    @Value("${oss.accessKeySecret}")
   private String accessKeySecret;
    @Value("${oss.bucket}")
   private String bucket;
    @Value("${vss.email}")
   private String email;
    @Value("${vss.webDomain}")
   private String webDomain;
    @Value("${vss.resourceDomain}")
   private String resourceDomain;

    @Bean
   public OssHelper getOssHelper(){
        OssHelper ossHelper=new OssHelper();
        ossHelper.setEndpoint(endpoint);
        ossHelper.setAccessKeyId(accessKeyId);
        ossHelper.setAccessKeySecret(accessKeySecret);
        ossHelper.setBucket(bucket);
        ossHelper.setEmail(email);
        ossHelper.setWebDomain(webDomain);
        ossHelper.setResourceDomain(resourceDomain);
        return ossHelper;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebDomain() {
        return webDomain;
    }

    public void setWebDomain(String webDomain) {
        this.webDomain = webDomain;
    }

    public String getResourceDomain() {
        return resourceDomain;
    }

    public void setResourceDomain(String resourceDomain) {
        this.resourceDomain = resourceDomain;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }
}
