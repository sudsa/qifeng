/**
 * 初始化区域管理详情对话框
 */
var VssRegionInfoDlg = {
    vssRegionInfoData : {}
};

/**
 * 清除数据
 */
VssRegionInfoDlg.clearData = function() {
    this.vssRegionInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssRegionInfoDlg.set = function(key, val) {
    this.vssRegionInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssRegionInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssRegionInfoDlg.close = function() {
    parent.layer.close(window.parent.VssRegion.layerIndex);
}

/**
 * 收集数据
 */
VssRegionInfoDlg.collectData = function() {
    this
    .set('uuid')
    .set('name')
    .set('citys')
    .set('domain')
    .set('status')
    .set('otherShare');
}

/**
 * 提交添加
 */
VssRegionInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssRegion/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssRegion.table.refresh();
        VssRegionInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssRegionInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssRegionInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssRegion/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssRegion.table.refresh();
        VssRegionInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssRegionInfoData);
    ajax.start();
}

$(function() {
    if($("#statusValue").val() == undefined){
        $("#status").val(1);
    }else{
        $("#status").val($("#statusValue").val());
    }

    if($("#otherShareValue").val() == undefined){
        $("#otherShare").val(1);
    }else{
        $("#otherShare").val($("#otherShareValue").val());
    }
});
