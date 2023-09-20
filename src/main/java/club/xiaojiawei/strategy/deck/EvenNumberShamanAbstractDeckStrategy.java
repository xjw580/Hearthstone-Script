package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.bean.BaseCard;
import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static club.xiaojiawei.data.ScriptStaticData.COIN;
import static club.xiaojiawei.enums.CardTypeEnum.MINION;
import static club.xiaojiawei.strategy.deck.EvenNumberShamanAbstractDeckStrategy.EvenNumberShamanTemp.*;

/**
 * @author 肖嘉威
 * @date 2023/7/8 22:02
 */
@Component
@Slf4j
public class EvenNumberShamanAbstractDeckStrategy extends AbstractDeckStrategy{
    /**
     ### 偶数萨
     # 职业：萨满祭司
     # 模式：狂野模式
     #
     # 2x (0) 图腾之力
     # 2x (0) 图腾潮涌
     # 2x (2) 冰霜撕咬
     # 2x (2) 图腾魔像
     # 2x (2) 海象人图腾师
     # 1x (2) 深海融合怪
     # 2x (2) 火舌图腾
     # 2x (2) 石雕凿刀
     # 2x (2) 阴燃电鳗
     # 2x (2) 风怒
     # 2x (2) 驻锚图腾
     # 2x (4) 分裂战斧
     # 1x (4) 图腾团聚
     # 1x (4) 锻石师
     # 1x (6) 吉恩·格雷迈恩
     # 2x (6) 深渊魔物
     # 2x (10) 图腾巨像
     #
     AAEBAaoIBM30ArLBBLHZBOmhBg0zvgayFJTvAp2jA9qlA/mRBPq0BIbUBKrZBL3lBMGeBt+hBgAA
     #
     # 想要使用这副套牌，请先复制到剪贴板，然后在游戏中点击“新套牌”进行粘贴。

     /**
     * 这个类的目的是为了节省内存，使用时才加载
     */
    static class EvenNumberShamanTemp {
        static final BaseCard 图腾之力 = new BaseCard("EX1_244");
        static final BaseCard 图腾潮涌 = new BaseCard("ULD_171");
        static final BaseCard 冰霜撕咬 = new BaseCard("AV_259");
        static final BaseCard 图腾魔像 = new BaseCard("AT_052");
        static final BaseCard 海象人图腾师 = new BaseCard("WON_081");
        static final BaseCard 即兴演奏 = new BaseCard("JAM_013");
        static final BaseCard 异教低阶牧师 = new BaseCard("SCH_713");
        static final BaseCard 深海融合怪 = new BaseCard("TSC_069");
        static final BaseCard 火舌图腾 = new BaseCard("EX1_565");
        static final BaseCard 石雕凿刀 = new BaseCard("REV_917");
        static final BaseCard 笔记能手 = new BaseCard("SCH_236");
        static final BaseCard 阴燃电鳗 = new BaseCard("GIL_530");
        static final BaseCard 风怒 = new BaseCard("CS2_039");
        static final BaseCard 驻锚图腾 = new BaseCard("TSC_922");
        static final BaseCard 分裂战斧 = new BaseCard("ULD_413");
        static final BaseCard 图腾团聚 = new BaseCard("WON_091");
        static final BaseCard 锻石师 = new BaseCard("REV_921");
        static final BaseCard 吉恩_格雷迈恩 = new BaseCard("GIL_692");
        static final BaseCard 深渊魔物 = new BaseCard("OG_028");
        static final BaseCard 图腾巨像 = new BaseCard("REV_838");
        static final BaseCard 末日预言者 = new BaseCard("NEW1_021");
    }

    @Override
    protected boolean executeChangeCard(Card card, int index) {
        if (
                cardEquals(card, 石雕凿刀)
                || cardEquals(card, 阴燃电鳗)
                || cardEquals(card, 驻锚图腾)
                || cardEquals(card, 锻石师)
                || cardEquals(card, 图腾潮涌)
                || cardEquals(card, 图腾魔像)
                || cardEquals(card, 海象人图腾师)
        ){
            return false;
        }
        if (findByCardId(myHandCards, 石雕凿刀) != -1
                &&
                (cardEquals(card, 火舌图腾) || cardEquals(card, 图腾之力))
        ){
            return false;
        }
        return true;
    }

