package club.xiaojiawei.strategy;

import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.bean.BaseCard;
import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.bean.Player;
import club.xiaojiawei.bean.area.HandArea;
import club.xiaojiawei.bean.area.PlayArea;
import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.GAME_RECT;
import static club.xiaojiawei.enums.CardTypeEnum.MINION;
import static club.xiaojiawei.enums.ConfigurationKeyEnum.STRATEGY_KEY;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:29
 */
@Slf4j
public abstract class AbstractDeckStrategy{
    @Resource
    protected MouseUtil mouseUtil;
    @Resource
    protected AtomicReference<BooleanProperty> isPause;
    @Resource
    protected Properties scriptConfiguration;
    /**
     * 每次行动后停顿时间
     */
    protected static final int ACTION_INTERVAL = 3500;
    protected static final float[] FIRST_HAND_CARD_HORIZONTAL_TO_CENTER_RATION = new float[]{
            (float) 0.033, (float) 0.08, (float) 0.123, (float) 0.167, (float) 0.177, (float) 0.193, (float) 0.203, (float) 0.213, (float) 0.22, (float) 0.227
    };
    protected static final float[] HAND_CARD_HORIZONTAL_CLEARANCE_RATION = new float[]{
            0, (float) 0.09, (float) 0.09, (float) 0.087, (float) 0.07, (float) 0.057, (float) 0.05, (float) 0.042, (float) 0.037, (float) 0.034
    };
    protected static final float HAND_CARD_VERTICAL_TO_BOTTOM_RATION = (float) 0.059;
    protected static final float RIVAL_HERO_VERTICAL_TO_BOTTOM_RATION = (float) 0.801;
    protected static final float MY_HERO_VERTICAL_TO_BOTTOM_RATION = (float) 0.26;
    protected static final float PLAY_CARD_HORIZONTAL_CLEARANCE_RATION = (float) 0.097;
    protected static final float MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION = (float) 0.45;
    protected static final float RIVAL_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION = (float) 0.62;
    protected static final float POWER_VERTICAL_TO_BOTTOM_RATION = (float) 0.23;
    protected static final float POWER_HORIZONTAL_TO_CENTER_RATION = (float) 0.133;
    protected static final float TURN_OVER_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.54;
    protected static final float TURN_OVER_BUTTON_HORIZONTAL_TO_CENTER_RATION = (float) 0.417;
    public static final float CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.23;
    protected static double BLOOD_WEIGHT = 0.4;
    protected static double ATC_WEIGHT = 0.6;
    protected static double FREE_EAT_MAX = 5;
    protected PlayArea myPlayArea;
    protected PlayArea rivalPlayArea;
    protected HandArea myHandArea;
    protected HandArea rivalHandArea;
    protected Player me;
    protected Player rival;
    protected List<Card> myHandCards;
    protected List<Card> myPlayCards;
    protected List<Card> rivalHandCards;
    protected List<Card> rivalPlayCards;

