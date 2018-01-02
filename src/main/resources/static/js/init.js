$(function () {
    initDataGrid();
});

function initDataGrid() {
    $('#zkweb_zkcfg').datagrid({
        onClickRow: function (rowIndex, rowData) {
            initTree(rowData.ID);
        },
        url: 'zkcfg/queryZkCfg'
    });
}

/****************************************************************************************************************************/
function initTree(cacheId) {

    $('#zkTree').tree({
        checkbox: false,
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
        onClick: function (node) {
            var tab = $('#zkTab').tabs('getSelected');
            if (tab != null) {
                tab.title = node.text;
                $('#zkTab').tabs('update', {
                    tab: tab,
                    options: {
                        title: node.text,
                        href: "info.html?path=" + encodeURI(encodeURI(node.attributes.path)) + "&cacheId=" + cacheId
                    }
                });
            } else {
                $('#zkTab').tabs('add', {
                    id: 0,
                    title: node.text,
                    href: "info.html?path=" + encodeURI(encodeURI(node.attributes.path)) + "&cacheId=" + cacheId
                });
            }
            doData(encodeURI(encodeURI(node.attributes.path)), cacheId);
        },
        onBeforeExpand: function (node, param) {
            if (node.attributes != null) {
                $('#zkTree').tree('options').url = "zk/queryZnode?path=" + encodeURI(encodeURI(node.attributes.path)) + "&cacheId=" + cacheId;
            }
        }
    });

}


function remove() {
    $.extend($.messager.defaults, {
        ok: "确定",
        cancel: "取消"
    });

    var node = $('#zkTree').tree('getSelected');
    if (node) {
        if ('/' == node.attributes.path || '/zookeeper' == node.attributes.path || '/zookeeper/quota' == node.attributes.path) {
            $.messager.alert('提示', '不能删除此节点！');
            return;
        }

        var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');

        if (_cfg) {

            $.messager.confirm('提示', '删除' + node.attributes.path + '下所有子节点！确认吗？', function (r) {
                if (r) {
                    //var s = node.text;
                    if (node.attributes) {
                        _path = node.attributes.path;
                        $.post("zk/deleteNode", {path: _path, cacheId: _cfg.ID},
                            function (data) {
                                //alert("Data Loaded: " + data);
                                $.messager.alert('提示', data);
                                //initTree(_cfg.ID);
                                //var tab = $('#zkTab').tabs('getTab',0);
                                //alert(tab.title);
                                $('#zkTab').tabs('close', 0);
                            }
                        );
                    }
                }
            });
        }

    } else {
        $.messager.alert('提示', '请选择一个节点');
    }
    ;
}

function collapse() {
    var node = $('#zkTree').tree('getSelected');
    $('#zkTree').tree('collapse', node.target);
}

function expand() {
    var node = $('#zkTree').tree('getSelected');
    $('#zkTree').tree('expand', node.target);
}

function addzkNode() {
    var _path = "/";
    var node = $('#zkTree').tree('getSelected');
    if (node) {
        //var s = node.text;
        if (node.attributes) {
            _path = node.attributes.path;
        }
    }
    _nodeName = $('#zkNodeName').val();

    var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');

    if (_cfg) {
        $.post("zk/createNode", {nodeName: _nodeName, path: _path, cacheId: _cfg.ID},
            function (data) {
                //alert("Data Loaded: " + data);
                $.messager.alert('提示', data);
                $('#w').window('close');
                initTree(_cfg.ID);
            }
        );
    } else {

        $.messager.alert('提示', '你必须选择一个配置');
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
        $.messager.alert('提示', '请选择一条记录');
    }

}

function openDelWin() {

    var _cfg = $('#zkweb_zkcfg').datagrid('getSelected');
    if (_cfg) {

        $.messager.confirm('提示', '确认删除这个配置吗?', function (r) {
            if (r) {
                //alert('confirmed:'+r);
                $.get('zkcfg/delZkCfg', {id: _cfg.ID}, function (data) {
                    $.messager.alert('提示', data);
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
        $.messager.alert('提示', '请选择一条记录');
    }
}

function doData(path, cacheId) {
    $.post("zk/queryZnodeInfo", {"path": path, "cacheId": cacheId},
        // $.post("zk/queryZnodeInfo", { "path": getQueryString("path"), "cacheId": getQueryString("cacheId")},
        function (data) {
            console.log(data);
            $("#r_path").val(data.path);
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
                    '\t\t\t\t\t\t<td><label >scheme</label></td>\n' +
                    '\t\t\t\t\t\t<td>' + dat.scheme + '</td>\n' +
                    '\t\t\t\t\t</tr>\n' +
                    '\t\t\t\t\t<tr>\n' +
                    '\t\t\t\t\t\t<td><label >id</label></td>\n' +
                    '\t\t\t\t\t\t<td>' + dat.id + '</td>\n' +
                    '\t\t\t\t\t</tr>\n' +
                    '\t\t\t\t\t<tr>\n' +
                    '\t\t\t\t\t\t<td><label >perms</label></td>\n' +
                    '\t\t\t\t\t\t<td>' + dat.perms + '</td>\n' +
                    '\t\t\t\t\t</tr>';
            }
            $("#r_table").append(html);
        }
    );
}

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}