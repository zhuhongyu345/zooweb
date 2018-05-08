$(function () {
    initDataGrid();
});

var curCacheId;

function initDataGrid() {
    $('#zkweb_zkcfg').datagrid({
        url: 'zkcfg/queryZkCfg'
    });
}

/****************************************************************************************************************************/
function initTree(cacheId) {

    $('#zkTree').tree({
        checkbox: true,
        url: "zk/queryZnode?cacheId=" + cacheId,
        animate: true,
        lines: true,
        onContextMenu: function (e, node) {
            e.preventDefault();
            $(this).tree('select', node.target);
            $('#mm').menu('show', {
                left: e.pageX,
                top: e.pageY
            });
        },
        onLoadSuccess:function(node, data) {
            if (data.length === 1 && data[0].text === '/')
                $('#zkTree').tree('expand', $('#zkTree').tree('getRoot').target);
        },
        onClick: function (node) {
            doData(encodeURI(encodeURI(node.attributes.path)), cacheId);
        },
        onBeforeExpand: function (node, param) {
            if (node.attributes != null) {
                $('#zkTree').tree('options').url = "zk/queryZnode?path=" + encodeURI(encodeURI(node.attributes.path)) + "&cacheId=" + cacheId;
            }
        }
    });

}

function messagerShow(title, msg) {
    $.messager.show({
        title:title,
        msg:msg,
        timeout: 1000,
        showType:'fade',
        style:{
            right: '',
            bottom: '',
        }
    });
}

function add() {
    var node = $('#zkTree').tree('getSelected');
    if (!node) {
        messagerShow('提示', '请选择一个节点');
        return;
    }
    $('#w').window('open');
}

function remove() {
    var nodes = $('#zkTree').tree('getChecked');
    if (nodes.length === 0) {
        messagerShow('提示', '请先选择节点');
        return;
    }
    var paths = [];
    $.each(nodes, function (index, node) {
        paths.push(node.attributes.path)
    });
    var topPaths = [], topNodes = [];
    $.each(nodes, function (index, node) {
        var parentExist = false;
        $.each(paths, function (pathIndex, path) {
            if (node.attributes.path !== path && node.attributes.path.indexOf(path) === 0) {
                parentExist = true;
                return false;
            }
        });
        if (!parentExist) {
            topPaths.push(node.attributes.path)
            topNodes.push(node)
        }
    });
    var systemPaths = ['/', '/zookeeper', '/zookeeper/quota'];
    var haveSystemPath = false;
    $.each(systemPaths, function (index, systemPath) {
        if (topPaths.indexOf(systemPath) >= 0) {
            haveSystemPath = true;
            return false;
        }
    });
    if (haveSystemPath) {
        $.messager.alert('提示', '不能删除系统保留节点：\n' + systemPaths);
        return;
    }
    var topNodeParents = [];
    $.each(topNodes, function (index, topNode) {
        var parentNode = $('#zkTree').tree('getParent', topNode.target)
        topNodeParents.push(parentNode)
    })
    $.messager.confirm('提示', '删除所选节点及其所有子节点！确认吗？', function (r) {
        if (r) {
            $.ajax({
                url: "zk/deleteNode",
                type: 'post',
                traditional:true,
                data: {paths: topPaths, cacheId: curCacheId},
                success: function (data) {
                    $.each(topNodeParents, function (index, topNodeParent) {
                        $('#zkTree').tree('reload', topNodeParent.target);
                    })
                    $.messager.alert('提示', data);
                }
            });
        }
    });
}

function reload() {
    var node = $('#zkTree').tree('getSelected');
    if (!node) {
        messagerShow('提示', '请选择一个节点');
        return;
    }
    $('#zkTree').tree('reload', node.target)
}

function collapse() {
    var node = $('#zkTree').tree('getSelected');
    $('#zkTree').tree('collapse', node.target);
    var node = $('#zkTree').tree('getSelected');
}

function expand() {
    var node = $('#zkTree').tree('getSelected');
    $('#zkTree').tree('expand', node.target);
}

function addzkNode() {
    var node = $('#zkTree').tree('getSelected');
    if (!node) {
        $.messager.alert('提示', '请选择一个节点');
        return;
    }
    var _path = node.attributes.path;
    var _nodeName = $('#zkNodeName').val();
    $.post("zk/createNode", {nodeName: _nodeName, path: _path, cacheId: curCacheId},
        function (data) {
            $.messager.alert('提示', data);
            if (!$('#zkTree').tree('isLeaf', node.target)) {
                $('#zkTree').tree('reload', node.target);
            } else {
                var parentNode = $('#zkTree').tree('getParent', node.target);
                $('#zkTree').tree('reload', parentNode.target);
                executeUntilSuccess(function () {
                    var newSelectNode = $('#zkTree').tree('find', node.id);
                    if (!newSelectNode) {
                        return false;
                    }
                    $('#zkTree').tree('reload', newSelectNode.target);
                });
            }
            $('#w').window('close');
        }
    );
}

