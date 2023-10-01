package club.xiaojiawei.controller;


import club.xiaojiawei.bean.Result;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.utils.DashboardUtil;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.enums.ConfigurationKeyEnum.ENABLE_VERIFY;
import static club.xiaojiawei.enums.ConfigurationKeyEnum.VERIFY_PASSWORD;


/**
 * @author 肖嘉威
 * @date 2022/11/24 15:37
 */
@Controller
@Slf4j
public class WebDashboardController {

    public static final LinkedHashSet<String> tokenSet = new LinkedHashSet<>();
    public static final int MAX_TOKEN = 3;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private DashboardUtil dashboardUtil;
    @Resource
    private Properties scriptConfiguration;

    @RequestMapping("/")
    public String index(){
        return "dashboard";
    }

    @RequestMapping("/verifyPsw")
    @ResponseBody
    public Result<Object> verifyPsw(@RequestParam("psw") String psw, HttpServletResponse response){
        Result<Object> result;
        if (Strings.isBlank(psw)
                ||
                !Objects.equals(psw, scriptConfiguration.getProperty(VERIFY_PASSWORD.getKey()))) {
            result = Result.ofFail(null);
        }else {
            String value = ScriptStaticData.AUTHOR + System.currentTimeMillis();
            if (tokenSet.size() >= MAX_TOKEN){
                for (String s : tokenSet) {
                    tokenSet.remove(s);
                    break;
                }
            }
            tokenSet.add(value);
            Cookie cookie = new Cookie("token", value);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            result = Result.ofSuccess(null);
        }
        return result;
    }
    @RequestMapping("/verifyCookie")
    @ResponseBody
    public Result<Object> verifyCookie(HttpServletRequest request){
        Result<Object> result;
        if (Objects.equals(scriptConfiguration.getProperty(ENABLE_VERIFY.getKey()), "false")){
            result = Result.ofSuccess(null);
        }else {
            Cookie[] cookies = request.getCookies();
            if (cookies == null){
                result = Result.ofFail(null);
            }else {
                Cookie cookie = null;
                for (Cookie c : cookies) {
                    if (Objects.equals(c.getName(), "token")){
                        cookie = c;
                        break;
                    }
                }
                if (cookie == null || !tokenSet.contains(cookie.getValue())){
                    result = Result.ofFail(null);
                }else {
                    result = Result.ofSuccess(null);
                }
            }
        }
        return result;
    }

    @RequestMapping("/dashboard/pause")
    @ResponseBody
    public void pause(){
        isPause.get().set(true);
    }

    @RequestMapping("/dashboard/start")
    @ResponseBody
    public void start(){
        isPause.get().set(false);
    }

    @RequestMapping("/dashboard/save")
    @ResponseBody
    public void save(@RequestParam("workDayFlagArr")String[] workDayFlagArr, @RequestParam("workTimeFlagArr") String[] workTimeFlagArr, @RequestParam("workTimeArr") String[] workTimeArr){
        System.arraycopy(workDayFlagArr, 0, Work.getWorkDayFlagArr(), 0, Work.getWorkDayFlagArr().length);
        System.arraycopy(workTimeFlagArr, 0, Work.getWorkDayFlagArr(), 0, Work.getWorkTimeFlagArr().length);
        System.arraycopy(workTimeArr, 0, Work.getWorkDayFlagArr(), 0, Work.getWorkTimeArr().length);
        Work.storeWorkDate();
    }

    @RequestMapping("/dashboard/changeDeck")
    @ResponseBody
    public void changeDeck(@RequestParam("deckComment")String deckComment){
        dashboardUtil.changeDeck(deckComment);
    }

    @RequestMapping("/dashboard/getAllDeckByMode")
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
