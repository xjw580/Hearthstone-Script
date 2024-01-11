package club.xiaojiawei.listener.log;

import club.xiaojiawei.config.LogListenerConfig;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 在 {@link LogListenerConfig#logListener()} 添加新的LogListener
 * @author 肖嘉威
 * @date 2023/9/20 16:54
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
    protected String logFileName;
    protected long listenInitialDelay;
    protected long listenPeriod;
    protected TimeUnit listenTimeUnit;
    protected AbstractLogListener nextLogListener;

    public AbstractLogListener setNextLogListener(AbstractLogListener nextLogListener) {
        return this.nextLogListener = nextLogListener;
    }

    public AbstractLogListener(String logFileName,
                               long listenInitialDelay,
                               long listenPeriod,
                               TimeUnit listenTimeUnit) {
        this.logFileName = logFileName;
        this.listenInitialDelay = listenInitialDelay;
        this.listenPeriod = listenPeriod;
        this.listenTimeUnit = listenTimeUnit;
    }

    protected abstract void readOldLog() throws Exception;

    protected abstract void listenLog() throws Exception;

    protected void otherListen(){};

    protected void cancelOtherListener(){};

    public synchronized void listen(){
        if (logScheduledFuture != null && !logScheduledFuture.isDone()){
            log.warn(logFileName + "正在被监听，无法再次被监听");
            if (nextLogListener != null){
                nextLogListener.listen();
            }
            return;
        }
        closeLogStream();
        File logFile = createFile();
        log.info("开始监听" + logFileName);
        try {
            accessFile = new RandomAccessFile(logFile, "r");
            readOldLog();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logScheduledFuture = listenFileThreadPool.scheduleAtFixedRate(() -> {
            if (isPause.get().get()){
                cancelListener();
            }else {
                try {
                    listenLog();
                }catch (Exception e){
                    log.warn(logFileName + "监听器发生错误", e);
                }
            }
        }, listenInitialDelay, listenPeriod, listenTimeUnit);
        otherListen();
        if (nextLogListener != null){
            nextLogListener.listen();
        }
    }

    private File createFile(){
        File logFile = new File(logDir.getAbsolutePath() + "/" + logFileName);
        if (!logFile.exists()){
            try(FileWriter fileWriter = new FileWriter(logFile)){
                fileWriter.write("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return logFile;
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
        }
        cancelOtherListener();
    }
}
