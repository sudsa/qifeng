package cn.stylefeng.guns.modular.vsshop.model;

import cn.stylefeng.guns.modular.system.model.VssPlanSite;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by jianyinlin on 2019/5/29
 */
public class CreateCodeReq {
    /**
     * 类型：1日；2月；3季度；4半年；5年；6终身;
     */
    private String dateType;
    /**
     * 次数
     */
    private String maxDown;

    /**
     * 计划uuid
     */
    @NotBlank(message = "计划不能为空")
    private String planUuid;
    /**
     * 生成个数
     */
    @NotNull(message = "生成数量不能为空")
    private Integer num;

    /**
     * 根据次数计划明细
     */
    private List<VssPlanSite> countList;
    /**
     * 根据积分计划明细
     */
    private List<VssPlanSite> coinList;


    public List<VssPlanSite> getCountList() {
        return countList;
    }

    public void setCountList(List<VssPlanSite> countList) {
        this.countList = countList;
    }

    public List<VssPlanSite> getCoinList() {
        return coinList;
    }

    public void setCoinList(List<VssPlanSite> coinList) {
        this.coinList = coinList;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getMaxDown() {
        return maxDown;
    }

    public void setMaxDown(String maxDown) {
        this.maxDown = maxDown;
    }

    public String getPlanUuid() {
        return planUuid;
    }

    public void setPlanUuid(String planUuid) {
        this.planUuid = planUuid;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
