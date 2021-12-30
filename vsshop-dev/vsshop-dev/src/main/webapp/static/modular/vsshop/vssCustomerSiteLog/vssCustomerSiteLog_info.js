/**
 * 初始化客户网站日志详情对话框
 */
var VssCustomerSiteLogInfoDlg = {
    vssCustomerSiteLogInfoData : {}
};

/**
 * 清除数据
 */
VssCustomerSiteLogInfoDlg.clearData = function() {
    this.vssCustomerSiteLogInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssCustomerSiteLogInfoDlg.set = function(key, val) {
    this.vssCustomerSiteLogInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssCustomerSiteLogInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssCustomerSiteLogInfoDlg.close = function() {
    parent.layer.close(window.parent.VssCustomerSiteLog.layerIndex);
}

/**
 * 收集数据
 */
VssCustomerSiteLogInfoDlg.collectData = function() {
    this
    .set('uuid')
    .set('customerUuid')
    .set('siteUuid')
    .set('count')
    .set('insertTime')
    .set('lastTime');
}

/**
 * 提交添加
 */
VssCustomerSiteLogInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssCustomerSiteLog/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssCustomerSiteLog.table.refresh();
        VssCustomerSiteLogInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssCustomerSiteLogInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssCustomerSiteLogInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssCustomerSiteLog/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssCustomerSiteLog.table.refresh();
        VssCustomerSiteLogInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssCustomerSiteLogInfoData);
    ajax.start();
}

$(function() {

});
