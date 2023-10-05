package club.xiaojiawei.listener.log;

import club.xiaojiawei.core.Core;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.enums.WarPhaseEnum.*;

/**
 * @author 肖嘉威
 * @date 2023/7/5 20:40
 * @msg
 */
@Slf4j
@Component
public class PowerLogListener extends AbstractLogListener{

    @Resource
    private Core core;

    @Autowired
    public PowerLogListener(SpringData springData) {
        super(springData.getPowerLogName(), 0, 1_000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void readOldLog() throws IOException {
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

    private void resolveLog(String line) {
        switch (War.getCurrentPhase()){
            case FILL_DECK_PHASE -> {
                War.setStartTime(System.currentTimeMillis());
                FILL_DECK_PHASE.getAbstractPhaseStrategy().deal(line);
            }
            case DRAWN_INIT_CARD_PHASE -> DRAWN_INIT_CARD_PHASE.getAbstractPhaseStrategy().deal(line);
            case REPLACE_CARD_PHASE -> REPLACE_CARD_PHASE.getAbstractPhaseStrategy().deal(line);
            case SPECIAL_EFFECT_TRIGGER_PHASE -> SPECIAL_EFFECT_TRIGGER_PHASE.getAbstractPhaseStrategy().deal(line);
            case GAME_TURN_PHASE -> GAME_TURN_PHASE.getAbstractPhaseStrategy().deal(line);
            case GAME_OVER_PHASE -> {
                War.setEndTime(War.getStartTime() == 0 ? 0 : System.currentTimeMillis());
                GAME_OVER_PHASE.getAbstractPhaseStrategy().deal(line);
                War.reset();
            }
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
        ScreenLogListener.setLastWorkTime(System.currentTimeMillis());
        return flag;
    }
}
