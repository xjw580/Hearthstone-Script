package club.xiaojiawei.strategy;

import club.xiaojiawei.data.GameStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.Player;
import club.xiaojiawei.entity.area.HandArea;
import club.xiaojiawei.entity.area.PlayArea;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static club.xiaojiawei.data.ScriptStaticData.GAME_RECT;
import static club.xiaojiawei.data.ScriptStaticData.ROBOT;
import static club.xiaojiawei.enums.CardTypeEnum.MINION;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:29
 */
@Slf4j
public class Test {
    /*calc*/
    /**
     * 计算总攻击力
     * @param cards
     * @return
     */
    protected int calcTotalAtc(List<Card> cards){
        int atc = 0;
        for (Card card : cards) {
            if (!card.isExhausted() && !card.isFrozen() && !card.isSpawnTimeCount()){
                atc += card.getAtc();
                if (card.isWindFury()){
                    atc += card.getAtc();
                }
            }
        }

        return atc;
    }
    /**
     * 计算包括英雄在内的总攻击力
     * @param cards
     * @return
     */
    protected int calcTotalAtcUltimate(List<Card> cards){
        int atc = calcTotalAtc(cards);
        atc += myPlayArea.getHero().getAtc();
        if (myPlayArea.getHero().isWindFury()){
            atc += myPlayArea.getHero().getAtc();
        }
        return atc;
    }
    /**
     * 计算纯粹的总攻击力，不考虑能不能动等情况
     * @param cards
     * @return
     */
    protected int calcPureTotalAtc(List<Card> cards){
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
     * 计算我的怪如何白吃对面的怪
     * @param myPlayCard
     * @param allowNotFreeEat 是否强制白吃
     * @return
     */
    protected int calcMyCardFreeEat(Card myPlayCard, boolean allowNotFreeEat){
        int index = -1;
        double weight = 0;
        int atc = myPlayCard.getAtc();
        int health = myPlayCard.getHealth() - myPlayCard.getDamage();
        double myWeight = atc * ATC_WEIGHT + health * HEALTH_WEIGHT;
//        寻找能白吃的
        for (int i = rivalPlayCards.size() - 1; i >= 0 ; i--) {
            Card card = rivalPlayCards.get(i);
            if (!card.isStealth() && !card.isSpawnTimeCount() && card.getCardType() == MINION && card.getHealth() - card.getDamage() <= atc && (card.getAtc() < health || myPlayCard.isDivineShield())){
                double newWeight = (card.getHealth()  - card.getDamage()) * HEALTH_WEIGHT + card.getAtc() * ATC_WEIGHT;
//                寻找最优白吃方法，既要白吃又不能白吃过头忽略打脸，如55白吃11这种
                if (newWeight > weight && myWeight - newWeight < FREE_EAT_MAX){
                    weight = newWeight;
                    index = i;
                }
            }
        }
        if (allowNotFreeEat && index == -1){
//            白吃不了，寻找比较赚的解法
            double myWeightPlus = health * HEALTH_WEIGHT + (atc + 1) * ATC_WEIGHT;
            weight = 0;
            for (int i = rivalPlayCards.size() - 1; i >= 0 ; i--) {
                Card card = rivalPlayCards.get(i);
                if (!card.isStealth() && !card.isSpawnTimeCount() && card.getHealth() - card.getDamage() <= atc){
                    double newWeight = (card.getHealth() - card.getDamage()) * HEALTH_WEIGHT + card.getAtc() * ATC_WEIGHT;
                    if (newWeight >= myWeightPlus && newWeight > weight){
                        index = i;
                        weight = newWeight;
                    }
                }
            }
        }
        return index;
    }
    /**
     * 计算如何白吃对面的嘲讽
     * @param rivalTauntCard
     * @return
     */
    protected List<Integer> calcFreeEatRivalTaunt(Card rivalTauntCard){
        TreeSet<List<Integer>> result = new TreeSet<>(Comparator.comparingInt(o -> o.get(o.size() - 1)));
        calcFreeEatRivalTauntRecursive(result, myPlayCards, new ArrayList<>(), rivalTauntCard.getHealth()  - rivalTauntCard.getDamage(), rivalTauntCard.getAtc(), 0, 0);
        return result.size() == 0? null : result.first();
    }
    /**
     * 穷举法
     * @param result
     * @param cards
     * @param list
     * @param health
     * @param atc
     * @param atcSum
     * @param index
     */
    private void calcFreeEatRivalTauntRecursive(TreeSet<List<Integer>> result, List<Card> cards, List<Integer> list, int health, int atc, int atcSum, int index){
//        终止条件
        if (atcSum >= health){
            list.add(atcSum);
            ArrayList<Integer> temp = new ArrayList<>(list);
            list.remove(list.size() - 1);
            if (result.contains(temp)){
                double oldFreeEatCount = 0, newFreeEatCount = 0;
                List<Integer> oldList = null;
                for (List<Integer> integers : result) {
                    if (integers.get(integers.size() - 1) == atcSum){
                        oldList = integers;
                        for (int i = 0; i < integers.size() - 1; i++) {
                            Card card = cards.get(i);
                            if (card.getHealth() - card.getDamage() > atc || card.isDivineShield()){
                                oldFreeEatCount++;
                            }
                        }
                    }
                }
                for (Integer integer : list) {
                    Card card = cards.get(integer);
                    if (card.getHealth() - card.getDamage() > atc || card.isDivineShield()){
                        newFreeEatCount++;
                    }
                }
                assert oldList != null;
                if (oldFreeEatCount / oldList.size() < newFreeEatCount / temp.size()){
                    result.remove(temp);
                    result.add(temp);
                }
            }else {
                result.add(temp);
            }
            return;
        }else if (index == cards.size()){
            return;
        }

        Card card = cards.get(index);
        if (!card.isExhausted() && !card.isFrozen() && card.getAtc() > 0){
            list.add(index);
            atcSum += card.getAtc();
//            选
            calcFreeEatRivalTauntRecursive(result, cards, list, health, atc, atcSum, index + 1);
            list.remove(list.size() - 1);
            atcSum -= card.getAtc();
        }
//        不选
        calcFreeEatRivalTauntRecursive(result, cards, list, health, atc,  atcSum, index + 1);
    }
    /*action*/
    /**
     * 从我方手牌放入我方战场
     * @param handIndex
     * @param playIndex
     */
    protected boolean myHandPointToMyPlay(int handIndex, int playIndex){
        if (myPlayArea.isFull()){
            return false;
        }
        myHandPointToMyPlayForQuick(handIndex, playIndex);
        ROBOT.delay(ACTION_INTERVAL);
        return true;
    }
    protected void myHeadPointToMyPlayNoPlace(int handIndex, int playIndex){
        myHandSpellPointToMyPlayForQuick(handIndex, playIndex);
        ROBOT.delay(ACTION_INTERVAL);
    }
    protected void myHeadPointToMyPlayNoPlace(int handIndex){
        myHandSpellPointToMyPlayForQuick(handIndex);
        ROBOT.delay(ACTION_INTERVAL);
    }
    private int[] myHandPointToMyPlayForQuick(int handIndex, int playIndex){
        if (handIndex < 0 || playIndex < 0){
            return null;
        }
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] handPos = getMyHandCardPos(me.getHandArea().getCards().size(), handIndex);
        int[] playPos = getMyPlayCardPos(me.getPlayArea().getCards().size() + 1, playIndex);
        mouseUtil.leftButtonDrag(
                handPos[0],
                handPos[1],
                playPos[0],
                playPos[1]
        );
        return playPos;
    }

