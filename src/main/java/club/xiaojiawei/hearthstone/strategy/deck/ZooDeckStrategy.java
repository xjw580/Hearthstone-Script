package club.xiaojiawei.hearthstone.strategy.deck;

import club.xiaojiawei.hearthstone.entity.Card;
import club.xiaojiawei.hearthstone.entity.CardMes;
import club.xiaojiawei.hearthstone.entity.Player;
import club.xiaojiawei.hearthstone.entity.area.HandArea;
import club.xiaojiawei.hearthstone.entity.area.PlayArea;
import club.xiaojiawei.hearthstone.enums.CardTypeEnum;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.strategy.DeckStrategy;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static club.xiaojiawei.hearthstone.constant.GameConst.*;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:36
 */
@Slf4j
public class ZooDeckStrategy extends DeckStrategy {

    public ZooDeckStrategy() {
        super(Map.ofEntries(
                Map.entry("VAN_EX1_308", new CardMes("灵魂之火", "VAN_EX1_308", 2)),
                Map.entry("VAN_EX1_316", new CardMes("力量的代价", "VAN_EX1_316", 1)),
                Map.entry("VAN_CS2_188", new CardMes("叫嚣的中士", "VAN_CS2_188", 10)),
                Map.entry("VAN_EX1_004", new CardMes("年轻的女祭司", "VAN_EX1_004", 14)),
                Map.entry("VAN_EX1_405", new CardMes("持盾卫士", "VAN_EX1_405", 12)),
                Map.entry("VAN_EX1_319", new CardMes("烈焰小鬼", "VAN_EX1_319", 19)),
                Map.entry("VAN_CS2_189", new CardMes("精灵弓箭手", "VAN_CS2_189", 11)),
                Map.entry("VAN_CS2_065", new CardMes("虚空行者", "VAN_CS2_065", 16)),
                Map.entry("VAN_EX1_008", new CardMes("银色侍从", "VAN_EX1_008", 15)),
                Map.entry("VAN_EX1_162", new CardMes("恐狼前锋","VAN_EX1_162", 25)),
                Map.entry("VAN_EX1_393", new CardMes("阿曼尼狂战士", "VAN_EX1_393", 28)),
                Map.entry("VAN_NEW1_019", new CardMes("飞刀杂耍者", "VAN_NEW1_019", 29)),
                Map.entry("VAN_EX1_019", new CardMes("破碎残阳祭司", "VAN_EX1_019", 33)),
                Map.entry("VAN_EX1_093", new CardMes("阿古斯防御者", "VAN_EX1_093", 45)),
                Map.entry("VAN_EX1_046", new CardMes("黑铁矮人", "VAN_EX1_046", 44)),
                Map.entry("VAN_EX1_310", new CardMes("末日守卫", "VAN_EX1_310", 55)),
                Map.entry("COIN", new CardMes("幸运币", "COIN", 0))
                ));
    }

    @SneakyThrows
    public boolean simple(){
        Player rival = War.getRival();
        Player me = War.getMe();
        List<Card> handCards = me.getHandArea().getCards();
        List<Card> playCards = me.getPlayArea().getCards();
        List<Card> rivalPlayCards = rival.getPlayArea().getCards();
        int resources = me.getResources();
        for (int i = handCards.size() - 1; i >= 0; i--) {
            if (!me.getPlayArea().isFull()){
                Card card = handCards.get(i);
                if (card.getCost() <= resources){
                    resources -= card.getCost();
                    myHandPointToMyPlay(i, playCards.size(), me);
                    Thread.sleep(3000);
                }
            }else {
                break;
            }
        }
        if (resources >= 2){
            clickMyPower();
        }
        boolean throughWall = true;
//        解嘲讽怪
        for (int i = rivalPlayCards.size() - 1; i >= 0; i--) {
            Card card = rivalPlayCards.get(i);
            if (card.isTaunt() && !card.isStealth()){
                List<Integer> result = freeEatTaunt(playCards, card);
                if (result == null){
//                    过墙失败
                    throughWall = false;
                    continue;
                }
                for (int j = result.size() - 2; j >= 0; j--) {
                    Integer integer = result.get(j);
                    myPlayPointToRivalPlay(integer, i, me, rival);
                    Thread.sleep(3000);
                }
            }
        }
        if (throughWall){
            for (int i = playCards.size() - 1; i >= 0; i--) {
                Card card = playCards.get(i);
                if (!card.isExhausted() && card.getAtc() > 0 && !card.isFrozen()){
                    myPlayPointToRivalHero(i, me);
                    Thread.sleep(3000);
                }
            }
        }
        clickTurnOverButton();
        return false;
    }

