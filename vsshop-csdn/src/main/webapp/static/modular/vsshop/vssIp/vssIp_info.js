/**
 * 初始化服务器管理详情对话框
 */
var VssIpInfoDlg = {
    vssIpInfoData : {}
};

/**
 * 清除数据
 */
VssIpInfoDlg.clearData = function() {
    this.vssIpInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssIpInfoDlg.set = function(key, val) {
    this.vssIpInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssIpInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssIpInfoDlg.close = function() {
    parent.layer.close(window.parent.VssIp.layerIndex);
}

/**
 * 收集数据
 */
VssIpInfoDlg.collectData = function() {
    this
    .set('uuid')
    .set('ip')
    .set('regionUuid')
    .set('status')
    .set('master');
}

/**
 * 提交添加
 */
VssIpInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssIp/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssIp.table.refresh();
        VssIpInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssIpInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssIpInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssIp/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssIp.table.refresh();
        VssIpInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssIpInfoData);
    ajax.start();
}

$(function() {
    if($("#statusValue").val() == undefined){
        $("#status").val(1);
    }else{
        $("#status").val($("#statusValue").val());
    }

    if($("#masterValue").val() == undefined){
        $("#master").val(1);
    }else{
        $("#master").val($("#masterValue").val());
    }
    var ajax = new $ax(Feng.ctxPath + "/vssIp/getRegions", function(data){
        if(data!='undefined'){
            var html="";
            var industry=$("#regionUuid").attr("value");
            for(var i=0;i<data.length;i++){
                if(data[i].uuid==industry){
                    html+="<option selected value='"+data[i].uuid+"'>"+data[i].name+"</option>"
                }else {
                    html+="<option value='"+data[i].uuid+"'>"+data[i].name+"</option>"
                }

            }
            $("#regionUuid").html(html);
        }
    },function(data){

    });
    ajax.start();

});
