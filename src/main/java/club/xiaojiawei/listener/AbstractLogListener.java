package club.xiaojiawei.listener;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2023/9/20 16:54
 * @msg
 */
@Slf4j
public abstract class AbstractLogListener {
    @Resource
    protected ScheduledThreadPoolExecutor listenFileThreadPool;
    @Resource
    protected AtomicReference<BooleanProperty> isPause;
    @Setter
    protected static File logDir;
    @Getter
    protected RandomAccessFile accessFile;
    protected ScheduledFuture<?> logScheduledFuture;
    protected String logName;
    protected long listenInitialDelay;
    protected long listenPeriod;
    protected TimeUnit listenUnit;

    public AbstractLogListener(String logName,
                               long listenInitialDelay,
                               long listenPeriod,
                               TimeUnit listenUnit) {
        this.logName = logName;
        this.listenInitialDelay = listenInitialDelay;
        this.listenPeriod = listenPeriod;
        this.listenUnit = listenUnit;
    }

    protected abstract void readOldLog();
    protected abstract void listenLog();
    protected void otherListen(){};
    protected void cancelOtherListener(){};
    public synchronized void listen(){
        if (logScheduledFuture != null && !logScheduledFuture.isDone()){
            log.warn(logName + "正在被监听，无法再次被监听");
            return;
        }
        File logFile = createFile();
        closeLogStream();
        log.info("开始监听" + logName);
        try {
            accessFile = new RandomAccessFile(logFile, "r");
            readOldLog();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logScheduledFuture = listenFileThreadPool.scheduleAtFixedRate(new LogRunnable(this::listenLog), listenInitialDelay, listenPeriod, listenUnit);
        otherListen();
    }
    private File createFile(){
        File screenLogFile = new File(logDir.getAbsolutePath() + "/" + logName);
        if (!screenLogFile.exists()){
            try(FileWriter fileWriter = new FileWriter(screenLogFile)){
                fileWriter.write("#created by " + ScriptStaticData.SCRIPT_NAME);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return screenLogFile;
    }
    private void closeLogStream(){
        if (accessFile != null){
            try {
                accessFile.close();
                accessFile = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void cancelListener(){
        if (logScheduledFuture != null && !logScheduledFuture.isDone()){
            logScheduledFuture.cancel(true);
            log.info("已停止监听" + logName);
            SystemUtil.delay(2000);
        }
        cancelOtherListener();
    }
}
