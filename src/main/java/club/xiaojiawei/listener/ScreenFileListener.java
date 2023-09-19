package club.xiaojiawei.listener;

import club.xiaojiawei.core.Core;
import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.GAME_ALIVE_CMD;
import static club.xiaojiawei.data.ScriptStaticData.ROBOT;

/**
 * @author 肖嘉威
 * @date 2023/7/5 14:55
 * @msg
 */

@Component
@Slf4j
public class ScreenFileListener {
    @Resource
    private ScheduledThreadPoolExecutor listenFileThreadPool;
    @Resource
    private Properties scriptProperties;
    @Resource
    private SpringData springData;
    @Resource
    private Core core;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    private static ScheduledFuture<?> scheduledFuture;
    private static ScheduledFuture<?> errorScheduledFuture;
    @Setter
    private volatile static long lastWorkTime;
    public static final long MAX_IDLE_TIME = 5 * 60 * 1000L;
    private static BufferedReader reader;
    public synchronized void listen(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            log.warn(springData.getScreenLogName() + "正在被监听，无法再次被监听");
            return;
        }
        File logPath = new File(scriptProperties.getProperty(ConfigurationKeyEnum.GAME_PATH_KEY.getKey()) + springData.getGameLogPath());
        File [] files;
        if (!logPath.exists() || (files = logPath.listFiles()) == null || files.length == 0){
            log.error(springData.getScreenLogName() + "日志文件读取失败");
            return;
        }
        Arrays.sort(files, Comparator.comparing(File::getName));
        File logDir = files[files.length - 1];
        log.info("开始监听" + springData.getScreenLogName());
        try {
            File screenLogFile = new File(logDir.getAbsolutePath() + "/" + springData.getScreenLogName());
            if (!screenLogFile.exists()){
                try(FileWriter fileWriter = new FileWriter(screenLogFile)){
                    fileWriter.write("");
                }
            }
            if (reader != null){
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            reader = new BufferedReader(new FileReader(screenLogFile));
            loadPrevData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scheduledFuture = listenFileThreadPool.scheduleAtFixedRate(new LogRunnable(() -> {
            try {
                String line;
                while (!isPause.get().get() && Strings.isNotBlank((line = reader.readLine()))){
                    Mode.setCurrMode(resolveLog(line));
                }
                if (isPause.get().get()){
                    cancelListener();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), 0, 1500, TimeUnit.MILLISECONDS);
        lastWorkTime = System.currentTimeMillis();
        log.info("开始监听是否出现异常情况");
        errorScheduledFuture = listenFileThreadPool.scheduleAtFixedRate(new LogRunnable(() -> {
            if (!isPause.get().get() && System.currentTimeMillis() - lastWorkTime > MAX_IDLE_TIME){
                core.restart();
            }
        }), 0, 1, TimeUnit.MINUTES);
    }

    public static void cancelListener(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
            log.info("已停止监听screen.log");
            SystemUtil.delay(2000);
        }
        if (errorScheduledFuture != null && !errorScheduledFuture.isDone()){
            errorScheduledFuture.cancel(true);
            log.info("已停止监听是否出现异常情况");
        }
    }

    private ModeEnum resolveLog(String line) {
        if (line == null){
            return null;
        }
        int index;
        if ((index = line.indexOf("currMode")) != -1){
            return ModeEnum.valueOf(line.substring(index + 9));
        }else if (line.contains("OnDestroy()")){
            try {
                Thread.sleep(2000);
                if (Strings.isBlank(new String(Runtime.getRuntime().exec(GAME_ALIVE_CMD).getInputStream().readAllBytes()))){
                    log.info("检测到游戏关闭，准备重新启动");
                    core.restart();
                }
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void loadPrevData() throws IOException {
        String line;
        int index;
        ModeEnum finalMode = null;
        while ((line = reader.readLine()) != null){
            if ((index = line.indexOf("currMode")) != -1){
                finalMode = ModeEnum.valueOf(line.substring(index + 9));
            }
        }
        Mode.setCurrMode(finalMode);
    }
}
