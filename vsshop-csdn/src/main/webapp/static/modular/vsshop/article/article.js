/**
 * VipArticle管理初始化
 */
var Article = {
    id: "ArticleTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Article.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
        {title: 'id', field: 'uuid', visible: false, align: 'center', valign: 'middle'},
        {title: '激活码', field: 'code', visible: true, align: 'center', valign: 'middle'},
        {title: '下载地址', field: 'downUrl', visible: true, align: 'center', valign: 'middle'},
        {
            title: '状态',
            field: 'status',
            visible: true,
            align: 'center',
            valign: 'middle',
            formatter: function (value, row, index) {
                if (value == '1') {
                    return "未使用";
                } else if (value == '2') {
                    return "已使用";
                } else if (value == '3') {
                    return "作废";
                }
            }
        },
        {title: 'ip', field: 'ip', visible: true, align: 'center', valign: 'middle'},
        {title: '邮箱', field: 'email', visible: true, align: 'center', valign: 'middle'},
        {title: '创建者', field: 'owner', visible: true, align: 'center', valign: 'middle'},
        {title: '创建时间', field: 'insertTime', visible: true, align: 'center', valign: 'middle'},
        {title: '使用时间', field: 'usedTime', visible: true, align: 'center', valign: 'middle'},
        {title: '最后时间', field: 'updateTime', visible: true, align: 'center', valign: 'middle'}
    ];
};

/**
 * 检查是否选中
 */
Article.check = function () {
    var selected = $('#' + this.id).bootstrapTable('getSelections');
    if (selected.length == 0) {
        Feng.info("请先选中表格中的某一记录！");
        return false;
    } else {
        Article.seItem = selected[0];
        return true;
    }
};

/**
 * 点击添加VipArticle
 */
Article.openAddArticle = function () {
    var index = layer.open({
        type: 2,
        title: '添加文章激活码',
        area: ['420px', '300px'], //宽高
        fix: false, //不固定
        maxmin: true,
        content: Feng.ctxPath + '/article/article_add'
    });
    this.layerIndex = index;
};

/**
 * 打开查看VipArticle详情
 */
Article.openArticleDetail = function () {
    if (this.check()) {
        var index = layer.open({
            type: 2,
            title: 'VipArticle详情',
            area: ['400px', '680px'], //宽高
            fix: false, //不固定
            maxmin: true,
            content: Feng.ctxPath + '/article/article_update/' + Article.seItem.uuid
        });
        this.layerIndex = index;
    }
};

/**
 * 删除VipArticle
 */
Article.delete = function () {
    if (this.check()) {
        var articleId = this.seItem.uuid;
        var operation = function () {
            var ajax = new $ax(Feng.ctxPath + "/article/delete", function (data) {
                Feng.success("删除成功!");
                Article.table.refresh();
            }, function (data) {
                Feng.error("删除失败!" + data.responseJSON.message + "!");
            });
            ajax.set("articleId", articleId);
            ajax.start();
        }
        Feng.confirm("是否刪除该激活码?", operation);
    }
};

/**
 * 导出激活码
 */
Article.export = function () {
    var queryData = {};
    queryData['status'] = $("#status").val();
    queryData['code'] = $("#code").val();
    window.location.href = "article/export" + _urlEncode(queryData).replace("&", "?");

};

/**
 * 查询VipArticle列表
 */
Article.search = function () {
    var queryData = {};
    queryData['status'] = $("#status").val();
    queryData['code'] = $("#code").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
    Article.table.refresh({query: queryData});
};

/**
 * param 将要转为URL参数字符串的对象
 * key URL参数字符串的前缀
 * encode true/false 是否进行URL编码,默认为true
 *
 * return URL参数字符串
 */
function _urlEncode(param, key, encode) {
    if (param == null) return '';
    var paramStr = '';
    var t = typeof (param);
    if (t == 'string' || t == 'number' || t == 'boolean') {
        paramStr += '&' + key + '=' + ((encode == null || encode) ? encodeURIComponent(param) : param);
    } else {
        for (var i in param) {
            var k = key == null ? i : key + (param instanceof Array ? '[' + i + ']' : '.' + i);
            paramStr += _urlEncode(param[i], k, encode);
        }
    }
    return paramStr;
};

function paramsMatter(value, row, index) {
    var span = document.createElement('span');
    span.setAttribute('title', value);
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
    var defaultColunms = Article.initColumn();
    var table = new BSTable(Article.id, "/article/list", defaultColunms);
    table.setPaginationType("server");
    Article.table = table.init();
});
