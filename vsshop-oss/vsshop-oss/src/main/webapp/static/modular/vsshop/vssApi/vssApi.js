/**
 * 别家api管理初始化
 */
var VssApi = {
    id: "VssApiTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssApi.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: 'uuid', field: 'uuid', visible: true, align: 'center', valign: 'middle'},
            {title: '', field: 'code', visible: true, align: 'center', valign: 'middle'},
            {title: '', field: 'downUrl', visible: true, align: 'center', valign: 'middle'},
            {title: '', field: 'apiUrl', visible: true, align: 'center', valign: 'middle'},
            {title: '', field: 'status', visible: true, align: 'center', valign: 'middle'},
            {title: '', field: 'order', visible: true, align: 'center', valign: 'middle'},
            {title: '', field: 'msg', visible: true, align: 'center', valign: 'middle'},
            {title: '', field: 'insertTime', visible: true, align: 'center', valign: 'middle'},
            {title: '', field: 'usedTime', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
VssApi.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssApi.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加别家api
 */
VssApi.openAddVssApi = function () {
    var index = layer.open({
        type: 2,
        title: '添加别家api',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssApi/vssApi_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看别家api详情
 */
VssApi.openVssApiDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '别家api详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssApi/vssApi_update/' + VssApi.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除别家api
 */
VssApi.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssApi/delete", function (data) {
            Feng.success("删除成功!");
            VssApi.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssApiId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询别家api列表
 */
VssApi.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    VssApi.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = VssApi.initColumn();
    var table = new BSTable(VssApi.id, "/vssApi/list", defaultColunms);
    table.setPaginationType("client");
    VssApi.table = table.init();
});
