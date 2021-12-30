/**
 * 初始化计划网站详情对话框
 */
var VssPlanSiteInfoDlg = {
    vssPlanSiteInfoData : {}
};

/**
 * 清除数据
 */
VssPlanSiteInfoDlg.clearData = function() {
    this.vssPlanSiteInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssPlanSiteInfoDlg.set = function(key, val) {
    this.vssPlanSiteInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssPlanSiteInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssPlanSiteInfoDlg.close = function() {
    parent.layer.close(window.parent.VssPlanSite.layerIndex);
}

/**
 * 收集数据
 */
VssPlanSiteInfoDlg.collectData = function() {
    this
    .set('uuid')
    .set('planUuid')
    .set('siteUuid')
    .set('daydown')
    .set('ipLimit')
    .set('coin')
    .set('bigCoinLine')
    .set('status')
    .set('insertTime');
}

/**
 * 提交添加
 */
VssPlanSiteInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssPlanSite/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssPlanSite.table.refresh();
        VssPlanSiteInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssPlanSiteInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssPlanSiteInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssPlanSite/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssPlanSite.table.refresh();
        VssPlanSiteInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssPlanSiteInfoData);
    ajax.start();
}

$(function() {

});
