package club.xiaojiawei.status;

import club.xiaojiawei.core.Core;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.enums.ConfigurationKeyEnum.*;

/**
 * @author 肖嘉威
 * @date 2023/9/10 22:04
 * @msg
 */
@Component
@Slf4j
public class Work {
    @Getter
    private static String[] workDayFlagArr;
    @Getter
    private static String[] workTimeFlagArr;
    @Getter
    private static String[] workTimeArr;
    private static PropertiesUtil propertiesUtil;
    private static Properties scriptProperties;

    private static AtomicReference<BooleanProperty> isPause;
    private static Core core;
    @Resource
    public void setScriptProperties(Properties scriptProperties) {
        Work.scriptProperties = scriptProperties;
        String workDayFlagStr = scriptProperties.getProperty(WORK_DAY_FLAG_KEY.getKey());
        workDayFlagArr = workDayFlagStr.split(",");
        String workTimeFlagStr = scriptProperties.getProperty(WORK_TIME_FLAG_KEY.getKey());
        workTimeFlagArr = workTimeFlagStr.split(",");
        String workTimeStr = scriptProperties.getProperty(WORK_TIME_KEY.getKey());
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
        scriptProperties.setProperty(WORK_DAY_FLAG_KEY.getKey(), String.join(",", workDayFlagArr));
        scriptProperties.setProperty(WORK_TIME_FLAG_KEY.getKey(), String.join(",", workTimeFlagArr));
        scriptProperties.setProperty(WORK_TIME_KEY.getKey(), String.join(",", workTimeArr));
        propertiesUtil.storeScriptProperties();
        checkWork();
    }

    @Setter
    @Getter
    private volatile static boolean working;
    public static void stopWork(){
        SystemUtil.killGame();
        SystemUtil.cancelAllRunnable();
        working = false;
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
        if (!isPause.get().get() && !working && canWork()){
            workLog();
            core.start();
        }
    }

    public static boolean canWork(){
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
