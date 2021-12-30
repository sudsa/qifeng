/**
 * 服务器管理管理初始化
 */
var VssIp = {
    id: "VssIpTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssIp.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: 'uuid', field: 'uuid', visible: true, align: 'center', valign: 'middle'},
            {title: 'ip', field: 'ip', visible: true, align: 'center', valign: 'middle'},
            {title: '区域id', field: 'regionUuid', visible: true, align: 'center', valign: 'middle'},
            {title: '是否主服务器', field: 'master', visible: true, align: 'center', valign: 'middle',formatter: function (value, row, index) {
                    if (value == '1') {
                        return "是";
                    } else if (value == '2'){
                        return "否";
                    }
                }
            },
            {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle',formatter: function (value, row, index) {
                    if (value == '1') {
                        return "正常";
                    } else if (value == '2'){
                        return "禁用";
                    }
                }
            }
    ];
};

/**
 * 检查是否选中
 */
VssIp.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssIp.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加服务器管理
 */
VssIp.openAddVssIp = function () {
    var index = layer.open({
        type: 2,
        title: '添加服务器管理',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssIp/vssIp_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看服务器管理详情
 */
VssIp.openVssIpDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '服务器管理详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssIp/vssIp_update/' + VssIp.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除服务器管理
 */
VssIp.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssIp/delete", function (data) {
            Feng.success("删除成功!");
            VssIp.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssIpId",this.seItem.uuid);
        ajax.start();
    }
};

/**
 * 查询服务器管理列表
 */
VssIp.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    VssIp.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = VssIp.initColumn();
    var table = new BSTable(VssIp.id, "/vssIp/list", defaultColunms);
    table.setPaginationType("client");
    VssIp.table = table.init();
});
