package club.xiaojiawei.hearthstone.controller;


import club.xiaojiawei.hearthstone.constant.SystemConst;
import club.xiaojiawei.hearthstone.entity.Result;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.Deck;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

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
        System.exit(0);
    }

    @RequestMapping("/start")
    @ResponseBody
    public Result<Object> start(){
        Core.start();
        return Result.ofSuccess(null);
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
        PROPERTIES.setProperty("gamepath", path);
        try(FileOutputStream fileOutputStream = new FileOutputStream(SystemConst.getPath() + SystemConst.getName())){
            PROPERTIES.store(fileOutputStream, "zerg");
        }
        return Result.ofSuccess(null);
    }

    @SneakyThrows
    @RequestMapping("/setPlatformPath")
    @ResponseBody
    public Result<Object> setPlatformPath(@RequestParam String path){
        PROPERTIES.setProperty("platformpath", path);
        try(FileOutputStream fileOutputStream = new FileOutputStream(SystemConst.getPath() + SystemConst.getName())){
            PROPERTIES.store(fileOutputStream, "zerg");
        }
        return Result.ofSuccess(null);
    }

}
