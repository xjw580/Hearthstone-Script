package club.xiaojiawei.status;

import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.controller.javafx.MainController;
import club.xiaojiawei.controls.TimeSelector;
import club.xiaojiawei.core.Core;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.listener.VersionListener;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.ws.WebSocketServer;
import jakarta.annotation.Resource;
import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.enums.ConfigEnum.*;

/**
 * 工作状态
 *
 * @author 肖嘉威
 * @date 2023/9/10 22:04
 */
@Component
@Slf4j
public class Work {

    /**
     * 是否处于工作中
     */
    @Setter
    @Getter
    private volatile static boolean working;
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
    private static Core core;
    private static boolean enableUpdate = true;

    @Autowired
    private void set(Properties scriptConfiguration, PropertiesUtil propertiesUtil) {
        Work.scriptProperties = scriptConfiguration;
        String workDayFlagStr = scriptConfiguration.getProperty(WORK_DAY_FLAG.name());
        workDayFlagArr = workDayFlagStr.split(",");
        String workTimeFlagStr = scriptConfiguration.getProperty(WORK_TIME_FLAG.name());
        workTimeFlagArr = workTimeFlagStr.split(",");
        String workTimeStr = scriptConfiguration.getProperty(WORK_TIME.name());
        workTimeArr = workTimeStr.split(",");
        Work.propertiesUtil = propertiesUtil;
    }

    @Resource
    @Lazy
    public void setCore(Core core) {
        Work.core = core;
    }

    public static void storeWorkDate() {
        scriptProperties.setProperty(WORK_DAY_FLAG.name(), String.join(",", workDayFlagArr));
        scriptProperties.setProperty(WORK_TIME_FLAG.name(), String.join(",", workTimeFlagArr));
        scriptProperties.setProperty(WORK_TIME.name(), String.join(",", workTimeArr));
        propertiesUtil.storeScriptProperties();
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.WORK_DATE, new String[][]{Work.getWorkDayFlagArr(), Work.getWorkTimeFlagArr(), Work.getWorkTimeArr()}));
        checkWork();
    }

    public static void stopWork() {
        working = false;
        SystemUtil.closeAll();
        cannotWorkLog();
        log.info("停止工作，准备关闭游戏");
        SystemUtil.killGame();
    }

    public static void cannotWorkLog() {
        String context = "现在是下班时间 🌜";
        SystemUtil.notice(context);
        log.info(context);
    }

    public static void workLog() {
        log.info("现在是上班时间 🌞");
    }

    @Scheduled(fixedDelay = 1000 * 60)
    void workSchedule() {
        checkWork();
    }

    private static void checkWork() {
        if (!working) {
            if (!PauseStatus.INSTANCE.isPause() && isDuringWorkDate()) {
                workLog();
                core.start();
            } else if (enableUpdate && Objects.equals(scriptProperties.getProperty(AUTO_UPDATE.name()), "true") && VersionListener.isCanUpdate()) {
                MainController.downloadRelease(VersionListener.getLatestRelease(), false, path -> {
                    enableUpdate = false;
                    if (path == null) {
                        log.warn(String.format("新版本<%s>下载失败", VersionListener.getLatestRelease().getTagName()));
                    } else {
                        Platform.runLater(() -> MainController.execUpdate(path));
                    }
                });
            }
        }
    }

    /**
     * 验证是否在工作时间内
     *
     * @return
     */
    public static boolean isDuringWorkDate() {
        //        天校验
        if (!Objects.equals(workDayFlagArr[0], "true") && Objects.equals(workDayFlagArr[LocalDate.now().getDayOfWeek().getValue()], "false")) {
            return false;
        }
        //        段校验
        LocalTime localTime = LocalTime.now();
        for (int i = 0; i < workTimeFlagArr.length; i++) {
            if (Objects.equals(workTimeFlagArr[i], "true") && !Objects.equals(workTimeArr[i], "null")) {
                String[] time = workTimeArr[i].split("-");
                String start = time[0], end = time[1], nowTime = TimeSelector.TIME_FORMATTER.format(localTime);
                if (
                        end.compareTo(start) == 0
                                ||
                                (end.compareTo(start) > 0 && nowTime.compareTo(start) >= 0 && nowTime.compareTo(end) <= 0)
                                ||
                                (end.compareTo(start) < 0 && (nowTime.compareTo(start) >= 0 || nowTime.compareTo(end) <= 0))
                ) {
                    return true;
                }
            }
        }
        return false;
    }
}
