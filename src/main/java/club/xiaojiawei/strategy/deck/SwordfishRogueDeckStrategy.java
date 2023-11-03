package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.bean.BaseCard;
import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

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
        public static final BaseCard 冷血 = new BaseCard("CS2_073");
        public static final BaseCard 南海船工 = new BaseCard("CS2_146");
        public static final BaseCard 奖品掠夺者 = new BaseCard("DMF_519");
        public static final BaseCard 旗标骷髅 = new BaseCard("NX2_006");
        public static final BaseCard 海盗帕奇斯 = new BaseCard("CFM_637");
        public static final BaseCard 秘密通道 = new BaseCard("SCH_305");
        public static final BaseCard 鱼排斗士 = new BaseCard("TSC_963");
        public static final BaseCard 悦耳嘻哈 = new BaseCard("ETC_717");
        public static final BaseCard 洞穴探宝者 = new BaseCard("LOOT_033");
        public static final BaseCard 空降歹徒 = new BaseCard("DRG_056");
        public static final BaseCard 船载火炮 = new BaseCard("GVG_075");
        public static final BaseCard 剑鱼 = new BaseCard("TSC_086");
        public static final BaseCard 携刃信使 = new BaseCard("TSC_085");
        public static final BaseCard 恐怖海盗 = new BaseCard("NEW1_022");
        public static final BaseCard 狂暴邪翼蝠 = new BaseCard("YOD_032");
    }
    @Override
    protected boolean executeChangeCard(Card card, int index) {
        if (
                cardEquals(card, 南海船工)
                || cardEquals(card, 旗标骷髅)
                || cardEquals(card, 鱼排斗士)
                || cardEquals(card, 空降歹徒)
                || cardEquals(card, 船载火炮)
        ){
            return false;
        }
        if (cardEquals(card, 奖品掠夺者)){
            boolean exist = false;
            for (int i = 0; i < index; i++) {
                if (cardEquals(myHandCards.get(i), 奖品掠夺者)){
                    exist = true;
                    break;
                }
            }
            return exist;
        }
        if (
                cardEquals(card, 洞穴探宝者)
                || cardEquals(card, 剑鱼)
        ){
            boolean exist = false;
            for (int i = 0; i < index; i++) {
                if (
                        cardEquals(myHandCards.get(i), 洞穴探宝者)
                        || cardEquals(myHandCards.get(i), 剑鱼)
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

    public static void main(String[] args) {
        SwordfishRogueDeckStrategy swordfishRogueDeckStrategy = new SwordfishRogueDeckStrategy();
        swordfishRogueDeckStrategy.rivalPlayCards = new ArrayList<>(){{
            add(new Card(){{setAtc(2);setHealth(3);}});//0
            add(new Card(){{setAtc(2);setHealth(3);}});//1
            add(new Card(){{setAtc(2);setHealth(3);}});//2
            add(new Card(){{setAtc(3);setHealth(6);setCardRace(CardRaceEnum.PIRATE);}});//3
            add(new Card(){{setAtc(5);setHealth(3);setCardRace(CardRaceEnum.PIRATE);setCardId("NEW1_027");}});//4
            add(new Card(){{setAtc(4);setHealth(3);setCardRace(CardRaceEnum.PIRATE);setEntityId("3");}});//5
            add(new Card(){{setAtc(3);setHealth(4);setTaunt(true);setCardRace(CardRaceEnum.PIRATE);setEntityId("4");}});//6
        }};
        swordfishRogueDeckStrategy.myPlayCards = new ArrayList<>(){{
            add(new Card(){{setAtc(3);setHealth(1);}});//0
            add(new Card(){{setAtc(3);setHealth(1);}});//1
            add(new Card(){{setAtc(2);setHealth(2);setEntityId("1");}});//2
            add(new Card(){{setAtc(5);setHealth(5);setEntityId("2");}});//3
            add(new Card(){{setAtc(8);setHealth(8);}});//4
            add(new Card(){{setAtc(1);setHealth(1);}});//5
            add(new Card(){{setAtc(3);setHealth(4);}});//6
        }};
//        swordfishRogueDeckStrategy.cleanRivalPlay(swordfishRogueDeckStrategy.myPlayCards, 1.3D, 1.1D, 0);
//        swordfishRogueDeckStrategy.cleanPlay(1.2D, 1.3D);
    }
}
