/**
 * 初始化网站管理详情对话框
 */
var VssSiteInfoDlg = {
    vssSiteInfoData : {}
};

/**
 * 清除数据
 */
VssSiteInfoDlg.clearData = function() {
    this.vssSiteInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssSiteInfoDlg.set = function(key, val) {
    this.vssSiteInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssSiteInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssSiteInfoDlg.close = function() {
    parent.layer.close(window.parent.VssSite.layerIndex);
}

/**
 * 收集数据
 */
VssSiteInfoDlg.collectData = function() {
    this
    .set('uuid')
    .set('name')
    .set('abbr')
    .set('domain')
    .set('status')
    .set('insertTime');
}

/**
 * 提交添加
 */
VssSiteInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssSite/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssSite.table.refresh();
        VssSiteInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssSiteInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssSiteInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssSite/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssSite.table.refresh();
        VssSiteInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssSiteInfoData);
    ajax.start();
}

$(function() {

});
