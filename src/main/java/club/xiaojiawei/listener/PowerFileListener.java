package club.xiaojiawei.listener;

import club.xiaojiawei.core.Core;
import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.PowerLogUtil;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.enums.TagEnum.*;
import static club.xiaojiawei.enums.WarPhaseEnum.*;

/**
 * @author 肖嘉威
 * @date 2023/7/5 20:40
 * @msg
 */
@Slf4j
@Component
public class PowerFileListener {

    @Resource
    private ScheduledThreadPoolExecutor listenFileThreadPool;
    @Resource
    private Properties scriptProperties;
    @Resource
    private SpringData springData;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private Core core;
    private static ScheduledFuture<?> scheduledFuture;
    private static ScheduledFuture<?> errorScheduledFuture;
    private static long mark;
    public static final long MAX_ERROR_TIME = 5 * 60 * 1000L;
    private static RandomAccessFile accessFile;

    public static RandomAccessFile getAccessFile() {
        return accessFile;
    }

    public static void setMark(long mark) {
        PowerFileListener.mark = mark;
    }

    public static void cancelListener(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            log.info("已停止监听power.log");
            scheduledFuture.cancel(true);
        }
        if (errorScheduledFuture != null && !errorScheduledFuture.isDone()){
            log.info("已停止监听是否出现异常情况");
            errorScheduledFuture.cancel(true);
        }
        if (accessFile != null){
            try {
                accessFile.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void listen(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            log.warn(springData.getPowerLogName() + "正在被监听，无法再次被监听");
            return;
        }
        mark = System.currentTimeMillis();
        File logPath = new File(scriptProperties.getProperty(ConfigurationKeyEnum.GAME_PATH_KEY.getKey()) + springData.getGameLogPath());
        File [] files;
        if (!logPath.exists() || (files = logPath.listFiles()) == null || files.length == 0){
            log.error(springData.getPowerLogName() + "日志文件读取失败");
            return;
        }
        Arrays.sort(files, Comparator.comparing(File::getName));
        File logDir = files[files.length - 1];
        try{
            File powerLog = new File(logDir.getAbsolutePath() + "/" + springData.getPowerLogName());
            if (!powerLog.exists()){
                try(FileWriter fileWriter = new FileWriter(powerLog)){
                    fileWriter.write("#created by " + ScriptStaticData.SCRIPT_NAME);
                }
            }
            accessFile = new RandomAccessFile(powerLog, "r");
            accessFile.seek(accessFile.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("开始监听" + springData.getPowerLogName());
        scheduledFuture = listenFileThreadPool.scheduleAtFixedRate(new LogRunnable(() -> {
            try {
                while (!isPause.get().get() && !AbstractPhaseStrategy.isDealing()){
                    resolveLog(accessFile.readLine(), accessFile);
                }
                if (isPause.get().get()){
                    cancelListener();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), 0, 1500, TimeUnit.MILLISECONDS);
        log.info("开始监听是否出现异常情况");
        errorScheduledFuture = listenFileThreadPool.scheduleAtFixedRate(new LogRunnable(() -> {
            if (!isPause.get().get() && System.currentTimeMillis() - mark > MAX_ERROR_TIME){
                core.restart();
            }
        }), 0, 1, TimeUnit.MINUTES);
    }

    public void resolveLog(String line, RandomAccessFile accessFile) throws IOException {
        if (line == null){
            if (accessFile.getFilePointer() > accessFile.length()){
                accessFile.seek(0);
            }
        }else if (isRelevance(line)){
            setMark(System.currentTimeMillis());
            if (War.getCurrentPhase() == null && line.contains("CREATE_GAME")){
                War.setCurrentPhase(FILL_DECK_PHASE, line);
            }else if (War.getCurrentPhase() == FILL_DECK_PHASE && line.contains("BLOCK_START")){
                War.setCurrentPhase(DRAWN_INIT_CARD_PHASE, line);
            }else if (War.getCurrentPhase() == DRAWN_INIT_CARD_PHASE && line.contains("TAG_CHANGE")){
                TagChangeEntity tagChangeEntity = PowerLogUtil.parseTagChange(line);
                if (tagChangeEntity.getTag() == MULLIGAN_STATE && Objects.equals(tagChangeEntity.getValue(), "INPUT")){
                    War.setCurrentPhase(REPLACE_CARD_PHASE, line);
                }
            }else if (War.getCurrentPhase() == REPLACE_CARD_PHASE && line.contains("BLOCK_END")){
                War.setCurrentPhase(SPECIAL_EFFECT_TRIGGER_PHASE, line);
            }else if (War.getCurrentPhase() == SPECIAL_EFFECT_TRIGGER_PHASE && line.contains("MAIN_READY")){
                War.setCurrentPhase(GAME_TURN_PHASE, line);
            }else if (War.getCurrentPhase() == GAME_OVER_PHASE
                    || (War.getCurrentPhase() != null
                    && (line.contains(StepEnum.FINAL_GAMEOVER.getValue()) || line.contains(StepEnum.FINAL_WRAPUP.getValue()))
            )){
                War.setCurrentPhase(GAME_OVER_PHASE, line);
            }
        }
    }

    public boolean isRelevance(String l){
        if (l.contains("Truncating log")){
            log.info("power.log文件过大，游戏停止输出日志，需要重启游戏");
            core.restart();
        }
        return l.contains("PowerTaskList.DebugPrintPower()");
    }
}
