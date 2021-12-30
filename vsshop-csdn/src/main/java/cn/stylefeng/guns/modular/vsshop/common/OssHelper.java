package cn.stylefeng.guns.modular.vsshop.common;

import cn.stylefeng.guns.config.oss.OssConfig;
import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


public class OssHelper{
    private OSSClient ossClient;
    private String bucket;
    private String email;
    private String webDomain;
    private String resourceDomain;
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
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

    public OSSClient getOssClient() {
        return new OSSClient(endpoint,accessKeyId,accessKeySecret);
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

    public void setOssClient(OSSClient ossClient) {
        this.ossClient = ossClient;
    }
}