function executeUntilSuccess(fn) {
    if (fn() === false) {
        setTimeout(function () {
            executeUntilSuccess(fn)
        }, 1000);
    }
}

/****************************************************************************************************************************/

function saveCfg() {
    $.messager.progress();
    $('#zkweb_add_cfg_form').form('submit', {
        url: 'zkcfg/addZkCfg',
        onSubmit: function () {
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');	// hide progress bar while the form is invalid
            }
            return isValid;	// return false will stop the form submission
        },
        success: function (data) {
            $.messager.alert('提示', data);
            $('#zkweb_zkcfg').datagrid("reload");
            $('#zkweb_add_cfg').window('close');
            $.messager.progress('close');	// hide progress bar while submit successfully
        }
    });
}

function updateCfg() {
    $.messager.progress();
    $('#zkweb_up_cfg_form').form('submit', {
        url: 'zkcfg/updateZkCfg',
        onSubmit: function () {
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');	// hide progress bar while the form is invalid
            }
            return isValid;	// return false will stop the form submission
        },
        success: function (data) {
            $.messager.alert('提示', data);
            $('#zkweb_zkcfg').datagrid("reload");
            $('#zkweb_up_cfg').window('close');
            $.messager.progress('close');	// hide progress bar while submit successfully
        }
    });
}

function openUpdateWin() {
    var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');
    if (_cfg) {
        $('#zkweb_up_cfg').window('open');

        $('#zkweb_up_cfg_form').form("load", "zkcfg/queryZkCfgById?id=" + _cfg.ID);
    } else {
        messagerShow('提示', '请选择一条记录');
    }
}

function openDelWin() {
    var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');
    if (_cfg) {

        $.messager.confirm('提示', '确认删除这个配置吗?', function (r) {
            if (r) {
                //alert('confirmed:'+r);
                $.get('zkcfg/delZkCfg', {id: _cfg.ID}, function (data) {
                    messagerShow('提示', data);
                });
                $('#zkweb_zkcfg').datagrid("reload");
                initTree();
                $('#zkTab').tabs('close', 0);
                //$('#zkweb_up_cfg').window('open');
                //$('#zkweb_up_cfg_form').form("load","zkcfg/queryZkCfgById?id="+_cfg.ID);
            }
        });
        //$('#zkweb_zkcfg').datagrid('selectRow',0);
    } else {
        messagerShow('提示', '请选择一条记录');
    }
}

function selectCfg() {
    var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');
    if (!_cfg) {
        messagerShow('提示', '请选择一条记录');
        return;
    }
    curCacheId = _cfg.ID;
    initTree(curCacheId);
}

function doData(path, cacheId) {
    $.post("zk/queryZnodeInfo", {"path": path, "cacheId": cacheId},
        // $.post("zk/queryZnodeInfo", { "path": getQueryString("path"), "cacheId": getQueryString("cacheId")},
        function (data) {
            console.log(data);
            $("#r_path").val(path);
            $("#r_cacheId").val(data.cacheId);
            $("#r_data").val(data.data);
            $("#r_czxid").text(data.arr.czxid);
            $("#r_mzxid").text(data.arr.mzxid);
            $("#r_ctime").text(data.arr.ctime);
            $("#r_mtime").text(data.arr.mtime);
            $("#r_version").text(data.arr.version);
            $("#r_cversion").text(data.arr.cversion);
            $("#r_ephemeralOwner").text(data.arr.ephemeralOwner);
            $("#r_aversion").text(data.arr.aversion);
            $("#r_dataLength").text(data.arr.dataLength);
            $("#r_numChildren").text(data.arr.numChildren);
            $("#r_pzxid").text(data.arr.pzxid);
            var html = '';
            var datas = data.acls;
            for (var i = 0; i < datas.length; i++) {
                var dat = datas[i];
                html += '<tr>\n' +
                    '\t\t\t\t\t\t<td><label >授权策略</label></td>\n' +
                    '\t\t\t\t\t\t<td>' + dat.scheme + '</td>\n' +
                    '\t\t\t\t\t</tr>\n' +
                    '\t\t\t\t\t<tr>\n' +
                    '\t\t\t\t\t\t<td><label >用户</label></td>\n' +
                    '\t\t\t\t\t\t<td>' + dat.id + '</td>\n' +
                    '\t\t\t\t\t</tr>\n' +
                    '\t\t\t\t\t<tr>\n' +
                    '\t\t\t\t\t\t<td><label >权限</label></td>\n' +
                    '\t\t\t\t\t\t<td>' + dat.perms + '</td>\n' +
                    '\t\t\t\t\t</tr>';
            }
            $("#r_acls").html(html);
        }
    );
}

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

function saveData() {
    $.messager.progress();
    $('#ff').form('submit', {
        url: 'zk/saveData',
        onSubmit: function () {
            var isValid = $(this).form('validate');
            if (!isValid) {
                $.messager.progress('close');	// hide progress bar while the form is invalid
            }
            return isValid;	// return false will stop the form submission
        },
        success: function (data) {
            messagerShow('提示', data);
            $.messager.progress('close');	// hide progress bar while submit successfully
        }
    });
}