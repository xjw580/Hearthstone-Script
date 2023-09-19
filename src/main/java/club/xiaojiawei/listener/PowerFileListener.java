package club.xiaojiawei.listener;

import club.xiaojiawei.core.Core;
import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
    @Getter
    private static RandomAccessFile accessFile;

    public static void cancelListener(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
            log.info("已停止监听power.log");
            SystemUtil.delay(1000);
        }
    }

    public synchronized void listen(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            log.warn(springData.getPowerLogName() + "正在被监听，无法再次被监听");
            return;
        }
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
            if (accessFile != null){
                try {
                    accessFile.close();
                    accessFile = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
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
                while (true){
                    if (isPause.get().get()){
                        cancelListener();
                        break;
                    }else if (AbstractPhaseStrategy.isDealing()){
                        break;
                    }else {
                        String s = accessFile.readLine();
                        if (s == null){
                            break;
                        }else if (isRelevance(s)){
                            resolveLog(s);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void resolveLog(String line) throws IOException {
        if (War.getCurrentTurnStep() == StepEnum.FINAL_GAMEOVER){
            War.setCurrentPhase(GAME_OVER_PHASE, line);
        }else if (War.getCurrentPhase() == null){
            War.reset();
            War.setCurrentPhase(FILL_DECK_PHASE, line);
        }else if (War.getCurrentPhase() == FILL_DECK_PHASE){
            War.setCurrentPhase(DRAWN_INIT_CARD_PHASE, line);
        }else if (War.getCurrentPhase() == DRAWN_INIT_CARD_PHASE){
            War.setCurrentPhase(REPLACE_CARD_PHASE, line);
        }else if (War.getCurrentPhase() == REPLACE_CARD_PHASE){
            War.setCurrentPhase(SPECIAL_EFFECT_TRIGGER_PHASE, line);
        }else if (War.getCurrentPhase() == SPECIAL_EFFECT_TRIGGER_PHASE){
            War.setCurrentPhase(GAME_TURN_PHASE, line);
        }
    }

    public boolean isRelevance(String l){
        boolean flag = false;
        if (l.contains("Truncating log")){
            log.info("power.log文件过大，游戏停止输出日志，准备重启游戏");
            core.restart();
        }else {
            flag = l.contains("PowerTaskList");
        }
        ScreenFileListener.setLastWorkTime(System.currentTimeMillis());
        return flag;
    }
}
