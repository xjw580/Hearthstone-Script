package club.xiaojiawei.listener;

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
    public void readOldLog() {
        try {
            accessFile.seek(accessFile.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void listenLog() {
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
        ScreenLogListener.setLastWorkTime(System.currentTimeMillis());
        return flag;
    }
}
