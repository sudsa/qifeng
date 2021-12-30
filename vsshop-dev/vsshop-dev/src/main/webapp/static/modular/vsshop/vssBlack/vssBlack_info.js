/**
 * 初始化网址黑名单详情对话框
 */
var VssBlackInfoDlg = {
    vssBlackInfoData : {}
};

/**
 * 清除数据
 */
VssBlackInfoDlg.clearData = function() {
    this.vssBlackInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssBlackInfoDlg.set = function(key, val) {
    this.vssBlackInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssBlackInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssBlackInfoDlg.close = function() {
    parent.layer.close(window.parent.VssBlack.layerIndex);
}

/**
 * 收集数据
 */
VssBlackInfoDlg.collectData = function() {
    this
    .set('uuid')
    .set('downUrl')
    .set('reason')
    .set('insertTime');
}

/**
 * 提交添加
 */
VssBlackInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssBlack/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssBlack.table.refresh();
        VssBlackInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssBlackInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssBlackInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssBlack/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssBlack.table.refresh();
        VssBlackInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssBlackInfoData);
    ajax.start();
}

$(function() {

});
