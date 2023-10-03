let ws;
const httpStatus = {SUCCESS: "SUCCESS", FAIL: "FAIL", ERROR: "ERROR"}
const wsStatus = {LOG:"LOG", PAUSE: "PAUSE", MODE: "MODE", DECK: "DECK", GAME_COUNT: "GAME_COUNT", WINNING_PERCENTAGE: "WINNING_PERCENTAGE", WORK_DATE: "WORK_DATE", MODE_LIST: "MODE_LIST"}
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
        const content = $("#content");
        ws = new WebSocket(window.location.href.replace("https", "ws").replace("http", "ws") + "/info");
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
                        if (arr[0][i] === "true"){
                            dayChildren[i].children[0].checked = true;
                        }
                    }
                    let timeChildren = $("#time").children();
                    for (let i = 0; i < arr[1].length; i++) {
                        timeChildren[i].children[0].checked = arr[1][i] === "true";
                        if (arr[2][i] !== "null"){
                            let times = arr[2][i].split('-');
                            timeChildren[i].children[1].value = times[0]
                            timeChildren[i].children[2].value = times[1]
                        }
                    }
                    break
                case wsStatus.MODE_LIST:
                    for (let datum of data.data) {
                        $mode.append(`<option value="${datum}">${datum}</option>`)
                    }
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
                        psw: md5(psw)
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
    getAllDeckByMode: (mode, res) => {
        $.get("/dashboard/getAllDeckByMode", {mode: mode}, res)
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
        workDayFlagArr[i] = workDayFlagArr[0] === true? false : days[i].children[0].checked + "";
    }
    let times = $("#time").children();
    for (let i = 0; i < times.length; i++){
        workTimeFlagArr[i] = times[i].children[0].checked + ""
        if (times[i].children[1].value !== '' && times[i].children[2].value !== '' && times[i].children[1].value !== times[i].children[2].value){
            if (i !== 0){
                workTimeArr[i] = times[i].children[1].value + "-" + times[i].children[2].value + ""
            }
        }else if (i !== 0){
            workTimeFlagArr[i] = false + ""
            workTimeArr[i] = "null"
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
