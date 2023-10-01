let ws;
const jqPromiseAjax = params => {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: params.url,
            type: params.type || 'get',
            dataType: 'json',
            headers: params.headers || {},
            data: params.data || {},
            success(res) {
                resolve(res)
            },
            error(err) {
                reject(err)
            }
        })
    })
}
$(
    async function (){
        const enablePsw = await jqPromiseAjax({
            url: "/verifyCookie"
        });
        if (enablePsw.status !== "SUCCESS"){
            while (true){
                const psw = prompt("密码")
                const data = await jqPromiseAjax({
                    url: "/verifyPsw",
                    data: {
                        psw: md5(psw)
                    }
                });
                if (data.status === "SUCCESS"){
                    console.log("密码正确😊")
                    break
                }
                console.log("密码错误🐷")
            }
        }
        const content = $("#content");
        ws = new WebSocket(window.location.href.replace("https", "ws").replace("http", "ws") + "/info");
        ws.onopen = function(){
            console.log("websocket连接成功");
        }
        ws.onmessage = function(e){
            const data = JSON.parse(e.data);
            switch (data.type){
                case "LOG":
                    if (content.children().length > 200){
                        content.empty('')
                    }
                    content.append(`<div class="scriptInfo">${data.data}</div>`)
                    content.scrollTop(content.prop("scrollHeight"))
                    break
                case "PAUSE":
                    changePause(data.data)
            }
        }
        ws.onclose = function(e){
            const msg = "websocket已断开";
            console.log(msg, e);
            content.append(`<div class="scriptInfo">${msg}</div>`)
            content.scrollTop(content.prop("scrollHeight"))
        }
        ws.onerror = function(e){
            console.log("websocket发生错误", e);
        }
        window.onbeforeunload = function() {
            ws.close();
        };
    }
)

function changePause(pause){
    $("#start").prop("disable", !pause)
    $("#start").css("background-color", pause? "#5CB85C" : "#5CB8")
    $("#pause").prop("disable", pause)
    $("#pause").css("background-color", pause? "#D953" : "#D9534F")
}
function start(){
    $.get("/dashboard/start")
    changePause(false)
}
function pause(){
    $.get("/dashboard/pause")
    $("#pause").prop("disable", true)
    $("#start").prop("disable", false)
    changePause(true)
}
function save(){

}
function screenCapture(){

}
