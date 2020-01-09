$(function () {
    $(function(){
        $('#ff').form({
            success:function(data){
                $.messager.alert('提示', data);
            }
        });
    });
    function initq() {
        $.post("zk/queryZnodeInfo", { "path": curPath, "cacheId": curCache},
            function(data){
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
                var html='';
                var datas = data.acls;
                for(var i=0;i<datas.length;i++){
                    var dat = datas[i];
                    html+='<tr>\n' +
                        '\t\t\t\t\t\t<td><label >scheme</label></td>\n' +
                        '\t\t\t\t\t\t<td>'+dat.scheme+'</td>\n' +
                        '\t\t\t\t\t</tr>\n' +
                        '\t\t\t\t\t<tr>\n' +
                        '\t\t\t\t\t\t<td><label >id</label></td>\n' +
                        '\t\t\t\t\t\t<td>'+dat.id+'</td>\n' +
                        '\t\t\t\t\t</tr>\n' +
                        '\t\t\t\t\t<tr>\n' +
                        '\t\t\t\t\t\t<td><label >perms</label></td>\n' +
                        '\t\t\t\t\t\t<td>'+dat.perms+'</td>\n' +
                        '\t\t\t\t\t</tr>';
                }
                $("#r_table").append(html);
            }
        );
    }
});
