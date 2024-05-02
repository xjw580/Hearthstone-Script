package club.xiaojiawei.controller.web;


import club.xiaojiawei.bean.Result;
import club.xiaojiawei.controller.javafx.MainController;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.listener.VersionListener;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.ROBOT;
import static club.xiaojiawei.enums.ConfigurationEnum.ENABLE_VERIFY;
import static club.xiaojiawei.enums.ConfigurationEnum.VERIFY_PASSWORD;


/**
 * @author 肖嘉威
 * @date 2022/11/24 15:37
 */
@Controller
@Slf4j
public class DashboardController {

    public static final LinkedHashSet<String> TOKEN_SET = new LinkedHashSet<>();

    public static final int MAX_TOKEN_COUNT = 3;

    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private MainController javafxMainController;

    @RequestMapping("/")
    public String index(Model model){
        model.addAttribute("version", "当前版本号：" + VersionListener.getCurrentRelease().getTagName());
        return "dashboard";
    }

    @RequestMapping("/verifyPsw")
    @ResponseBody
    public Result<Object> verifyPsw(@RequestParam("psw") String psw, HttpServletResponse response){
        Result<Object> result;
        if (Strings.isBlank(psw)
                || !Objects.equals(psw, scriptConfiguration.getProperty(VERIFY_PASSWORD.getKey()))) {
            result = Result.ofFail();
        }else {
            String value = ScriptStaticData.AUTHOR + System.currentTimeMillis();
            if (TOKEN_SET.size() >= MAX_TOKEN_COUNT){
                TOKEN_SET.removeFirst();
            }
            TOKEN_SET.add(value);
            Cookie cookie = new Cookie("token", value);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            result = Result.ofSuccess();
        }
        return result;
    }

    @RequestMapping("/verifyCookie")
    @ResponseBody
    public Result<Object> verifyCookie(HttpServletRequest request){
        Result<Object> result;
        if (Objects.equals(scriptConfiguration.getProperty(ENABLE_VERIFY.getKey()), "false")){
            result = Result.ofSuccess();
        }else {
            Cookie[] cookies = request.getCookies();
            if (cookies == null){
                result = Result.ofFail();
            }else {
                Cookie cookie = null;
                for (Cookie c : cookies) {
                    if (Objects.equals(c.getName(), "token")){
                        cookie = c;
                        break;
                    }
                }
                if (cookie == null || !TOKEN_SET.contains(cookie.getValue())){
                    result = Result.ofFail();
                }else {
                    result = Result.ofSuccess();
                }
            }
        }
        return result;
    }

    @RequestMapping("/dashboard/pause")
    @ResponseBody
    public Result<Object> pause(){
        isPause.get().set(true);
        return Result.ofSuccess();
    }

    @RequestMapping("/dashboard/start")
    @ResponseBody
    public Result<Object> start(){
        isPause.get().set(false);
        return Result.ofSuccess();
    }

    @RequestMapping("/dashboard/closeGame")
    @ResponseBody
    public Result<Object> closeGame(){
        SystemUtil.killGame();
        return Result.ofSuccess("游戏已关闭");
    }

    @RequestMapping("/dashboard/closePlatform")
    @ResponseBody
    public Result<Object> closePlatform(){
        SystemUtil.killPlatform();
        return Result.ofSuccess("战网已关闭");
    }

    @RequestMapping("/dashboard/save")
    @ResponseBody
    public Result<Object> save(@RequestParam("workDayFlagArr")String[] workDayFlagArr, @RequestParam("workTimeFlagArr") String[] workTimeFlagArr, @RequestParam("workTimeArr") String[] workTimeArr){
        System.arraycopy(workDayFlagArr, 0, Work.getWorkDayFlagArr(), 0, Work.getWorkDayFlagArr().length);
        System.arraycopy(workTimeFlagArr, 0, Work.getWorkTimeFlagArr(), 0, Work.getWorkTimeFlagArr().length);
        System.arraycopy(workTimeArr, 0, Work.getWorkTimeArr(), 0, Work.getWorkTimeArr().length);
        Work.storeWorkDate();
        javafxMainController.initWorkDate();
        return Result.ofSuccess();
    }

    @RequestMapping("/dashboard/changeDeck")
    @ResponseBody
    public Result<Object> changeDeck(@RequestParam("deckComment")String deckComment){
        javafxMainController.changeDeck(deckComment);
        return Result.ofSuccess();
    }

    @RequestMapping("/dashboard/getAllDeckByRunMode")
    @ResponseBody
    public Result<ArrayList<String>> getAllDeckByRunMode(@RequestParam("runMode")String runMode){
        ArrayList<String> result = new ArrayList<>();
        for (DeckEnum deck : DeckEnum.values()) {
            if (Objects.equals(deck.getRunMode().getComment(), runMode) && deck.isEnable()){
                result.add(deck.getComment());
            }
        }
        return Result.ofSuccess(result);
    }

    @RequestMapping("/dashboard/screenCapture")
    public void screenCapture(HttpServletResponse response) throws IOException {
        BufferedImage bufferedImage = ROBOT.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(outputStream.toByteArray());
    }

}
