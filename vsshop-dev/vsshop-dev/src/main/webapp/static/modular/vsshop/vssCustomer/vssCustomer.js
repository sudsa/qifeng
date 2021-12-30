/**
 * 客户管理管理初始化
 */
var VssCustomer = {
    id: "VssCustomerTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
VssCustomer.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
            {title: '用户码', field: 'customerCode', visible: true, align: 'center', valign: 'middle',width:'250px',cellStyle:formatTableUnit, formatter:paramsMatter},
        // 1:未使用，2已使用，3作废
        {title: '状态', field: 'status', visible: true, align: 'center', valign: 'middle',formatter: function (value, row, index) {
                if (value == '1') {
                    return "未使用";
                } else if(value == '2'){
                    return "已使用";
                }else if(value == '3'){
                    return "作废";
                }
            }
        },
        {title: '用户名', field: 'name', visible: true, align: 'center', valign: 'middle'},
            {title: '密码', field: 'pwd', visible: true, align: 'center', valign: 'middle'},
            {title: '昵称', field: 'nick', visible: true, align: 'center', valign: 'middle'},
            {title: '邮箱', field: 'email', visible: true, align: 'center', valign: 'middle'},
            {title: '手机号', field: 'mobile', visible: true, align: 'center', valign: 'middle'},
            {title: '微信', field: 'wechat', visible: true, align: 'center', valign: 'middle'},
            {title: 'ip', field: 'ip', visible: true, align: 'center', valign: 'middle'},
            {title: '拥有者', field: 'ownerName', visible: true, align: 'center', valign: 'middle'},
            {title: '拥有者_uuid', field: 'ownerUuid', visible: true, align: 'center', valign: 'middle'},
            {title: '计划名称', field: 'planName', visible: true, align: 'center', valign: 'middle'},
            {title: '计划类型', field: 'planUuid', visible: true, align: 'center', valign: 'middle'},
            {title: '类型：1日；2月；3季度；4半年；5年；6终身；7次', field: 'type', visible: true, align: 'center', valign: 'middle'},
            {title: '创建时间', field: 'insertTime', visible: true, align: 'center', valign: 'middle'},
            {title: '首次登陆时间', field: 'firstTime', visible: true, align: 'center', valign: 'middle'},
            {title: '失效时间', field: 'expiredTime', visible: true, align: 'center', valign: 'middle'},
            {title: '最后时间', field: 'lastTime', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
VssCustomer.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if(selected.length == 0){
        Feng.info("请先选中表格中的某一记录！");
        return false;
    }else{
        VssCustomer.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加客户管理
 */
VssCustomer.openAddVssCustomer = function () {
    var index = layer.open({
        type: 2,
        title: '添加客户管理',
        area: ['800px', '420px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/vssCustomer/vssCustomer_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看客户管理详情
 */
VssCustomer.openVssCustomerDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: '客户管理详情',
            area: ['800px', '420px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/vssCustomer/vssCustomer_update/' + VssCustomer.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除客户管理
 */
VssCustomer.delete = function () {
    if (this.check()) {
        var ajax = new $ax(Feng.ctxPath + "/vssCustomer/delete", function (data) {
            Feng.success("删除成功!");
            VssCustomer.table.refresh();
        }, function (data) {
            Feng.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("vssCustomerId",this.seItem.uuid);
        ajax.start();
    }
};

/**
 * 查询客户管理列表
 */
VssCustomer.search = function () {
    var queryData = {};
    queryData['status'] = $("#status").val();
    queryData['code'] = $("#code").val();
    VssCustomer.table.refresh({query: queryData});
};
/**
 * 导出激活码
 */
VssCustomer.export = function () {
    var queryData = {};
    queryData['status'] = $("#status").val();
    queryData['code'] = $("#code").val();
    window.location.href="vssCustomer/export"+_urlEncode(queryData).replace("&","?");

};
/**
 * param 将要转为URL参数字符串的对象
 * key URL参数字符串的前缀
 * encode true/false 是否进行URL编码,默认为true
 *
 * return URL参数字符串
 */
function _urlEncode(param, key, encode) {
    if(param==null) return '';
    var paramStr = '';
    var t = typeof (param);
    if (t == 'string' || t == 'number' || t == 'boolean') {
        paramStr += '&' + key + '=' + ((encode==null||encode) ? encodeURIComponent(param) : param);
    } else {
        for (var i in param) {
            var k = key == null ? i : key + (param instanceof Array ? '[' + i + ']' : '.' + i);
            paramStr += _urlEncode(param[i], k, encode);
        }
    }
    return paramStr;
};

function paramsMatter(value,row,index) {
    var span=document.createElement('span');
    span.setAttribute('title',value);
    span.innerHTML = value;
    return span.outerHTML;
}

function formatTableUnit(value, row, index) {
    return {
        css: {
            "white-space": 'nowrap',
            "text-overflow": 'ellipsis',
            "overflow": 'hidden'
        }
    }
}
$(function () {
    var defaultColunms = VssCustomer.initColumn();
    var table = new BSTable(VssCustomer.id, "/vssCustomer/list", defaultColunms);
    table.setPaginationType("server");
    VssCustomer.table = table.init();
});