    private int[] myHandSpellPointToMyPlayForQuick(int handIndex, int playIndex){
        if (handIndex < 0 || playIndex < 0){
            return null;
        }
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] handPos = getMyHandCardPos(me.getHandArea().getCards().size(), handIndex);
        int[] playPos = getMyPlayCardPos(me.getPlayArea().getCards().size(), playIndex);
        mouseUtil.leftButtonDrag(
                handPos[0],
                handPos[1],
                playPos[0],
                playPos[1]
        );
        return playPos;
    }

    private int[] myHandSpellPointToMyPlayForQuick(int handIndex){
        if (handIndex < 0){
            return null;
        }
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] handPos = getMyHandCardPos(me.getHandArea().getCards().size(), handIndex);
        int[] playPos = {
                (GAME_RECT.right + GAME_RECT.left) >> 1,
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
        };
        mouseUtil.leftButtonDrag(
                handPos[0],
                handPos[1],
                playPos[0],
                playPos[1]
        );
        return playPos;
    }


    /**
     * 从我方手牌放入我方战场，不指定战场位置
     * @param handIndex
     */
    protected boolean myHandPointToMyPlay(int handIndex){
        if (myPlayArea.isFull()){
            return false;
        }
        myHandPointToMyPlayForQuick(handIndex);
        ROBOT.delay(ACTION_INTERVAL);
        return true;
    }
    private int[] myHandPointToMyPlayForQuick(int handIndex){
        if (handIndex < 0){
            return null;
        }
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] handPos = getMyHandCardPos(me.getHandArea().getCards().size(), handIndex);
        int endY = (int) (GAME_RECT.bottom - MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION * (GAME_RECT.bottom - GAME_RECT.top));
        mouseUtil.leftButtonDrag(
                handPos[0],
                handPos[1],
                handPos[0],
                endY
        );
        handPos[1] = endY;
        return handPos;
    }

    /**
     * 从我方手牌指向对方英雄
     * @param myHandIndex
     */
    protected void myHandPointToRivalHero(int myHandIndex){
        myHandPointToRivalHeroForQuick(myHandIndex);
        ROBOT.delay(ACTION_INTERVAL);
    }
    private int[] myHandPointToRivalHeroForQuick(int myHandIndex){
        if (myHandIndex < 0){
            return null;
        }
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] handPos = getMyHandCardPos(me.getHandArea().getCards().size(), myHandIndex);
        int[] rivalHeroPos = getRivalHeroPos();
        mouseUtil.leftButtonDrag(
                handPos[0],
                handPos[1],
                rivalHeroPos[0],
                rivalHeroPos[1]
        );
        return rivalHeroPos;
    }

    protected void myHeroPointToRivalHero(){
        int[] rivalHeroPos = getRivalHeroPos();
        int[] myHeroPos = getMyHeroPos();
        mouseUtil.leftButtonDrag(
                myHeroPos[0], myHeroPos[1],
                rivalHeroPos[0], rivalHeroPos[1]
        );
        ROBOT.delay(ACTION_INTERVAL);
    }
    protected void myHeroPointToRivalPlay(int rivalPlayIndex){
        int[] myHeroPos = getMyHeroPos();
        int[] rivalPlayPos = getRivalPlayPos(rivalPlayCards.size(), rivalPlayIndex);
        mouseUtil.leftButtonDrag(
                myHeroPos[0], myHeroPos[1],
                rivalPlayPos[0], rivalPlayPos[1]
        );
        ROBOT.delay(ACTION_INTERVAL);
    }
    /**
     * 从我方手牌指向对方战场
     * @param myHandIndex
     * @param rivalPlayIndex
     */
    protected void myHandPointToRivalPlay(int myHandIndex, int rivalPlayIndex){
        myHandPointToRivalPlayForQuick(myHandIndex, rivalPlayIndex);
        ROBOT.delay(ACTION_INTERVAL);
    }
    protected void myHandPointToRivalPlay(int myHandIndex, Player me){
        myHandPointToRivalPlayForQuick(myHandIndex);
        ROBOT.delay(ACTION_INTERVAL);
    }
    private int[] myHandPointToRivalPlayForQuick(int myHandIndex, int rivalPlayIndex){
        if (myHandIndex < 0 || rivalPlayIndex < 0){
            return null;
        }
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] handPos = getMyHandCardPos(me.getHandArea().getCards().size(), myHandIndex);
        int[] rivalPlayPos = getRivalPlayPos(rival.getPlayArea().getCards().size(), rivalPlayIndex);
        mouseUtil.leftButtonDrag(
                handPos[0],
                handPos[1],
                rivalPlayPos[0],
                rivalPlayPos[1]
        );
        return rivalPlayPos;
    }

    private int[] myHandPointToRivalPlayForQuick(int myHandIndex){
        if (myHandIndex < 0){
            return null;
        }
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] handPos = getMyHandCardPos(me.getHandArea().getCards().size(), myHandIndex);
        int[] rivalPlayPos = {(GAME_RECT.right + GAME_RECT.left) >> 1, (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * RIVAL_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)};
        mouseUtil.leftButtonDrag(
                handPos[0],
                handPos[1],
                rivalPlayPos[0],
                rivalPlayPos[1]
        );
        return rivalPlayPos;
    }

    /**
     * 从我方战场指向对方英雄
     * @param myPlayIndex
     */
    protected void myPlayPointToRivalHero(int myPlayIndex){
        myPlayPointToRivalHeroForQuick(myPlayIndex);
        ROBOT.delay(ACTION_INTERVAL);
    }
    private void myPlayPointToRivalHeroForQuick(int myPlayIndex){
        if (myPlayIndex < 0){
            return;
        }
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] playPos = getMyPlayCardPos(me.getPlayArea().getCards().size(), myPlayIndex);
        int[] rivalHeroPos = getRivalHeroPos();
        mouseUtil.leftButtonDrag(
                playPos[0],
                playPos[1],
                rivalHeroPos[0],
                rivalHeroPos[1]
        );
        ROBOT.delay(ACTION_INTERVAL);
    }
    protected int calcRivalHeroBlood(){
        return rivalPlayArea.getHero().getHealth() + rivalPlayArea.getHero().getArmor() - rivalPlayArea.getHero().getDamage();
    }

    /**
     * 从我方战场指向对方战场
     * @param myPlayIndex
     * @param rivalPlayIndex
     */
    protected void myPlayPointToRivalPlay(int myPlayIndex, int rivalPlayIndex){
        myPlayPointToRivalPlayForQuick(myPlayIndex, rivalPlayIndex);
        ROBOT.delay(ACTION_INTERVAL);
    }
    private void myPlayPointToRivalPlayForQuick(int myPlayIndex, int rivalPlayIndex){
        if (myPlayIndex < 0 || rivalPlayIndex < 0){
            return;
        }
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] myPlayPos = getMyPlayCardPos(me.getPlayArea().getCards().size(), myPlayIndex);
        int[] rivalPlayPos = getRivalPlayPos(rival.getPlayArea().getCards().size(), rivalPlayIndex);
        mouseUtil.leftButtonDrag(
                myPlayPos[0],
                myPlayPos[1],
                rivalPlayPos[0],
                rivalPlayPos[1]
        );
        ROBOT.delay(ACTION_INTERVAL);
    }

    /**
     * 点击我方技能
     */
    protected void clickMyPower(){
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] myPowerPos = getMyPowerPos();
        mouseUtil.leftButtonClick(myPowerPos[0], myPowerPos[1]);
        ROBOT.delay(ACTION_INTERVAL);
    }

    /**
     * 点击回合结束按钮
     */
    protected void clickTurnOverButton(){
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        mouseUtil.leftButtonClick(
                (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (GAME_RECT.bottom - GAME_RECT.top) * GameStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * TURN_OVER_BUTTON_HORIZONTAL_TO_CENTER_RATION + RandomUtil.getRandom(-5, 5)),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * TURN_OVER_BUTTON_VERTICAL_TO_BOTTOM_RATION)
        );
    }

    protected boolean myHandPointToMyPlayThenPointToMyPlay(int handIndex, int playIndex, int thenPlayIndex){
        if (handIndex < 0 || playIndex < 0 || myPlayArea.isFull()){
            return false;
        }
        int[] playPos = myHandPointToMyPlayForQuick(handIndex, playIndex);
        systemUtil.delayMedium();
        if (playIndex <= thenPlayIndex){
            thenPlayIndex++;
        }
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] thenPlayPos = getMyPlayCardPos(me.getPlayArea().getCards().size() + 1, thenPlayIndex);
        mouseUtil.leftButtonMoveThenClick(
                playPos[0],
                playPos[1],
                thenPlayPos[0],
                thenPlayPos[1]
        );
        ROBOT.delay(ACTION_INTERVAL);
        return true;
    }

    protected boolean myHandPointToMyPlayThenPointToRivalPlay(int myHandIndex, int myPlayIndex, int rivalPlayIndex){
        if (myHandIndex < 0 || myPlayIndex < 0 || myPlayArea.isFull()){
            return false;
        }
        int[] playPos = myHandPointToMyPlayForQuick(myHandIndex, myPlayIndex);
        systemUtil.delayMedium();
        if (myPlayIndex <= rivalPlayIndex){
            rivalPlayIndex++;
        }
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] rivalPlayPos = getRivalPlayPos(rival.getPlayArea().getCards().size(), rivalPlayIndex);
        mouseUtil.leftButtonMoveThenClick(
                playPos[0],
                playPos[1],
                rivalPlayPos[0],
                rivalPlayPos[1]
        );
        ROBOT.delay(ACTION_INTERVAL);
        return true;
    }

    protected boolean myHandPointToMyPlayThenPointToRivalHero(int myHandIndex, int myPlayIndex){
        if (myHandIndex < 0 || myPlayIndex < 0 || myPlayArea.isFull()){
            return false;
        }
        int[] playPos = myHandPointToMyPlayForQuick(myHandIndex, myPlayIndex);
        systemUtil.delayMedium();
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        int[] rivalHeroPos = getRivalHeroPos();
        mouseUtil.leftButtonMoveThenClick(
                playPos[0],
                playPos[1],
                rivalHeroPos[0],
                rivalHeroPos[1]
        );
        ROBOT.delay(ACTION_INTERVAL);
        return true;
    }
    /**
     * 点击悬浮卡牌，如发现
     * @param clearance
     * @param firstCardPos
     * @param floatCardIndex
     */
    protected void clickFloatCard(float clearance, float firstCardPos, int floatCardIndex){
        mouseUtil.leftButtonClick(
                (int) (firstCardPos + floatCardIndex * clearance) + RandomUtil.getRandom(-10, 10),
                (GAME_RECT.bottom + GAME_RECT.top >> 1) + RandomUtil.getRandom(-15, 15)
        );
        systemUtil.delayMedium();
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
            if (card.getHealth() - card.getDamage() == blood){
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
     * 寻找在等于指定血量中攻击力最大的
     * @param cards
     * @param blood 生命值减去伤害等得出
     * @return
     */
    protected int findMaxAtcByBlood(List<Card> cards, int blood){
        int atk = 0;
        int index = -1;
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.getHealth() - card.getDamage() == blood){
                if (card.getAtc() > atk){
                    atk = card.getAtc();
                    index = i;
                }
            }
        }
        return index;
    }
    /**
     * 寻找能动的怪
     * @param cards
     * @return
     */
    protected int findCanMove(List<Card> cards){
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (!card.isExhausted() && !card.isFrozen() && !card.isSpawnTimeCount()){
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
     * 寻找非力竭卡牌
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
    protected int getUsableResource(Player player){
        int resource = player.getResources() - player.getUsedResources() + player.getTempResources();
        log.info("回合开始水晶数:" + resource);
        return resource;
    }
    protected float getFloatCardClearanceForFourCard(){
        return GameStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * GameStaticData.CARD_HORIZONTAL_CLEARANCE_WHEN_FOUR_CARD * (GAME_RECT.bottom - GAME_RECT.top);
    }
    protected float getFloatCardClearanceForThreeCard(){
        return GameStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * GameStaticData.CARD_HORIZONTAL_CLEARANCE_WHEN_THREE_CARD * (GAME_RECT.bottom - GAME_RECT.top);
    }
    protected float getFloatCardFirstCardPosForFourCard(){
        return (GAME_RECT.left + GAME_RECT.right >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * GameStaticData.FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_FOUR_CARD * GameStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }
    protected float getFloatCardFirstCardPosForThreeCard(){
        return (GAME_RECT.left + GAME_RECT.right >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * GameStaticData.FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_THREE_CARD * GameStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }

    /**
     * 获取指定手牌位置
     * @param size
     * @param handIndex
     * @return
     */
    protected int[] getMyHandCardPos(int size, int handIndex){
        float clearance = GameStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * HAND_CARD_HORIZONTAL_CLEARANCE_RATION[size - 1] * (GAME_RECT.bottom - GAME_RECT.top),
                firstCardPos = (GAME_RECT.left + GAME_RECT.right >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * FIRST_HAND_CARD_HORIZONTAL_TO_CENTER_RATION[size - 1] * GameStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
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
        float clearance = (GAME_RECT.bottom - GAME_RECT.top) * GameStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * PLAY_CARD_HORIZONTAL_CLEARANCE_RATION;
        int x;
        if ((size & 1) == 0){
            x =  (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (-(size >> 1) + 0.5 + playIndex) * clearance);
        }else {
            x = (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (-(size >> 1) + playIndex) * clearance);
        }
        return new int[]{
                x,
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
        };
    }
    protected int[] getRivalPlayPos(int length, int playIndex){
        float clearance = (GAME_RECT.bottom - GAME_RECT.top) * GameStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * PLAY_CARD_HORIZONTAL_CLEARANCE_RATION;
        int x;
        if ((length & 1) == 0){
            x =  (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (-(length >> 1) + 0.5 + playIndex) * clearance);
        }else {
            x = (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (-(length >> 1) + playIndex) * clearance);
        }
        return new int[]{
                x,
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * RIVAL_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
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
                (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + POWER_HORIZONTAL_TO_CENTER_RATION * GameStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * (GAME_RECT.bottom - GAME_RECT.top) + RandomUtil.getRandom(-5 , 5)),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * POWER_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5 , 5))
        };
    }


    protected static final double HEALTH_WEIGHT = 0.4;
    protected static final double ATC_WEIGHT = 0.6;
    protected static final double FREE_EAT_MAX = 5;
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
    @Resource
    protected SystemUtil systemUtil;
    @Resource
    protected MouseUtil mouseUtil;
    /**
     * 每次行动后停顿时间
     */
    protected static final int ACTION_INTERVAL = 2500;
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
    private volatile static boolean myTurn;
    public static boolean isMyTurn() {
        return !myTurn;
    }
    public static void setMyTurn(boolean myTurn) {
        Test.myTurn = myTurn;
    }

}
