
package club.xiaojiawei.strategy;

import club.xiaojiawei.DeckStrategy;
import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.Player;
import club.xiaojiawei.status.War;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.enums.ConfigurationEnum.STRATEGY;

/**
 * 卡牌策略抽象类
 * @author 肖嘉威
 * @date 2022/11/29 17:29
 */
@Slf4j
@Component
public class DeckStrategyActuator {

    @Resource
    protected AtomicReference<BooleanProperty> isPause;

    @Resource
    private Properties scriptConfiguration;

    @Setter
    private DeckStrategy deckStrategy;

    public void changeCard() {
        Player me = War.getMe();
        Player rival = War.getRival();
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey(), STRATEGY.getDefaultValue()))) {
            try {
                log.info("执行换牌策略");
                HashSet<Card> copyHandCards = new HashSet<>(me.getHandArea().getCards());
                deckStrategy.executeChangeCard(copyHandCards);
                for (int i = 0; i < me.getHandArea().getCards().size(); i++) {
                    Card card = me.getHandArea().getCards().get(i);
                    if (!copyHandCards.contains(card)) {
                        log.info("换掉起始卡牌：【entityId:" + card.getEntityId() + "，entityName:" + card.getEntityName() + "，cardId:" + card.getCardId() + "】");
                        GameUtil.clickDiscover(i, me.getHandArea().cardSize());
                        SystemUtil.delayShortMedium();
                    }
                }
                log.info("执行换牌策略完毕");
            }finally {
                for (int i = 0; i < 3; i++) {
                    GameUtil.CONFIRM_RECT.lClick();
                    SystemUtil.delayShort();
                }
            }
        }
    }

    public void outCard(){
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey()))){
            try{
                log.info("执行出牌策略");
                log.info("回合开始可用水晶数：" + War.getMe().getUsableResource());
                deckStrategy.executeOutCard();
                log.info("执行出牌策略完毕");
            }finally {
                GameUtil.cancelAction();
                for (int i = 0; i < 3 && War.isMyTurn(); i++) {
                    SystemUtil.delayShortMedium();
                    GameUtil.END_TURN_RECT.lClick();
                }
            }
        }
    }

    public void discoverChooseCard(Card...cards){
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey(), STRATEGY.getDefaultValue()))) {
            SystemUtil.delay(1000);
            log.info("执行发现选牌策略");
            int index = deckStrategy.executeDiscoverChooseCard(cards);
            GameUtil.clickDiscover(index, War.getMe().getHandArea().cardSize());
            SystemUtil.delayShortMedium();
            Card card = cards[index];
            log.info("选择了：" + card.toSimpleString());
            log.info("执行发现选牌策略完毕");
        }
    }

}