    @Override
    public void afterIntoReplaceCardPhase(Object o) {
        Player me = War.getMe();
        if (me == null){
            return;
        }
        HandArea handArea = me.getHandArea();
        List<Card> cards = handArea.getCards();
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        float clearance = GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO, firstCardPos;
        if (cards.size() == 3){
            clearance *= CARD_HORIZONTAL_CLEARANCE_WHEN_THREE_CARD * (rect.bottom - rect.top);
            firstCardPos = (rect.left + rect.right >> 1) - (rect.bottom - rect.top) * FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_THREE_CARD * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        }else {
            clearance *= CARD_HORIZONTAL_CLEARANCE_WHEN_FOUR_CARD * (rect.bottom - rect.top);
            firstCardPos = (rect.left + rect.right >> 1) - (rect.bottom - rect.top) * FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_FOUR_CARD * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        }
        Card card;
        int oneCostCount = 0;
        for (int i = 0; i < cards.size(); i++) {
            card = cards.get(i);
//            只留费用小于等于2的
            if (card.getCost() > 2
//                    只留随从
                    || (card.getCardType() != CardTypeEnum.MINION && !Objects.equals(card.getCardId(), "COIN"))
//                    不留叫嚣
                    || Objects.equals(card.getCardId(), "VAN_CS2_188")
//                    不留持盾，如果没有恐狼或者飞刀
                    || (Objects.equals(card.getCardId(), "VAN_EX1_405") && findByCardId(cards, "VAN_EX1_162") == -1 && findByCardId(cards, "VAN_NEW1_019") == -1)
            ){
                clickFloatCard(clearance, firstCardPos, rect, i);
                SystemUtil.delayMedium();
            }
            if (card.getCost() == 1){
                oneCostCount++;
            }
        }
//        如果没有一张一费牌，则将恐狼换下去
        if (oneCostCount == 0){
            clickFloatCard(clearance, firstCardPos, rect, findByCardId(cards, "VAN_EX1_162"));
            SystemUtil.delayMedium();
        }
    }

    PlayArea myPlayArea;
    PlayArea rivalPlayArea;
    private Player me = War.getMe();
    private Player rival = War.getRival();
    private List<Card> myHandCards;
    private List<Card> myPlayCards;
    private List<Card> rivalPlayCards;
    private int coinIndex;
    private int index1, index2;
    private boolean usedPower;

