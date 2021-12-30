/**
 * 会员商品管理初始化
 */
var VssVip = {
    id: "VssVipTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssVip.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: 'ID', field: 'id', visible: true, align: 'center', valign: 'middle'},
            {title: '会员名称', field: 'name', visible: true, align: 'center', valign: 'middle'},
            {title: '描述', field: 'remark', visible: true, align: 'center', valign: 'middle'},
            {title: '单价', field: 'price', visible: true, align: 'center', valign: 'middle'},
            {title: '库存', field: 'stock', visible: true, align: 'center', valign: 'middle'},
            {title: '链接', field: 'link', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
VssVip.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssVip.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加会员商品
 */
VssVip.openAddVssVip = function () {
    var index = layer.open({
        type: 2,
        title: '添加会员商品',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssVip/vssVip_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看会员商品详情
 */
VssVip.openVssVipDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '会员商品详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssVip/vssVip_update/' + VssVip.seItem.id
        });
        this.layerIndex = index;
    }
};

/**
 * 删除会员商品
 */
VssVip.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssVip/delete", function (data) {
            Feng.success("删除成功!");
            VssVip.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssVipId",this.seItem.id);
        ajax.start();
    }
};

/**
 * 查询会员商品列表
 */
VssVip.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    VssVip.table.refresh({query: queryData});
};

$(function () {
    var defaultColunms = VssVip.initColumn();
    var table = new BSTable(VssVip.id, "/vssVip/list", defaultColunms);
    table.setPaginationType("server");
    VssVip.table = table.init();
});
