/**
 * 初始化第三方用户详情对话框
 */
var VssUserInfoDlg = {
    vssUserInfoData : {}
};

/**
 * 清除数据
 */
VssUserInfoDlg.clearData = function() {
    this.vssUserInfoData = {};
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssUserInfoDlg.set = function(key, val) {
    this.vssUserInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
}

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
VssUserInfoDlg.get = function(key) {
    return $("#" + key).val();
}

/**
 * 关闭此对话框
 */
VssUserInfoDlg.close = function() {
    parent.layer.close(window.parent.VssUser.layerIndex);
}

/**
 * 收集数据
 */
VssUserInfoDlg.collectData = function() {
    this
    .set('uuid')
    .set('source')
    .set('type')
    .set('username')
    .set('password')
    .set('cookie')
    .set('loginStatus')
    .set('status')
    .set('coin')
    .set('dayDown')
    .set('ipUuid')
}

/**
 * 提交添加
 */
VssUserInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssUser/add", function(data){
        Feng.success("添加成功!");
        window.parent.VssUser.table.refresh();
        VssUserInfoDlg.close();
    },function(data){
        Feng.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssUserInfoData);
    ajax.start();
}

/**
 * 提交修改
 */
VssUserInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Feng.ctxPath + "/vssUser/update", function(data){
        Feng.success("修改成功!");
        window.parent.VssUser.table.refresh();
        VssUserInfoDlg.close();
    },function(data){
        Feng.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.vssUserInfoData);
    ajax.start();
}

$(function() {
    if($("#statusValue").val() == undefined){
        $("#status").val(1);
    }else{
        $("#status").val($("#statusValue").val());
    }
    if($("#sourceValue").val() == undefined){
        $("#source").val(1);
    }else{
        $("#source").val($("#sourceValue").val());
    }
    if($("#typeValue").val() == undefined){
        $("#type").val(1);
    }else{
        $("#type").val($("#typeValue").val());
    }
    if($("#loginStatusValue").val() == undefined){
        $("#loginStatus").val(1);
    }else{
        $("#loginStatus").val($("#loginStatusValue").val());
    }


    var ajax = new $ax(Feng.ctxPath + "/vssIp/list", function(data){
        if(data!='undefined'){
            var html="";
            var industry=$("#ipUuid").attr("value");
            for(var i=0;i<data.length;i++){
                if(data[i].uuid==industry){
                    html+="<option selected value='"+data[i].uuid+"'>"+data[i].ip+" : "+data[i].regionUuid+"</option>"
                }else {
                    html+="<option value='"+data[i].uuid+"'>"+data[i].ip+" : "+data[i].regionUuid+"</option>"
                }

            }
            $("#ipUuid").html(html);
        }
    },function(data){

    });
    ajax.start();
});
