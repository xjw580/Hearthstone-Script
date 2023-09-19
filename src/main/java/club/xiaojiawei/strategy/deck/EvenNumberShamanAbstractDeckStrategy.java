package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.entity.BaseCard;
import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.area.SetasideArea;
import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
     * ### 偶数萨
     * # 职业：萨满祭司
     * # 模式：狂野模式
     * #
     * # 2x (0) 图腾之力    EX1_244
     * # 2x (0) 图腾潮涌    ULD_171
     * # 2x (2) 冰霜撕咬    AV_259
     * # 1x (2) 即兴演奏    JAM_013
     * # 2x (2) 异教低阶牧师  SCH_713
     * # 2x (2) 深海融合怪   TSC_069
     * # 2x (2) 火舌图腾    EX1_565
     * # 2x (2) 石雕凿刀    REV_917
     * # 1x (2) 笔记能手    SCH_236
     * # 2x (2) 阴燃电鳗    GIL_530
     * # 2x (2) 风怒      CS2_039
     * # 2x (2) 驻锚图腾    TSC_922
     * # 2x (4) 分裂战斧    ULD_413
     * # 1x (4) 锻石师     REV_921
     * # 1x (6) 吉恩·格雷迈恩 GIL_692
     * # 2x (6) 深渊魔物    OG_028
     * # 2x (10) 图腾巨像   REV_838
     * #
     * AAEBAaoIBM30AuHMA7HZBPTyBQ0zvgaU7wKdowPapQP5kQT6tASywQSG1ASq2QS95QTBngbQngYAAA==
     * #
     * # 想要使用这副套牌，请先复制到剪贴板，然后在游戏中点击“新套牌”进行粘贴。
     */
    /**
     * 这个类的目的是为了节省内存，使用时才加载
     */
    static class EvenNumberShamanTemp {
        static final BaseCard 图腾之力 = new BaseCard("EX1_244");
        static final BaseCard 图腾潮涌 = new BaseCard("ULD_171");
        static final BaseCard 冰霜撕咬 = new BaseCard("AV_259");
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
        static final BaseCard 锻石师 = new BaseCard("REV_921");
        static final BaseCard 吉恩_格雷迈恩 = new BaseCard("GIL_692");
        static final BaseCard 深渊魔物 = new BaseCard("OG_028");
        static final BaseCard 图腾巨像 = new BaseCard("REV_838");
        static final BaseCard 末日预言者 = new BaseCard("NEW1_021");
    }

    @Override
    protected boolean executeChangeCard(Card card, int index) {
        if (cardEquals(card, 深海融合怪)
                || cardEquals(card, 石雕凿刀)
                || cardEquals(card, 阴燃电鳗)
                || cardEquals(card, 驻锚图腾)){
            return false;
        }
        if (findByCardId(myHandCards, 石雕凿刀) != -1
                &&
                (cardEquals(card, 锻石师) || cardEquals(card, 火舌图腾) || cardEquals(card, 图腾之力) || cardEquals(card, 图腾潮涌))
        ){
            return false;
        }
        return true;
    }

    @Override
    protected void executeOutCard() {
        isUsePower = false;
        dealResource();
        boolean throughWall = true;
        //        算斩杀
        int allAtc = calcEnablePlayTotalAtc(myPlayCards);
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
            if (!myPlayArea.getHero().isExhausted() && myPlayArea.getHero().getAtc() > 0){
                if (findTauntCard(rivalPlayCards) == -1){
                    int index;
                    if (calcEnableTotalAtc(myPlayCards) >= calcRivalHeroBlood() && canPointRivalHero()){
                        myHeroPointToRivalHero();
                    }else if ((index = findMaxAtcByBlood(rivalPlayCards, myPlayArea.getHero().getAtc())) != -1){
                        myHeroPointToRivalPlay(index);
                    }else if (canPointRivalHero()){
                        myHeroPointToRivalHero();
                    }
                }
            }
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
            if (!me.getPlayArea().isFull()){
                Card card = myHandCards.get(i);
                if (card.getCost() <= resource() && card.getCardType() == MINION && !card.isBattlecry()){
                    if (myHandPointToMyPlay(i, myPlayCards.size())){
                        decreaseResource(card.getCost());
                    }else {
                        break;
                    }
                }
            }else {
                break;
            }
        }
        if (log.isDebugEnabled()){
            log.debug("myPlayCards:" + myPlayCards);
            log.debug("myHandCards:" + myHandCards);
        }
    }
    private boolean isUsePower;
    private int resource(){
        return me.getResources() + me.getTempResources() - me.getResourcesUsed();
    };

    private void decreaseResource(int num){
        log.info("当前可用水晶数：" + resource());
    }
    private void dealResource(){
        switch (resource()){
            case 1 -> dealOneResource();
            case 2 -> {
                int index = findByCardId(myHandCards, COIN);
                if (index != -1){
                    myHandPointToMyPlay(index);
                    decreaseResource(-1);
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

    private void dealZeroResource(){
        if (!War.isMyTurn() || isPause.get().get()){
            return;
        }
        //        巨像
        int index = findByCardId(myHandCards, 图腾巨像);
        if (index != -1){
            if (myHandCards.get(index).getCost() == 0){
                myHandPointToMyPlay(index, myPlayCards.size());
                index = findByCardId(myHandCards, 图腾巨像);
                if (index != -1){
                    myHandPointToMyPlay(index, myPlayCards.size());
                }
            }
        }
//        魔物
        if ((index = findByCardId(myHandCards, 深渊魔物)) != -1){
            int cost = myHandCards.get(index).getCost();
            if (cost == 0){
                myHandPointToMyPlay(index, myPlayCards.size());
                if ((index = findByCardId(myHandCards, 深渊魔物)) != -1){
                    myHandPointToMyPlay(index, myPlayCards.size());
                }
            }
        }
//        释放零费法术
        if (calcCardRaceCount(myPlayCards, CardRaceEnum.TOTEM) >= 3){
            //            武器攻击
            if (!myPlayArea.getHero().isExhausted() && cardEquals(myPlayArea.getWeapon(), 石雕凿刀) && findTauntCard(rivalPlayCards) == -1){
                int i;
                if (calcEnableTotalAtc(myPlayCards) >= calcRivalHeroBlood() && canPointRivalHero()){
                    myHeroPointToRivalHero();
                } else if ((i = findMaxAtcByBlood(rivalPlayCards, myPlayArea.getHero().getAtc())) != -1){
                    myHeroPointToRivalPlay(i);
                }else if (canPointRivalHero()){
                    myHeroPointToRivalHero();
                }
            }
            if ((index = findByCardId(myHandCards, 图腾潮涌)) != -1 && myHandCards.get(index).getCost() <= resource()){
                myHandPointToMyPlayNoPlace(index);
                if ((index = findByCardId(myHandCards, 图腾潮涌)) != -1){
                    myHandPointToMyPlayNoPlace(index);
                }
            }
//            图腾之力
            if ((index = findByCardId(myHandCards, 图腾之力)) != -1){
                myHandPointToMyPlayNoPlace(index);
                if ((index = findByCardId(myHandCards, 图腾之力)) != -1){
                    myHandPointToMyPlayNoPlace(index);
                }
            }
        }
    }
    private void dealOneResource(){
        if (resource() < 1 || !War.isMyTurn() || isPause.get().get()){
            return;
        }
        if (!isUsePower && !myPlayArea.isFull()){
            clickMyPower();
            isUsePower = true;
            decreaseResource(1);
            return;
        }
//        巨像
        int index = findByCardId(myHandCards, 图腾巨像);
        if (index != -1 && myHandCards.get(index).getCost() == 1 && myHandPointToMyPlay(index)){
            decreaseResource(1);
            return;
        }
//        魔物
        if ((index = findByCardId(myHandCards, 深渊魔物)) != -1 && myHandCards.get(index).getCost() == 1 && myHandPointToMyPlay(index)){
            decreaseResource(1);
        }
    }
    private void dealTwoResource(){
        if (resource() < 2 || !War.isMyTurn() || isPause.get().get()){
            return;
        }
        int index;
//        风怒
        if ((index = findByCardId(myHandCards, 风怒)) != -1){
            int playIndex = findMaxAtcByGEAtkNotWindFury(myPlayCards, 4);
            if (playIndex != -1){
                Card card = myHandCards.get(index);
                myHandPointToMyPlayNoPlace(index, playIndex);
                if (log.isDebugEnabled()){
                    log.debug("出牌：" + card.getCardId());
                }
                decreaseResource(card.getCost());
                return;
            }
        }
//        刀
        if ((index = findByCardId(myHandCards, 石雕凿刀)) != -1){
            Card card = myHandCards.get(index);
            if (myPlayArea.getWeapon() != null && findTauntCard(rivalPlayCards) == -1 && !myPlayArea.getHero().isExhausted()){
                if (calcEnableTotalAtc(myPlayCards) >= calcRivalHeroBlood() && canPointRivalHero()){
                    myHeroPointToRivalHero();
                }
                if ((index = findMaxAtcByBlood(rivalPlayCards, myPlayArea.getHero().getAtc())) != -1){
                    myHeroPointToRivalPlay(index);
                }else if (canPointRivalHero()){
                    myHeroPointToRivalHero();
                }
            }
            if (myPlayArea.getWeapon() == null){
                myHandPointToMyPlayNoPlace(index);
            }
            if (log.isDebugEnabled()){
                log.debug("出牌：REV_917");
            }
            decreaseResource(card.getCost());
            if (findTauntCard(rivalPlayCards) == -1 && !myPlayArea.getHero().isExhausted()){
                if ((index = findMaxAtcByBlood(rivalPlayCards, myPlayArea.getHero().getAtc())) != -1){
                    myHeroPointToRivalPlay(index);
                }else if (canPointRivalHero()){
                    myHeroPointToRivalHero();
                }
                return;
            }
        }
//        驻锚图腾
        if ((index = findByCardId(myHandCards, 驻锚图腾)) != -1 && myHandPointToMyPlay(index, myPlayCards.size())){
            if (log.isDebugEnabled()){
                log.debug("出牌：TSC_922");
            }
            decreaseResource(2);
            return;
        }
//        火舌
        if ((index = findByCardId(myHandCards, 火舌图腾)) != -1 && myHandPointToMyPlay(index, (myPlayCards.size() + 1) >> 1)){
            if (log.isDebugEnabled()){
                log.debug("出牌：EX1_565");
            }
            decreaseResource(2);
            return;
        }
//        电鳗
        if ((index = findByCardId(myHandCards, 阴燃电鳗)) != -1){
            if (calcEnableTotalAtc(myHandCards) + 2 >= calcRivalHeroBlood() && myHandPointToMyPlayThenPointToRivalHero(index, myPlayCards.size())){
                if (log.isDebugEnabled()){
                    log.debug("出牌：GIL_530");
                }
                decreaseResource(2);
                return;
            }
            int rivalIndex = findMaxAtcByBlood(rivalPlayCards, 2);
            if (rivalIndex != -1){
                Card card = rivalPlayCards.get(rivalIndex);
                if (!card.isCantBeTargetedBySpells() && !card.isStealth() && myHandPointToMyPlayThenPointToRivalPlay(index, myPlayCards.size(), rivalIndex)){
                    if (log.isDebugEnabled()){
                        log.debug("出牌：GIL_530");
                    }
                    decreaseResource(2);
                    return;
                }
            }else if ((rivalIndex = findMaxAtcByBlood(rivalPlayCards, 1)) != -1){
                Card card = rivalPlayCards.get(rivalIndex);
                if (!card.isCantBeTargetedBySpells() && !card.isStealth() && myHandPointToMyPlayThenPointToRivalPlay(index, myPlayCards.size(), rivalIndex)){
                    if (log.isDebugEnabled()){
                        log.debug("出牌：GIL_530");
                    }
                    decreaseResource(2);
                    return;
                }
            }else if (myHandPointToMyPlayThenPointToRivalHero(index, myPlayCards.size())){
                if (log.isDebugEnabled()){
                    log.debug("出牌：GIL_530");
                }
                decreaseResource(2);
                return;
            }
        }
        //        冰霜撕咬
        if ((index = findByCardId(myHandCards, 冰霜撕咬)) != -1){
            Card card = myHandCards.get(index);
            if (calcEnablePlayTotalAtc(myHandCards) + 3 >= calcRivalHeroBlood() && canPointRivalHero()){
                myHandPointToRivalHero(index);
                if (log.isDebugEnabled()){
                    log.debug("出牌：AV_259");
                }
                decreaseResource(card.getCost());
                return;
            }
            int rivalIndex = findMaxAtcByBlood(rivalPlayCards, 3);
            if (rivalIndex != -1){
                Card rivalCard = rivalPlayCards.get(rivalIndex);
                if (!rivalCard.isCantBeTargetedBySpells() && !rivalCard.isStealth()){
                    myHandPointToRivalPlay(index, rivalIndex);
                    if (log.isDebugEnabled()){
                        log.debug("出牌：AV_259");
                    }
                    decreaseResource(card.getCost());
                    return;
                }
            }
        }
        int i;
        //        融合怪
        if ((index = findByCardId(myHandCards, 深海融合怪)) != -1 && (i = findCardByCardRace(myPlayCards, CardRaceEnum.TOTEM, CardRaceEnum.ALL, CardRaceEnum.PET)) != -1 && myHandPointToMyPlayThenPointToMyPlay(index, myPlayCards.size(), i)){
            SetasideArea setasideArea = me.getSetasideArea();
            List<Card> cards = setasideArea.getCards();
            i = 0;
            for (int j = cards.size() - 1; j >= cards.size() - 3; j--) {
                Card card = cards.get(j);
                if (!card.isBattlecry()){
                    i = cards.size() - 1 - j;
                }
                if (card.getCardRace() == CardRaceEnum.TOTEM){
                    i = cards.size() - 1 - j;
                    break;
                }
            }
            clickFloatCard(getFloatCardClearanceForThreeCard(), getFloatCardFirstCardPosForThreeCard(), i);
            if (log.isDebugEnabled()){
                log.debug("出牌：TSC_069");
            }
            decreaseResource(2);
            SystemUtil.delay(ACTION_INTERVAL + 1000);
            return;
        }
        //        低阶牧师
        if ((index = findByCardId(myHandCards, 异教低阶牧师)) != -1 && myHandPointToMyPlay(index, myPlayCards.size())){
            if (log.isDebugEnabled()){
                log.debug("出牌：SCH_713");
            }
            decreaseResource(2);
            return;
        }
//        即兴演奏
        if ((index = findByCardId(myHandCards, 即兴演奏)) != -1 && myPlayCards.size() > 0){
            if (findCountByBlood(myPlayCards, 1) < 2){
                int pointIndex = findNotExhaustedCard(myPlayCards);
                if (pointIndex == -1 || myPlayCards.get(pointIndex).isCantBeTargetedBySpells()){
                    pointIndex = 0;
                }
                if (!myPlayCards.get(pointIndex).isCantBeTargetedBySpells()){
                    Card card = myHandCards.get(index);
                    myHandPointToMyPlayNoPlace(index, pointIndex);
                    if (log.isDebugEnabled()){
                        log.debug("出牌：JAM_013");
                    }
                    decreaseResource(card.getCost());
                    return;
                }
            }
        }
//        笔记能手
        if ((index = findByCardId(myHandCards, 笔记能手)) != -1 && myHandPointToMyPlay(index, myPlayCards.size())){
            if (log.isDebugEnabled()){
                log.debug("出牌：SCH_236");
            }
            decreaseResource(2);
            return;
        }
        //        冰霜撕咬
        if ((index = findByCardId(myHandCards, 冰霜撕咬)) != -1){
            int rivalIndex = findMaxAtcByBlood(rivalPlayCards, 2);
            if (rivalIndex != -1){
                Card rivalCard = rivalPlayCards.get(rivalIndex);
                Card card = myHandCards.get(index);
                if (!rivalCard.isCantBeTargetedBySpells() && !rivalCard.isStealth()){
                    myHandPointToRivalPlay(index, rivalIndex);
                    if (log.isDebugEnabled()){
                        log.debug("出牌：AV_259");
                    }
                    decreaseResource(card.getCost());
                    return;
                }
            }
        }
        dealOneResource();
    }
    private void dealThreeResource(){
        if (resource() < 3 || !War.isMyTurn() || isPause.get().get()){
            return;
        }
        dealTwoResource();
        dealOneResource();
    }
    private void dealFourResource(){
        if (resource() < 4 || !War.isMyTurn() || isPause.get().get()){
            return;
        }
        int index;
//        分裂战斧
        if ((index = findByCardId(myHandCards, 分裂战斧)) != -1 && (calcCardRaceCount(myPlayCards, CardRaceEnum.TOTEM) >= 2 || findByCardId(myPlayCards, 图腾巨像) != -1)){
            dealZeroResource();
            myHandPointToMyPlayNoPlace(index);
            SystemUtil.delayMedium();
            if (findTauntCard(rivalPlayCards) == -1 && !myPlayArea.getHero().isExhausted()){
                if (calcEnableTotalAtc(myPlayCards) >= calcRivalHeroBlood() && canPointRivalHero()){
                    myHeroPointToRivalHero();
                }
                if ((index = findMaxAtcByBlood(rivalPlayCards, myPlayArea.getHero().getAtc())) != -1){
                    myHeroPointToRivalPlay(index);
                }else if (canPointRivalHero()){
                    myHeroPointToRivalHero();
                }
            }
            decreaseResource(4);
            return;
        }
//        锻石师
        if ((index = findByCardId(myHandCards, 锻石师)) != -1 &&myHandPointToMyPlay(index, myPlayCards.size())){
            decreaseResource(4);
            return;
        }
        dealTwoResource();
        dealTwoResource();
    }
    private void dealFiveResource(){
        if (resource() < 5 || !War.isMyTurn() || isPause.get().get()){
            return;
        }
        dealFourResource();
        dealThreeResource();
    }
    private void dealSixResource(){
        if (resource() < 6 || !War.isMyTurn() || isPause.get().get()){
            return;
        }
        int index = findByCardId(myHandCards, 吉恩_格雷迈恩);
        if (index != -1 && myHandPointToMyPlay(index, myPlayCards.size())){
            decreaseResource(6);
            return;
        }
        dealFiveResource();
        dealFourResource();
    }
    private void dealSevenResource(){
        if (resource() < 7 || !War.isMyTurn() || isPause.get().get()){
            return;
        }
        dealSixResource();
        dealFiveResource();
    }
    private void dealEightResource(){
        if (resource() < 8 || !War.isMyTurn() || isPause.get().get()){
            return;
        }
        dealSevenResource();
        dealSixResource();
    }
    private void dealNineResource(){
        if (resource() < 9 || !War.isMyTurn() || isPause.get().get()){
            return;
        }
        dealEightResource();
        dealSevenResource();
    }
    private void dealTenResource(){
        if (resource() < 10 || !War.isMyTurn() || isPause.get().get()){
            return;
        }
        dealNineResource();
        dealEightResource();
    }

}
