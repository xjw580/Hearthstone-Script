package club.xiaojiawei.status;

import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.bean.entity.ExtraEntity;
import club.xiaojiawei.bean.Player;
import club.xiaojiawei.bean.entity.TagChangeEntity;
import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.enums.ZoneEnum;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static club.xiaojiawei.data.ScriptStaticData.CARD_AREA_MAP;
import static club.xiaojiawei.enums.WarPhaseEnum.FILL_DECK_PHASE;

/**
 * @author 肖嘉威
 * @date 2022/11/25 20:57
 * 对局状态
 */
@Slf4j
public class War {

    @Getter
    private volatile static Player currentPlayer;
    @Getter
    private static String firstPlayerGameId;
    @Getter
    @Setter
    private volatile static WarPhaseEnum currentPhase = FILL_DECK_PHASE;
    @Getter
    private volatile static StepEnum currentTurnStep;
    @Getter
    @Setter
    private volatile static Player me;
    @Getter
    @Setter
    private volatile static Player rival;
    @Getter
    private volatile static Player player1;
    @Getter
    private volatile static Player player2;
    @Getter
    @Setter
    private volatile static int warTurn;
    @Getter
    @Setter
    private volatile static String won;
    @Getter
    @Setter
    private volatile static String lost;
    @Getter
    @Setter
    private volatile static String conceded;
    public volatile static SimpleIntegerProperty warCount = new SimpleIntegerProperty();
    public final static AtomicInteger winCount = new AtomicInteger();
    @Getter
    @Setter
    private volatile static boolean myTurn;
    public static void reset(){
        currentPlayer = null;
        firstPlayerGameId = null;
        currentPhase = FILL_DECK_PHASE;
        currentTurnStep = null;
        me = null;
        rival = null;
        player1 = new Player("1");
        player2 = new Player("2");
        warTurn = 0;
        won = "";
        lost = "";
        conceded = "";
        CARD_AREA_MAP.clear();
        log.info("已重置游戏状态");
    }

    public static void setCurrentTurnStep(StepEnum currentTurnStep) {
        log.info((War.currentTurnStep = currentTurnStep).getComment());
    }

    private static void printResult(){
        if (Objects.equals(War.getWon(), War.getMe().getGameId())){
            War.winCount.incrementAndGet();
        }
        log.info("本局游戏胜者：" + won);
        log.info("本局游戏败者：" + lost);
        log.info("本局游戏投降者：" + conceded);
    }

    public static void setFirstPlayerGameId(String firstPlayerGameId) {
        log.info("先手玩家：" + (War.firstPlayerGameId = firstPlayerGameId));
    }

    public static void setCurrentPlayer(Player currentPlayer) {
        log.info((War.currentPlayer = currentPlayer).getGameId() + " 的回合");
    }

    public static synchronized void increaseWarCount(){
        if (War.getMe() != null){
            printResult();
        }
        warCount.set(warCount.get() + 1);
    }
    public static Card exchangeAreaOfCard(ExtraEntity extraEntity){
        Area sourceArea = CARD_AREA_MAP.get(extraEntity.getEntityId());
        if (sourceArea == null){
            sourceArea = War.getPlayer(extraEntity.getPlayerId()).getArea(extraEntity.getZone());
        }
        Area targetArea = War.getPlayer(extraEntity.getPlayerId()).getArea(extraEntity.getExtraCard().getZone());
        Card card = sourceArea.removeByEntityId(extraEntity.getEntityId());
        targetArea.add(card, extraEntity.getExtraCard().getZonePos());
        return card;
    }
    public static void exchangeAreaOfCard(TagChangeEntity tagChangeEntity){
        Area sourceArea = CARD_AREA_MAP.get(tagChangeEntity.getEntityId());
        Area targetArea = War.getPlayer(tagChangeEntity.getPlayerId()).getArea(ZoneEnum.valueOf(tagChangeEntity.getValue()));
        targetArea.add(sourceArea.removeByEntityId(tagChangeEntity.getEntityId()), 0);
    }

    public static Player getPlayer(String playerId){
        return player1.getPlayerId().equals(playerId)? player1 : player2;
    }
    public static Player getReversePlayer(String playerId){
        return player1.getPlayerId().equals(playerId)? player2 : player1;
    }


    public static Player getPlayerByArea(Area area){
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
    public static Area getReverseArea(Area area){
        if (area == player1.getPlayArea()){
            return player2.getPlayArea();
        }else if (area == player1.getHandArea()){
            return player2.getHandArea();
        }else if (area == player1.getDeckArea()){
            return player2.getDeckArea();
        }else if (area == player1.getGraveyardArea()){
            return player2.getGraveyardArea();
        }else if (area == player1.getRemovedfromgameArea()){
            return player2.getRemovedfromgameArea();
        }else if (area == player1.getSecretArea()){
            return player2.getSecretArea();
        }else if (area == player1.getSetasideArea()){
            return player2.getSetasideArea();
        }else if (area == player2.getPlayArea()){
            return player1.getPlayArea();
        }else if (area == player2.getHandArea()){
            return player1.getHandArea();
        }else if (area == player2.getDeckArea()){
            return player1.getDeckArea();
        }else if (area == player2.getGraveyardArea()){
            return player1.getGraveyardArea();
        }else if (area == player2.getRemovedfromgameArea()){
            return player1.getRemovedfromgameArea();
        }else if (area == player2.getSecretArea()){
            return player1.getSecretArea();
        }else if (area == player2.getSetasideArea()){
            return player1.getSetasideArea();
        }
        return null;
    }
}
