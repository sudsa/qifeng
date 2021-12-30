/**
 * 计划网站管理初始化
 */
var VssPlanSite = {
    id: "VssPlanSiteTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssPlanSite.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: 'uuid', field: 'uuid', visible: true, align: 'center', valign: 'middle'},
            {title: '客户uuid', field: 'planUuid', visible: true, align: 'center', valign: 'middle'},
            {title: '网站uuid', field: 'siteUuid', visible: true, align: 'center', valign: 'middle'},
            {title: '每天下载次数限制', field: 'daydown', visible: true, align: 'center', valign: 'middle'},
            {title: '每个ip每天下载次数', field: 'ipLimit', visible: true, align: 'center', valign: 'middle'},
            {title: '积分总数', field: 'coin', visible: true, align: 'center', valign: 'middle'},
            {title: '大积分界限', field: 'bigCoinLine', visible: true, align: 'center', valign: 'middle'},
            {title: '1;正常；2禁用', field: 'status', visible: true, align: 'center', valign: 'middle'},
            {title: '创建时间', field: 'insertTime', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
VssPlanSite.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssPlanSite.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加计划网站
 */
VssPlanSite.openAddVssPlanSite = function () {
    var index = layer.open({
        type: 2,
        title: '添加计划网站',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssPlanSite/vssPlanSite_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看计划网站详情
 */
VssPlanSite.openVssPlanSiteDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '计划网站详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssPlanSite/vssPlanSite_update/' + VssPlanSite.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除计划网站
 */
VssPlanSite.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssPlanSite/delete", function (data) {
            Feng.success("删除成功!");
            VssPlanSite.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssPlanSiteId",this.seItem.uuid);
        ajax.start();
    }
};

/**
 * 查询计划网站列表
 */
VssPlanSite.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    VssPlanSite.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = VssPlanSite.initColumn();
    var table = new BSTable(VssPlanSite.id, "/vssPlanSite/list", defaultColunms);
    table.setPaginationType("server");
    VssPlanSite.table = table.init();
});
