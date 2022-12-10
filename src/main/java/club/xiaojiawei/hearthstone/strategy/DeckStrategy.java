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

import static club.xiaojiawei.hearthstone.constant.GameConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:29
 */
@Slf4j
public abstract class DeckStrategy implements Strategy<Object>{

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
    protected final Map<String, CardMes> cardMap;

    protected static final int ACTION_INTERVAL = 3000;

    public DeckStrategy(Map<String, CardMes> cardMap) {
        this.cardMap = cardMap;
    }

    @Override
    public void afterInto(Object o) {
        log.info("执行换牌策略");
        afterIntoReplaceCardPhase(o);
    }

    @Override
    public void afterInto() {
        afterInto(null);
    }

    /**
     * 初始换牌阶段策略
     * @param o
     */
    public abstract void afterIntoReplaceCardPhase(Object o);

    /**
     * 正常出牌策略
     */
    public abstract void afterIntoMyTurn();

    protected int findByCardId(List<Card> cards, String cardId){
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (Objects.equals(cards.get(i).getCardId(), cardId)){
                return i;
            }
        }
        return -1;
    }

    protected int findTaunt(List<Card> cards){
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (cards.get(i).isTaunt()){
                return i;
            }
        }
        return -1;
    }

    protected int calcAtc(List<Card> cards){
        int atc = 0;
        for (Card card : cards) {
            if (!card.isExhausted() && !card.isFrozen()){
                atc += card.getAtc();
            }
        }
        return atc;
    }

    protected static final double HEALTH_WEIGHT = 0.4;
    protected static final double ATC_WEIGHT = 0.6;
    protected static final double FREE_EAT_MAX = 1;

    protected int bestFreeEat(List<Card> rivalPlayCards, Card myPlayCard, boolean allowNotFreeEat){
        int index = -1;
        double weight = 0;
        int atc = myPlayCard.getAtc();
        int health = myPlayCard.getHealth() - myPlayCard.getDamage();
        double maxFreeEatWeight = (atc - FREE_EAT_MAX) * ATC_WEIGHT + (health - FREE_EAT_MAX) * HEALTH_WEIGHT;
//        寻找能白吃的
        for (int i = rivalPlayCards.size() - 1; i >= 0 ; i--) {
            Card card = rivalPlayCards.get(i);
            if (!card.isStealth() && card.getHealth() - card.getDamage() <= atc && (card.getAtc() < health || myPlayCard.isDivineShield())){
                double newWeight = (card.getHealth()  - card.getDamage()) * HEALTH_WEIGHT + card.getAtc() * ATC_WEIGHT;
                if (newWeight > weight && maxFreeEatWeight >= newWeight ){
                    weight = newWeight;
                    index = i;
                }
            }
        }
        if (allowNotFreeEat && index == -1){
//            寻找比较赚的解法
            double myWeight = health * HEALTH_WEIGHT + (atc + 1) * ATC_WEIGHT;
            weight = 0;
            for (int i = rivalPlayCards.size() - 1; i >= 0 ; i--) {
                Card card = rivalPlayCards.get(i);
                if (!card.isStealth() && card.getHealth() - card.getDamage() <= atc){
                    double newWeight = (card.getHealth() - card.getDamage()) * HEALTH_WEIGHT + card.getAtc() * ATC_WEIGHT;
                    if (newWeight >= myWeight && newWeight > weight){
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


    private void recursive(TreeSet<List<Integer>> result, List<Card> cards, List<Integer> list, int health, int atc, int atcSum, int index){
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
            recursive(result, cards, list, health, atc, atcSum, index + 1);
            list.remove(list.size() - 1);
            atcSum -= card.getAtc();
        }
        recursive(result, cards, list, health, atc,  atcSum, index + 1);
    }

    protected boolean existCost(List<Card> cards, int cost){
        for (Card card : cards) {
            if (card.getCost() == cost){
                return true;
            }
        }
        return false;
    }

    protected int findCanMove(List<Card> cards){
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (!card.isExhausted() && !card.isFrozen()){
                return i;
            }
        }
        return -1;
    }

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

    protected void clickFloatCard(float clearance, float firstCardPos, WinDef.RECT rect, int i){
        MouseUtil.leftButtonClick(
                (int) (firstCardPos + i * clearance) + RandomUtil.getRandom(-10, 10),
                (rect.bottom + rect.top >> 1) + RandomUtil.getRandom(-15, 15)
        );
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

    protected int[] myHandPointToMyPlay(int handIndex, int playIndex, Player me){
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

    protected int[] myHandPointToMyPlay(int handIndex, Player me){
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


    protected int[] myHandPointToRivalHero(int myHandIndex, Player me){
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

    protected int[] myHandPointToRivalPlay(int myHandIndex, int rivalPlayIndex, Player me, Player rival){
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

    protected void myPlayPointToRivalHero(int myPlayIndex, Player me){
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

    protected void myPlayPointToRivalPlay(int myPlayIndex, int rivalPlayIndex, Player me, Player rival){
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

    protected void myHandPointToMyPlayAndPointToMyPlay(int handIndex, int playIndex, int thenPlayIndex, Player me){
        if (handIndex < 0 || playIndex < 0){
            return;
        }
        int[] playPos = myHandPointToMyPlay(handIndex, playIndex, me);
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
    }

    protected void myHandPointToMyPlayAndPointToRivalPlay(int myHandIndex, int myPlayIndex, int rivalPlayIndex, Player me, Player rival){
        if (myHandIndex < 0 || myPlayIndex < 0){
            return;
        }
        int[] playPos = myHandPointToMyPlay(myHandIndex, myPlayIndex, me);
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
    }

    protected void myHandPointToMyPlayAndPointToRivalHero(int myHandIndex, int myPlayIndex, Player me){
        if (myHandIndex < 0 || myPlayIndex < 0){
            return;
        }
        int[] playPos = myHandPointToMyPlay(myHandIndex, myPlayIndex, me);
        SystemUtil.delayMedium();
        int[] rivalHeroPos = getRivalHeroPos(SystemUtil.getRect(Core.getGameHWND()));
        MouseUtil.leftButtonMoveThenClick(
                playPos[0],
                playPos[1],
                rivalHeroPos[0],
                rivalHeroPos[1]
        );
    }

    protected void clickMyPower(){
        int[] myPowerPos = getMyPowerPos(SystemUtil.getRect(Core.getGameHWND()));
        MouseUtil.leftButtonClick(myPowerPos[0], myPowerPos[1]);
    }

    protected void clickTurnOverButton(){
        WinDef.RECT rect = SystemUtil.getRect(Core.getGameHWND());
        MouseUtil.leftButtonClick(
                (int) ((rect.right + rect.left >> 1) + (rect.bottom - rect.top) * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * TURN_OVER_BUTTON_HORIZONTAL_TO_CENTER_RATION + RandomUtil.getRandom(-5, 5)),
                (int) (rect.bottom - (rect.bottom - rect.top) * TURN_OVER_BUTTON_VERTICAL_TO_BOTTOM_RATION)
        );
    }
}
