/**
 * 计划网站日志管理初始化
 */
var VssUserSiteLog = {
    id: "VssUserSiteLogTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssUserSiteLog.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: 'uuid', field: 'uuid', visible: true, align: 'center', valign: 'middle'},
            {title: '客户uuid', field: 'userUuid', visible: true, align: 'center', valign: 'middle'},
            {title: '网站uuid', field: 'siteUuid', visible: true, align: 'center', valign: 'middle'},
            {title: '下载次数', field: 'count', visible: true, align: 'center', valign: 'middle'},
            {title: '创建时间', field: 'insertTime', visible: true, align: 'center', valign: 'middle'},
            {title: '最后时间', field: 'lastTime', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
VssUserSiteLog.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssUserSiteLog.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加计划网站日志
 */
VssUserSiteLog.openAddVssUserSiteLog = function () {
    var index = layer.open({
        type: 2,
        title: '添加计划网站日志',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssUserSiteLog/vssUserSiteLog_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看计划网站日志详情
 */
VssUserSiteLog.openVssUserSiteLogDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '计划网站日志详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssUserSiteLog/vssUserSiteLog_update/' + VssUserSiteLog.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除计划网站日志
 */
VssUserSiteLog.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssUserSiteLog/delete", function (data) {
            Feng.success("删除成功!");
            VssUserSiteLog.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssUserSiteLogId",this.seItem.uuid);
        ajax.start();
    }
};

/**
 * 查询计划网站日志列表
 */
VssUserSiteLog.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    VssUserSiteLog.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = VssUserSiteLog.initColumn();
    var table = new BSTable(VssUserSiteLog.id, "/vssUserSiteLog/list", defaultColunms);
    table.setPaginationType("server");
    VssUserSiteLog.table = table.init();
});