    @Override
    protected void executeOutCard() {
        dealResource();
        boolean throughWall = true;
        //        算斩杀
        int allAtc = calcMyAllTotalAtc();
        int blood = calcRivalHeroBlood();
//        解嘲讽怪
        for (int i = rivalPlayCards.size() - 1; i >= 0; i--) {
            Card card = rivalPlayCards.get(i);
//            NEW1_021 末日也要解
            if ((card.isTaunt() || (card.getCardId() != null && cardEquals(card, 末日预言者) && blood > allAtc)) && !card.isStealth() && card.getCardType() == MINION && !card.isDormantAwakenConditionEnchant()){
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
//            武器攻击
            dealWeapon();
            dealResource();
            if (allAtc >= blood){
                //        怪全部打脸
                for (int i = myPlayCards.size() - 1; i >= 0; i--) {
                    Card card = myPlayCards.get(i);
                    if (!card.isExhausted() && !card.isFrozen() && card.getAtc() > 0){
                        myPlayPointToRivalHero(i);
                        if (card.isWindFury() && card.getHealth() > 0){
                            myPlayPointToRivalHero(i);
                        }
                    }
                }
                return;
            }
            //        能走到这说明斩杀不了，按原计划进行
            //        解光环怪
            for (int i = rivalPlayCards.size() - 1; i >= 0; i--) {
                Card card = rivalPlayCards.get(i);
                if ((card.isAura() || card.isAdjacentBuff()) && !card.isStealth() && card.getCardType() == MINION && !card.isDormantAwakenConditionEnchant()){
                    List<Integer> result = calcFreeEatRivalTaunt(card);
                    if (result == null){
                        continue;
                    }
                    for (int j = result.size() - 2; j >= 0; j--) {
                        myPlayPointToRivalPlay(result.get(j), i);
                    }
                }
            }
//        怪解场或打脸
            for (int i = myPlayCards.size() - 1; i >= 0; i--) {
                Card card = myPlayCards.get(i);
                if (!card.isExhausted() && !card.isFrozen() && card.getAtc() > 0 && card.getCardType() == MINION){
                    int rivalIndex;
                    if (card.isAura()
                            || card.isAdjacentBuff()
                    ){
                        rivalIndex = calcMyCardFreeEat(card, false);
                    }else {
                        rivalIndex = calcMyCardFreeEat(card, true);
                    }
                    if (rivalIndex != -1){
                        myPlayPointToRivalPlay(i, rivalIndex);
                        if (card.isWindFury() && card.getHealth() > 0){
                            myPlayPointToRivalPlay(i, rivalIndex);
                        }
                    }else {
                        myPlayPointToRivalHero(i);
                        if (card.isWindFury() && card.getHealth() > 0){
                            myPlayPointToRivalHero(i);
                        }
                    }
                }
            }
            dealResource();
        }else {
            dealResource();
        }
//        出随从牌
        for (int i = myHandCards.size() - 1; i >= 0; i--) {
            Card card = myHandCards.get(i);
            if (card.getCardType() == MINION){
                myHandPointToMyPlay(i, myPlayCards.size());
            }
        }
        if (log.isDebugEnabled()){
            log.debug("myPlayCards:" + myPlayCards);
            log.debug("myHandCards:" + myHandCards);
        }
    }

    @Override
    protected int executeDiscoverChooseCard(Card ... cards) {
        int index = 0;
        for (int j = 0; j < Math.min(3, cards.length); j++) {
            Card card = cards[j];
            if (!card.isBattlecry()){
                index = cards.length - 1 - j;
            }
            if (card.getCardRace() == CardRaceEnum.TOTEM){
                index = cards.length - 1 - j;
                break;
            }
        }
        return index;
    }

    private void dealResource(){
        switch (getMyUsableResource()){
            case 1 -> dealOneResource();
            case 2 -> {
                int index = findByCardId(myHandCards, COIN);
                if (index != -1 && findByCost(myHandCards, 2) != -1){
                    myHandPointToMyPlay(index);
                    dealThreeResource();
                }else {
                    dealTwoResource();
                }
            }
            case 3 -> dealThreeResource();
            case 4 -> dealFourResource();
            case 5 -> dealFiveResource();
            case 6 -> dealSixResource();
            case 7 -> dealSevenResource();
            case 8 -> dealEightResource();
            case 9 -> dealNineResource();
            case 10 -> dealTenResource();
        }
        dealZeroResource();
    }
    int index;
    int otherIndex;
    private boolean deal图腾巨像(int maxCost){
        index = findByCardId(myHandCards, 图腾巨像);
        if (index != -1 && myHandCards.get(index).getCost() <= maxCost){
            if (myHandPointToMyPlay(index, myPlayCards.size())){
                return true;
            }
        }
        return false;
    }
    private boolean deal深渊魔物(int maxCost){
        index = findByCardId(myHandCards, 深渊魔物);
        if (index != -1 && myHandCards.get(index).getCost() <= maxCost){
            if (myHandPointToMyPlay(index, myPlayCards.size())){
                return true;
            }
        }
        return false;
    }
    private int calcMyAllTotalAtc(){
        return calcMyTotalAtc() + calcCardCount(myHandCards, 图腾潮涌) * 2 * calcCardRaceCount(myPlayCards, CardRaceEnum.TOTEM, true);
    }
    private boolean dealWeapon(){
        if (canMove(myPlayArea.getHero()) && calcMyHeroTotalAtc() > 0){
            if ((otherIndex = findTauntCard(rivalPlayCards)) == -1){
                if (calcMyAllTotalAtc() >= calcRivalHeroBlood()){
                    return myHeroPointToRivalHero();
                }else if ((otherIndex = findMaxAtcByBlood(rivalPlayCards, calcMyHeroTotalAtc(), calcMyHeroBlood() - 1, true)) != -1){
                    return myHeroPointToRivalPlay(otherIndex);
                }else {
                    return myHeroPointToRivalHero();
                }
            }else if (rivalPlayCards.get(otherIndex).getHealth() <= calcMyHeroTotalAtc()){
                return myHeroPointToRivalPlay(otherIndex);
            }
        }
        return false;
    }
    private void dealZeroResource(){
        if (!canExecute(0)){
            return;
        }
        if (deal图腾巨像(0)){
            dealZeroResource();
            return;
        }
        if (deal深渊魔物(0)){
            dealZeroResource();
            return;
        }
        //        释放零费法术
        if (calcCardRaceCount(myPlayCards, CardRaceEnum.TOTEM, false) > 2){
            //            武器攻击
            if (cardEquals(myPlayArea.getWeapon(), 石雕凿刀)){
                dealWeapon();
            }
            if ((index = findByCardId(myHandCards, 图腾潮涌)) != -1){
                if (myHandPointToNoPlace(index)){
                    dealZeroResource();
                    return;
                }
            }
            if ((index = findByCardId(myHandCards, 图腾之力)) != -1){
                if (myHandPointToNoPlace(index)){
                    dealZeroResource();
                }
            }
        }
    }
    private void dealOneResource(){
        if (!canExecute(1)){
            return;
        }
        dealZeroResource();
        if (clickPower()){
//            todo
            if (Objects.equals(myPlayArea.getPower().getCardId(), "HERO_02bp2")){
                SystemUtil.delay(500);
                clickFloatCard(getFloatCardClearanceForFourCard(), getFloatCardFirstCardPosForFourCard(), 0 );
            }
            dealZeroResource();
            return;
        }
        deal图腾巨像(1);
        deal深渊魔物(1);
    }
    private void dealTwoResource(){
        if (!canExecute(2)){
            return;
        }
        if ((index = findByCardId(myHandCards, 风怒)) != -1){
            int playIndex = findMaxAtcByGEAtkNotWindFury(myPlayCards, 6);
            if (playIndex != -1 && myHandPointToMyPlayNoPlace(index, playIndex)){
                dealZeroResource();
                return;
            }
        }
        if ((index = findByCardId(myHandCards, 石雕凿刀)) != -1){
            if (myPlayArea.getWeapon() == null){
                if (myHandPointToNoPlace(index)){
                    dealWeapon();
                    dealZeroResource();
                    return;
                }
            }else if (myPlayArea.getWeapon().getHealth() == 1 && canMove(myPlayArea.getHero())){
                dealWeapon();
                if (myHandPointToNoPlace(index)){
                    dealZeroResource();
                    return;
                }
            }
        }
        if ((index = findByCardId(myHandCards, 驻锚图腾)) != -1 && myHandPointToMyPlay(index) && getMyUsableResource() > 2){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 海象人图腾师)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 图腾魔像)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 火舌图腾)) != -1 && myHandPointToMyPlay(index, (myPlayCards.size() + 1) >> 1)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 阴燃电鳗)) != -1){
            if (calcMyAllTotalAtc() + 2 >= calcRivalHeroBlood() && myHandPointToMyPlayThenPointToRivalHero(index, myPlayCards.size())){
                dealZeroResource();
                return;
            }
            int rivalIndex = findMaxAtcByBlood(rivalPlayCards, 2, true);
            if (rivalIndex != -1 && myHandPointToMyPlayThenPointToRivalPlay(index, myPlayCards.size(), rivalIndex)){
                dealZeroResource();
                return;
            }else if (myHandPointToMyPlayThenPointToRivalHero(index, myPlayCards.size())){
                dealZeroResource();
                return;
            }
        }
        if ((index = findByCardId(myHandCards, 驻锚图腾)) != -1){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 风怒)) != -1){
            int playIndex = findMaxAtcByGEAtkNotWindFury(myPlayCards, 5);
            if (playIndex != -1 && myHandPointToMyPlayNoPlace(index, playIndex)){
                dealZeroResource();
                return;
            }
        }
        if ((index = findByCardId(myHandCards, 冰霜撕咬)) != -1){
            if (canPointToRivalHero() && calcMyPlayTotalAtc() + 3 >= calcRivalHeroBlood() && myHandPointToRivalHeroNoPlace(index)){
                dealZeroResource();
                return;
            }
            int rivalIndex = findMaxAtcByBlood(rivalPlayCards, 3, false);
            if (rivalIndex != -1 && myHandPointToRivalPlayNoPlace(index, rivalIndex)){
                dealZeroResource();
                return;
            }
        }
        if ((index = findByCardId(myHandCards, 深海融合怪)) != -1 && (otherIndex = findCardByCardRace(myPlayCards, CardRaceEnum.TOTEM, CardRaceEnum.ALL, CardRaceEnum.PET)) != -1 && myHandPointToMyPlayThenPointToMyPlay(index, myPlayCards.size(), otherIndex)){
            SystemUtil.delay(ACTION_INTERVAL + 1000);
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 异教低阶牧师)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 即兴演奏)) != -1 && !myPlayCards.isEmpty()){
            if (findCountByBlood(myPlayCards, 1) == 0){
                int pointIndex = findNotExhaustedCard(myPlayCards);
                if (pointIndex == -1 || canPointedToMe(myPlayCards.get(pointIndex))){
                    pointIndex = 0;
                }
                if (!canPointedToMe(myPlayCards.get(pointIndex)) && myHandPointToMyPlayNoPlace(index, pointIndex)){
                    dealZeroResource();
                    return;
                }
            }
        }
        if ((index = findByCardId(myHandCards, 笔记能手)) != -1 && myHandPointToMyPlay(index)){
            return;
        }
        if ((index = findByCardId(myHandCards, 冰霜撕咬)) != -1){
            int rivalIndex = findMaxAtcByBlood(rivalPlayCards, 2, false);
            if (rivalIndex != -1 && myHandPointToRivalPlayNoPlace(index, rivalIndex)){
                dealZeroResource();
                return;
            }
        }
        dealOneResource();
    }
    private void dealThreeResource(){
        if (!canExecute(3)){
            return;
        }
        dealTwoResource();
        dealOneResource();
    }
    private void dealFourResource(){
        if (!canExecute(4)){
            return;
        }
        if ((index = findByCardId(myHandCards, 分裂战斧)) != -1
                &&
                (
                        (calcCardRaceCount(myPlayCards, CardRaceEnum.TOTEM, false) >= 2 && myPlayCards.size() < 5)
                        || (canMove(myPlayArea.getHero()) && calcMyAllTotalAtc() + 3 >= calcRivalHeroBlood())
                        || findByCardId(myPlayCards, 图腾巨像) != -1
                )
        ){
            dealZeroResource();
            myHandPointToNoPlace(index);
            SystemUtil.delayMedium();
            dealWeapon();
            return;
        }
        if ((index = findByCardId(myHandCards, 锻石师)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 图腾团聚)) != -1 && myHandPointToNoPlace(index)){
            dealZeroResource();
            return;
        }
        dealTwoResource();
        dealTwoResource();
    }
    private void dealFiveResource(){
        if (!canExecute(5)){
            return;
        }
        dealFourResource();
        dealThreeResource();
    }
    private void dealSixResource(){
        if (!canExecute(6)){
            return;
        }
        if ((index = findByCardId(myHandCards, 吉恩_格雷迈恩)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return;
        }
        dealFiveResource();
        dealFourResource();
    }
    private void dealSevenResource(){
        if (!canExecute(7)){
            return;
        }
        dealSixResource();
        dealFiveResource();
    }
    private void dealEightResource(){
        if (!canExecute(8)){
            return;
        }
        dealSevenResource();
        dealSixResource();
    }
    private void dealNineResource(){
        if (!canExecute(9)){
            return;
        }
        dealEightResource();
        dealSevenResource();
    }
    private void dealTenResource(){
        if (!canExecute(10)){
            return;
        }
        dealNineResource();
        dealEightResource();
    }

    private boolean canExecute(int cost){
        return War.isMyTurn() && !isPause.get().get() && findByLECost(myHandCards, cost) != -1;
    }
}
