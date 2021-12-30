/**
 * 初始化计划管理详情对话框
 */
var VssPlanInfoDlg = {
    vssPlanInfoData : {}
};

/**
 * 清除数据
 */
VssPlanInfoDlg.clearData = function() {
    this.vssPlanInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssPlanInfoDlg.set = function(key, val) {
    this.vssPlanInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssPlanInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssPlanInfoDlg.close = function() {
    parent.layer.close(window.parent.VssPlan.layerIndex);
}

/**
 * 收集数据
 */
VssPlanInfoDlg.collectData = function() {
    this
    .set('uuid')
    .set('name')
    .set('type')
    .set('createUuid')
    .set('insertTime');
}

/**
 * 提交添加
 */
VssPlanInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssPlan/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssPlan.table.refresh();
        VssPlanInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssPlanInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssPlanInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssPlan/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssPlan.table.refresh();
        VssPlanInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssPlanInfoData);
    ajax.start();
}

$(function() {

});
