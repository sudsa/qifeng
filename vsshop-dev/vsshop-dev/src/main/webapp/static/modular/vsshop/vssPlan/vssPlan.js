/**
 * 计划管理管理初始化
 */
var VssPlan = {
    id: "VssPlanTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssPlan.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: 'uuid', field: 'uuid', visible: true, align: 'center', valign: 'middle'},
            {title: '计划名称', field: 'name', visible: true, align: 'center', valign: 'middle'},
            {title: '计划类型：1：客户使用；2：网站用户使用', field: 'type', visible: true, align: 'center', valign: 'middle'},
            {title: '拥有者uuid', field: 'createUuid', visible: true, align: 'center', valign: 'middle'},
            {title: '创建时间', field: 'insertTime', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
VssPlan.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssPlan.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加计划管理
 */
VssPlan.openAddVssPlan = function () {
    var index = layer.open({
        type: 2,
        title: '添加计划管理',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssPlan/vssPlan_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看计划管理详情
 */
VssPlan.openVssPlanDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '计划管理详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssPlan/vssPlan_update/' + VssPlan.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除计划管理
 */
VssPlan.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssPlan/delete", function (data) {
            Feng.success("删除成功!");
            VssPlan.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssPlanId",this.seItem.uuid);
        ajax.start();
    }
};

/**
 * 查询计划管理列表
 */
VssPlan.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    VssPlan.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = VssPlan.initColumn();
    var table = new BSTable(VssPlan.id, "/vssPlan/list", defaultColunms);
    table.setPaginationType("server");
    VssPlan.table = table.init();
});
