package cn.stylefeng.guns.modular.vsshop.model;


import cn.hutool.core.util.StrUtil;

/**
 * Created by jianyinlin on 2018/9/21.
 */
public class OperateResponse {
    private boolean isSuccess = true;
    private StringBuffer outMessage = new StringBuffer();
    private StringBuffer logMessage = new StringBuffer();

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public StringBuffer getOutMessage() {
        return outMessage;
    }

    public void appendOutMessage(String outMessage) {
        this.outMessage.append(outMessage);
    }

    public StringBuffer getLogMessage() {
        return logMessage;
    }

    public void appendLogMessage(String logMessage) {
        this.logMessage.append(logMessage);
    }

    public void throwable(Throwable throwable) {
        if (throwable != null) {
            if (!StrUtil.isEmpty(throwable.getMessage())) {
                this.logMessage.append(throwable.getMessage());
            } else {
                this.logMessage.append(throwable.toString());
            }
        }
    }
}
