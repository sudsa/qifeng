/**
 * 初始化客户管理详情对话框
 */
var VssCustomerInfoDlg = {
    vssCustomerInfoData : {}
};

/**
 * 清除数据
 */
VssCustomerInfoDlg.clearData = function() {
    this.vssCustomerInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssCustomerInfoDlg.set = function(key, val) {
    this.vssCustomerInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssCustomerInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssCustomerInfoDlg.close = function() {
    parent.layer.close(window.parent.VssCustomer.layerIndex);
}

/**
 * 收集数据
 */
VssCustomerInfoDlg.collectData = function() {
    this
    .set('uuid')
    .set('customerCode')
    .set('name')
    .set('pwd')
    .set('nick')
    .set('email')
    .set('mobile')
    .set('wechat')
    .set('ip')
    .set('ownerName')
    .set('ownerUuid')
    .set('planName')
    .set('planUuid')
    .set('type')
    .set('status')
    .set('insertTime')
    .set('firstTime')
    .set('expiredTime')
    .set('lastTime');
}

/**
 * 提交添加
 */
VssCustomerInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssCustomer/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssCustomer.table.refresh();
        VssCustomerInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssCustomerInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssCustomerInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssCustomer/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssCustomer.table.refresh();
        VssCustomerInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssCustomerInfoData);
    ajax.start();
}

$(function() {

});
