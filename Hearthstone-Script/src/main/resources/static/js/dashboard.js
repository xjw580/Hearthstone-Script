let ws;
const httpStatus = {SUCCESS: "SUCCESS", FAIL: "FAIL", ERROR: "ERROR"}
const wsStatus = {LOG:"LOG", PAUSE: "PAUSE", MODE: "MODE", DECK: "DECK", GAME_COUNT: "GAME_COUNT", WINNING_PERCENTAGE: "WINNING_PERCENTAGE", WORK_DATE: "WORK_DATE", MODE_LIST: "MODE_LIST", GAME_TIME: "GAME_TIME", EXP: "EXP"}
const promiseAjax = params => {
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
        await common.verify()
        common.wsListener()
        window.onbeforeunload = function() {
            ws.close();
        };
    }
)
const common = {
    wsListener: () => {
        if (window.WebSocket){
            const content = $("#content");
            ws = new WebSocket(window.location.href.replace("https", "wss").replace("http", "ws") + "/info");
            ws.onopen = function(){
                console.log("websocket连接成功");
            }
            ws.onmessage = function(e){
                const data = JSON.parse(e.data);
                let $mode = $("#mode");
                switch (data.type){
                    case wsStatus.LOG:
                        if (content.children().length > 200){
                            content.empty('')
                        }
                        content.append(`<div class="scriptInfo">${data.data}</div>`)
                        content.scrollTop(content.prop("scrollHeight"))
                        break
                    case wsStatus.PAUSE:
                        common.changePause(data.data)
                        break
                    case wsStatus.MODE:
                        $mode.val(data.data)
                        break
                    case wsStatus.DECK:
                        common.getAllDeckByMode($mode.val(), res => {
                            let $deck = $("#deck");
                            $deck.empty('')
                            for (let datum of res.data) {
                                $deck.append(`<option value="${datum}">${datum}</option>`)
                            }
                            $deck.val(data.data)
                        })
                        break
                    case wsStatus.GAME_COUNT:
                        $("#warCount").text(data.data)
                        break
                    case wsStatus.WINNING_PERCENTAGE:
                        $("#winningPercentage").text(data.data)
                        break
                    case wsStatus.WORK_DATE:
                        let arr = data.data;
                        let dayChildren = $("#day").children();
                        for (let i = 0; i < arr[0].length; i++) {
                            dayChildren[i].children[0].checked = arr[0][i] === "true";
                        }
                        let timeChildren = $("#time").children();
                        for (let i = 0; i < arr[1].length; i++) {
                            if (arr[2][i] === "null"){
                                timeChildren[i].children[0].checked = false;
                                timeChildren[i].children[1].value = ''
                                timeChildren[i].children[2].value = ''
                            }else {
                                let times = arr[2][i].split('-');
                                timeChildren[i].children[1].value = times[0]
                                timeChildren[i].children[2].value = times[1]
                                timeChildren[i].children[0].checked = arr[1][i] === "true";
                            }
                        }
                        break
                    case wsStatus.MODE_LIST:
                        for (let datum of data.data) {
                            $mode.append(`<option value="${datum}">${datum}</option>`)
                        }
                        break
                    case wsStatus.GAME_TIME:
                        let time = data.data;
                        if (time === 0){
                        } else if (time < 60){
                            time = `${time}m`
                        }else if (time < 1440){
                            if (time % 60 === 0){
                                time = `${Math.trunc(time / 60)}h`
                            }else {
                                time = `${Math.trunc(time / 60)}h${time % 60}m`
                            }
                        }else {
                            if (time % 1440 === 0){
                                time = `${Math.trunc(time / 1440)}d`
                            }else {
                                time = `${Math.trunc(time / 1440)}d${Math.trunc(time % 1440 / 60)}h`
                            }
                        }
                        $("#gameTime").text(time)
                        break
                    case wsStatus.EXP:
                        $("#exp").text(data.data)
                        break
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
        }else {
            console.error("当前浏览器不支持WebSocket")
        }
    },
    changePause: pause => {
        let $start = $("#start");
        $start.prop("disable", !pause)
        $start.css("background-color", pause? "#5CB85C" : "#5CB8")
        let $pause = $("#pause");
        $pause.prop("disable", pause)
        $pause.css("background-color", pause? "#D953" : "#D9534F")
    },
    verify: async function(){
        const enablePsw = await promiseAjax({
            url: "/verifyCookie"
        });
        if (enablePsw.status !== httpStatus.SUCCESS){
            while (true){
                const psw = prompt("验证密码🤐")
                const data = await promiseAjax({
                    url: "/verifyPsw",
                    data: {
                        psw: psw
                    }
                });
                if (data.status === httpStatus.SUCCESS){
                    console.log("密码正确😊")
                    break
                }
                console.log("密码错误🐷")
            }
        }
    },
    getAllDeckByMode: (runMode, res) => {
        $.get("/dashboard/getAllDeckByRunMode", {runMode}, res)
    }
}
function start(){
    $.get("/dashboard/start", {}, () => {
        common.changePause(false)
    })
}
function pause(){
    $.get("/dashboard/pause", {}, () => {
        common.changePause(true)
    })
}
function closeGame(){
    if (confirm("确认关闭炉石吗？")){
        $.get("/dashboard/closeGame", {}, () => {})
    }
}
function closePlatform(){
    if (confirm("确认关闭战网吗？")){
        $.get("/dashboard/closePlatform", {}, () => {})
    }
}
function getAllDeckByMode(){
    common.getAllDeckByMode($("#mode").val(), res => {
        let $deck = $("#deck");
        $deck.empty('')
        for (let datum of res.data) {
            $deck.append(`<option value="${datum}">${datum}</option>`)
        }
        $deck.val("")
    })
}
function changeDeck(){
    $.get("/dashboard/changeDeck", {deckComment: $("#deck").val()})
}
function save(){
    let days = $("#day").children();
    let workDayFlagArr = []
    let workTimeFlagArr = []
    let workTimeArr = [null]
    for (let i = 0; i < days.length; i++){
        workDayFlagArr[i] = days[i].children[0].checked + "";
    }
    let times = $("#time").children();
    for (let i = 0; i < times.length; i++){
        if (times[i].children[1].value !== '' && times[i].children[2].value !== ''){
            workTimeArr[i] = times[i].children[1].value + "-" + times[i].children[2].value + ""
            workTimeFlagArr[i] = times[i].children[0].checked + ""
        }else {
            workTimeArr[i] = "null"
            times[i].children[1].value = ''
            times[i].children[2].value = ''
            workTimeFlagArr[i] = false
            times[i].children[0].checked = false
        }
    }
    $.get(`/dashboard/save?workDayFlagArr=${workDayFlagArr}&workTimeFlagArr=${workTimeFlagArr}&workTimeArr=${workTimeArr}`, {}, res => {
        $("#tip").text("保存成功")
        setTimeout(() => {
            $("#tip").text("")
        }, 1500)
    })
}
function screenCapture(){
    window.open("/dashboard/screenCapture")
}
