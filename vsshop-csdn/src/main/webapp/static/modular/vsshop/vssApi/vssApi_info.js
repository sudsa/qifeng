/**
 * 初始化别家api详情对话框
 */
var VssApiInfoDlg = {
    vssApiInfoData : {}
};

/**
 * 清除数据
 */
VssApiInfoDlg.clearData = function() {
    this.vssApiInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssApiInfoDlg.set = function(key, val) {
    this.vssApiInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssApiInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssApiInfoDlg.close = function() {
    parent.layer.close(window.parent.VssApi.layerIndex);
}

/**
 * 收集数据
 */
VssApiInfoDlg.collectData = function() {
    this
    .set('uuid')
    .set('code')
    .set('downUrl')
    .set('apiUrl')
    .set('status')
    .set('order')
    .set('msg')
    .set('insertTime')
    .set('usedTime');
}

/**
 * 提交添加
 */
VssApiInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssApi/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssApi.table.refresh();
        VssApiInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssApiInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssApiInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssApi/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssApi.table.refresh();
        VssApiInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssApiInfoData);
    ajax.start();
}

$(function() {

});
