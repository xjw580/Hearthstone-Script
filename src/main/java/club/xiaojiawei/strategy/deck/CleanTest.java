package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.bean.area.PlayArea;
import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.enums.CardTypeEnum;
import club.xiaojiawei.strategy.AbstractDeckStrategy;

import java.util.ArrayList;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/4 19:11
 */
public class CleanTest extends AbstractDeckStrategy {
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

    public static void main(String[] args) {
        CleanTest cleanTest = new CleanTest();
        cleanTest.rivalPlayCards = new ArrayList<>(){{
//            add(new Card(){{setAtc(4);setHealth(4);setEntityId("1");}});
//            add(new Card(){{setAtc(2);setHealth(3);setEntityId("2");}});
//            add(new Card(){{setAtc(8);setHealth(8);setEntityId("3");}});
//            add(new Card(){{setAtc(3);setHealth(6);setEntityId("4");setCardRace(CardRaceEnum.PIRATE);}});
//            add(new Card(){{setAtc(0);setHealth(2);setEntityId("5");setCardRace(CardRaceEnum.PIRATE);setCardId("NEW1_027");setCardType(CardTypeEnum.MINION);}});
//            add(new Card(){{setAtc(4);setHealth(3);setEntityId("6");setCardRace(CardRaceEnum.PIRATE);}});
            add(new Card(){{setAtc(3);setHealth(5);setEntityId("7");setCardRace(CardRaceEnum.PIRATE);}});
        }};
        cleanTest.rivalPlayArea = new PlayArea();
        cleanTest.rivalPlayArea.setHero(new Card(){{setAtc(3);setHealth(10);setEntityId("0");}});
        cleanTest.myPlayArea = new PlayArea();
        cleanTest.myPlayArea.setHero(new Card(){{setAtc(3);setHealth(1);setEntityId("00");}});
        cleanTest.myPlayCards = new ArrayList<>(){{
            add(new Card(){{setAtc(5);setHealth(5);setEntityId("01");}});
//            add(new Card(){{setAtc(3);setHealth(3);setEntityId("02");}});
//            add(new Card(){{setAtc(2);setHealth(8);setEntityId("03");}});
//            add(new Card(){{setAtc(5);setHealth(5);setEntityId("04");}});
//            add(new Card(){{setAtc(4);setHealth(4);setEntityId("05");}});
//            add(new Card(){{setAtc(1);setHealth(1);setEntityId("06");}});
//            add(new Card(){{setAtc(1);setHealth(3);setEntityId("07");}});
//            add(new Card(){{setAtc(1);setHealth(3);setEntityId("07");}});
        }};
        cleanTest.cleanNormal();
//        cleanTest.cleanTaunt();
//        cleanTest.cleanBuff();
    }
}
