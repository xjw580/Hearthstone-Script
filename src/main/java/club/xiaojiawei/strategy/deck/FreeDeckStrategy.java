package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


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
        for (int i = myHandCards.size() - 1; i >= 0; i--) {
            myHandPointToMyPlay(i);
        }
        boolean cleanTaunt = (cleanTaunt() || cleanTaunt());
        if (cleanTaunt){
            cleanBuff();
            cleanDanger();
            cleanNormal();
            allAtcRivalHero();
        }
        for (int i = myHandCards.size() - 1; i >= 0; i--) {
            myHandPointToMyPlay(i);
        }
        if (calcMyUsableResource() >= 2){
            clickPower();
        }
    }

    @Override
    protected int executeDiscoverChooseCard(Card ... cards) {
        return 0;
    }
}
