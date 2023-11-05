package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 免费套牌
 * @author 肖嘉威
 * @date 2022/11/29 17:41
 */
@Component
@Slf4j
public class FreeDeckStrategy extends AbstractDeckStrategy {


    @Override
    protected boolean executeChangeCard(Card card, int index) {
        return card.getCost() > 2;
    }

    @Override
    protected void executeOutCard() {
        List<Card> handCards = myHandArea.getCards();
        List<Card> playCards = myPlayArea.getCards();
        List<Card> rivalPlayCards = rivalPlayArea.getCards();
        for (int i = handCards.size() - 1; i >= 0; i--) {
            if (!me.getPlayArea().isFull()){
                log.info("not full");
                Card card = handCards.get(i);
                if (card.getCost() <= calcMyUsableResource()){
                    log.info("play");
                    myHandPointToMyPlay(i);
                }
            }else {
                break;
            }
        }
        if (calcMyUsableResource() >= 2){
            clickPower();
        }
        boolean throughWall = true;
//        解嘲讽怪
        for (int i = rivalPlayCards.size() - 1; i >= 0; i--) {
            Card card = rivalPlayCards.get(i);
            if (card.isTaunt() && !card.isStealth()){
                List<Integer> result = calcEatRivalCard(card);
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

    @Override
    protected int executeDiscoverChooseCard(Card ... cards) {
        return 0;
    }
}
