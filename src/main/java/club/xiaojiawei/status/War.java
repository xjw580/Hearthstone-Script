package club.xiaojiawei.status;

import club.xiaojiawei.data.GameStaticData;
import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.ExtraEntity;
import club.xiaojiawei.entity.Player;
import club.xiaojiawei.entity.area.Area;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.strategy.phase.GameTurnAbstractPhaseStrategy;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 肖嘉威
 * @date 2022/11/25 20:57
 * 对局状态
 */
@SuppressWarnings("all")
@Slf4j
public class War {

    public volatile static SimpleIntegerProperty warCount = new SimpleIntegerProperty();
    public final static AtomicInteger winCount = new AtomicInteger();
    private volatile static WarPhaseEnum currentPhase;
    private volatile static Player me;
    private volatile static Player rival;

    private volatile static Player player1;

    private volatile static Player player2;

    public static WarPhaseEnum getCurrentPhase() {
        return currentPhase;
    }

    public static synchronized void increaseWarCount(){
        warCount.set(warCount.get() + 1);
    }

    public static void setCurrentPhase(WarPhaseEnum currentPhase) {
        setCurrentPhase(currentPhase, null);
    }
    public static void setCurrentPhase(WarPhaseEnum currentPhase, String l) {
        War.currentPhase = currentPhase;
        currentPhase.getAbstractPhaseStrategy().dealing(l);
    }

    public static Player getMe() {
        return me;
    }

    public static Player getRival() {
        return rival;
    }

    public static void setMe(Player me) {
        War.me = me;
    }

    public static void setRival(Player rival) {
        War.rival = rival;
    }

    public static void reset(){
        player1 = new Player();
        player1.setPlayerId("1");
        player2 = new Player();
        player2.setPlayerId("2");
        AbstractPhaseStrategy.setDealing(false);
        GameStaticData.CARD_AREA_MAP.clear();
        GameTurnAbstractPhaseStrategy.reset();
        me = null;
        rival = null;
        currentPhase = null;
        log.info("已重置游戏状态");
    }

    public static Card exchangeAreaOfCard(ExtraEntity extraEntity){
        Area sourceArea = GameStaticData.CARD_AREA_MAP.get(extraEntity.getEntityId());
        Area targetArea = War.getPlayer(extraEntity.getPlayerId()).getArea(extraEntity.getExtraCard().getZone());
        Card sourceCard = sourceArea.removeByEntityId(extraEntity.getEntityId());
        targetArea.add(sourceCard, extraEntity.getExtraCard().getZonePos());
        return sourceCard;
    }

    public static Player getPlayer1(){
        return player1;
    }

    public static Player getPlayer2(){
        return player2;
    }

    public static Player getPlayer(String playerId){
        return player1.getPlayerId().equals(playerId)? player1 : player2;
    }

    public static Player testArea(Area area){
        if (area == player1.getPlayArea()
                || area == player1.getHandArea()
                || area == player1.getDeckArea()
                || area == player1.getGraveyardArea()
                || area == player1.getRemovedfromgameArea()
                || area == player1.getSecretArea()
                || area == player1.getSetasideArea()
        ){
            return player1;
        }
        return player2;
    }
}