    @SneakyThrows
    @Override
    public void afterIntoMyTurn() {
        if (!simple()){
            return;
        }
        me = War.getMe();
        rival = War.getRival();
        if (me == null || rival == null){
            return;
        }
        myPlayCards = me.getPlayArea().getCards();
        usedPower = false;
        rivalPlayArea = rival.getPlayArea();
        myPlayArea = me.getPlayArea();
        myHandCards = me.getHandArea().getCards();
        rivalPlayCards = rivalPlayArea.getCards();
        coinIndex = findByCardId(myHandCards, "COIN");
        log.info("我方本回合费用：" + me.getResources());
        log.info("我方手牌：" + myHandCards);
        log.info("我方战场：" + myPlayCards);
        log.info("对方生命值上限：" + rivalPlayArea.getHero().getHealth());
        log.info("对方护甲值：" + rivalPlayArea.getHero().getArmor());
        log.info("对方受到的总伤害：" + rivalPlayArea.getHero().getDamage());
        for (Card playCard : myPlayCards) {
            playCard.setExhausted(false);
        }
        dealResource();
        boolean throughWall = true;
//        解嘲讽怪
        for (int i = rivalPlayCards.size() - 1; i >= 0; i--) {
            Card card = rivalPlayCards.get(i);
            if (card.isTaunt() && !card.isStealth()){
                List<Integer> result = freeEatTaunt(myPlayCards, card);
                if (result == null){
//                    过墙失败
                    throughWall = false;
                    continue;
                }
                for (int j = result.size() - 2; j >= 0; j--) {
                    Integer integer = result.get(j);
                    myPlayPointToRivalPlay(integer, i, me, rival);
                    ROBOT.delay(ACTION_INTERVAL);
                }
            }
        }
        dealResource();
//        算斩杀
        int allAtc = calcAtc(myPlayCards);
        int blood = rivalPlayArea.getHero().getHealth() + rivalPlayArea.getHero().getArmor() - rivalPlayArea.getHero().getDamage();
        int soulFireCount = 0;
        if (throughWall){
            if (allAtc >= blood){
                //        怪全部打脸
                for (int i = myPlayCards.size() - 1; i >= 0; i--) {
                    Card card = myPlayCards.get(i);
                    if (!card.isExhausted() && !card.isFrozen() && card.getAtc() > 0){
                        myPlayPointToRivalHero(i, me);
                        ROBOT.delay(ACTION_INTERVAL);
                    }
                }
                return;
            }else {
                //        魂火数量
                for (Card myHandCard : myHandCards) {
                    if (Objects.equals(myHandCard.getCardId(), "VAN_EX1_308")){
                        soulFireCount++;
                    }
                }
                if (soulFireCount == 2 && myHandCards.size() == 2){
                    soulFireCount = 1;
                }
                if (soulFireCount * 4 + allAtc >= blood){
                    int index = findByCardId(myHandCards, "VAN_EX1_308");
                    myHandPointToRivalHero(index, me);
                    ROBOT.delay(ACTION_INTERVAL);
                    if (--soulFireCount > 0){
                        index = findByCardId(myHandCards, "VAN_EX1_308");
                        myHandPointToRivalHero(index, me);
                        ROBOT.delay(ACTION_INTERVAL);
                    }
                    if (soulFireCount <= 0){
                        //        怪全部打脸
                        for (int i = myPlayCards.size() - 1; i >= 0; i--) {
                            Card card = myPlayCards.get(i);
                            if (!card.isExhausted() && !card.isFrozen() && card.getAtc() > 0){
                                myHandPointToRivalHero(i, me);
                                ROBOT.delay(ACTION_INTERVAL);
                            }
                        }
                        return;
                    }
                }
            }
            //        能走到这说明斩杀不了，按原计划进行
            //        解光环怪
            for (int i = rivalPlayCards.size() - 1; i >= 0; i--) {
                Card card = rivalPlayCards.get(i);
                if ((card.isAura() || card.isAdjacentBuff()) && !card.isStealth()){
                    List<Integer> result = freeEatTaunt(myPlayCards, card);
                    if (result == null){
                        continue;
                    }
                    for (int j = result.size() - 2; j >= 0; j--) {
                        myPlayPointToRivalPlay(result.get(j), i, me, rival);
                        ROBOT.delay(ACTION_INTERVAL);
                    }
                }
            }
            dealResource();
//        怪解场或打脸
            for (int i = myPlayCards.size() - 1; i >= 0; i--) {
                Card card = myPlayCards.get(i);
                if (!card.isExhausted() && !card.isFrozen() && card.getAtc() > 0){
                    int rivalIndex;
                    if (card.isAura()
                            || card.isAdjacentBuff()
                            || Objects.equals(card.getCardId(), "VAN_EX1_004")
                            || Objects.equals(card.getCardId(), "VAN_NEW1_019")
                    ){
                       rivalIndex = bestFreeEat(rivalPlayCards, card, false);
                    }else {
                        rivalIndex = bestFreeEat(rivalPlayCards, card, true);
                    }
                    if (rivalIndex != -1){
                        myPlayPointToRivalPlay(i, rivalIndex, me, rival);
                    }else {
                        myPlayPointToRivalHero(i, me);
                    }
                }
                ROBOT.delay(ACTION_INTERVAL);
            }
        }
//        魂火解场
        if (soulFireCount > 0){
            double weight = 0;
            int index = -1;
            for (int i = rivalPlayCards.size() - 1; i >= 0; i--) {
                Card card = rivalPlayCards.get(i);
                double newWeight = (card.getHealth() - card.getDamage()) * HEALTH_WEIGHT + card.getAtc() * ATC_WEIGHT;
                if ((card.getHealth() - card.getDamage()) <= 4 && newWeight >= soulFireWeight && newWeight > weight){
                    weight = newWeight;
                    index = i;
                }
            }
            if (index != -1){
                myHandPointToRivalPlay(findByCardId(myHandCards, "VAN_EX1_308"), index, me, rival);
                ROBOT.delay(ACTION_INTERVAL);
            }
        }
        clickTurnOverButton();
    }

