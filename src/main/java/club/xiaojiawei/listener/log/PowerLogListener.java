package club.xiaojiawei.listener.log;

import club.xiaojiawei.interfaces.closer.LogListenerCloser;
import club.xiaojiawei.core.Core;
import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.enums.WarPhaseEnum.*;

/**
 * 对局日志监听器
 * @author 肖嘉威
 * @date 2023/7/5 20:40
 */
@Slf4j
@Component
public class PowerLogListener extends AbstractLogListener implements LogListenerCloser {

    @Resource
    private Core core;

    private ScheduledFuture<?> errorScheduledFuture;

    private volatile long lastWorkTime;

    private static final long MAX_IDLE_TIME = 5 * 60 * 1_000L;

    @Autowired
    public PowerLogListener(SpringData springData) {
        super(springData.getPowerLogName(), 0, 1_000L, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void readOldLog() throws IOException {
        accessFile.seek(accessFile.length());
        War.reset();
    }

    @Override
    protected void listenLog() throws IOException {
        while (!isPause.get().get() && !AbstractPhaseStrategy.isDealing()){
            String line = accessFile.readLine();
            if (line == null){
                break;
            }else if (isRelevance(line)){
                resolveLog(line);
            }
        }
    }

    @Override
    protected void otherListen() {
        lastWorkTime = System.currentTimeMillis();
        log.info("开始监听异常情况");
        errorScheduledFuture = listenFileThreadPool.scheduleAtFixedRate(new LogRunnable(() -> {
            if (!isPause.get().get() && System.currentTimeMillis() - lastWorkTime > MAX_IDLE_TIME){
                log.info("监听到异常情况，准备重启游戏");
                lastWorkTime = System.currentTimeMillis();
                core.restart();
            }
        }), 0, 1, TimeUnit.MINUTES);
    }

    @Override
    protected void cancelOtherListener() {
        if (errorScheduledFuture != null && !errorScheduledFuture.isDone()){
            errorScheduledFuture.cancel(true);
        }
    }

    private void resolveLog(String line) {
        switch (War.getCurrentPhase()){
            case FILL_DECK_PHASE -> {
                War.setStartTime(System.currentTimeMillis());
                FILL_DECK_PHASE.getAbstractPhaseStrategy().deal(line);
            }
            case GAME_OVER_PHASE -> {
                War.setEndTime(War.getStartTime() == 0 ? 0 : System.currentTimeMillis());
                GAME_OVER_PHASE.getAbstractPhaseStrategy().deal(line);
                War.reset();
            }
            default -> War.getCurrentPhase().getAbstractPhaseStrategy().deal(line);
        }
        if (War.getCurrentTurnStep() == StepEnum.FINAL_GAMEOVER){
            War.setCurrentPhase(GAME_OVER_PHASE);
        }
    }

    public boolean isRelevance(String l){
        boolean flag = false;
        if (l.contains("Truncating log")){
            log.info("power.log达到10000KB，游戏停止输出日志，准备重启游戏");
            core.restart();
        }else {
            flag = l.contains("PowerTaskList");
        }
        lastWorkTime = System.currentTimeMillis();
        return flag;
    }

    @Override
    public void closeLogListener() {
        cancelListener();
    }

}
