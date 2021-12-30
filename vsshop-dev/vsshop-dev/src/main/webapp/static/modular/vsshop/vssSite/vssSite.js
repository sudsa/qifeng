/**
 * 网站管理管理初始化
 */
var VssSite = {
    id: "VssSiteTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssSite.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: 'uuid', field: 'uuid', visible: true, align: 'center', valign: 'middle'},
            {title: '网站名称', field: 'name', visible: true, align: 'center', valign: 'middle'},
            {title: '缩写', field: 'abbr', visible: true, align: 'center', valign: 'middle'},
            {title: '域名', field: 'domain', visible: true, align: 'center', valign: 'middle'},
            {title: '1:正常；2禁用', field: 'status', visible: true, align: 'center', valign: 'middle'},
            {title: '插入时间', field: 'insertTime', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
VssSite.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssSite.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加网站管理
 */
VssSite.openAddVssSite = function () {
    var index = layer.open({
        type: 2,
        title: '添加网站管理',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssSite/vssSite_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看网站管理详情
 */
VssSite.openVssSiteDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '网站管理详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssSite/vssSite_update/' + VssSite.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除网站管理
 */
VssSite.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssSite/delete", function (data) {
            Feng.success("删除成功!");
            VssSite.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssSiteId",this.seItem.uuid);
        ajax.start();
    }
};

/**
 * 查询网站管理列表
 */
VssSite.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    VssSite.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = VssSite.initColumn();
    var table = new BSTable(VssSite.id, "/vssSite/list", defaultColunms);
    table.setPaginationType("server");
    VssSite.table = table.init();
});
