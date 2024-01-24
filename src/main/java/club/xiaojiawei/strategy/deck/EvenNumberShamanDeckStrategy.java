package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.bean.BaseCard;
import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static club.xiaojiawei.data.CommonBaseDeck.*;
import static club.xiaojiawei.data.ScriptStaticData.COIN;
import static club.xiaojiawei.enums.CardTypeEnum.MINION;
import static club.xiaojiawei.strategy.deck.EvenNumberShamanDeckStrategy.EvenNumberShaman.*;

/**
 * 偶数萨
 * @author 肖嘉威
 * @date 2023/7/8 22:02
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class EvenNumberShamanDeckStrategy extends AbstractDeckStrategy{

    static class EvenNumberShaman {
        public static final BaseCard 图腾之力 = new BaseCard("EX1_244");
        public static final BaseCard 图腾潮涌 = new BaseCard("ULD_171");
        public static final BaseCard 冰霜撕咬 = new BaseCard("AV_259");
        public static final BaseCard 图腾魔像 = new BaseCard("AT_052");
        public static final BaseCard 海象人图腾师 = new BaseCard("WON_081");
        public static final BaseCard 即兴演奏 = new BaseCard("JAM_013");
        public static final BaseCard 异教低阶牧师 = new BaseCard("SCH_713");
        public static final BaseCard 深海融合怪 = new BaseCard("TSC_069");
        public static final BaseCard 石雕凿刀 = new BaseCard("REV_917");
        public static final BaseCard 笔记能手 = new BaseCard("SCH_236");
        public static final BaseCard 阴燃电鳗 = new BaseCard("GIL_530");
        public static final BaseCard 风怒 = new BaseCard("CS2_039");
        public static final BaseCard 分裂战斧 = new BaseCard("ULD_413");
        public static final BaseCard 图腾团聚 = new BaseCard("WON_091");
        public static final BaseCard 锻石师 = new BaseCard("REV_921");
        public static final BaseCard 吉恩_格雷迈恩 = new BaseCard("GIL_692");
        public static final BaseCard 深渊魔物 = new BaseCard("OG_028");
        public static final BaseCard 图腾巨像 = new BaseCard("REV_838");
        public static final BaseCard 远古图腾 = new BaseCard("TTN_710");
        public static final BaseCard 可靠陪伴 = new BaseCard("WW_027");
    }

    @Override
    protected boolean executeChangeCard(Card card, int index) {
        if (
                cardContains(card, 石雕凿刀)
                || cardContains(card, 阴燃电鳗)
                || cardContains(card, 驻锚图腾)
                || cardContains(card, 锻石师)
                || cardContains(card, 图腾潮涌)
                || cardContains(card, 图腾魔像)
                || cardContains(card, 海象人图腾师)
                || cardContains(card, 可靠陪伴)
        ){
            return false;
        }
        if (
                findByCardId(myHandCards, 石雕凿刀) != -1
                &&
                (cardContains(card, 火舌图腾) || cardContains(card, 图腾之力))
        ){
            return false;
        }
        return true;
    }

    @Override
    protected void executeOutCard() {
        calcKillHero();
        dealResource();
        calcKillHero();
        boolean cleanTaunt = (cleanTaunt() || cleanTaunt());
        if (cleanTaunt){
            cleanBuff();
            cleanDanger();
            double plusRivalAtcWeight = 0D, minusLazyLevel = 0D;
            if (calcCardBlood(myPlayArea.getHero()) - calcRivalTotalAtc() < 5){
                log.info("形势危急，全力解场");
                plusRivalAtcWeight = 5D;
                minusLazyLevel = -1.5D;
            }
            if (Objects.equals(rivalPlayArea.getHero().getCardId(), "HERO_02fbp") && rivalPlayArea.getPower().getCost() < 2){
                log.info("解场优先级提高");
                cleanNormal(1.3D, 1.35D + plusRivalAtcWeight, 0.5D + minusLazyLevel);
            }else {
                cleanNormal(1.3D, 1.35D + plusRivalAtcWeight, 0.7D + minusLazyLevel);
            }
            dealWeapon();
            dealZeroResource();
            allAtcRivalHero();
        }
        dealResource();
        for (int i = myHandCards.size() - 1; i >= 0; i--) {
            Card card = myHandCards.get(i);
            if (card.getCardType() == MINION){
                if (calcMyUsableResource() >= myHandCards.get(i).getCost()){
                    myHandPointToMyPlay(i);
                    MouseUtil.gameCancel();
                }
            }
        }
        if (cleanTaunt){
            allAtcRivalHero();
        }
        dealWeapon();
        clickPower();
        for (Card card : exhaustedCard) {
            card.setExhausted(false);
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
        if (log.isDebugEnabled()){
            log.debug("Resource:" + calcMyUsableResource());
        }
        switch (calcMyUsableResource()){
            case 0 -> dealZeroResource();
            case 1 -> dealOneResource();
            case 2 -> dealTwoResource();
            case 3 -> dealThreeResource();
            case 4 -> dealFourResource();
            case 5 -> dealFiveResource();
            case 6 -> dealSixResource();
            case 7 -> dealSevenResource();
            case 8 -> dealEightResource();
            case 9 -> dealNineResource();
            default -> dealTenResource();
        }
        dealZeroResource();
    }
    int index;
    int other;
    private boolean deal图腾巨像(int maxCost){
        index = findByCardId(myHandCards, 图腾巨像);
        if (index != -1 && myHandCards.get(index).getCost() <= maxCost){
            return myHandPointToMyPlay(index);
        }
        return false;
    }
    private boolean deal深渊魔物(int maxCost){
        index = findByCardId(myHandCards, 深渊魔物);
        if (index != -1 && myHandCards.get(index).getCost() <= maxCost){
            return myHandPointToMyPlay(index);
        }
        return false;
    }
    private int calcMyAllTotalAtc(){
        return calcMyTotalAtc() + calcCardCount(myHandCards, 图腾潮涌) * 2 * calcCardRaceCount(myPlayCards, CardRaceEnum.TOTEM, true);
    }
    private boolean dealWeapon(){
        log.debug("尝试武器攻击");
        if (canMove(myPlayArea.getHero()) && calcMyHeroAtc() > 0){
            log.debug("可以攻击");
            if ((other = findTauntCard(rivalPlayCards)) == -1){
                log.debug("没有嘲讽");
                if (calcCardRaceCount(myPlayCards, CardRaceEnum.TOTEM, true) * calcCardCount(myHandCards, 图腾潮涌) + calcMyAllTotalAtc() >= calcRivalHeroBlood()){
                    log.debug("可以斩杀，打脸");
                    return myHeroPointToRivalHero();
                } else if ((other = findMaxAtcByBlood(rivalPlayCards, calcMyHeroAtc(), calcMyHeroBlood() - 1, true)) != -1){
                    log.debug("可以解场");
                    return myHeroPointToRivalPlay(other);
                }else if (!cardContains(myPlayArea.getWeapon(), 石雕凿刀) || !myPlayArea.isFull()){
                    log.debug("兜底：打脸");
                    return myHeroPointToRivalHero();
                }
            }else if (calcCardBlood(rivalPlayCards.get(other))  <= calcMyHeroAtc()){
                log.debug("解嘲讽怪");
                return myHeroPointToRivalPlay(other);
            }
        }
        return false;
    }
    private void dealZeroResource(){
        if (!canExecute(0)){
            return;
        }
        if ((index = findByCardId(myHandCards, 远古图腾)) !=-1 && myHandPointToMyPlay(index)){
            dealZeroResource();
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
        if (
                calcCardRaceCount(myPlayCards, CardRaceEnum.TOTEM, false) > 2
                || calcCardRaceCount(myPlayCards, CardRaceEnum.TOTEM, true) * calcCardCount(myHandCards, 图腾潮涌) + calcMyAllTotalAtc() >= calcRivalHeroBlood()
        ){
            //            武器攻击
            if (cardContains(myPlayArea.getWeapon(), 石雕凿刀)){
                dealWeapon();
            }
            if ((index = findByCardId(myHandCards, 图腾潮涌)) != -1){
                clickPower();
                if (myHandPointToNoPlace(index)){
                    dealZeroResource();
                    return;
                }
            }
            if ((index = findByCardId(myHandCards, 图腾之力)) != -1){
                clickPower();
                if (myHandPointToNoPlace(index)){
                    dealZeroResource();
                }
            }
        }
    }

    @Override
    protected boolean clickPower() {
        boolean result = super.clickPower();
        if (cardContains(myPlayArea.getPower(), "HERO_02bp2") || cardContains(myPlayArea.getPower(), "HERO_02fbp2")){
            SystemUtil.delay(500);
            clickFloatCard(getFloatCardClearanceForFourCard(), getFloatCardFirstCardPosForFourCard(), 3);
        }
        return result;
    }

    private void dealOneResource(){
        if (!canExecute(1)){
            return;
        }
        if (clickPower()){
            dealZeroResource();
            return;
        }
        dealZeroResource();
        deal图腾巨像(1);
        deal深渊魔物(1);
    }
    private void dealTwoResource(){
        if (!canExecute(2)){
            return;
        }
        if ((index = findByCardId(myHandCards, COIN)) != -1 && findByCost(myHandCards, 2) != -1){
            if (myHandPointToNoPlace(index)){
                dealThreeResource();
                return;
            }
        }
        if (calcMyAllTotalAtc() + 2 >= calcRivalHeroBlood() && (index = findByCardId(myHandCards, 可靠陪伴)) != -1 && (other = findNotExhaustedCard(myPlayCards)) != -1 && myHandPointToMyPlayNoPlace(index, other)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 阴燃电鳗)) != -1){
            if (calcMyAllTotalAtc() + 2 >= calcRivalHeroBlood() && myHandPointToMyPlayThenPointToRivalHero(index, myPlayCards.size())){
                dealZeroResource();
                return;
            }
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
                    dealZeroResource();
                    return;
                }
            }else if (calcCardBlood(myPlayArea.getWeapon()) == 1 && canMove(myPlayArea.getHero())){
                dealWeapon();
                if (myHandPointToNoPlace(index)){
                    dealZeroResource();
                    return;
                }
            }
        }
        if (calcMyUsableResource() > 2 && (index = findByCardId(myHandCards, 驻锚图腾)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 阴燃电鳗)) != -1
                && (other = findMaxAtcByBlood(rivalPlayCards, 2, true)) != -1
                && myHandPointToMyPlayThenPointToRivalPlay(index, myPlayCards.size(), other)
        ){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 可靠陪伴)) != -1 && (other = findCardByCardRace(myPlayCards, CardRaceEnum.TOTEM)) != -1 && myHandPointToMyPlayNoPlace(index, other)){
            dealZeroResource();
            return;
        }
        if (myPlayCards.size() > 2 && (index = findByCardId(myHandCards, 火舌图腾)) != -1 && myHandPointToMyPlay(index, (myPlayCards.size() + 1) >> 1)){
            dealZeroResource();
            return;
        }
        if (myPlayCards.size() < 5 && (index = findByCardId(myHandCards, 海象人图腾师)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 可靠陪伴)) != -1 && (other = findNotExhaustedCard(myPlayCards)) != -1 && myHandPointToMyPlayNoPlace(index, other)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 图腾魔像)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return;
        }
        if (myPlayCards.size() > 1 && (index = findByCardId(myHandCards, 火舌图腾)) != -1 && myHandPointToMyPlay(index, (myPlayCards.size() + 1) >> 1)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 可靠陪伴)) != -1 && !myPlayCards.isEmpty() && myHandPointToMyPlayNoPlace(index, 0)){
            dealZeroResource();
            return;
        }
        if (myPlayCards.size() < 6 && (index = findByCardId(myHandCards, 海象人图腾师)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 阴燃电鳗)) != -1 && myHandPointToMyPlayThenPointToRivalHero(index, myPlayCards.size())){
            dealZeroResource();
            return;
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
            if (canSpellPointedByRival(rivalPlayArea.getHero()) && calcMyPlayAtc() + (3 + calcMySpellPower()) >= calcRivalHeroBlood() && myHandPointToRivalHeroNoPlace(index)){
                dealZeroResource();
                return;
            }
            int rivalIndex = findMaxAtcByBlood(rivalPlayCards, 3 + calcMySpellPower(), false);
            if (rivalIndex != -1 && myHandPointToRivalPlayNoPlace(index, rivalIndex)){
                dealZeroResource();
                return;
            }
        }
        if ((index = findByCardId(myHandCards, 深海融合怪)) != -1 && (other = findCardByCardRace(myPlayCards, CardRaceEnum.TOTEM, CardRaceEnum.ALL, CardRaceEnum.PET)) != -1 && myHandPointToMyPlayThenPointToMyPlay(index, myPlayCards.size(), other)){
            SystemUtil.delay(ACTION_INTERVAL + 1000);
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 异教低阶牧师)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return;
        }
        if ((index = findByCardId(myHandCards, 风怒)) != -1){
            int playIndex = findMaxAtcByGEAtkNotWindFury(myPlayCards, 4);
            if (playIndex != -1 && myHandPointToMyPlayNoPlace(index, playIndex)){
                dealZeroResource();
                return;
            }
        }
        if ((index = findByCardId(myHandCards, 火舌图腾)) != -1 && myHandPointToMyPlay(index, (myPlayCards.size() + 1) >> 1)){
            dealZeroResource();
            return;
        }
        if (!myPlayCards.isEmpty() && (index = findByCardId(myHandCards, 即兴演奏)) != -1){
            if ((other = findCountByBlood(myPlayCards, calcMySpellPower() + 1)) + 1 <= findCountByBlood(rivalPlayCards, calcMySpellPower() + 1) || other == 0){
                int pointIndex = findNotExhaustedCard(myPlayCards);
                if (pointIndex == -1 || canSpellPointedByMe(myPlayCards.get(pointIndex))){
                    pointIndex = findCanSpellPointedByMe(myPlayCards);
                }
                if (myHandPointToMyPlayNoPlace(index, pointIndex)){
                    SystemUtil.delay(2000);
                    dealZeroResource();
                    return;
                }
            }
        }
        if ((index = findByCardId(myHandCards, 笔记能手)) != -1 && myHandPointToMyPlay(index)){
            return;
        }
        if ((index = findByCardId(myHandCards, 冰霜撕咬)) != -1){
            int rivalIndex = findMaxAtcByBlood(rivalPlayCards, 2 + calcMySpellPower(), false);
            if (rivalIndex != -1 && myHandPointToRivalPlayNoPlace(index, rivalIndex)){
                dealZeroResource();
                return;
            }
        }
        if ((index = findByCardId(myHandCards, 海象人图腾师)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return;
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
    private boolean dealFourResourcePure(){
        if ((index = findByCardId(myHandCards, 分裂战斧)) != -1){
            int tempIndex = index;
            if (canMove(myPlayArea.getHero()) && calcMyAllTotalAtc() + 3 >= calcRivalHeroBlood()){
                if (calcMyUsableResource() > 4 && myPlayCards.size() < 3){
                    clickPower();
                }
                dealZeroResource();
                int count = calcCardRaceCount(myPlayCards, CardRaceEnum.TOTEM, false) - 1;
                myHandPointToNoPlace(tempIndex);
                if (findByCardId(myPlayCards, 驻锚图腾) == -1){
                    SystemUtil.delay(500 * count);
                }else {
                    SystemUtil.delay(2000 * count);
                }
                deal分裂战斧Exhausted();
                dealWeapon();
                return true;
            }else if (
                    (calcCardRaceCount(myPlayCards, CardRaceEnum.TOTEM, false) >= 2 && myPlayCards.size() < 5)
                            || (findByCardId(myPlayCards, 图腾巨像) != -1 && myPlayCards.size() < 4)
            ) {
                dealZeroResource();
                myHandPointToNoPlace(tempIndex);
                deal分裂战斧Exhausted();
                return true;
            }
        }
        if (
                (index = findByCardId(myHandCards, 图腾团聚)) != -1
                && (( myPlayCards.size() <= 3 && findByCardId(myPlayCards, 驻锚图腾) != -1) || ( myPlayCards.size() >= rivalPlayCards.size() && myPlayCards.size() <= 2))
        ){
            if (calcMyUsableResource() > 5 && (other = findByCardId(myHandCards, 驻锚图腾)) != -1){
                myHandPointToMyPlay(other);
            }
            if (myHandPointToNoPlace(index)){
                if (findByCardId(myPlayCards, 驻锚图腾) != -1){
                    SystemUtil.delay(5000);
                }
                dealZeroResource();
                return true;
            }
        }
        if ((index = findByCardId(myHandCards, 锻石师)) != -1 && myHandPointToMyPlay(index)){
            dealZeroResource();
            return true;
        }
        if (
                (index = findByCardId(myHandCards, 图腾团聚)) != -1
                && (myPlayCards.size() == 3 && calcMyUsableResource() == 4 || myPlayCards.size() < 3)
        ){
            if (calcMyUsableResource() > 5 && (other = findByCardId(myHandCards, 驻锚图腾)) != -1){
                myHandPointToMyPlay(other);
            }
            if (myHandPointToNoPlace(index)){
                if (findByCardId(myPlayCards, 驻锚图腾) != -1){
                    SystemUtil.delay(5000);
                }
                dealZeroResource();
                return true;
            }
        }
        return false;
    }
    private void dealFourResource(){
        if (!canExecute(4)){
            return;
        }
        if (dealFourResourcePure()){
            return;
        }
        dealTwoResource();
        dealTwoResource();
    }
    private final List<Card> exhaustedCard = new ArrayList<>();
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
        dealFourResourcePure();
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
        Card power = myPlayArea.getPower();
        return War.isMyTurn() && !isPause.get().get() && (findByLECost(myHandCards, cost) != -1 || !power.isExhausted() && power.getCost() <= cost);
    }

    private void deal分裂战斧Exhausted(){
        Card card = null;
        for (Card myPlayCard : myPlayCards) {
            if (card == null) {
                card = myPlayCard;
            } else if (card.getCardRace() == CardRaceEnum.TOTEM && Objects.equals(card.getCardId(), myPlayCard.getCardId())) {
                myPlayCard.setExhausted(true);
                exhaustedCard.add(myPlayCard);
                card = null;
            }else {
                card = myPlayCard;
            }
        }
    }

    private void calcKillHero(){
//        火舌、冰霜撕咬、加攻法术、刀
        int damageOf火舌图腾 = 0, damageOf冰霜撕咬 = Math.min(calcCardCount(myHandCards, 冰霜撕咬), calcMyUsableResource() >> 1) * (3 + calcMySpellPower()), countOf火舌图腾 = 0;
        if (!myPlayCards.isEmpty()){
            countOf火舌图腾 = Math.min(calcCardCount(myHandCards, 火舌图腾), calcMyUsableResource() >> 1);
            if (countOf火舌图腾 == 1){
                damageOf火舌图腾 = Math.min(myPlayCards.size(), 2) * 2;
            }else if (countOf火舌图腾 == 2){
                damageOf火舌图腾 = (myPlayCards.size() > 2? 4 : 2) * 2;
            }
        }
        int damageOfWeapon = 0;
        if (calcMyHeroAtc() < 3 && calcMyUsableResource() >= 4){
            if (findByCardId(myHandCards, 分裂战斧) != -1){
                damageOfWeapon = 3 - calcMyHeroAtc();
            }
        } else if (calcMyHeroAtc() == 0 && calcMyUsableResource() >= 2){
            if (findByCardId(myHandCards, 石雕凿刀) != -1){
                damageOfWeapon = 1;
            }
        }
        int mark = -1;
        if (damageOfWeapon > damageOf冰霜撕咬){
            if (damageOfWeapon > damageOf火舌图腾){
                mark = 0;
            }else if (damageOfWeapon < damageOf火舌图腾){
                mark = 1;
            }
        }else if (damageOfWeapon < damageOf冰霜撕咬){
            if (damageOf冰霜撕咬 > damageOf火舌图腾){
                mark = 2;
            }else if (damageOf冰霜撕咬 < damageOf火舌图腾){
                mark = 1;
            }
        }
        int rivalHeroBlood = calcRivalHeroBlood(), myTotalAtc = calcMyTotalAtc() + Math.max(Math.max(damageOf冰霜撕咬, damageOf火舌图腾), damageOfWeapon), tauntIndex = findTauntCard(rivalPlayCards);
        if (myTotalAtc >= rivalHeroBlood){
            if (tauntIndex == -1){
                switch (mark){
                    case 0:{
                        if (damageOfWeapon > 1){
                            myHandPointToNoPlace(findByCardId(myHandCards, 分裂战斧));
                        }else {
                            myHandPointToNoPlace(findByCardId(myHandCards, 石雕凿刀));
                        }
                    }
                    case 1:{
                        if (countOf火舌图腾 == 2){
                            myHandPointToMyPlay(findByCardId(myHandCards, 火舌图腾), 2);
                        }
                        myHandPointToMyPlay(findByCardId(myHandCards, 火舌图腾), 1);
                    }
                    case 2:{
                        if (myHandPointToRivalHeroNoPlace(findByCardId(myHandCards, 冰霜撕咬))){
                            myHandPointToRivalHeroNoPlace(findByCardId(myHandCards, 冰霜撕咬));
                        }
                    }
                }
                allAtcRivalHero();
            }else {
                if (cleanTaunt(1.3D, 2D, 0.01D)){
                    if (calcMyTotalAtc() + Math.max(damageOf冰霜撕咬, damageOf火舌图腾) > rivalHeroBlood){
                        switch (mark){
                            case 0:{
                                if (damageOfWeapon > 1){
                                    myHandPointToNoPlace(findByCardId(myHandCards, 分裂战斧));
                                }else {
                                    myHandPointToNoPlace(findByCardId(myHandCards, 石雕凿刀));
                                }
                            }
                            case 1:{
                                if (countOf火舌图腾 == 2){
                                    myHandPointToMyPlay(findByCardId(myHandCards, 火舌图腾), 2);
                                }
                                myHandPointToMyPlay(findByCardId(myHandCards, 火舌图腾), 1);
                            }
                            case 2:{
                                if (myHandPointToRivalHeroNoPlace(findByCardId(myHandCards, 冰霜撕咬))){
                                    myHandPointToRivalHeroNoPlace(findByCardId(myHandCards, 冰霜撕咬));
                                }
                            }
                        }
                        allAtcRivalHero();
                    }
                }
            }
        }
    }
}
