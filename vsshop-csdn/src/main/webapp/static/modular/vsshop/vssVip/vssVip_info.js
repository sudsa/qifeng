/**
 * 初始化会员商品详情对话框
 */
var VssVipInfoDlg = {
    vssVipInfoData : {}
};

/**
 * 清除数据
 */
VssVipInfoDlg.clearData = function() {
    this.vssVipInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssVipInfoDlg.set = function(key, val) {
    this.vssVipInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssVipInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssVipInfoDlg.close = function() {
    parent.layer.close(window.parent.VssVip.layerIndex);
}

/**
 * 收集数据
 */
VssVipInfoDlg.collectData = function() {
    this
    .set('id')
    .set('name')
    .set('remark')
    .set('price')
    .set('stock')
    .set('link');
}

/**
 * 提交添加
 */
VssVipInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssVip/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssVip.table.refresh();
        VssVipInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssVipInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssVipInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssVip/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssVip.table.refresh();
        VssVipInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssVipInfoData);
    ajax.start();
}

$(function() {

});
