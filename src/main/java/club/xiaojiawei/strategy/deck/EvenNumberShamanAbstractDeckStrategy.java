package club.xiaojiawei.strategy.deck;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static club.xiaojiawei.data.ScriptStaticData.ROBOT;
import static club.xiaojiawei.enums.CardTypeEnum.MINION;
import static club.xiaojiawei.enums.CardTypeEnum.WEAPON;

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


    @Override
    protected void executeChangeCard(List<Card> myHandCards, float clearance , float firstCardPos) {
        int size = Math.min(myHandCards.size(), 4);
        for (int i = 0; i < size; i++) {
            Card card = myHandCards.get(i);
            if (!(card.getCost() == 2 && (card.getCardType() == MINION || card.getCardType() == WEAPON))){
                if (log.isDebugEnabled()){
                    log.debug("换掉" + card);
                }
                clickFloatCard(clearance, firstCardPos, i);
            }
        }
    }

    @Override
    protected void outCard() {
        isUsePower = false;
        resource = getUsableResource(me);
        dealResource();
        boolean throughWall = true;
        //        算斩杀
        int allAtc = calcTotalAtc(myPlayCards);
        int blood = calcRivalHeroBlood();
//        解嘲讽怪
        for (int i = rivalPlayCards.size() - 1; i >= 0; i--) {
            Card card = rivalPlayCards.get(i);
//            NEW1_021 末日也要解
            if ((card.isTaunt() || (card.getCardId() != null && card.getCardId().contains("NEW1_021") && blood > allAtc)) && !card.isStealth() && card.getCardType() == MINION && !card.isDormantAwakenConditionEnchant()){
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
                    if (calcTotalAtcUltimate(myPlayCards) >= calcRivalHeroBlood()){
                        myHeroPointToRivalHero();
                    }
                    if ((index = findMaxAtcByBlood(rivalPlayCards, myPlayArea.getHero().getAtc())) != -1){
                        myHeroPointToRivalPlay(index);
                    }else {
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
                if (card.getCost() <= resource && card.getCardType() == MINION && !card.isBattlecry()){
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
    private int resource;

    private void decreaseResource(int num){
        log.info("当前可用水晶数：" + (resource -= num));
    }
    private void dealResource(){
        switch (resource){
            case 1 -> dealOneResource();
            case 2 -> {
                int index = findByCardId(myHandCards, "COIN");
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
        //        巨像
        int index = findByCardId(myHandCards, "REV_838");
        if (index != -1){
            if (myHandCards.get(index).getCost() == 0){
                myHandPointToMyPlay(index, myPlayCards.size());
                index = findByCardId(myHandCards, "REV_838");
                if (index != -1){
                    myHandPointToMyPlay(index, myPlayCards.size());
                }
            }
        }
//        魔物
        if ((index = findByCardId(myHandCards, "OG_028")) != -1){
            int cost = myHandCards.get(index).getCost();
            if (cost == 0){
                myHandPointToMyPlay(index, myPlayCards.size());
                if ((index = findByCardId(myHandCards, "OG_028")) != -1){
                    myHandPointToMyPlay(index, myPlayCards.size());
                }
            }
        }
//        释放零费法术
        if (myPlayCards.size() >= 3){
            //            武器攻击
            if (!myPlayArea.getHero().isExhausted() && myPlayArea.getHero().getAtc() == 1 && findTauntCard(rivalPlayCards) == -1){
                if (calcTotalAtcUltimate(myPlayCards) >= calcRivalHeroBlood()){
                    myHeroPointToRivalHero();
                }
                int i;
                if ((i = findMaxAtcByBlood(rivalPlayCards, myPlayArea.getHero().getAtc())) != -1){
                    myHeroPointToRivalPlay(i);
                }else {
                    myHeroPointToRivalHero();
                }
            }
            if ((index = findByCardId(myHandCards, "ULD_171")) != -1){
                myHandPointToMyPlayNoPlace(index);
                if ((index = findByCardId(myHandCards, "ULD_171")) != -1){
                    myHandPointToMyPlayNoPlace(index);
                }
            }
//            图腾之力
            if ((index = findByCardId(myHandCards, "EX1_244")) != -1){
                myHandPointToMyPlayNoPlace(index);
                if ((index = findByCardId(myHandCards, "EX1_244")) != -1){
                    myHandPointToMyPlayNoPlace(index);
                }
            }
        }
    }
    private void dealOneResource(){
        if (resource < 1){
            return;
        }
        if (!isUsePower && !myPlayArea.isFull()){
            clickMyPower();
            isUsePower = true;
            decreaseResource(1);
            return;
        }
//        巨像
        int index = findByCardId(myHandCards, "REV_838");
        if (index != -1 && myHandCards.get(index).getCost() == 1 && myHandPointToMyPlay(index)){
            decreaseResource(1);
            return;
        }
//        魔物
        if ((index = findByCardId(myHandCards, "OG_028")) != -1 && myHandCards.get(index).getCost() == 1 && myHandPointToMyPlay(index)){
            decreaseResource(1);
        }
    }
    private void dealTwoResource(){
        if (resource < 2){
            return;
        }
        int index;
//        风怒
        if ((index = findByCardId(myHandCards, "CS2_039")) != -1){
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
        if ((index = findByCardId(myHandCards, "REV_917")) != -1){
            if (myPlayArea.getHero().getAtc() > 0 && findTauntCard(rivalPlayCards) == -1 && !myPlayArea.getHero().isExhausted()){
                if (calcTotalAtcUltimate(myPlayCards) >= calcRivalHeroBlood()){
                    myHeroPointToRivalHero();
                }
                if ((index = findMaxAtcByBlood(rivalPlayCards, myPlayArea.getHero().getAtc())) != -1){
                    myHeroPointToRivalPlay(index);
                }else {
                    myHeroPointToRivalHero();
                }
            }
            myHandPointToMyPlayNoPlace(index);
            if (log.isDebugEnabled()){
                log.debug("出牌：REV_917");
            }
            decreaseResource(2);
            if (findTauntCard(rivalPlayCards) == -1 && !myPlayArea.getHero().isExhausted()){
                if ((index = findMaxAtcByBlood(rivalPlayCards, myPlayArea.getHero().getAtc())) != -1){
                    myHeroPointToRivalPlay(index);
                }else {
                    myHeroPointToRivalHero();
                }
                return;
            }
        }
//        驻锚图腾
        if ((index = findByCardId(myHandCards, "TSC_922")) != -1 && myHandPointToMyPlay(index, myPlayCards.size())){
            if (log.isDebugEnabled()){
                log.debug("出牌：TSC_922");
            }
            decreaseResource(2);
            return;
        }
//        火舌
        if ((index = findByCardId(myHandCards, "EX1_565")) != -1 && myHandPointToMyPlay(index, myPlayCards.size() >> 1)){
            if (log.isDebugEnabled()){
                log.debug("出牌：EX1_565");
            }
            decreaseResource(2);
            return;
        }
//        电鳗
        if ((index = findByCardId(myHandCards, "GIL_530")) != -1){
            if (calcTotalAtc(myHandCards) + 2 >= calcRivalHeroBlood() && myHandPointToMyPlayThenPointToRivalHero(index, myPlayCards.size())){
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
        if ((index = findByCardId(myHandCards, "AV_259")) != -1){
            if (calcTotalAtc(myHandCards) + 3 >= calcRivalHeroBlood()){
                Card card = myHandCards.get(index);
                myHandPointToRivalHero(index);
                if (log.isDebugEnabled()){
                    log.debug("出牌：AV_259");
                }
                decreaseResource(card.getCost());
                return;
            }
            int rivalIndex = findMaxAtcByBlood(rivalPlayCards, 3);
            if (rivalIndex != -1){
                Card card = rivalPlayCards.get(rivalIndex);
                if (!card.isCantBeTargetedBySpells() && !card.isStealth()){
                    myHandPointToRivalPlay(index, rivalIndex);
                    if (log.isDebugEnabled()){
                        log.debug("出牌：AV_259");
                    }
                    decreaseResource(card.getCost());
                    return;
                }
            }
        }
        //        融合怪
        if ((index = findByCardId(myHandCards, "TSC_069")) != -1 && myPlayCards.size() > 0 && myHandPointToMyPlayThenPointToMyPlay(index, myPlayCards.size(), myPlayCards.size() - 1)){
            clickFloatCard(getFloatCardClearanceForThreeCard(), getFloatCardFirstCardPosForThreeCard(), 0);
            if (log.isDebugEnabled()){
                log.debug("出牌：TSC_069");
            }
            decreaseResource(2);
            ROBOT.delay(ACTION_INTERVAL + 1000);
            return;
        }
        //        低阶牧师
        if ((index = findByCardId(myHandCards, "SCH_713")) != -1 && myHandPointToMyPlay(index, myPlayCards.size())){
            if (log.isDebugEnabled()){
                log.debug("出牌：SCH_713");
            }
            decreaseResource(2);
            return;
        }
//        即兴演奏
        if ((index = findByCardId(myHandCards, "JAM_013")) != -1 && myPlayCards.size() > 0){
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
        if ((index = findByCardId(myHandCards, "SCH_236")) != -1 && myHandPointToMyPlay(index, myPlayCards.size())){
            if (log.isDebugEnabled()){
                log.debug("出牌：SCH_236");
            }
            decreaseResource(2);
            return;
        }
        //        冰霜撕咬
        if ((index = findByCardId(myHandCards, "AV_259")) != -1){
            int rivalIndex = findMaxAtcByBlood(rivalPlayCards, 2);
            if (rivalIndex != -1){
                Card card = rivalPlayCards.get(rivalIndex);
                if (!card.isCantBeTargetedBySpells() && !card.isStealth()){
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
        if (resource < 3){
            return;
        }
        dealTwoResource();
        dealOneResource();
    }
    private void dealFourResource(){
        if (resource < 4){
            return;
        }
        int index;
//        分裂战斧
        if ((index = findByCardId(myHandCards, "ULD_413")) != -1 && myPlayCards.size() > 3 && myPlayArea.getHero().getAtc() <= 0){
            dealZeroResource();
            myHandPointToMyPlayNoPlace(index);
            if (findTauntCard(rivalPlayCards) == -1 && !myPlayArea.getHero().isExhausted()){
                if (calcTotalAtcUltimate(myPlayCards) >= calcRivalHeroBlood()){
                    myHeroPointToRivalHero();
                }
                if ((index = findMaxAtcByBlood(rivalPlayCards, myPlayArea.getHero().getAtc())) != -1){
                    myHeroPointToRivalPlay(index);
                }else {
                    myHeroPointToRivalHero();
                }
            }
            decreaseResource(4);
            return;
        }
//        锻石师
        if ((index = findByCardId(myHandCards, "REV_921")) != -1 &&myHandPointToMyPlay(index, myPlayCards.size())){
            decreaseResource(4);
            return;
        }
        dealTwoResource();
        dealTwoResource();
    }
    private void dealFiveResource(){
        if (resource < 5){
            return;
        }
        dealFourResource();
        dealThreeResource();
    }
    private void dealSixResource(){
        if (resource < 6){
            return;
        }
        int index = findByCardId(myHandCards, "GIL_692");
        if (index != -1 && myHandPointToMyPlay(index, myPlayCards.size())){
            decreaseResource(6);
            return;
        }
        dealFiveResource();
        dealFourResource();
    }
    private void dealSevenResource(){
        if (resource < 7){
            return;
        }
        dealSixResource();
        dealFiveResource();
    }
    private void dealEightResource(){
        if (resource < 8){
            return;
        }
        dealSevenResource();
        dealSixResource();
    }
    private void dealNineResource(){
        if (resource < 9){
            return;
        }
        dealEightResource();
        dealSevenResource();
    }
    private void dealTenResource(){
        if (resource < 10){
            return;
        }
        dealNineResource();
        dealEightResource();
    }
}
