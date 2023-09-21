package club.xiaojiawei.controller;


import club.xiaojiawei.bean.Result;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.utils.DashboardUtil;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author 肖嘉威
 * @date 2022/11/24 15:37
 */
@Controller
@RequestMapping("/dashboard")
@Slf4j
public class WebDashboardController {

    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private DashboardUtil dashboardUtil;

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/pause")
    @ResponseBody
    public void pause(){
        isPause.get().set(true);
    }

    @RequestMapping("/start")
    @ResponseBody
    public void start(){
        isPause.get().set(false);
    }

    @RequestMapping("/save")
    @ResponseBody
    public void save(@RequestParam("workDayFlagArr")String[] workDayFlagArr, @RequestParam("workTimeFlagArr") String[] workTimeFlagArr, @RequestParam("workTimeArr") String[] workTimeArr){
        System.arraycopy(workDayFlagArr, 0, Work.getWorkDayFlagArr(), 0, Work.getWorkDayFlagArr().length);
        System.arraycopy(workTimeFlagArr, 0, Work.getWorkDayFlagArr(), 0, Work.getWorkTimeFlagArr().length);
        System.arraycopy(workTimeArr, 0, Work.getWorkDayFlagArr(), 0, Work.getWorkTimeArr().length);
        Work.storeWorkDate();
    }

    @RequestMapping("/changeDeck")
    @ResponseBody
    public void changeDeck(@RequestParam("deckComment")String deckComment){
        dashboardUtil.changeDeck(deckComment);
    }

    @RequestMapping("/getAllDeckByMode")
    @ResponseBody
    public Result<ArrayList<DeckEnum>> getAllDeckByMode(@RequestParam("mode")String mode){
        ArrayList<DeckEnum> result = new ArrayList<>();
        for (DeckEnum deck : DeckEnum.values()) {
            if (Objects.equals(deck.getRunMode().getValue(), mode)){
                result.add(deck);
            }
        }
        return Result.ofSuccess(result);
    }
}
