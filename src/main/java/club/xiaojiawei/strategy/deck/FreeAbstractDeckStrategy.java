package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.Player;
import club.xiaojiawei.entity.area.HandArea;
import club.xiaojiawei.run.Core;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;

import java.util.List;

import static club.xiaojiawei.constant.GameRatioConst.*;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:41
 */
public class FreeAbstractDeckStrategy extends AbstractDeckStrategy {
    public FreeAbstractDeckStrategy() {
        super(null);
    }

    @Override
    protected void afterIntoReplaceCardPhase(Object o) {
        Player me = War.getMe();
        if (me == null){
            return;
        }
        HandArea handArea = me.getHandArea();
        List<Card> cards = handArea.getCards();
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        float clearance = GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO, firstCardPos;
        if (cards.size() == 3){
            clearance *= CARD_HORIZONTAL_CLEARANCE_WHEN_THREE_CARD * (rect.bottom - rect.top);
            firstCardPos = (rect.left + rect.right >> 1) - (rect.bottom - rect.top) * FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_THREE_CARD * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        }else {
            clearance *= CARD_HORIZONTAL_CLEARANCE_WHEN_FOUR_CARD * (rect.bottom - rect.top);
            firstCardPos = (rect.left + rect.right >> 1) - (rect.bottom - rect.top) * FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_FOUR_CARD * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        }
        Card card;
        for (int i = 0; i < cards.size(); i++) {
            card = cards.get(i);
//            只留费用小于等于2的
            if (card.getCost() > 2){
                clickFloatCard(clearance, firstCardPos, rect, i);
            }
        }
    }

    @Override
    protected void outCard() {
        Player rival = War.getRival();
        Player me = War.getMe();
        List<Card> handCards = me.getHandArea().getCards();
        List<Card> playCards = me.getPlayArea().getCards();
        List<Card> rivalPlayCards = rival.getPlayArea().getCards();
        int resources = me.getResources();
        for (int i = handCards.size() - 1; i >= 0; i--) {
            if (!me.getPlayArea().isFull()){
                Card card = handCards.get(i);
                if (card.getCost() <= resources){
                    resources -= card.getCost();
                    myHandPointToMyPlay(i, playCards.size(), me);
                }
            }else {
                break;
            }
        }
        if (resources >= 2){
            clickMyPower();
        }
        boolean throughWall = true;
//        解嘲讽怪
        for (int i = rivalPlayCards.size() - 1; i >= 0; i--) {
            Card card = rivalPlayCards.get(i);
            if (card.isTaunt() && !card.isStealth()){
                List<Integer> result = freeEatTaunt(playCards, card);
                if (result == null){
//                    过墙失败
                    throughWall = false;
                    continue;
                }
                for (int j = result.size() - 2; j >= 0; j--) {
                    Integer integer = result.get(j);
                    myPlayPointToRivalPlay(integer, i, me, rival);
                }
            }
        }
        if (throughWall){
            for (int i = playCards.size() - 1; i >= 0; i--) {
                Card card = playCards.get(i);
                if (!card.isExhausted() && card.getAtc() > 0 && !card.isFrozen()){
                    myPlayPointToRivalHero(i, me);
                }
            }
        }
    }
}
