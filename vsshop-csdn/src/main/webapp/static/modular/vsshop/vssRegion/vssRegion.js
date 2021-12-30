/**
 * 区域管理管理初始化
 */
var VssRegion = {
    id: "VssRegionTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssRegion.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: 'uuid', field: 'uuid', visible: true, align: 'center', valign: 'middle'},
            {title: '名称', field: 'name', visible: true, align: 'center', valign: 'middle'},
            {title: '城市', field: 'citys', visible: true, align: 'center', valign: 'middle'},
            {title: '域名', field: 'domain', visible: true, align: 'center', valign: 'middle'},
            {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle',formatter: function (value, row, index) {
                    if (value == '1') {
                        return "正常";
                    } else if (value == '2'){
                        return "禁用";
                    }
                }
            },
            {title: '他域共享', field: 'otherShare', visible: true, align: 'center', valign: 'middle',formatter: function (value, row, index) {
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
VssRegion.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssRegion.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加区域管理
 */
VssRegion.openAddVssRegion = function () {
    var index = layer.open({
        type: 2,
        title: '添加区域管理',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssRegion/vssRegion_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看区域管理详情
 */
VssRegion.openVssRegionDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '区域管理详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssRegion/vssRegion_update/' + VssRegion.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除区域管理
 */
VssRegion.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssRegion/delete", function (data) {
            Feng.success("删除成功!");
            VssRegion.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssRegionId",this.seItem.uuid);
        ajax.start();
    }
};

/**
 * 查询区域管理列表
 */
VssRegion.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    VssRegion.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = VssRegion.initColumn();
    var table = new BSTable(VssRegion.id, "/vssRegion/list", defaultColunms);
    table.setPaginationType("client");
    VssRegion.table = table.init();
});
