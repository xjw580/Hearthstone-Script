
package club.xiaojiawei.strategy;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.Player;
import club.xiaojiawei.status.War;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.extern.slf4j.Slf4j;

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
public abstract class AbstractDeckStrategy {

    protected static AtomicReference<BooleanProperty> isPause = new AtomicReference<>(new SimpleBooleanProperty(true));

    private static Properties scriptConfiguration = new Properties();

    public AbstractDeckStrategy(AtomicReference<BooleanProperty> isPause, Properties scriptConfiguration) {
        AbstractDeckStrategy.isPause = isPause;
        AbstractDeckStrategy.scriptConfiguration = scriptConfiguration;
    }

    protected Player me;

    protected Player rival;

    public void changeCard() {
        me = War.getMe();
        rival = War.getRival();
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey(), STRATEGY.getDefaultValue()))) {
            try {
                log.info("执行换牌策略");
                HashSet<Card> copyHandCards = new HashSet<>(me.getHandArea().getCards());
                executeChangeCard(copyHandCards);
                for (int i = 0; i < me.getHandArea().getCards().size(); i++) {
                    Card card = me.getHandArea().getCards().get(i);
                    if (!copyHandCards.contains(card)) {
                        log.info("换掉起始卡牌：【entityId:" + card.getEntityId() + "，entityName:" + card.getEntityName() + "，cardId:" + card.getCardId() + "】");
                        GameUtil.clickDiscover(i, me.getHandArea().cardSize());
                        SystemUtil.delayShort();
                    }
                }
                log.info("执行换牌策略完毕");
            }finally {
                GameUtil.CONFIRM_RECT.lClick();
            }
        }
    }

    public void outCard(){
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey()))){
            try{
                log.info("执行出牌策略");
                log.info("回合开始可用水晶数：" + War.getMe().getUsableResource());
                executeOutCard();
                log.info("执行出牌策略完毕");
            }finally {
                GameUtil.cancelAction();
                GameUtil.END_TURN_RECT.lClick();
            }
        }
    }

    public void discoverChooseCard(Card...cards){
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey(), STRATEGY.getDefaultValue()))) {
            SystemUtil.delay(1000);
            log.info("执行发现选牌策略");
            int index = executeDiscoverChooseCard(cards);
            GameUtil.clickDiscover(index, me.getHandArea().cardSize());
            Card card = cards[index];
            log.info("选择了：" + card.toSimpleString());
            log.info("执行发现选牌策略完毕");
        }
    }

    /**
     * 执行换牌策略
     * @param cards
     * @return 返回true表示换掉该牌
     */
    protected abstract void executeChangeCard(HashSet<Card> cards);

    /**
     * 执行出牌策略
     */
    protected abstract void executeOutCard();

    /**
     * 执行发现选牌
     * @param cards
     * @return 返回0~2的数字
     */
    protected abstract int executeDiscoverChooseCard(Card...cards);

}
