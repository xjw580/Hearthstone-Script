package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author 肖嘉威
 * @date 2022/11/29 17:41
 */
@Component
@Slf4j
public class FreeAbstractDeckStrategy extends AbstractDeckStrategy {

    @Override
    protected void executeChangeCard(List<Card> myHandCards, float clearance , float firstCardPos) {
        Card card;
        for (int i = 0; i < myHandCards.size(); i++) {
            card = myHandCards.get(i);
//            只留费用小于等于2的
            if (card.getCost() > 2){
                clickFloatCard(clearance, firstCardPos, i);
            }
        }
    }

    @Override
    protected void outCard() {
        List<Card> handCards = myHandArea.getCards();
        List<Card> playCards = myPlayArea.getCards();
        List<Card> rivalPlayCards = rivalPlayArea.getCards();
        int resources = me.getResources();
        for (int i = handCards.size() - 1; i >= 0; i--) {
            if (!me.getPlayArea().isFull()){
                Card card = handCards.get(i);
                if (card.getCost() <= resources){
                    resources -= card.getCost();
                    myHandPointToMyPlay(i, playCards.size());
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
                List<Integer> result = calcFreeEatRivalTaunt(card);
                if (result == null){
//                    过墙失败
                    throughWall = false;
                    continue;
                }
                for (int j = result.size() - 2; j >= 0; j--) {
                    Integer integer = result.get(j);
                    myPlayPointToRivalPlay(integer, i);
                }
            }
        }
        if (throughWall){
            for (int i = playCards.size() - 1; i >= 0; i--) {
                Card card = playCards.get(i);
                if (!card.isExhausted() && card.getAtc() > 0 && !card.isFrozen()){
                    myPlayPointToRivalHero(i);
                }
            }
        }
    }
}
