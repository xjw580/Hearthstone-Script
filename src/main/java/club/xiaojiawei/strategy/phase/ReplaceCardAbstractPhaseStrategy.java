package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.data.GameStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.listener.ScreenFileListener;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.PowerLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.enums.TagEnum.NEXT_STEP;
import static club.xiaojiawei.enums.WarPhaseEnum.GAME_OVER_PHASE;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
@Slf4j
@Component
public class ReplaceCardAbstractPhaseStrategy extends AbstractPhaseStrategy<String> {

    private static final float CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.20;
    @Resource
    private Properties scriptProperties;

    @Override
    protected void execute(String l, RandomAccessFile accessFile) {
//        等待动画结束
        ScriptStaticData.ROBOT.delay(20_000);
//        执行换牌策略
        DeckEnum.valueOf(scriptProperties.getProperty(ConfigurationKeyEnum.DECK_KEY.getKey())).getAbstractDeckStrategy().changeCard();
        systemUtil.delayMedium();
        TagChangeEntity tagChangeEntity;
        while (true) {
            if (isPause.get().get()){
                return;
            }
            try {
                if ((l = accessFile.readLine()) == null) {
                    if (accessFile.getFilePointer() > accessFile.length()){
                        accessFile.seek(0);
                    }else {
                        ScriptStaticData.ROBOT.delay(1_000);
                    }
                }else if (powerFileListener.isRelevance(l)){
                    ScreenFileListener.setMark(System.currentTimeMillis());
                    if (l.contains(GameStaticData.SHOW_ENTITY)){
                        PowerLogUtil.dealShowEntity(l, accessFile);
                    }else if (l.contains(GameStaticData.TAG_CHANGE)){
                        tagChangeEntity = PowerLogUtil.parseTagChange(l);
                        if (!PowerLogUtil.dealTagChange(tagChangeEntity)){
                            if (tagChangeEntity.getTag() == NEXT_STEP && Objects.equals(StepEnum.MAIN_READY.getValue(), tagChangeEntity.getValue())){
                                break;
                            }else if (Objects.equals(tagChangeEntity.getValue(), StepEnum.FINAL_GAMEOVER.getValue())){
                                War.setCurrentPhase(GAME_OVER_PHASE, l);
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
