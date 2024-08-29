
package club.xiaojiawei.strategy;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.extra.AlgorithmDeckStrategy;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.GameRationStaticData.CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION;
import static club.xiaojiawei.data.ScriptStaticData.GAME_RECT;
import static club.xiaojiawei.enums.ConfigurationEnum.STRATEGY;

/**
 * 卡牌策略抽象类
 * @author 肖嘉威
 * @date 2022/11/29 17:29
 */
@Slf4j
public abstract class AbstractDeckStrategy extends AlgorithmDeckStrategy {

    @Resource
    protected AtomicReference<BooleanProperty> isPause;
    @Resource
    protected Properties scriptConfiguration;
    @Resource
    private GameUtil gameUtil;

    public void changeCard() {
        assign();
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey()))){
            try {
                log.info("执行换牌策略");
                double clearance ,firstCardPos;
                if (myHandCards.size() == 3){
                    clearance  = getFloatCardClearanceForThreeCard();
                    firstCardPos = getFloatCardFirstCardPosForThreeCard();
                }else {
                    clearance  = getFloatCardClearanceForFourCard();
                    firstCardPos = getFloatCardFirstCardPosForFourCard();
                }
                SystemUtil.updateGameRect();
                int size = Math.min(myHandCards.size(), 4);
                for (int index = 0; index < size; index++) {
                    Card card = myHandCards.get(index);
                    if (executeChangeCard(card, index)){
                        clickFloatCard(clearance, firstCardPos, index);
                        log.info("换掉起始卡牌：【entityId:" + card.getEntityId() + "，entityName:" + card.getEntityName() + "，cardId:" + card.getCardId() + "】");
                    }
                }
                log.info("执行换牌策略完毕");
            }finally {
                SystemUtil.updateGameRect();
                //        点击确认按钮
                mouseUtil.leftButtonClick(
                        ((GAME_RECT.right + GAME_RECT.left) >> 1) + RandomUtil.getRandom(-10, 10),
                        (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5, 5))
                );
            }
        }
    }

    public void outCard(){
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey()))){
            try{
                log.info("执行出牌策略");
                if (log.isDebugEnabled()){
                    log.debug("我方手牌：" + myHandCards);
                    log.debug("我方战场：" + myPlayCards);
                    log.debug("我方英雄：" + myPlayArea.getHero());
                    log.debug("我方武器：" + myPlayArea.getWeapon());
                    log.debug("我方技能：" + myPlayArea.getPower());
                }
                log.info("回合开始可用水晶数：" + calcMyUsableResource());
                if (Objects.equals(War.getRival().getGameId(), "SBBaoXue#31568")){
                    gameUtil.surrender();
                    return;
                }
                executeOutCard();
                log.info("执行出牌策略完毕");
            }finally {
                MouseUtil.gameCancel();
                clickTurnOverButton();
            }
        }
    }
    public void discoverChooseCard(Card...cards){
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey()))){
            SystemUtil.delay(1000);
            log.info("执行发现选牌策略");
            int index = executeDiscoverChooseCard(cards);
            clickFloatCard(getFloatCardClearanceForThreeCard(), getFloatCardFirstCardPosForThreeCard(), index);
            Card card = cards[index];
            log.info("选择了：【entityId:" + card.getEntityId() + "，entityName:" + card.getEntityName() + "，cardId:" + card.getCardId() + "】");
            log.info("执行发现选牌策略完毕");
        }
    }

    /**
     * 执行换牌策略
     * @param card
     * @return 返回true表示换掉该牌
     */
    protected abstract boolean executeChangeCard(Card card, int index);

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