    public void changeCard() {
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY_KEY.getKey()))){
            try {
                log.info("执行换牌策略");
                assign();
                float clearance ,firstCardPos;
                if (myHandCards.size() == 3){
                    clearance  = getFloatCardClearanceForThreeCard();
                    firstCardPos = getFloatCardFirstCardPosForThreeCard();
                }else {
                    clearance  = getFloatCardClearanceForFourCard();
                    firstCardPos = getFloatCardFirstCardPosForFourCard();
                }
                SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
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
                SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
                //        点击确认按钮
                mouseUtil.leftButtonClick(
                        ((GAME_RECT.right + GAME_RECT.left) >> 1) + RandomUtil.getRandom(-10, 10),
                        (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5, 5))
                );
            }
        }
    }

    public void outCard(){
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY_KEY.getKey()))){
            try{
                log.info("执行出牌策略");
                if (log.isDebugEnabled()){
                    log.debug("我方手牌：" + myHandCards);
                    log.debug("我方战场：" + myPlayCards);
                    log.debug("我方英雄：" + myPlayArea.getHero());
                    log.debug("我方武器：" + myPlayArea.getWeapon());
                    log.debug("我方技能：" + myPlayArea.getPower());
                }
                log.info("回合开始可用水晶数：" + getMyUsableResource());
                executeOutCard();
                MouseUtil.cancel();
                log.info("执行出牌策略完毕");
            }finally {
                clickTurnOverButton();
            }
        }
    }
    public void discoverChooseCard(Card...cards){
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY_KEY.getKey()))){
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
     * 执行发现选牌,返回0~2的数字
     */
    protected abstract int executeDiscoverChooseCard(Card...cards);

    private void assign(){
        this.me = War.getMe();
        this.rival = War.getRival();
        this.myHandArea = me.getHandArea();
        this.myPlayArea = me.getPlayArea();
        this.rivalHandArea = rival.getHandArea();
        this.rivalPlayArea = rival.getPlayArea();
        this.myHandCards = myHandArea.getCards();
        this.myPlayCards = myPlayArea.getCards();
        this.rivalHandCards = rivalHandArea.getCards();
        this.rivalPlayCards = rivalPlayArea.getCards();
        if (log.isDebugEnabled()){
            log.debug("我方手牌：" + myHandCards);
        }
    }

    /*calc*/
    protected int calcMySpellPower(){
        int spellPower = 0;
        for (Card playCard : myPlayCards) {
            spellPower += playCard.getSpellPower();
        }
        spellPower += myPlayArea.getHero().getSpellPower();
        if (myPlayArea.getWeapon() != null){
            spellPower += myPlayArea.getWeapon().getSpellPower();
        }
        return spellPower;
    }
    protected int calcCardBlood(Card card){
        return card.getHealth() + card.getArmor() - card.getDamage();
    }
    protected int calcCardCount(List<Card> cards, String cardId){
        int count = 0;
        for (Card card : cards) {
            if (Objects.equals(card.getCardId(), cardId)){
                count++;
            }
        }
        return count;
    }
    protected int calcCardCount(List<Card> cards, BaseCard card){
        return calcCardCount(cards, card.cardId());
    }
    /**
     * 计算对方英雄血量
     * @return
     */
    protected int calcRivalHeroBlood(){
        return calcCardBlood(rivalPlayArea.getHero());
    }
    protected int calcMyHeroTotalAtc(){
        return Math.max((myPlayArea.getWeapon() == null)? 0 : myPlayArea.getWeapon().getAtc(), myPlayArea.getHero().getAtc());
    }
    protected int calcMyHeroBlood(){
        return calcCardBlood(myPlayArea.getHero());
    }
    protected int calcMyPlayTotalAtc(){
        int atc = 0;
        for (Card card : myPlayCards) {
            if (!card.isExhausted() && !card.isFrozen() && !card.isDormantAwakenConditionEnchant()){
                atc += card.getAtc();
                if (card.isWindFury()){
                    atc += card.getAtc();
                }
            }
        }
        return atc;
    }
    protected int calcMyTotalAtc(){
        int atc = calcMyPlayTotalAtc();
        if (!myPlayArea.getHero().isFrozen()){
            atc += calcMyHeroTotalAtc();
            if (myPlayArea.getHero().isWindFury() || (myPlayArea.getWeapon() != null && myPlayArea.getWeapon().isWindFury())){
                atc += calcMyHeroTotalAtc();
            }
        }
        return atc;
    }
    protected int calcTotalAtc(List<Card> cards){
        int atc = 0;
        for (Card card : cards) {
            atc += card.getAtc();
            if (card.isWindFury()){
                atc += card.getAtc();
            }
        }
        return atc;
    }

    /**
     * 计算指定种族的数量
     * @param cards
     * @param cardRace
     * @return
     */
    protected int calcCardRaceCount(List<Card> cards, CardRaceEnum cardRace, boolean canMove){
        int count = 0;
        for (Card card : cards) {
            if ((card.getCardRace() == cardRace || card.getCardRace() == CardRaceEnum.ALL) && (!canMove || canMove(card))){
                count++;
            }
        }
        return count;
    }
    /**
     * 计算我的一个怪如何白吃对面一个怪
     * @param myPlayCard
     * @param allowDeath 允许不白吃
     * @return
     */
    protected int calcMyCardFreeEat(Card myPlayCard, boolean allowDeath){
        int rivalIndex = -1;
        double weight = 0;
        int myAtc = myPlayCard.getAtc();
        int myBlood = calcCardBlood(myPlayCard);
        double myWeight = myAtc * ATC_WEIGHT + myBlood * BLOOD_WEIGHT;
//        寻找能白吃的
        for (int i = rivalPlayCards.size() - 1; i >= 0 ; i--) {
            Card rivalCard = rivalPlayCards.get(i);
            if (canPointedToRival(rivalCard) && rivalCard.getCardType() == MINION && calcCardBlood(rivalCard) <= myAtc && (rivalCard.getAtc() < myBlood || myPlayCard.isDivineShield())){
                double newWeight = calcCardBlood(rivalCard) * BLOOD_WEIGHT + rivalCard.getAtc() * ATC_WEIGHT;
//                寻找最优白吃方法，既要白吃又不能白吃过头忽略打脸，如55白吃11这种
                if (newWeight > weight && myWeight - newWeight < FREE_EAT_MAX){
                    weight = newWeight;
                    rivalIndex = i;
                }
            }
        }
//            白吃不了，寻找比较赚的解法
        if (allowDeath && rivalIndex == -1){
            double myWeightPlus = myBlood * BLOOD_WEIGHT + (myAtc + 1) * ATC_WEIGHT;
            weight = 0;
            for (int i = rivalPlayCards.size() - 1; i >= 0 ; i--) {
                Card card = rivalPlayCards.get(i);
                if (canPointedToRival(card) && card.getCardType() == MINION && calcCardBlood(card) <= myAtc){
                    double newWeight = calcCardBlood(card) * BLOOD_WEIGHT + card.getAtc() * ATC_WEIGHT;
                    if (newWeight >= myWeightPlus && newWeight > weight){
                        rivalIndex = i;
                        weight = newWeight;
                    }
                }
            }
        }
        return rivalIndex;
    }

    /**
     * 计算我的多个怪如何吃对面一个怪
     * @param rivalCard
     * @param maxDeathCount
     * @param subtractRivalBlood 需要减少的敌方怪的血量
     * @return 储存下标的list，最后一位存储总攻击力
     */
    protected List<Integer> calcEatRivalCard(Card rivalCard, int maxDeathCount, int subtractRivalBlood){
        if (rivalCard == null){
            return null;
        }
        TreeSet<List<Integer>> result = new TreeSet<>(Comparator.comparingInt(o -> o.get(o.size() - 1)));
        calcFreeEatRivalCardRecursive(result, myPlayCards, new ArrayList<>(), calcCardBlood(rivalCard) - subtractRivalBlood, rivalCard.getAtc(), 0, 0, maxDeathCount);
        return result.isEmpty() ? null : result.first();
    }
    protected List<Integer> calcEatRivalCard(Card rivalCard){
        return calcEatRivalCard(rivalCard, Integer.MAX_VALUE, 0);
    }
    protected List<Integer> calcFreeEatRivalCard(Card rivalCard){
        return calcEatRivalCard(rivalCard, 0, 0);
    }
    /**
     * 穷举法
     * @param result
     * @param myPlayCards
     * @param indexList 存储卡组下标，最后一位存储总攻击力
     * @param rivalBlood
     * @param rivalAtc
     * @param atcSum
     * @param index
     */
    private void calcFreeEatRivalCardRecursive(TreeSet<List<Integer>> result, List<Card> myPlayCards, List<Integer> indexList, int rivalBlood, int rivalAtc, int atcSum, int index, int maxDeathCount){
//        终止条件
        if (atcSum >= rivalBlood){
            indexList.add(atcSum);
            ArrayList<Integer> tempIndexList = new ArrayList<>(indexList);
            indexList.remove(indexList.size() - 1);
//            由于重写了compare方法，所以这里比较的是总攻击力是否相等
            if (result.contains(tempIndexList)){
                double oldFreeEatCount = 0, newFreeEatCount = 0;
                List<Integer> oldList = null;
                for (List<Integer> lists : result) {
//                取出总攻击力相等的那个旧list
                    if (lists.get(lists.size() - 1) == atcSum){
                        oldList = lists;
//                计算旧list的白吃数
                        for (int i = 0; i < lists.size() - 1; i++) {
                            Card card = myPlayCards.get(lists.get(i));
                            if (calcCardBlood(card) > rivalAtc || card.isDivineShield() || card.isImmune()){
                                oldFreeEatCount++;
                            }
                        }
                        break;
                    }
                }
//                计算新list的白吃数
                for (Integer integer : indexList) {
                    Card card = myPlayCards.get(integer);
                    if (calcCardBlood(card) > rivalAtc || card.isDivineShield() || card.isImmune()){
                        newFreeEatCount++;
                    }
                }
                assert oldList != null;
//                比较新旧list白吃率
                if (oldFreeEatCount / oldList.size() < newFreeEatCount / tempIndexList.size()){
                    result.remove(tempIndexList);
                    result.add(tempIndexList);
                }
            }else {
                if (maxDeathCount != Integer.MAX_VALUE){
                    int deathCount = 0;
                    for (Integer integer : indexList) {
                        Card card = myPlayCards.get(integer);
//                        保证全部白吃才能添加
                        if (calcCardBlood(card) <= rivalAtc && !card.isDivineShield() && !card.isImmune()){
                            if (++deathCount > maxDeathCount){
                                return;
                            }
                        }
                    }
                }
                result.add(tempIndexList);
            }
            return;
        }else if (index == myPlayCards.size()){
            return;
        }

        Card card = myPlayCards.get(index);
        if (canMove(card)){
//            选
            indexList.add(index);
            atcSum += card.getAtc();
            calcFreeEatRivalCardRecursive(result, myPlayCards, indexList, rivalBlood, rivalAtc, atcSum, index + 1, maxDeathCount);
            indexList.remove(indexList.size() - 1);
            atcSum -= card.getAtc();
        }
//        不选
        calcFreeEatRivalCardRecursive(result, myPlayCards, indexList, rivalBlood, rivalAtc,  atcSum, index + 1, maxDeathCount);
    }
    /*action*/
    
    private void myHandPointTo(int handIndex, int[] endPos){
        if (handIndex < 0){
            return;
        }
        int[] handPos = getMyHandCardPos(me.getHandArea().getCards().size(), handIndex);
        mouseUtil.leftButtonDrag(
                handPos,
                endPos
        );
    }
    protected boolean myHandPointToMyPlay(int handIndex){
        if (handIndex >= myHandCards.size()){
            return false;
        }
        Card card = myHandCards.get(handIndex);
        if (myPlayArea.isFull() || card.getCost() > getMyUsableResource()){
            return false;
        }
        myHandPointToMyPlayForBase(handIndex, myPlayCards.size(), true);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        return findByEntityId(myHandCards, card) == -1;
    }
    protected boolean myHandPointToMyPlay(int myHandIndex, int myPlayIndex){
        if (myHandIndex >= myHandCards.size() || myPlayIndex >= myPlayCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (myPlayArea.isFull() || card.getCost() > getMyUsableResource()){
            return false;
        }
        myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        return findByEntityId(myHandCards, card) == -1;
    }
    protected boolean myHandPointToMyPlayNoPlace(int myHandIndex, int myPlayIndex){
        if (myHandIndex >= myHandCards.size() || myPlayIndex >= myPlayCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > getMyUsableResource() || !canSpellPointedToMe(myPlayCards.get(myPlayIndex))){
            return false;
        }
        myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, false);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        return findByEntityId(myHandCards, card) == -1;
    }

    private int[] myHandPointToMyPlayForBase(int handIndex, int playIndex, boolean insertGap){
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] playPos;
        if (playIndex < 0){
            playPos = new int[]{
                    (GAME_RECT.right + GAME_RECT.left) >> 1,
                    (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
            };
        }else {
            playPos = getMyPlayCardPos(me.getPlayArea().getCards().size() + (insertGap? 1 : 0), playIndex);
        }
        myHandPointTo(handIndex, playPos);
        return playPos;
    }

    protected boolean myHandPointToRivalPlayNoPlace(int myHandIndex, int rivalPlayIndex){
        if (myHandIndex >= myHandCards.size() || rivalPlayIndex >= rivalPlayCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > getMyUsableResource() || !canSpellPointedToRival(rivalPlayCards.get(rivalPlayIndex))){
            return false;
        }
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        myHandPointTo(myHandIndex, getRivalPlayCardPos(rival.getPlayArea().getCards().size(), rivalPlayIndex));
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        return findByEntityId(myHandCards, card) == -1;
    }
    protected boolean myHandPointToRivalHeroNoPlace(int myHandIndex){
        if (myHandIndex >= myHandCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > getMyUsableResource() || !canSpellPointedToRival(rivalPlayArea.getHero())){
            return false;
        }
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        myHandPointTo(myHandIndex, getRivalHeroPos());
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        return findByEntityId(myHandCards, card) == -1;
    }
    protected boolean myHandPointToNoPlace(int myHandIndex){
        if (myHandIndex >= myHandCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > getMyUsableResource()){
            return false;
        }
        myHandPointToMyPlayForBase(myHandIndex, -1, false);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        return findByEntityId(myHandCards, card) == -1;
    }
    

    protected boolean myHeroPointToRivalHero(){
        if (!canPointedToRival(rivalPlayArea.getHero()) || !canMove(myPlayArea.getHero()) || calcMyHeroTotalAtc() <= 0){
           return false;
        }
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        myHeroPointTo(getRivalHeroPos());
        SystemUtil.delay(ACTION_INTERVAL);
        return true;
    }
    protected boolean myHeroPointToRivalPlay(int rivalPlayIndex){
        if (rivalPlayIndex >= rivalPlayCards.size() || !canPointedToRival(rivalPlayCards.get(rivalPlayIndex)) || !canMove(myPlayArea.getHero()) || calcMyHeroTotalAtc() <= 0){
            return false;
        }
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] rivalPlayPos = getRivalPlayCardPos(rivalPlayCards.size(), rivalPlayIndex);
        myHeroPointTo(rivalPlayPos);
        SystemUtil.delay(ACTION_INTERVAL);
        return true;
    }
    private void myHeroPointTo(int[] endPos){
        mouseUtil.leftButtonDrag(
                getMyHeroPos(),
                endPos
        );
    }


    protected boolean myPlayPointToRivalHero(int myPlayIndex){
        if (myPlayIndex >= myPlayCards.size()){
            return false;
        }
        Card card = myPlayCards.get(myPlayIndex);
        if (!canPointedToRival(rivalPlayArea.getHero()) || !canMove(card)){
            return false;
        }
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] rivalHeroPos = getRivalHeroPos();
        mouseUtil.leftButtonDrag(
                getMyPlayCardPos(me.getPlayArea().getCards().size(), myPlayIndex),
                rivalHeroPos
        );
        SystemUtil.delay(ACTION_INTERVAL - 500);
        return true;
    }

    /**
     * 从我方战场指向对方战场
     * @param myPlayIndex
     * @param rivalPlayIndex
     */
    protected boolean myPlayPointToRivalPlay(int myPlayIndex, int rivalPlayIndex){
        if (myPlayIndex >= myPlayCards.size() || rivalPlayIndex >= rivalPlayCards.size()){
            return false;
        }
        Card card = myPlayCards.get(myPlayIndex);
        if (!canPointedToRival(rivalPlayCards.get(rivalPlayIndex)) || !canMove(card)){
            return false;
        }
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] myPlayPos = getMyPlayCardPos(me.getPlayArea().getCards().size(), myPlayIndex);
        int[] rivalPlayPos = getRivalPlayCardPos(rival.getPlayArea().getCards().size(), rivalPlayIndex);
        mouseUtil.leftButtonDrag(
                myPlayPos[0],
                myPlayPos[1],
                rivalPlayPos[0],
                rivalPlayPos[1]
        );
        SystemUtil.delay(ACTION_INTERVAL + 500);
        return true;
    }

    /**
     * 点击我方技能
     */
    protected boolean clickPower(){
        if (myPlayArea.getPower().isExhausted() || getMyUsableResource() < myPlayArea.getPower().getCost() || myPlayArea.isFull()){
            return false;
        }
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        mouseUtil.leftButtonClick(getMyPowerPos());
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        return true;
    }

    /**
     * 点击回合结束按钮
     */
    protected void clickTurnOverButton(){
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        mouseUtil.leftButtonClick(
                (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * TURN_OVER_BUTTON_HORIZONTAL_TO_CENTER_RATION + RandomUtil.getRandom(-6, 6)),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * TURN_OVER_BUTTON_VERTICAL_TO_BOTTOM_RATION  + RandomUtil.getRandom(-3, 3))
        );
    }

    protected boolean myHandPointToMyPlayThenPointToMyPlay(int myHandIndex, int myPlayIndex, int thenMyPlayIndex){
        if (myHandIndex >= myHandCards.size() || myPlayArea.isFull() || myPlayCards.get(thenMyPlayIndex).isDormantAwakenConditionEnchant() || myHandCards.get(myHandIndex).getCost() > getMyUsableResource()){
            return false;
        }
        int[] playPos = myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        if (myPlayIndex <= thenMyPlayIndex){
            thenMyPlayIndex++;
        }
        SystemUtil.delayMedium();
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        mouseUtil.leftButtonMoveThenClick(
                playPos,
                getMyPlayCardPos(me.getPlayArea().getCards().size() + 1, thenMyPlayIndex)
        );
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        return true;
    }

    protected boolean myHandPointToMyPlayThenPointToRivalPlay(int myHandIndex, int myPlayIndex, int rivalPlayIndex){
        if (myHandIndex >= myHandCards.size() || rivalPlayIndex >= rivalPlayCards.size() || myPlayArea.isFull() || !canPointedToRival(rivalPlayCards.get(rivalPlayIndex)) || myHandCards.get(myHandIndex).getCost() > getMyUsableResource()){
            return false;
        }
        int[] playPos = myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        SystemUtil.delayMedium();
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        mouseUtil.leftButtonMoveThenClick(
                playPos,
                getRivalPlayCardPos(rival.getPlayArea().getCards().size(), rivalPlayIndex)
        );
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        return true;
    }

    protected boolean myHandPointToMyPlayThenPointToRivalHero(int myHandIndex, int myPlayIndex){
        if (myHandIndex >= myHandCards.size() || myPlayArea.isFull() || !canPointedToRival(rivalPlayArea.getHero()) || myHandCards.get(myHandIndex).getCost() > getMyUsableResource()){
            return false;
        }
        int[] playPos = myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        SystemUtil.delayMedium();
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        mouseUtil.leftButtonMoveThenClick(
                playPos,
                getRivalHeroPos()
        );
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        return true;
    }
    /**
     * 点击悬浮卡牌，如发现,
     * @param clearance
     * @param firstCardPos
     * @param floatCardIndex 0~2
     */
    protected void clickFloatCard(float clearance, float firstCardPos, int floatCardIndex){
        SystemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        mouseUtil.leftButtonClick(
                (int) (firstCardPos + floatCardIndex * clearance) + RandomUtil.getRandom(-10, 10),
                (GAME_RECT.bottom + GAME_RECT.top >> 1) + RandomUtil.getRandom(-15, 15)
        );
        SystemUtil.delayShort();
    }
    /*exist*/
    /**
     * 是否存在指定费用的牌
     * @param cards
     * @param cost
     * @return
     */
    protected boolean existByCost(List<Card> cards, int cost){
        return findByCost(cards, cost) != -1;
    }
    /*find*/
    /**
     * 寻找指定血量随从数量
     * @return
     */
    protected int findCountByBlood(List<Card> cards, int blood){
        int count = 0;
        for (Card card : cards) {
            if (calcCardBlood(card) == blood){
                count++;
            }
        }
        return count;
    }
    /**
     * 寻找在大于等于指定攻击力中攻击力最大的
     * @param cards
     * @param atk
     * @return
     */
    protected int findMaxAtcByGEAtk(List<Card> cards, int atk){
        int index = -1, attackNum = 0;
        for (int i = 0; i < cards.size(); i++) {
            int atc = cards.get(i).getAtc();
            if (atc >= atk && atc > attackNum){
                index = i;
                attackNum = atc;
            }
        }
        return index;
    }

    /**
     *
     * @param cards
     * @param atcLine
     * @return
     */
    protected int findMaxAtcByGEAtkNotWindFury(List<Card> cards, int atcLine){
        int index = -1, atcMax = 0;
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            int atc = card.getAtc();
            if ((atc > Math.max(atcMax, atcLine) || (atc == Math.max(atcMax, atcLine) && (index != -1 && calcCardBlood(card) > calcCardBlood(cards.get(index))))) && !card.isWindFury()){
                index = i;
                atcMax = atc;
            }
        }
        return index;
    }
    protected int findCardByCardRace(List<Card> cards, CardRaceEnum ...cardRace){
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            for (CardRaceEnum cardRaceEnum : cardRace) {
                if (cardRaceEnum == card.getCardRace()){
                    return i;
                }
            }
        }
        return -1;
    }
    /**
     * 寻找在等于指定血量中攻击力最大的
     * @param cards
     * @param blood 生命值减去伤害等得出
     * @return
     */
    protected int findMaxAtcByBlood(List<Card> cards, int blood, int maxAtc, boolean canLTBlood){
        int atk = 0;
        int index = -1;
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (
                    (calcCardBlood(card) == blood || (canLTBlood && calcCardBlood(card) < blood))
                    && card.getAtc() > atk
                    && card.getAtc() <= maxAtc
            ){
                    atk = card.getAtc();
                    index = i;
            }
        }
        return index;
    }
    protected int findMaxAtcByBlood(List<Card> cards, int blood, boolean canLTBlood){
        return findMaxAtcByBlood(cards, blood, Integer.MAX_VALUE, canLTBlood);
    }
    /**
     * 寻找能动的怪
     * @param cards
     * @return
     */
    protected int findCanMove(List<Card> cards){
        for (int i = 0; i < cards.size(); i++) {
            if (canMove(cards.get(i))){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找指定费用的卡牌
     * @param cards
     * @param cost
     * @return
     */
    protected int findByCost(List<Card> cards, int cost){
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getCost() == cost){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找小于等于费用的卡牌
     * @param cards
     * @param cost
     * @return
     */
    protected int findByLECost(List<Card> cards, int cost){
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getCost() <= cost){
                return i;
            }
        }
        return -1;
    }

    /**
     * 寻找指定cardId的card
     * @param cards
     * @param cardId
     * @return
     */
    protected int findByCardId(List<Card> cards, String cardId){
        for (int i = cards.size() - 1; i >= 0; i--) {
            String id = cards.get(i).getCardId();
            if (id != null && id.contains(cardId)){
                return i;
            }
        }
        return -1;
    }
    protected int findByCardId(List<Card> cards, Card card){
        String cardId = card.getCardId();
        for (int i = cards.size() - 1; i >= 0; i--) {
            String id = cards.get(i).getCardId();
            if (id != null && id.contains(cardId)){
                return i;
            }
        }
        return -1;
    }
    protected int findByEntityId(List<Card> cards, Card card){
        String entityId = card.getEntityId();
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (Objects.equals(entityId, cards.get(i).getEntityId())){
                return i;
            }
        }
        return -1;
    }
    protected int findByEntityId(List<Card> cards, String entityId){
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (Objects.equals(entityId, cards.get(i).getEntityId())){
                return i;
            }
        }
        return -1;
    }
    protected int findByCardId(List<Card> cards, BaseCard baseCard){
        for (int i = cards.size() - 1; i >= 0; i--) {
            String id = cards.get(i).getCardId();
            if (id != null && id.contains(baseCard.cardId())){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找所有指定费用的card
     * @param cards
     * @param cost
     * @return
     */
    protected List<Card> findAllByCost(List<Card> cards, int cost){
        ArrayList<Card> list = new ArrayList<>();
        for (Card card : cards) {
            if (card.getCost() == cost){
                list.add(card);
            }
        }
        return list;
    }
    /**
     * 寻找第一个有嘲讽的随从
     * @param cards
     * @return
     */
    protected int findTauntCard(List<Card> cards){
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (cards.get(i).isTaunt()){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找非疲劳卡牌
     * @param cards
     * @return
     */
    protected int findNotExhaustedCard(List<Card> cards){
        for (int i = 0; i < cards.size(); i++) {
            if (!cards.get(i).isExhausted()){
                return i;
            }
        }
        return -1;
    }

    /*get*/
    /**
     * 获取可用水晶数
     * @return
     */
    protected int getMyUsableResource(){
        return me.getResources() - me.getResourcesUsed() + me.getTempResources();
    }
    protected float getFloatCardClearanceForFourCard(){
        return GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * GameRationStaticData.CARD_HORIZONTAL_CLEARANCE_WHEN_FOUR_CARD * (GAME_RECT.bottom - GAME_RECT.top);
    }
    protected float getFloatCardClearanceForThreeCard(){
        return GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * GameRationStaticData.CARD_HORIZONTAL_CLEARANCE_WHEN_THREE_CARD * (GAME_RECT.bottom - GAME_RECT.top);
    }
    protected float getFloatCardFirstCardPosForFourCard(){
        return (GAME_RECT.left + GAME_RECT.right >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_FOUR_CARD * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }
    protected float getFloatCardFirstCardPosForThreeCard(){
        return (GAME_RECT.left + GAME_RECT.right >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_THREE_CARD * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }

    /**
     * 获取指定手牌位置
     * @param size
     * @param handIndex
     * @return
     */
    protected int[] getMyHandCardPos(int size, int handIndex){
        float clearance = GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * HAND_CARD_HORIZONTAL_CLEARANCE_RATION[size - 1] * (GAME_RECT.bottom - GAME_RECT.top),
                firstCardPos = (GAME_RECT.left + GAME_RECT.right >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * FIRST_HAND_CARD_HORIZONTAL_TO_CENTER_RATION[size - 1] * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        return new int[]{
                (int) (firstCardPos + handIndex * clearance) + RandomUtil.getRandom(-5, 5),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * HAND_CARD_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5, 5))
        };
    }
    /**
     * 获取指定战场位置
     * @param size
     * @param playIndex
     * @return
     */
    protected int[] getMyPlayCardPos(int size, int playIndex){
        return calcPlayCardPos(size, playIndex, MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION);
    }
    protected int[] getRivalPlayCardPos(int size, int playIndex){
        return calcPlayCardPos(size, playIndex, RIVAL_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION);
    }

    private int[] calcPlayCardPos(int size, int playIndex, float rivalPlayCardVerticalToBottomRation) {
        float clearance = (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * PLAY_CARD_HORIZONTAL_CLEARANCE_RATION;
        int x;
        if ((size & 1) == 0){
            x =  (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (-(size >> 1) + 0.5 + playIndex) * clearance);
        }else {
            x = (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (-(size >> 1) + playIndex) * clearance);
        }
        return new int[]{
                x,
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * rivalPlayCardVerticalToBottomRation) + RandomUtil.getRandom(-5, 5)
        };
    }

    protected int[] getRivalHeroPos(){
        return new int[]{
                (GAME_RECT.right + GAME_RECT.left >> 1) + RandomUtil.getRandom(-10 , 10),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * RIVAL_HERO_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-15 , 15))
        };
    }
    protected int[] getMyHeroPos(){
        return new int[]{
                (GAME_RECT.right + GAME_RECT.left >> 1) + RandomUtil.getRandom(-10 , 10),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * MY_HERO_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-15 , 15))
        };
    }
    protected int[] getMyPowerPos(){
        return new int[]{
                (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + POWER_HORIZONTAL_TO_CENTER_RATION * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * (GAME_RECT.bottom - GAME_RECT.top) + RandomUtil.getRandom(-5 , 5)),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * POWER_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5 , 5))
        };
    }

    protected boolean cardEquals(Card longCard, BaseCard shortCard){
        return longCard != null && longCard.getCardId().contains(shortCard.cardId());
    }

    /**
     * 能否被指向
     * @param card
     * @return
     */
    protected boolean canSpellPointedToRival(Card card){
        return canPointedToRival(card) && !isImmunityMagic(card);
    }

    protected boolean canSpellPointedToMe(Card card){
        return canPointedToMe(card) && !isImmunityMagic(card);
    }
    protected boolean canPointedToRival(Card card){
        return !(card.isImmune() || card.isStealth() || card.isDormantAwakenConditionEnchant());
    }

    protected boolean canPointedToMe(Card card){
        return !(card.isImmune() || card.isDormantAwakenConditionEnchant() || isImmunityMagic(card));
    }

    /**
     * 是不是魔免
     * @param card
     * @return
     */
    protected boolean isImmunityMagic(Card card){
        return card.isCantBeTargetedByHeroPowers() && card.isCantBeTargetedBySpells();
    }

    protected boolean canMove(Card card){
        return !(card.isExhausted() || card.isFrozen() || card.isDormantAwakenConditionEnchant() || card.getAtc() <= 0);
    }
}
