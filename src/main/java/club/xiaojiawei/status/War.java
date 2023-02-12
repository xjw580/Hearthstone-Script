package club.xiaojiawei.status;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.ExtraEntity;
import club.xiaojiawei.entity.Player;
import club.xiaojiawei.entity.area.Area;
import club.xiaojiawei.enums.WarPhaseEnum;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.extern.slf4j.Slf4j;

import static club.xiaojiawei.constant.GameMapConst.CARD_AREA_MAP;

/**
 * @author 肖嘉威
 * @date 2022/11/25 20:57
 * 对局状态
 */
@SuppressWarnings("all")
@Slf4j
public class War {

    public static SimpleIntegerProperty warCount = new SimpleIntegerProperty();
    private static WarPhaseEnum currentPhase;
    private static Player me;
    private static Player rival;

    private static Player player1;

    private static Player player2;

    public static WarPhaseEnum getCurrentPhase() {
        return currentPhase;
    }

    public static void setCurrentPhase(WarPhaseEnum currentPhase) {
        War.currentPhase = currentPhase;
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

    static {
        reset();
    }

    public static void reset(){
        currentPhase = null;
        player1 = new Player();
        player1.setPlayerId("1");
        player2 = new Player();
        player2.setPlayerId("2");
        me = null;
        rival = null;
        CARD_AREA_MAP.clear();
        log.info("已重置游戏");
    }

    public static Card exchangeAreaOfCard(ExtraEntity extraEntity){
        Area sourceArea = CARD_AREA_MAP.get(extraEntity.getEntityId());
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
