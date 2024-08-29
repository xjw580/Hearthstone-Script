package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.log.ExtraEntity;
import club.xiaojiawei.bean.log.TagChangeEntity;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static club.xiaojiawei.data.ScriptStaticData.CARD_AREA_MAP;
import static club.xiaojiawei.enums.StepEnum.BEGIN_MULLIGAN;
import static club.xiaojiawei.enums.TagEnum.*;

/**
 * 抽起始牌阶段
 * @author 肖嘉威
 * @date 2022/11/27 13:35
 */
@Slf4j
@Component
public class DrawnInitCardPhaseStrategy extends AbstractPhaseStrategy{

    /**
     * SHOW_ENTITY解析来的reverse为false
     * TAG_CHANGE解析来的reverse为true
     * @param playerId
     * @param reverse
     */
    public void verifyPlayer(String playerId, boolean reverse){
        if (reverse){
            playerId = Objects.equals(playerId, "1")? "2" : "1";
        }
        if (War.getMe() == null && Strings.isNotBlank(playerId)) {
            switch (playerId) {
                case "1" -> {
                    War.setMe(War.getPlayer1());
                    War.setRival(War.getPlayer2());
                    log.info("确定双方玩家号，我方1号，对方2号");
                }
                case "2" -> {
                    War.setMe(War.getPlayer2());
                    War.setRival(War.getPlayer1());
                    log.info("确定双方玩家号，我方2号，对方1号");
                }
                default -> log.warn("不支持的playId");
            }
        }
    }

    @Override
    protected boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity) {
        if (tagChangeEntity.getTag() == ZONE){
            verifyPlayer(tagChangeEntity.getPlayerId(), true);
        }else if (tagChangeEntity.getTag() == NEXT_STEP && Objects.equals(tagChangeEntity.getValue(), BEGIN_MULLIGAN.name())){
            War.setCurrentPhase(WarPhaseEnum.REPLACE_CARD_PHASE);
            return true;
        }
        return false;
    }

    @Override
    protected boolean dealShowEntityThenIsOver(String line, ExtraEntity extraEntity) {
        if (Objects.equals(extraEntity.getEntityName(), ScriptStaticData.UNKNOWN)){
            verifyPlayer(extraEntity.getPlayerId(), false);
        }
        return false;
    }

    /**
     * 确定一方玩家的游戏id，{@link #verifyPlayer(String, boolean)}方法绝对会在此方法执行前执行
     * @param line
     * @param extraEntity
     * @return
     */
    @Override
    protected boolean dealFullEntityThenIsOver(String line, ExtraEntity extraEntity) {
        Card card = CARD_AREA_MAP.get(extraEntity.getEntityId()).findByEntityId(extraEntity.getEntityId());
        if (Objects.equals(card.getEntityName(), ScriptStaticData.UNKNOWN) || Objects.equals(card.getEntityName(), "幸运币")){
            card.setEntityName("幸运币");
            if (Strings.isNotBlank(card.getCardId())){
                War.getRival().setGameId(War.getFirstPlayerGameId());
                log.info("对方游戏id：" + War.getFirstPlayerGameId());
            }else {
                War.getMe().setGameId(War.getFirstPlayerGameId());
                log.info("我方游戏id：" + War.getFirstPlayerGameId());
            }
            if (Objects.equals(War.getMe().getGameId(), War.getFirstPlayerGameId()) || (War.getRival().getGameId() != null && !Objects.equals(War.getRival().getGameId(), War.getFirstPlayerGameId()))){
                War.setCurrentPlayer(War.getMe());
            }else {
                War.setCurrentPlayer(War.getRival());
            }
        }
        return false;
    }

}
