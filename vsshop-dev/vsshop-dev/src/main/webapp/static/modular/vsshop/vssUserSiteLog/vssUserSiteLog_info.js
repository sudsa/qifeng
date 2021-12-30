/**
 * 初始化计划网站日志详情对话框
 */
var VssUserSiteLogInfoDlg = {
    vssUserSiteLogInfoData : {}
};

/**
 * 清除数据
 */
VssUserSiteLogInfoDlg.clearData = function() {
    this.vssUserSiteLogInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssUserSiteLogInfoDlg.set = function(key, val) {
    this.vssUserSiteLogInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssUserSiteLogInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssUserSiteLogInfoDlg.close = function() {
    parent.layer.close(window.parent.VssUserSiteLog.layerIndex);
}

/**
 * 收集数据
 */
VssUserSiteLogInfoDlg.collectData = function() {
    this
    .set('uuid')
    .set('userUuid')
    .set('siteUuid')
    .set('count')
    .set('insertTime')
    .set('lastTime');
}

/**
 * 提交添加
 */
VssUserSiteLogInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssUserSiteLog/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssUserSiteLog.table.refresh();
        VssUserSiteLogInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssUserSiteLogInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssUserSiteLogInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssUserSiteLog/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssUserSiteLog.table.refresh();
        VssUserSiteLogInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssUserSiteLogInfoData);
    ajax.start();
}

$(function() {

});
