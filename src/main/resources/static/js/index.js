const ws = new WebSocket("ws://localhost:8848/info");
$(
    function (){
        ws.onopen = function(){
            console.log("websocket连接成功");
        }
        ws.onmessage = function(e){
            const data = JSON.parse(e.data);
            if (data.wsResultType === "LOG"){
                if ($("#content").children().length > 200){
                    $("#content").empty('')
                }
                $("#content").append(`<div class="scriptInfo">${data.msg}</div>`)
                document.getElementById('content').scrollTop = document.getElementById('content').scrollHeight
            }
        }
        ws.onclose = function(e){
            console.log("websocket已断开", e);
            window.close()
        }
        ws.onerror = function(e){
            console.log("websocket发生错误", e);
        }
        window.onbeforeunload = function() {
            ws.close();
        };
    }
)
function changeStatus(){
    const $status = $("#status");
    if ($status.text() === "关闭"){
        $.get("/dashboard/pause")
        $status.text("已关")
    }else {
        $.get("/dashboard/start").then(data => {
            if (data.resultStatus === "SUCCESS"){
                $status.text("关闭")
            }
        })
    }
}
function start(){
    $.get("/dashboard/start")
    $("#start").prop("disable", true)
    $("#pause").prop("disable", false)
    $("#start").attr("disable", true)
    $("#pause").attr("disable", false)
}
function pause(){
    $.get("/dashboard/pause")
    $("#pause").prop("disable", true)
    $("#start").prop("disable", false)
}
function save(){

}
function screenCapture(){

}
