/**
 * 网址黑名单管理初始化
 */
var VssBlack = {
    id: "VssBlackTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssBlack.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: 'uuid', field: 'uuid', visible: true, align: 'center', valign: 'middle'},
            {title: 'url', field: 'downUrl', visible: true, align: 'center', valign: 'middle'},
            {title: '原因', field: 'reason', visible: true, align: 'center', valign: 'middle'},
            {title: '创建时间', field: 'insertTime', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
VssBlack.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssBlack.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加网址黑名单
 */
VssBlack.openAddVssBlack = function () {
    var index = layer.open({
        type: 2,
        title: '添加网址黑名单',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssBlack/vssBlack_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看网址黑名单详情
 */
VssBlack.openVssBlackDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '网址黑名单详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssBlack/vssBlack_update/' + VssBlack.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除网址黑名单
 */
VssBlack.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssBlack/delete", function (data) {
            Feng.success("删除成功!");
            VssBlack.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssBlackId",this.seItem.uuid);
        ajax.start();
    }
};

/**
 * 查询网址黑名单列表
 */
VssBlack.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    VssBlack.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = VssBlack.initColumn();
    var table = new BSTable(VssBlack.id, "/vssBlack/list", defaultColunms);
    table.setPaginationType("server");
    VssBlack.table = table.init();
});
