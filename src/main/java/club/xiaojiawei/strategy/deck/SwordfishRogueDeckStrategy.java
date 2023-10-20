package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import org.springframework.stereotype.Component;

/**
 * 剑鱼贼
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/20 20:13
 */
@Component
public class SwordfishRogueDeckStrategy extends AbstractDeckStrategy {
    @Override
    protected boolean executeChangeCard(Card card, int index) {
        return false;
    }

    @Override
    protected void executeOutCard() {

    }

    @Override
    protected int executeDiscoverChooseCard(Card... cards) {
        return 0;
    }
}
