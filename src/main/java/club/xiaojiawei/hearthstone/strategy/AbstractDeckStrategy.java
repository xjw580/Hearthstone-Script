package club.xiaojiawei.hearthstone.strategy;

import club.xiaojiawei.hearthstone.entity.Card;
import club.xiaojiawei.hearthstone.entity.CardMes;
import club.xiaojiawei.hearthstone.entity.Player;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.utils.MouseUtil;
import club.xiaojiawei.hearthstone.utils.RandomUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static club.xiaojiawei.hearthstone.constant.GameRatioConst.*;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:29
 */
@Slf4j
public abstract class AbstractDeckStrategy implements Strategy<Object>{

    protected static final float[] FIRST_HAND_CARD_HORIZONTAL_TO_CENTER_RATION = new float[]{
            (float) 0.033, (float) 0.08, (float) 0.123, (float) 0.167, (float) 0.177, (float) 0.193, (float) 0.203, (float) 0.213, (float) 0.22, (float) 0.227
    };
    protected static final float[] HAND_CARD_HORIZONTAL_CLEARANCE_RATION = new float[]{
            0, (float) 0.09, (float) 0.09, (float) 0.087, (float) 0.07, (float) 0.057, (float) 0.05, (float) 0.042, (float) 0.037, (float) 0.034
    };
    protected static final float HAND_CARD_VERTICAL_TO_BOTTOM_RATION = (float) 0.059;
    protected static final float RIVAL_HERO_VERTICAL_TO_BOTTOM_RATION = (float) 0.801;
    protected static final float PLAY_CARD_HORIZONTAL_CLEARANCE_RATION = (float) 0.097;
    protected static final float MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION = (float) 0.45;
    protected static final float RIVAL_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION = (float) 0.62;
    protected static final float POWER_VERTICAL_TO_BOTTOM_RATION = (float) 0.23;
    protected static final float POWER_HORIZONTAL_TO_CENTER_RATION = (float) 0.133;
    protected static final float TURN_OVER_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.54;
    protected static final float TURN_OVER_BUTTON_HORIZONTAL_TO_CENTER_RATION = (float) 0.417;
    public static final float CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.23;
    protected final Map<String, CardMes> cardMap;

    /**
     * 每次行动后停顿时间
     */
    protected static final int ACTION_INTERVAL = 3500;
    private volatile static boolean myTurn;

    public static boolean isMyTurn() {
        return myTurn;
    }

    public static void setMyTurn(boolean myTurn) {
        AbstractDeckStrategy.myTurn = myTurn;
    }

    public AbstractDeckStrategy(Map<String, CardMes> cardMap) {
        this.cardMap = cardMap;
    }

