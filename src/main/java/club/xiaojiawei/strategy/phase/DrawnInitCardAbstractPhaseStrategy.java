package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.ExtraEntity;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static club.xiaojiawei.data.ScriptStaticData.CARD_AREA_MAP;
import static club.xiaojiawei.enums.TagEnum.ZONE;

/**
 * @author 肖嘉威
 * @date 2022/11/27 13:35
 */
@Slf4j
@Component
public class DrawnInitCardAbstractPhaseStrategy extends AbstractPhaseStrategy{

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
//        设置我和对手哪个是一号玩家，哪个是二号玩家
        if (War.getMe() == null && Strings.isNotBlank(playerId)) {
            switch (playerId) {
                case "1" -> {
                    War.setMe(War.getPlayer1());
                    War.setRival(War.getPlayer2());
                }
                case "2" -> {
                    War.setMe(War.getPlayer2());
                    War.setRival(War.getPlayer1());
                }
                default -> log.warn("不支持的playId");
            }
        }
    }

    @Override
    protected boolean dealTagChangeThenIsOver(String s, TagChangeEntity tagChangeEntity) {
        if (tagChangeEntity.getTag() == ZONE){
            verifyPlayer(tagChangeEntity.getPlayerId(), true);
        }
        return false;
    }

    @Override
    protected boolean dealShowEntityThenIsOver(String s, ExtraEntity extraEntity) {
        verifyPlayer(extraEntity.getPlayerId(), false);
        return false;
    }

    @Override
    protected boolean dealFullEntityThenIsOver(String s, ExtraEntity extraEntity) {
        Card card = CARD_AREA_MAP.get(extraEntity.getEntityId()).findByEntityId(extraEntity.getEntityId());
//        确定一方玩家的游戏id，verifyPlayer方法绝对会在此方法执行前执行
        card.setEntityName("幸运币");
        if (Strings.isNotBlank(card.getCardId())){
            War.getRival().setGameId(War.getFirstPlayerGameId());
            log.info("对方：" + War.getFirstPlayerGameId());
        }else {
            War.getMe().setGameId(War.getFirstPlayerGameId());
            log.info("我方：" + War.getFirstPlayerGameId());
        }
        if (Objects.equals(War.getMe().getGameId(), War.getFirstPlayerGameId()) || (War.getRival().getGameId() != null && !Objects.equals(War.getRival().getGameId(), War.getFirstPlayerGameId()))){
            War.setCurrentPlayer(War.getMe());
        }else {
            War.setCurrentPlayer(War.getRival());
        }
        return false;
    }

    @Override
    protected boolean dealOtherThenIsOver(String s) {
        return s.contains("BLOCK_END");
    }
}