    private final static double soulFireWeight = 3.6;

    public void dealResource(){
        switch (getResource()){
            case 1 -> dealOneResource();
            case 2 -> dealTwoResource();
            case 3 -> dealThreeResource();
            case 4 -> dealFourResource();
            case 5 -> dealFiveResource();
            case 6 -> dealSixResource();
            case 7 -> dealSevenResource();
            case 8 -> dealEightResource();
            case 9 -> dealNineResource();
            case 10 -> dealTenResource();
        }
    }

    public boolean canUsePower(){
        return  myPlayArea.getHero().getHealth() - myPlayArea.getHero().getDamage() - calcAtc(rivalPlayCards) > 2;
    }

    public int getResource(){
        return me.getResources() - me.getUsedResources() + me.getTempResources();
    }
    public void dealOneResource(){
        if (getResource() < 1){
            return;
        }
        if (!myPlayArea.isFull()){
//                    下烈焰小鬼或或小蓝胖或银色侍从或女祭司或持盾卫士
            if ((index1 = findByCardId(myHandCards, "VAN_EX1_319")) != -1
                    || (index1 = findByCardId(myHandCards, "VAN_CS2_065")) != -1
                    || (index1 = findByCardId(myHandCards, "VAN_EX1_008")) != -1
                    || (index1 = findByCardId(myHandCards, "VAN_EX1_004")) != -1
            ){
                if (myPlayCards.size() > 0 && Objects.equals(myPlayCards.get(0).getCardId(), "VAN_EX1_162")){
                    myHandPointToMyPlay(index1, 0, me);
                }else {
                    myHandPointToMyPlay(index1, myPlayCards.size(), me);
                }
                ROBOT.delay(ACTION_INTERVAL);
//                        有精灵弓箭手且对方场上有一血怪
            }else if ((index1 = findByCardId(myHandCards, "VAN_CS2_189")) != -1){
                if ((index2 = findHealthAndAtkMax(rivalPlayCards, 1)) != -1 && !rivalPlayCards.get(index2).isStealth()){
                    myHandPointToMyPlayAndPointToRivalPlay(index1, myPlayCards.size(), index2, me, rival);
                }else {
                    myHandPointToMyPlayAndPointToRivalHero(index1, myPlayCards.size(), me);
                }
                ROBOT.delay(ACTION_INTERVAL);
//                        有能动的怪则下叫嚣
            }else if ((index1 = findByCardId(myHandCards, "VAN_EX1_405")) != -1){
                if (myPlayCards.size() > 0 && Objects.equals(myPlayCards.get(0).getCardId(), "VAN_EX1_162")){
                    myHandPointToMyPlay(index1, 0, me);
                }else {
                    myHandPointToMyPlay(index1, myPlayCards.size(), me);
                }
                ROBOT.delay(ACTION_INTERVAL);
            }else if ((index1 = findByCardId(myHandCards, "VAN_CS2_188")) != -1 && (index2 = findCanMove(myPlayCards)) != -1){
                myHandPointToMyPlayAndPointToMyPlay(index1, myPlayCards.size(), index2, me);
                ROBOT.delay(ACTION_INTERVAL);
//                        都没有则考虑两费
            }else if (coinIndex != -1
                    && ((index1 = findByCardId(myHandCards, "VAN_EX1_393")) != -1//阿曼尼
                    || (index1 = findByCardId(myHandCards, "VAN_NEW1_019")) != -1//飞刀
                    || (index1 = findByCardId(myHandCards, "VAN_EX1_162")) != -1)//恐狼
            ){
                coinIndex = findByCardId(myHandCards, "COIN");
                myHandPointToMyPlay(coinIndex, me);
                ROBOT.delay(ACTION_INTERVAL);
                coinIndex = -1;
                dealTwoResource();
            }
            if (!myPlayArea.isFull()){
                if (coinIndex != -1
                        && (existCost(myHandCards, 2) || !existCost(myHandCards, 3))
                        && ((index1 = findByCardId(myHandCards, "VAN_EX1_319")) != -1
                        || (index1 = findByCardId(myHandCards, "VAN_CS2_065")) != -1
                        || (index1 = findByCardId(myHandCards, "VAN_EX1_004")) != -1
                        || (index1 = findByCardId(myHandCards, "VAN_EX1_008")) != -1)
                        || (index1 = findByCardId(myHandCards, "VAN_CS2_189")) != -1
                ){
                    coinIndex = findByCardId(myHandCards, "COIN");
                    myHandPointToMyPlay(coinIndex, me);
                    coinIndex = -1;
                    ROBOT.delay(ACTION_INTERVAL);
                    dealOneResource();
                }
            }
        }
    }
    public void dealTwoResource(){
        if (getResource() < 2){
            dealOneResource();
            return;
        }
        if (!myPlayArea.isFull()){
            if ((index1 = findByCardId(myHandCards, "VAN_NEW1_019")) != -1//飞刀
                    || (index1 = findByCardId(myHandCards, "VAN_EX1_393")) != -1//阿曼尼
            ){
                if (myPlayCards.size() > 0 && Objects.equals(myPlayCards.get(0).getCardId(), "VAN_EX1_162")){
                    myHandPointToMyPlay(index1, 0, me);
                }else {
                    myHandPointToMyPlay(index1, myPlayCards.size(), me);
                }
                ROBOT.delay(ACTION_INTERVAL);
            }else if ((index1 = findByCardId(myHandCards, "VAN_EX1_162")) != -1){//恐狼
                if (myPlayCards.size() > 1){
                    int tauntIndex = 0;
                    for (int i = 0; i < myPlayCards.size(); i++) {
                        if (myPlayCards.get(i).isTaunt()){
                            if (tauntIndex == 0){
                                tauntIndex = i;
                            }else if (Objects.equals(myPlayCards.get(i).getCardId(), "VAN_EX1_405")){//持盾卫士
                                tauntIndex = i;
                            }
                        }
                    }
                    myHandPointToMyPlay(index1, tauntIndex == 0? 1 : tauntIndex, me);
                    ROBOT.delay(ACTION_INTERVAL);
                }else {
                    myHandPointToMyPlay(index1, 1, me);
                    ROBOT.delay(ACTION_INTERVAL);
                }
            }
            dealOneResource();
            dealOneResource();
        }
        if (canUsePower() && getResource() >= 2 && !usedPower){
            usedPower = true;
            clickMyPower();
            ROBOT.delay(ACTION_INTERVAL);
        }
    }
    public void dealThreeResource(){
        if (getResource() < 3){
            dealTwoResource();
            return;
        }
        if (!myPlayArea.isFull()){
//                    祭司
            if ((index1 = findByCardId(myHandCards, "VAN_EX1_019")) != -1 && myPlayCards.size() > 0){
                index2 = -1;
                for (int i = 0; i < myPlayCards.size(); i++) {
                    Card card = myPlayCards.get(i);
                    if (!card.isExhausted() && !card.isFrozen()){
                        index2 = i;
                        myHandPointToMyPlayAndPointToMyPlay(index1, myPlayCards.size(), i, me);
                        ROBOT.delay(ACTION_INTERVAL);
                        break;
                    }
                }
                if (index2 == -1){
                    for (int i = 0; i < myPlayCards.size(); i++) {
                        Card card = myPlayCards.get(i);
                        if (card.isTaunt() || card.isDivineShield()){
                            index2 = i;
                            myHandPointToMyPlayAndPointToMyPlay(index1, myPlayCards.size(), i, me);
                            ROBOT.delay(ACTION_INTERVAL);
                            break;
                        }
                    }
                }
                if (index2 == -1
                        && ((index2 = findByCardId(myPlayCards, "VAN_NEW1_019")) != -1//飞刀
                        || (index2 = findByCardId(myPlayCards, "VAN_EX1_162")) != -1)//恐狼
                ){
                    if (myPlayCards.size() > 0 && Objects.equals(myPlayCards.get(0).getCardId(), "VAN_EX1_162")){
                        myHandPointToMyPlayAndPointToMyPlay(index1, 0, index2, me);
                    }else {
                        myHandPointToMyPlayAndPointToMyPlay(index1, myPlayCards.size(), index2, me);
                    }
                    ROBOT.delay(ACTION_INTERVAL);
                }
                if (index2 == -1){
                    if (myPlayCards.size() > 0 && Objects.equals(myPlayCards.get(0).getCardId(), "VAN_EX1_162")){
                        myHandPointToMyPlayAndPointToMyPlay(index1, 0, 0, me);
                    }else {
                        myHandPointToMyPlayAndPointToMyPlay(index1, myPlayCards.size(), 0, me);
                    }
                }
                ROBOT.delay(ACTION_INTERVAL);
            }else if (coinIndex != -1){
//            有幸运币和阿古斯
                if (myPlayCards.size() > 1 &&  (index1 = findByCardId(myHandCards, "VAN_EX1_093")) != -1){
                    coinIndex = findByCardId(myHandCards, "COIN");
                    coinIndex = findByCardId(myHandCards, "COIN");
                    myHandPointToMyPlay(coinIndex, me);
                    ROBOT.delay(ACTION_INTERVAL);
                    index2 = 1;
                    for (int i = 0; i < myPlayCards.size(); i++) {
                        Card card = myPlayCards.get(i);
                        if (card.isTaunt() || card.isDivineShield()){
                            if (i != 0){
                                index2 = i;
                            }
                        }
                    }
                    myHandPointToMyPlay(coinIndex < index1? index1 - 1 : index1, index2, me);
                    coinIndex = -1;
                    ROBOT.delay(ACTION_INTERVAL);
//                黑铁
                }else if ((index1 = findByCardId(myHandCards, "VAN_EX1_046")) != -1){
                    boolean flag = false;
                    coinIndex = findByCardId(myHandCards, "COIN");
                    myHandPointToMyPlay(coinIndex, me);
                    ROBOT.delay(ACTION_INTERVAL);
                    index1 = coinIndex < index1? index1 - 1 : index1;
                    for (int i = 0; i < myPlayCards.size(); i++) {
                        Card card = myPlayCards.get(i);
                        if (!card.isExhausted() && !card.isFrozen()){
                            flag = true;
                            myHandPointToMyPlayAndPointToMyPlay(index1, myPlayCards.size(), i, me);
                            ROBOT.delay(ACTION_INTERVAL);
                            break;
                        }
                    }
                    if (!flag){
                        if (myPlayCards.size() == 0){
                            if (rivalPlayArea.size() == 0){
                                myHandPointToMyPlay(index1, myPlayCards.size(), me);
                            }else {
                                int index = -1;
                                for (int i = 0; i < rivalPlayCards.size(); i++) {
                                    if (!rivalPlayCards.get(i).isStealth()){
                                        index = i;
                                        break;
                                    }
                                }
                                if (index == -1){
                                    myHandPointToMyPlay(index1, myPlayCards.size(), me);
                                }else {
                                    myHandPointToMyPlayAndPointToRivalPlay(index1, myPlayCards.size(), index, me, rival);
                                }
                            }
                        }else {
                            if (Objects.equals(myPlayCards.get(0).getCardId(), "VAN_EX1_162")){
                                myHandPointToMyPlayAndPointToMyPlay(index1, 0, 0, me);
                            }else {
                                myHandPointToMyPlayAndPointToMyPlay(index1, myPlayCards.size(), 0, me);
                            }
                        }
                        ROBOT.delay(ACTION_INTERVAL);
                    }
                    coinIndex = -1;
                }
            }
            dealTwoResource();
            dealOneResource();
        }
        if (canUsePower() && getResource() >= 2 && !usedPower){
            usedPower = true;
            clickMyPower();
            ROBOT.delay(ACTION_INTERVAL);
            dealOneResource();
        }
    }
    public void dealFourResource(){
        if (getResource() < 4){
            dealThreeResource();
            return;
        }
        if (!myPlayArea.isFull()){
//        阿古斯
            if (myPlayCards.size() > 1 && (index1 = findByCardId(myHandCards, "VAN_EX1_093")) != -1){
                index2 = 1;
                for (int i = 0; i < myPlayCards.size(); i++) {
                    Card card = myPlayCards.get(i);
                    if (card.isTaunt() || card.isDivineShield()){
                        if (i != 0){
                            index2 = i;
                        }
                    }
                }
                myHandPointToMyPlay(index1, index2, me);
                ROBOT.delay(ACTION_INTERVAL);
//            黑铁
            }else if ((index1 = findByCardId(myHandCards, "VAN_EX1_046")) != -1){
                boolean flag = false;
                for (int i = 0; i < myPlayCards.size(); i++) {
                    Card card = myPlayCards.get(i);
                    if (!card.isExhausted() && !card.isFrozen()){
                        flag = true;
                        System.out.println(myHandCards.size());
                        System.out.println("index:" + index1);
                        myHandPointToMyPlayAndPointToMyPlay(index1, myPlayCards.size(), i, me);
                        ROBOT.delay(ACTION_INTERVAL);
                        break;
                    }
                }
                if (!flag){
                    if (myPlayCards.size() == 0){
                        if (rivalPlayArea.size() == 0){
                            myHandPointToMyPlay(index1, myPlayCards.size(), me);
                        }else {
                            int index = -1;
                            for (int i = 0; i < rivalPlayCards.size(); i++) {
                                if (!rivalPlayCards.get(i).isStealth()){
                                    index = i;
                                    break;
                                }
                            }
                            if (index == -1){
                                myHandPointToMyPlay(index1, myPlayCards.size(), me);
                            }else {
                                myHandPointToMyPlayAndPointToRivalPlay(index1, myPlayCards.size(), index, me, rival);
                            }
                        }
                    }else {
                        if (Objects.equals(myPlayCards.get(0).getCardId(), "VAN_EX1_162")){
                            myHandPointToMyPlayAndPointToMyPlay(index1, 0, 0, me);
                        }else {
                            myHandPointToMyPlayAndPointToMyPlay(index1, myPlayCards.size(), 0, me);
                        }
                    }
                    ROBOT.delay(ACTION_INTERVAL);
                }
            }
            if (findByCardId(myHandCards, "VAN_NEW1_019") == -1){
                dealThreeResource();
                dealTwoResource();
            }else {
                dealTwoResource();
                dealThreeResource();
            }
            dealOneResource();
        }
        if (canUsePower() && getResource() >= 2 && !usedPower){
            usedPower = true;
            clickMyPower();
            ROBOT.delay(ACTION_INTERVAL);
            dealTwoResource();
        }
    }
    public void dealFiveResource(){
        if (getResource() < 5){
            dealFourResource();
            return;
        }
        if (!myPlayArea.isFull()){
            int blood = rivalPlayArea.getHero().getHealth() + rivalPlayArea.getHero().getArmor() - rivalPlayArea.getHero().getDamage();
            int myPlayAtc = calcAtc(myPlayCards);
//        57大哥
            if ((index1 = findByCardId(myHandCards, "VAN_EX1_310")) != -1 && (myHandCards.size() <= 2
                    || blood - myPlayAtc <= 10 || myHandCards.size() > 4)
            ){
                int i = -1;
//            恐狼
                if ((index2 = findByCardId(myPlayCards, "VAN_EX1_162")) != -1){
                    if (index2 + 1 < myPlayCards.size()){
                        i = index2 + 1;
                        Card rightCards = myPlayCards.get(i);
                        if (!rightCards.isExhausted() && !rightCards.isFrozen()){
                            if (findTaunt(rivalPlayCards) == -1){
                                myPlayPointToRivalHero(i, me);
                                ROBOT.delay(ACTION_INTERVAL);
                            }else {
                                i = i == 1? 0 : i + 1;
                            }
                        }
                    }
                }
                myHandPointToMyPlay(index1, i == -1? myPlayCards.size() : i, me);
                ROBOT.delay(ACTION_INTERVAL);
            }
            dealFourResource();
            dealThreeResource();
            dealTwoResource();
            dealOneResource();
        }
        if (canUsePower() && getResource() >= 2 && !usedPower){
            usedPower = true;
            clickMyPower();
            ROBOT.delay(ACTION_INTERVAL);
            dealThreeResource();
        }
    }
    public void dealSixResource(){
        if (getResource() < 6){
            dealFiveResource();
            return;
        }
        if (!myPlayArea.isFull()){
            dealFiveResource();
            dealTwoResource();
            dealFourResource();
            dealThreeResource();
            dealOneResource();
        }
        if (canUsePower() && getResource() >= 2 && !usedPower){
            usedPower = true;
            clickMyPower();
            ROBOT.delay(ACTION_INTERVAL);
            dealFourResource();
        }
    }
    public void dealSevenResource(){
        if (getResource() < 7){
            dealSixResource();
            return;
        }
        if (!myPlayArea.isFull()){
            dealSixResource();
            dealFiveResource();
            dealFourResource();
            dealThreeResource();
            dealTwoResource();
            dealOneResource();
        }
        if (canUsePower() && getResource() >= 2 && !usedPower){
            usedPower = true;
            clickMyPower();
            ROBOT.delay(ACTION_INTERVAL);
            dealFiveResource();
        }
    }
    public void dealEightResource(){
        if (getResource() < 7){
            dealSevenResource();
            return;
        }
        if (!myPlayArea.isFull()){
            dealSevenResource();
            dealFiveResource();
            dealFourResource();
            dealThreeResource();
            dealTwoResource();
            dealOneResource();
        }
        if (canUsePower() && getResource() >= 2 && !usedPower){
            usedPower = true;
            clickMyPower();
            ROBOT.delay(ACTION_INTERVAL);
            dealSixResource();
        }
    }
    public void dealNineResource(){
        if (getResource() < 9){
            dealEightResource();
            return;
        }
        if (!myPlayArea.isFull()){
            dealEightResource();
            dealFiveResource();
            dealFourResource();
            dealThreeResource();
            dealTwoResource();
            dealOneResource();
        }
        if (canUsePower() && getResource() >= 2 && !usedPower){
            usedPower = true;
            clickMyPower();
            ROBOT.delay(ACTION_INTERVAL);
            dealSevenResource();
        }
    }
    public void dealTenResource(){
        if (!myPlayArea.isFull()){
            dealNineResource();
            dealFiveResource();
            dealFourResource();
            dealThreeResource();
            dealTwoResource();
            dealOneResource();
        }
        if (canUsePower() && getResource() >= 2 && !usedPower){
            usedPower = true;
            clickMyPower();
            ROBOT.delay(ACTION_INTERVAL);
            dealEightResource();
        }
    }
//
//    public int[] maxPriority(List<CardMes> cards, int resource){
//        int[] dp = new int[resource + 1];
//        int[][] g = new int[resource + 1][cards.size()];
//        for (int i = 0; i < cards.size(); i++){
//            CardMes cardMes = cards.get(i);
//            for (int j = resource; j >= cardMes.getCost(); j--){
//                if (dp[j - cardMes.getCost()] + cardMes.getPriority() + cardMes.getAdditionalPriority() > dp[j]){
//                    dp[j] = dp[j - cardMes.getCost()] + cardMes.getPriority() + cardMes.getAdditionalPriority();
//                    g[j][i] = 1;
//                }else {
//                    g[j][i] = 0;
//                }
//            }
//        }
//        int[] arr = new int[cards.size() + 1];
//        System.arraycopy(g[resource], 0, arr, 0, g[resource].length);
//        arr[cards.size()] = dp[resource];
//        return arr;
//    }
}
