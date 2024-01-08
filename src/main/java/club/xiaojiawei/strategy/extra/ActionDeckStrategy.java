package club.xiaojiawei.strategy.extra;

import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static club.xiaojiawei.data.GameRationStaticData.*;
import static club.xiaojiawei.data.ScriptStaticData.GAME_RECT;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/5 22:12
 */
@Slf4j
@Component
public class ActionDeckStrategy extends FindDeckStrategy{
    @Resource
    protected MouseUtil mouseUtil;
    /**
     * 每次行动后停顿时间
     */
    protected static final int ACTION_INTERVAL = 3500;

    protected double getFloatCardClearanceForFourCard(){
        return GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * GameRationStaticData.CARD_HORIZONTAL_CLEARANCE_WHEN_FOUR_CARD * (GAME_RECT.bottom - GAME_RECT.top);
    }
    protected double getFloatCardClearanceForThreeCard(){
        return GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * GameRationStaticData.CARD_HORIZONTAL_CLEARANCE_WHEN_THREE_CARD * (GAME_RECT.bottom - GAME_RECT.top);
    }
    protected double getFloatCardFirstCardPosForFourCard(){
        return (GAME_RECT.left + GAME_RECT.right >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_FOUR_CARD * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }
    protected double getFloatCardFirstCardPosForThreeCard(){
        return (GAME_RECT.left + GAME_RECT.right >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_THREE_CARD * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }

    /**
     * 获取指定手牌位置
     * @param size
     * @param handIndex
     * @return
     */
    protected int[] getMyHandCardPos(int size, int handIndex){
        double clearance = GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * HAND_CARD_HORIZONTAL_CLEARANCE_RATION[size - 1] * (GAME_RECT.bottom - GAME_RECT.top),
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
    private int[] getMyPlayCardPos(int size, int playIndex){
        return calcPlayCardPos(size, playIndex, MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION);
    }
    private int[] getRivalPlayCardPos(int size, int playIndex){
        return calcPlayCardPos(size, playIndex, RIVAL_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION);
    }

    private int[] calcPlayCardPos(int size, int playIndex, double playCardVerticalToBottomRation) {
        double clearance = (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * PLAY_CARD_HORIZONTAL_CLEARANCE_RATION;
        int x;
        if ((size & 1) == 0){
            x =  (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (-(size >> 1) + 0.5 + playIndex) * clearance);
        }else {
            x = (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (-(size >> 1) + playIndex) * clearance);
        }
        return new int[]{
                x,
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * playCardVerticalToBottomRation) + RandomUtil.getRandom(-5, 5)
        };
    }

    private int[] getRivalHeroPos(){
        return new int[]{
                (GAME_RECT.right + GAME_RECT.left >> 1) + RandomUtil.getRandom(-10 , 10),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * RIVAL_HERO_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-15 , 15))
        };
    }
    private int[] getMyHeroPos(){
        return new int[]{
                (GAME_RECT.right + GAME_RECT.left >> 1) + RandomUtil.getRandom(-10 , 10),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * MY_HERO_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-15 , 15))
        };
    }
    private int[] getMyPowerPos(){
        return new int[]{
                (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + POWER_HORIZONTAL_TO_CENTER_RATION * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * (GAME_RECT.bottom - GAME_RECT.top) + RandomUtil.getRandom(-5 , 5)),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * POWER_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5 , 5))
        };
    }
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
        if (handIndex < 0 || handIndex >= myHandCards.size()){
            return false;
        }
        Card card = myHandCards.get(handIndex);
        if (myPlayArea.isFull() || card.getCost() > calcMyUsableResource()){
            return false;
        }
        myHandPointToMyPlayForBase(handIndex, myPlayCards.size(), true);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + calcMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.gameCancel();
        return false;
    }
    protected boolean myHandPointToMyPlay(int myHandIndex, int myPlayIndex){
        if (myHandIndex < 0 || myHandIndex >= myHandCards.size() || myPlayIndex >= myPlayCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (myPlayArea.isFull() || card.getCost() > calcMyUsableResource()){
            return false;
        }
        myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + calcMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.gameCancel();
        return false;
    }
    protected boolean myHandPointToMyPlayNoPlace(int myHandIndex, int myPlayIndex){
        if (myPlayIndex < 0 || myHandIndex < 0 || myHandIndex >= myHandCards.size() || myPlayIndex >= myPlayCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > calcMyUsableResource() || !canSpellPointedByMe(myPlayCards.get(myPlayIndex))){
            return false;
        }
        myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, false);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + calcMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.gameCancel();
        return false;
    }

    private int[] myHandPointToMyPlayForBase(int handIndex, int playIndex, boolean insertGap){
        SystemUtil.updateGameRect();
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
        if (myHandIndex < 0 || rivalPlayIndex < 0 || myHandIndex >= myHandCards.size() || rivalPlayIndex >= rivalPlayCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > calcMyUsableResource() || !canSpellPointedByRival(rivalPlayCards.get(rivalPlayIndex))){
            return false;
        }
        SystemUtil.updateGameRect();
        myHandPointTo(myHandIndex, getRivalPlayCardPos(rival.getPlayArea().getCards().size(), rivalPlayIndex));
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + calcMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.gameCancel();
        return false;
    }
    protected boolean myHandPointToRivalHeroNoPlace(int myHandIndex){
        if (myHandIndex < 0 || myHandIndex >= myHandCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > calcMyUsableResource() || !canSpellPointedByRival(rivalPlayArea.getHero())){
            return false;
        }
        SystemUtil.updateGameRect();
        myHandPointTo(myHandIndex, getRivalHeroPos());
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + calcMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.gameCancel();
        return false;
    }
    protected boolean myHandPointToNoPlace(int myHandIndex){
        if (myHandIndex < 0 || myHandIndex >= myHandCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > calcMyUsableResource()){
            return false;
        }
        myHandPointToMyPlayForBase(myHandIndex, -1, false);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + calcMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.gameCancel();
        return false;
    }

    protected boolean myHeroPointToRivalHero(){
        if (!canPointedByRival(rivalPlayArea.getHero()) || !canMove(myPlayArea.getHero()) || calcMyHeroAtc() <= 0){
            return false;
        }
        SystemUtil.updateGameRect();
        myHeroPointTo(getRivalHeroPos());
        SystemUtil.delay(ACTION_INTERVAL);
        return true;
    }
    protected boolean myHeroPointToRivalPlay(int rivalPlayIndex){
        if (
                rivalPlayIndex >= rivalPlayCards.size()
                        || !canPointedByRival(rivalPlayCards.get(rivalPlayIndex))
                        || !canMove(myPlayArea.getHero())
                        || calcMyHeroAtc() <= 0
                        || calcCardBlood(myPlayArea.getHero()) <= rivalPlayCards.get(rivalPlayIndex).getAtc()
        ){
            return false;
        }
        SystemUtil.updateGameRect();
        int[] rivalPlayPos = getRivalPlayCardPos(rivalPlayCards.size(), rivalPlayIndex);
        myHeroPointTo(rivalPlayPos);
        SystemUtil.delay(ACTION_INTERVAL + 750);
        return true;
    }
    private void myHeroPointTo(int[] endPos){
        mouseUtil.leftButtonDrag(
                getMyHeroPos(),
                endPos
        );
    }

    protected void allAtcRivalHero(){
        for (int i = myPlayCards.size() - 1; i >= 0; i--) {
            if (myPlayPointToRivalHero(i)){
                myPlayPointToRivalHero(i);
            }
        }
        if (myHeroPointToRivalHero()){
            myHeroPointToRivalHero();
        }
    }
    protected boolean myPlayPointToRivalHero(int myPlayIndex){
        if (myPlayIndex < 0 || myPlayIndex >= myPlayCards.size()){
            return false;
        }
        Card card = myPlayCards.get(myPlayIndex);
        if (!canPointedByRival(rivalPlayArea.getHero()) || !canMove(card)){
            return false;
        }
        SystemUtil.updateGameRect();
        int[] rivalHeroPos = getRivalHeroPos();
        mouseUtil.leftButtonDrag(
                getMyPlayCardPos(me.getPlayArea().getCards().size(), myPlayIndex),
                rivalHeroPos
        );
        SystemUtil.delay(ACTION_INTERVAL - 700);
        return true;
    }

    /**
     * 从我方战场指向对方战场
     * @param myPlayIndex
     * @param rivalPlayIndex
     */
    protected boolean myPlayPointToRivalPlay(int myPlayIndex, int rivalPlayIndex){
        if (myPlayIndex < 0 || rivalPlayIndex < 0 || myPlayIndex >= myPlayCards.size() || rivalPlayIndex >= rivalPlayCards.size()){
            log.info("下标错误");
            return false;
        }
        Card card = myPlayCards.get(myPlayIndex);
        if (!canPointedByRival(rivalPlayCards.get(rivalPlayIndex)) || !canMove(card)){
            log.info("动不了");
            return false;
        }
        SystemUtil.updateGameRect();
        int[] myPlayPos = getMyPlayCardPos(me.getPlayArea().getCards().size(), myPlayIndex);
        int[] rivalPlayPos = getRivalPlayCardPos(rival.getPlayArea().getCards().size(), rivalPlayIndex);
        mouseUtil.leftButtonDrag(
                myPlayPos[0],
                myPlayPos[1],
                rivalPlayPos[0],
                rivalPlayPos[1]
        );
        SystemUtil.delay(ACTION_INTERVAL + 750);
        return true;
    }

    /**
     * 点击我方技能
     */
    protected boolean clickPower(){
        if (myPlayArea.getPower().isExhausted() || calcMyUsableResource() < myPlayArea.getPower().getCost() || myPlayArea.isFull()){
            return false;
        }
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonClick(getMyPowerPos());
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + calcMyUsableResource());
        return true;
    }

    /**
     * 点击回合结束按钮
     */
    protected void clickTurnOverButton(){
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonClick(
                (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * TURN_OVER_BUTTON_HORIZONTAL_TO_CENTER_RATION + RandomUtil.getRandom(-6, 6)),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * TURN_OVER_BUTTON_VERTICAL_TO_BOTTOM_RATION  + RandomUtil.getRandom(-3, 3))
        );
    }

    protected boolean myHandPointToMyPlayThenPointToMyPlay(int myHandIndex, int myPlayIndex, int thenMyPlayIndex){
        if (myHandIndex < 0 || myHandIndex >= myHandCards.size() || myPlayArea.isFull() || myPlayCards.get(thenMyPlayIndex).isDormantAwakenConditionEnchant() || myHandCards.get(myHandIndex).getCost() > calcMyUsableResource()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        int[] playPos = myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        if (myPlayIndex <= thenMyPlayIndex){
            thenMyPlayIndex++;
        }
        SystemUtil.delayMedium();
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonMoveThenClick(
                playPos,
                getMyPlayCardPos(me.getPlayArea().getCards().size() + 1, thenMyPlayIndex)
        );
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + calcMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.gameCancel();
        return false;
    }

    protected boolean myHandPointToMyPlayThenPointToRivalPlay(int myHandIndex, int myPlayIndex, int rivalPlayIndex){
        if (myHandIndex < 0 || myHandIndex >= myHandCards.size() || rivalPlayIndex >= rivalPlayCards.size() || myPlayArea.isFull() || !canPointedByRival(rivalPlayCards.get(rivalPlayIndex)) || myHandCards.get(myHandIndex).getCost() > calcMyUsableResource()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        int[] playPos = myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        SystemUtil.delayMedium();
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonMoveThenClick(
                playPos,
                getRivalPlayCardPos(rival.getPlayArea().getCards().size(), rivalPlayIndex)
        );
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + calcMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.gameCancel();
        return false;
    }

    protected boolean myHandPointToMyPlayThenPointToRivalHero(int myHandIndex, int myPlayIndex){
        if (myHandIndex < 0 || myHandIndex >= myHandCards.size() || myPlayArea.isFull() || !canPointedByRival(rivalPlayArea.getHero()) || myHandCards.get(myHandIndex).getCost() > calcMyUsableResource()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        int[] playPos = myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        SystemUtil.delayMedium();
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonMoveThenClick(
                playPos,
                getRivalHeroPos()
        );
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + calcMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.gameCancel();
        return false;
    }
    /**
     * 点击悬浮卡牌，如发现,
     * @param clearance
     * @param firstCardPos
     * @param doubleCardIndex 0~2
     */
    protected void clickFloatCard(double clearance, double firstCardPos, int doubleCardIndex){
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonClick(
                (int) (firstCardPos + doubleCardIndex * clearance) + RandomUtil.getRandom(-10, 10),
                (GAME_RECT.bottom + GAME_RECT.top >> 1) + RandomUtil.getRandom(-15, 15)
        );
        SystemUtil.delayShort();
    }

}
