package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.bean.PureCard;
import club.xiaojiawei.bean.Card;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static club.xiaojiawei.strategy.deck.SwordfishRogueDeckStrategy.SwordfishRogue.*;

/**
 * 剑鱼贼
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/20 20:13
 */
@Component
@Slf4j
public class SwordfishRogueDeckStrategy extends AbstractDeckStrategy {
    static class SwordfishRogue {
        public static final PureCard 冷血 = new PureCard("CS2_073");
        public static final PureCard 南海船工 = new PureCard("CS2_146");
        public static final PureCard 奖品掠夺者 = new PureCard("DMF_519");
        public static final PureCard 旗标骷髅 = new PureCard("NX2_006");
        public static final PureCard 海盗帕奇斯 = new PureCard("CFM_637");
        public static final PureCard 秘密通道 = new PureCard("SCH_305");
        public static final PureCard 鱼排斗士 = new PureCard("TSC_963");
        public static final PureCard 悦耳嘻哈 = new PureCard("ETC_717");
        public static final PureCard 洞穴探宝者 = new PureCard("LOOT_033");
        public static final PureCard 空降歹徒 = new PureCard("DRG_056");
        public static final PureCard 船载火炮 = new PureCard("GVG_075");
        public static final PureCard 剑鱼 = new PureCard("TSC_086");
        public static final PureCard 携刃信使 = new PureCard("TSC_085");
        public static final PureCard 恐怖海盗 = new PureCard("NEW1_022");
        public static final PureCard 狂暴邪翼蝠 = new PureCard("YOD_032");
    }
    @Override
    protected boolean executeChangeCard(Card card, int index) {
        if (
                cardContains(card, 南海船工)
                || cardContains(card, 旗标骷髅)
                || cardContains(card, 鱼排斗士)
                || cardContains(card, 空降歹徒)
                || cardContains(card, 船载火炮)
        ){
            return false;
        }
        if (cardContains(card, 奖品掠夺者)){
            boolean exist = false;
            for (int i = 0; i < index; i++) {
                if (cardContains(myHandCards.get(i), 奖品掠夺者)){
                    exist = true;
                    break;
                }
            }
            return exist;
        }
        if (
                cardContains(card, 洞穴探宝者)
                || cardContains(card, 剑鱼)
        ){
            boolean exist = false;
            for (int i = 0; i < index; i++) {
                if (
                        cardContains(myHandCards.get(i), 洞穴探宝者)
                        || cardContains(myHandCards.get(i), 剑鱼)
                ){
                    exist = true;
                    break;
                }
            }
            return exist;
        }
        return true;
    }

    @Override
    protected void executeOutCard() {

    }

    @Override
    protected int executeDiscoverChooseCard(Card... cards) {
        return 0;
    }

}
