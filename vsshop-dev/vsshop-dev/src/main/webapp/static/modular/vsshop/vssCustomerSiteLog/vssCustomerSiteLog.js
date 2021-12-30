/**
 * 客户网站日志管理初始化
 */
var VssCustomerSiteLog = {
    id: "VssCustomerSiteLogTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssCustomerSiteLog.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: 'uuid', field: 'uuid', visible: true, align: 'center', valign: 'middle'},
            {title: '客户uuid', field: 'customerUuid', visible: true, align: 'center', valign: 'middle'},
            {title: '网站uuid', field: 'siteUuid', visible: true, align: 'center', valign: 'middle'},
            {title: '下载次数', field: 'count', visible: true, align: 'center', valign: 'middle'},
            {title: '创建时间', field: 'insertTime', visible: true, align: 'center', valign: 'middle'},
            {title: '最后时间', field: 'lastTime', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
VssCustomerSiteLog.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssCustomerSiteLog.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加客户网站日志
 */
VssCustomerSiteLog.openAddVssCustomerSiteLog = function () {
    var index = layer.open({
        type: 2,
        title: '添加客户网站日志',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssCustomerSiteLog/vssCustomerSiteLog_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看客户网站日志详情
 */
VssCustomerSiteLog.openVssCustomerSiteLogDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '客户网站日志详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssCustomerSiteLog/vssCustomerSiteLog_update/' + VssCustomerSiteLog.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除客户网站日志
 */
VssCustomerSiteLog.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssCustomerSiteLog/delete", function (data) {
            Feng.success("删除成功!");
            VssCustomerSiteLog.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssCustomerSiteLogId",this.seItem.uuid);
        ajax.start();
    }
};

/**
 * 查询客户网站日志列表
 */
VssCustomerSiteLog.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    VssCustomerSiteLog.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = VssCustomerSiteLog.initColumn();
    var table = new BSTable(VssCustomerSiteLog.id, "/vssCustomerSiteLog/list", defaultColunms);
    table.setPaginationType("server");
    VssCustomerSiteLog.table = table.init();
});
