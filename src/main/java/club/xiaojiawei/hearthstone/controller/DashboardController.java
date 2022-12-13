package club.xiaojiawei.hearthstone.controller;


import club.xiaojiawei.hearthstone.constant.SystemConst;
import club.xiaojiawei.hearthstone.entity.Result;
import club.xiaojiawei.hearthstone.entity.WsResult;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.Deck;
import club.xiaojiawei.hearthstone.ws.WebSocketServer;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Objects;

import static club.xiaojiawei.hearthstone.constant.SystemConst.PROPERTIES;

/**
 * @author 肖嘉威
 * @date 2022/11/24 15:37
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/shutdown")
    @ResponseBody
    public void shutdown(){
        Core.setPause(true);
        WebSocketServer.sendAllMessage(WsResult.ofScriptLog("系统关闭"));
        System.exit(0);
    }

    @RequestMapping("/start")
    @ResponseBody
    public Result<Object> start(){
        Core.start();
        return Result.ofSuccess(null);
    }

    @RequestMapping("/getStatus")
    @ResponseBody
    public Result<Object> getStatus(){
        return Result.ofSuccess(Core.getPause());
    }

    @RequestMapping("/getRunningTime")
    @ResponseBody
    public Result<Object> getRunningTime(){
        return Result.ofSuccess(List.of(PROPERTIES.getProperty("date"), PROPERTIES.getProperty("time")));
    }

    @RequestMapping("/getCurrentDeck")
    @ResponseBody
    public Result<Object> getCurrentDeck(){
        return Result.ofSuccess(Deck.getCurrentDeck());
    }

    @SneakyThrows
    @RequestMapping("/setGamePath")
    @ResponseBody
    public Result<Object> setGamePath(@RequestParam String path){
        if (new File(path).exists()){
            if (!new File(path + "/" + SystemConst.GAME_PROGRAM_NAME).exists()){
                return Result.ofFail("不是炉石路径，正确的路径下有" + SystemConst.GAME_PROGRAM_NAME + "文件，请重新填写");
            }
            PROPERTIES.setProperty("gamepath", path);
            try(FileOutputStream fileOutputStream = new FileOutputStream(SystemConst.getPath() + SystemConst.getName())){
                PROPERTIES.store(fileOutputStream, "zerg");
            }
            return Result.ofSuccess("重启后生效");
        }
        return Result.ofFail("路径不存在，请核实后重新填写");
    }

    @SneakyThrows
    @RequestMapping("/setPlatformPath")
    @ResponseBody
    public Result<Object> setPlatformPath(@RequestParam String path){
        if (new File(path).exists()){
            PROPERTIES.setProperty("platformpath", path);
            try(FileOutputStream fileOutputStream = new FileOutputStream(SystemConst.getPath() + SystemConst.getName())){
                PROPERTIES.store(fileOutputStream, "zerg");
            }
            return Result.ofSuccess("重启后生效");
        }
        return Result.ofFail("路径不存在，请核实后重新填写");
    }

}
