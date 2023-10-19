package club.xiaojiawei.status;

import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.controller.JavaFXDashboardController;
import club.xiaojiawei.core.Core;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.listener.VersionListener;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.ws.WebSocketServer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.TEMP_PATH;
import static club.xiaojiawei.enums.ConfigurationEnum.*;

/**
 * 工作状态
 * @author 肖嘉威
 * @date 2023/9/10 22:04
 */
@Component
@Slf4j
public class Work {
    /**
     * 工作日标记
     */
    @Getter
    private static String[] workDayFlagArr;
    /**
     * 工作时间标记
     */
    @Getter
    private static String[] workTimeFlagArr;
    /**
     * 工作时间段
     */
    @Getter
    private static String[] workTimeArr;
    private static PropertiesUtil propertiesUtil;
    private static Properties scriptProperties;
    private static AtomicReference<BooleanProperty> isPause;
    private static Core core;
    @Resource
    public void setScriptProperties(Properties scriptConfiguration) {
        Work.scriptProperties = scriptConfiguration;
        String workDayFlagStr = scriptConfiguration.getProperty(WORK_DAY_FLAG.getKey());
        workDayFlagArr = workDayFlagStr.split(",");
        String workTimeFlagStr = scriptConfiguration.getProperty(WORK_TIME_FLAG.getKey());
        workTimeFlagArr = workTimeFlagStr.split(",");
        String workTimeStr = scriptConfiguration.getProperty(WORK_TIME.getKey());
        workTimeArr = workTimeStr.split(",");
    }
    @Resource
    public void setIsPause(AtomicReference<BooleanProperty> isPause){
        Work.isPause = isPause;
    }
    @Resource
    public void setPropertiesUtil(PropertiesUtil propertiesUtil){
        Work.propertiesUtil = propertiesUtil;
    }
    @Resource
    @Lazy
    public void setCore(Core core){
        Work.core = core;
    }

    public static void storeWorkDate(){
        scriptProperties.setProperty(WORK_DAY_FLAG.getKey(), String.join(",", workDayFlagArr));
        scriptProperties.setProperty(WORK_TIME_FLAG.getKey(), String.join(",", workTimeFlagArr));
        scriptProperties.setProperty(WORK_TIME.getKey(), String.join(",", workTimeArr));
        propertiesUtil.storeScriptProperties();
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.WORK_DATE, new String[][]{Work.getWorkDayFlagArr(), Work.getWorkTimeFlagArr(), Work.getWorkTimeArr()}));
        checkWork();
    }

    /**
     * 是否处于工作中
     */
    @Setter
    @Getter
    private volatile static boolean working;
    public static void stopWork(){
        working = false;
        cannotWorkLog();
        log.info("停止工作，准备关闭游戏");
        SystemUtil.killGame();
        SystemUtil.cancelAllRunnable();
    }
    public static void cannotWorkLog(){
        String context = "现在是下班时间 🌜";
        SystemUtil.notice(context);
        log.info(context);
    }
    public static void workLog(){
        log.info("现在是上班时间 🌞");
    }
    @Scheduled(fixedDelay = 1000 * 60)
    void workSchedule(){
        checkWork();
    }

    private static void checkWork(){
        if (!isPause.get().get() && !working && validateDate()){
            workLog();
            core.start();
        }
    }

    public static boolean canWork(){
        //         版本校验，开启自动更新并且有更新可用时将停止工作以升级版本
        if (Objects.equals(scriptProperties.getProperty(AUTO_UPDATE.getKey()), "true") && VersionListener.isCanUpdate()){
            if (!new File(TEMP_PATH).exists()){
                JavaFXDashboardController.downloadRelease(VersionListener.getLatestRelease());
            }
            Platform.runLater(JavaFXDashboardController::execUpdate);
            return false;
        }
        return validateDate();
    }

    private static boolean validateDate(){
        //        天校验
        if (!Objects.equals(workDayFlagArr[0], "true") && Objects.equals(workDayFlagArr[LocalDate.now().getDayOfWeek().getValue()], "false")){
            return false;
        }
        //        段校验
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < workTimeFlagArr.length; i++) {
            if (Objects.equals(workTimeFlagArr[i], "true") && !Objects.equals(workTimeArr[i],  "null")){
                String[] time = workTimeArr[i].split("-");
                String nowTime = dateTimeFormatter.format(localTime);
                if (
                        (time[1].compareTo(time[0]) > 0 && nowTime.compareTo(time[0]) >= 0 && nowTime.compareTo(time[1]) < 0)
                                ||
                                (time[0].compareTo(time[1]) > 0 && (nowTime.compareTo(time[0]) >= 0 || nowTime.compareTo(time[1]) < 0))
                ){
                    return true;
                }
            }
        }
        return false;
    }

}