    @Override
    public void afterInto(Object o) {
        log.info("执行换牌策略");
        afterIntoReplaceCardPhase(o);
        WinDef.RECT gameRect = SystemUtil.getRect(Core.getGameHWND());
//        点击确认
        MouseUtil.leftButtonClick(
                ((gameRect.right + gameRect.left) >> 1) + RandomUtil.getRandom(-10, 10),
                (int) (gameRect.bottom - (gameRect.bottom - gameRect.top) * CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5, 5))
        );
    }

    @Override
    public void afterInto() {
        afterInto(null);
    }

    public void afterIntoMyTurn(){
        myTurn = true;
        outCard();
//        点击回合结束按钮
        clickTurnOverButton();
        myTurn = false;
    }

    /**
     * 初始换牌阶段策略
     * @param o
     */
    protected abstract void afterIntoReplaceCardPhase(Object o);

    /**
     * 正常出牌策略
     */
    protected abstract void outCard();

    /**
     * 寻找指定cardId
     * @param cards
     * @param cardId
     * @return
     */
    protected int findByCardId(List<Card> cards, String cardId){
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (Objects.equals(cards.get(i).getCardId(), cardId)){
                return i;
            }
        }
        return -1;
    }

    /**
     * 倒序寻找第一个有嘲讽的怪
     * @param cards
     * @return
     */
    protected int findTaunt(List<Card> cards){
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (cards.get(i).isTaunt()){
                return i;
            }
        }
        return -1;
    }

    /**
     * 计算总攻击力
     * @param cards
     * @return
     */
    protected int calcTotalAtc(List<Card> cards){
        int atc = 0;
        for (Card card : cards) {
            if (!card.isExhausted() && !card.isFrozen()){
                atc += card.getAtc();
            }
        }
        return atc;
    }

    /**
     * 计算总攻击力，不考虑疲劳等
     * @param cards
     * @return
     */
    protected int calcPureTotalAtc(List<Card> cards){
        int atc = 0;
        for (Card card : cards) {
            atc += card.getAtc();
        }
        return atc;
    }

    protected static final double HEALTH_WEIGHT = 0.4;
    protected static final double ATC_WEIGHT = 0.6;
    protected static final double FREE_EAT_MAX = 5;

    /**
     * 如何白吃
     * @param rivalPlayCards
     * @param myPlayCard
     * @param allowNotFreeEat 是否强制白吃
     * @return
     */
    protected int bestFreeEat(List<Card> rivalPlayCards, Card myPlayCard, boolean allowNotFreeEat){
        int index = -1;
        double weight = 0;
        int atc = myPlayCard.getAtc();
        int health = myPlayCard.getHealth() - myPlayCard.getDamage();
        double myWeight = atc * ATC_WEIGHT + health * HEALTH_WEIGHT;
//        寻找能白吃的
        for (int i = rivalPlayCards.size() - 1; i >= 0 ; i--) {
            Card card = rivalPlayCards.get(i);
            if (!card.isStealth() && card.getHealth() - card.getDamage() <= atc && (card.getAtc() < health || myPlayCard.isDivineShield())){
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
                if (!card.isStealth() && card.getHealth() - card.getDamage() <= atc){
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
     * 计算如何白吃嘲讽
     * @param myPlayCards
     * @param rivalTarget
     * @return
     */
    protected List<Integer> freeEatTaunt(List<Card> myPlayCards, Card rivalTarget){
        TreeSet<List<Integer>> result = new TreeSet<>(Comparator.comparingInt(o -> o.get(o.size() - 1)));
        recursive(result, myPlayCards, new ArrayList<>(), rivalTarget.getHealth()  - rivalTarget.getDamage(), rivalTarget.getAtc(), 0, 0);
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
    private void recursive(TreeSet<List<Integer>> result, List<Card> cards, List<Integer> list, int health, int atc, int atcSum, int index){
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
            recursive(result, cards, list, health, atc, atcSum, index + 1);
            list.remove(list.size() - 1);
            atcSum -= card.getAtc();
        }
//        不选
        recursive(result, cards, list, health, atc,  atcSum, index + 1);
    }

    /**
     * 是否存在指定费用的牌
     * @param cards
     * @param cost
     * @return
     */
    protected boolean existCost(List<Card> cards, int cost){
        for (Card card : cards) {
            if (card.getCost() == cost){
                return true;
            }
        }
        return false;
    }

    /**
     * 寻找能动的怪
     * @param cards
     * @return
     */
    protected int findCanMove(List<Card> cards){
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (!card.isExhausted() && !card.isFrozen()){
                return i;
            }
        }
        return -1;
    }

    /**
     * 寻找指定生命值的怪中攻击力最高的
     * @param cards
     * @param health
     * @return
     */
    protected int findHealthAndAtkMax(List<Card> cards, int health){
        int atk = 0;
        int index = -1;
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.getHealth() - card.getDamage() == health){
                if (card.getAtc() > atk){
                    atk = card.getAtc();
                    index = i;
                }
            }
        }
        return index;
    }

    /**
     * 点击悬浮卡牌，如发现
     * @param clearance
     * @param firstCardPos
     * @param rect
     * @param floatIndex
     */
    protected void clickFloatCard(float clearance, float firstCardPos, WinDef.RECT rect, int floatIndex){
        MouseUtil.leftButtonClick(
                (int) (firstCardPos + floatIndex * clearance) + RandomUtil.getRandom(-10, 10),
                (rect.bottom + rect.top >> 1) + RandomUtil.getRandom(-15, 15)
        );
        SystemUtil.delayMedium();
    }

    protected float getFloatCardClearanceForFourCard(WinDef.RECT rect){
        return GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * CARD_HORIZONTAL_CLEARANCE_WHEN_FOUR_CARD * (rect.bottom - rect.top);
    }

    protected float getFloatCardClearanceForThreeCard(WinDef.RECT rect){
        return GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * CARD_HORIZONTAL_CLEARANCE_WHEN_THREE_CARD * (rect.bottom - rect.top);
    }

    protected float getFloatCardFirstCardPosForFourCard(WinDef.RECT rect){
        return (rect.left + rect.right >> 1) - (rect.bottom - rect.top) * FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_FOUR_CARD * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }

    protected float getFloatCardFirstCardPosForThreeCard(WinDef.RECT rect){
        return (rect.left + rect.right >> 1) - (rect.bottom - rect.top) * FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_THREE_CARD * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }

    protected int[] getMyHandPos(int length, int handIndex, WinDef.RECT rect){
        float clearance = GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * HAND_CARD_HORIZONTAL_CLEARANCE_RATION[length - 1] * (rect.bottom - rect.top),
                firstCardPos = (rect.left + rect.right >> 1) - (rect.bottom - rect.top) * FIRST_HAND_CARD_HORIZONTAL_TO_CENTER_RATION[length - 1] * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        return new int[]{
                (int) (firstCardPos + handIndex * clearance) + RandomUtil.getRandom(-5, 5),
                (int) (rect.bottom - (rect.bottom - rect.top) * HAND_CARD_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5, 5))
        };
    }

    protected int[] getMyPlayPos(int length, int playIndex, WinDef.RECT rect){
        float clearance = (rect.bottom - rect.top) * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * PLAY_CARD_HORIZONTAL_CLEARANCE_RATION;
        int x;
        if ((length & 1) == 0){
            x =  (int) ((rect.right + rect.left >> 1) + (-(length >> 1) + 0.5 + playIndex) * clearance);
        }else {
            x = (int) ((rect.right + rect.left >> 1) + (-(length >> 1) + playIndex) * clearance);
        }
        return new int[]{
                x,
                (int) (rect.bottom - (rect.bottom - rect.top) * MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
        };
    }

    protected int[] getRivalPlayPos(int length, int playIndex, WinDef.RECT rect){
        float clearance = (rect.bottom - rect.top) * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * PLAY_CARD_HORIZONTAL_CLEARANCE_RATION;
        int x;
        if ((length & 1) == 0){
            x =  (int) ((rect.right + rect.left >> 1) + (-(length >> 1) + 0.5 + playIndex) * clearance);
        }else {
            x = (int) ((rect.right + rect.left >> 1) + (-(length >> 1) + playIndex) * clearance);
        }
        return new int[]{
                x,
                (int) (rect.bottom - (rect.bottom - rect.top) * RIVAL_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
        };
    }

    protected int[] getRivalHeroPos(WinDef.RECT rect){
        return new int[]{
                (rect.right + rect.left >> 1) + RandomUtil.getRandom(-10 , 10),
                (int) (rect.bottom - (rect.bottom - rect.top) * RIVAL_HERO_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-15 , 15))
        };
    }

    protected int[] getMyPowerPos(WinDef.RECT rect){
        return new int[]{
                (int) ((rect.right + rect.left >> 1) + POWER_HORIZONTAL_TO_CENTER_RATION * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * (rect.bottom - rect.top) + RandomUtil.getRandom(-5 , 5)),
                (int) (rect.bottom - (rect.bottom - rect.top) * POWER_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5 , 5))
        };
    }

    /**
     * 从我方手牌放入我方战场
     * @param handIndex
     * @param playIndex
     * @param me
     */
    protected void myHandPointToMyPlay(int handIndex, int playIndex, Player me){
        myHandPointToMyPlayForQuick(handIndex, playIndex, me);
        ROBOT.delay(ACTION_INTERVAL);
    }
    private int[] myHandPointToMyPlayForQuick(int handIndex, int playIndex, Player me){
        if (handIndex < 0 || playIndex < 0){
            return null;
        }
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        int[] handPos = getMyHandPos(me.getHandArea().getCards().size(), handIndex, rect);
        int[] playPos = getMyPlayPos(me.getPlayArea().getCards().size() + 1, playIndex, rect);
        MouseUtil.leftButtonDrag(
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
     * @param me
     */
    protected void myHandPointToMyPlay(int handIndex, Player me){
        myHandPointToMyPlayForQuick(handIndex, me);
        ROBOT.delay(ACTION_INTERVAL);
    }
    private int[] myHandPointToMyPlayForQuick(int handIndex, Player me){
        if (handIndex < 0){
            return null;
        }
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        int[] handPos = getMyHandPos(me.getHandArea().getCards().size(), handIndex, rect);
        int endY = (int) (rect.bottom - MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION * (rect.bottom - rect.top));
        MouseUtil.leftButtonDrag(
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
     * @param me
     */
    protected void myHandPointToRivalHero(int myHandIndex, Player me){
        myHandPointToRivalHeroForQuick(myHandIndex, me);
        ROBOT.delay(ACTION_INTERVAL);
    }
    private int[] myHandPointToRivalHeroForQuick(int myHandIndex, Player me){
        if (myHandIndex < 0){
            return null;
        }
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        int[] handPos = getMyHandPos(me.getHandArea().getCards().size(), myHandIndex, rect);
        int[] rivalHeroPos = getRivalHeroPos(rect);
        MouseUtil.leftButtonDrag(
                handPos[0],
                handPos[1],
                rivalHeroPos[0],
                rivalHeroPos[1]
        );
        return rivalHeroPos;
    }

    /**
     * 从我方手牌指向对方战场
     * @param myHandIndex
     * @param rivalPlayIndex
     * @param me
     * @param rival
     */
    protected void myHandPointToRivalPlay(int myHandIndex, int rivalPlayIndex, Player me, Player rival){
        myHandPointToRivalPlayForQuick(myHandIndex, rivalPlayIndex, me, rival);
        ROBOT.delay(ACTION_INTERVAL);
    }
    private int[] myHandPointToRivalPlayForQuick(int myHandIndex, int rivalPlayIndex, Player me, Player rival){
        if (myHandIndex < 0 || rivalPlayIndex < 0){
            return null;
        }
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        int[] handPos = getMyHandPos(me.getHandArea().getCards().size(), myHandIndex, rect);
        int[] rivalPlayPos = getRivalPlayPos(rival.getPlayArea().getCards().size(), rivalPlayIndex, rect);
        MouseUtil.leftButtonDrag(
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
     * @param me
     */
    protected void myPlayPointToRivalHero(int myPlayIndex, Player me){
        myPlayPointToRivalHeroForQuick(myPlayIndex, me);
        ROBOT.delay(ACTION_INTERVAL);
    }
    private void myPlayPointToRivalHeroForQuick(int myPlayIndex, Player me){
        if (myPlayIndex < 0){
            return;
        }
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        int[] playPos = getMyPlayPos(me.getPlayArea().getCards().size(), myPlayIndex, rect);
        int[] rivalHeroPos = getRivalHeroPos(rect);
        MouseUtil.leftButtonDrag(
                playPos[0],
                playPos[1],
                rivalHeroPos[0],
                rivalHeroPos[1]
        );
    }

    /**
     * 从我方战场指向对方战场
     * @param myPlayIndex
     * @param rivalPlayIndex
     * @param me
     * @param rival
     */
    protected void myPlayPointToRivalPlay(int myPlayIndex, int rivalPlayIndex, Player me, Player rival){
        myPlayPointToRivalPlayForQuick(myPlayIndex, rivalPlayIndex, me, rival);
        ROBOT.delay(ACTION_INTERVAL);
    }
    private void myPlayPointToRivalPlayForQuick(int myPlayIndex, int rivalPlayIndex, Player me, Player rival){
        if (myPlayIndex < 0 || rivalPlayIndex < 0){
            return;
        }
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        int[] myPlayPos = getMyPlayPos(me.getPlayArea().getCards().size(), myPlayIndex, rect);
        int[] rivalPlayPos = getRivalPlayPos(rival.getPlayArea().getCards().size(), rivalPlayIndex, rect);
        MouseUtil.leftButtonDrag(
                myPlayPos[0],
                myPlayPos[1],
                rivalPlayPos[0],
                rivalPlayPos[1]
        );
    }

    /**
     * 点击我方技能
     */
    protected void clickMyPower(){
        int[] myPowerPos = getMyPowerPos(SystemUtil.getRect(Core.getGameHWND()));
        MouseUtil.leftButtonClick(myPowerPos[0], myPowerPos[1]);
        ROBOT.delay(ACTION_INTERVAL);
    }

    /**
     * 点击回合结束按钮
     */
    protected void clickTurnOverButton(){
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        MouseUtil.leftButtonClick(
                (int) ((rect.right + rect.left >> 1) + (rect.bottom - rect.top) * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * TURN_OVER_BUTTON_HORIZONTAL_TO_CENTER_RATION + RandomUtil.getRandom(-5, 5)),
                (int) (rect.bottom - (rect.bottom - rect.top) * TURN_OVER_BUTTON_VERTICAL_TO_BOTTOM_RATION)
        );
    }

    protected void myHandPointToMyPlayThenPointToMyPlay(int handIndex, int playIndex, int thenPlayIndex, Player me){
        if (handIndex < 0 || playIndex < 0){
            return;
        }
        int[] playPos = myHandPointToMyPlayForQuick(handIndex, playIndex, me);
        SystemUtil.delayMedium();
        if (playIndex <= thenPlayIndex){
            thenPlayIndex++;
        }
        int[] thenPlayPos = getMyPlayPos(me.getPlayArea().getCards().size() + 1, thenPlayIndex, SystemUtil.getRect(Core.getGameHWND()));
        MouseUtil.leftButtonMoveThenClick(
                playPos[0],
                playPos[1],
                thenPlayPos[0],
                thenPlayPos[1]
        );
        ROBOT.delay(ACTION_INTERVAL);
    }

    protected void myHandPointToMyPlayThenPointToRivalPlay(int myHandIndex, int myPlayIndex, int rivalPlayIndex, Player me, Player rival){
        if (myHandIndex < 0 || myPlayIndex < 0){
            return;
        }
        int[] playPos = myHandPointToMyPlayForQuick(myHandIndex, myPlayIndex, me);
        SystemUtil.delayMedium();
        if (myPlayIndex <= rivalPlayIndex){
            rivalPlayIndex++;
        }
        int[] rivalPlayPos = getRivalPlayPos(rival.getPlayArea().getCards().size(), rivalPlayIndex, SystemUtil.getRect(Core.getGameHWND()));
        MouseUtil.leftButtonMoveThenClick(
                playPos[0],
                playPos[1],
                rivalPlayPos[0],
                rivalPlayPos[1]
        );
        ROBOT.delay(ACTION_INTERVAL);
    }

    protected void myHandPointToMyPlayThenPointToRivalHero(int myHandIndex, int myPlayIndex, Player me){
        if (myHandIndex < 0 || myPlayIndex < 0){
            return;
        }
        int[] playPos = myHandPointToMyPlayForQuick(myHandIndex, myPlayIndex, me);
        SystemUtil.delayMedium();
        int[] rivalHeroPos = getRivalHeroPos(SystemUtil.getRect(Core.getGameHWND()));
        MouseUtil.leftButtonMoveThenClick(
                playPos[0],
                playPos[1],
                rivalHeroPos[0],
                rivalHeroPos[1]
        );
        ROBOT.delay(ACTION_INTERVAL);
    }
}
