package club.xiaojiawei.bean;

import club.xiaojiawei.CardAction;
import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.bean.area.HandArea;
import club.xiaojiawei.bean.area.PlayArea;
import club.xiaojiawei.status.War;
import club.xiaojiawei.utils.GameUtil;

import java.util.Map;
import java.util.Objects;

/**
 * @author 肖嘉威
 * @date 2024/9/5 22:42
 */
public class DefaultCardAction extends CardAction {

    protected GameRect lastRect;

    protected GameRect getCardRect(Card card){
        if (card == null) {
            return GameRect.INVALID;
        }
        Area area = card.getArea();
        int index;
        if (Objects.equals(area, War.getMe().getPlayArea())){
            if ((index = area.indexOfCard(card)) >= 0) {
                return GameUtil.getMyHandCardRect(index, area.cardSize());
            }
        }else if (Objects.equals(area, War.getRival().getPlayArea())){
            if ((index = area.indexOfCard(card)) >= 0) {
                return GameUtil.getRivalPlayCardRect(index, area.cardSize());
            }
        }else if (Objects.equals(area, War.getMe().getHandArea())){
            if ((index = area.indexOfCard(card)) >= 0) {
                return GameUtil.getMyHandCardRect(index, area.cardSize());
            }
        }else if (Objects.equals(card, War.getMe().getPlayArea().getHero())){
            return GameUtil.MY_HERO_RECT;
        }else if (Objects.equals(card, War.getRival().getPlayArea().getHero())){
            return GameUtil.RIVAL_HERO_RECT;
        }else if (Objects.equals(card, War.getMe().getPlayArea().getPower())){
            return GameUtil.MY_POWER_RECT;
        }else if (Objects.equals(card, War.getRival().getPlayArea().getPower())){
            return GameUtil.RIVAL_POWER_RECT;
        }
        return GameRect.INVALID;
    }

    @Override
    public String getCardId() {
        return "";
    }

    @Override
    public boolean execPower() {
        return execPower(Math.max(War.getMe().getPlayArea().cardSize() - 1, 0));
    }

    @Override
    public boolean execPower(Card card) {
        GameRect startRect;
        if ((startRect = GameUtil.getMyHandCardRect(War.getMe().getHandArea().indexOfCard(getBelongCard()), getBelongCard().getArea().cardSize())).isValid()){
            if (card.getArea() instanceof PlayArea){
                GameRect endRect = getCardRect(card);
                if (endRect.isValid()){
                    startRect.lClickMoveLClick(endRect);
                    lastRect = endRect;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean execPower(int index) {
        GameRect startRect;
        if ((startRect = GameUtil.getMyHandCardRect(War.getMe().getHandArea().indexOfCard(getBelongCard()), getBelongCard().getArea().cardSize())).isValid()){
            GameRect endRect = GameUtil.getMyPlayCardRect(index, War.getMe().getPlayArea().cardSize());
            if (endRect.isValid()){
                startRect.lClickMoveLClick(endRect);
                lastRect = endRect;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean execAttackMinion(Card card) {
        GameRect startRect = GameUtil.getMyPlayCardRect(War.getMe().getPlayArea().indexOfCard(getBelongCard()), getBelongCard().getArea().cardSize());
        if (startRect.isValid()){
            if (Objects.equals(card.getArea(), War.getRival().getPlayArea())){
                GameRect endRect = getCardRect(card);
                if (endRect.isValid()){
                    startRect.lClickMoveLClick(endRect);
                    lastRect = endRect;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean execAttackHero() {
        GameRect startRect = GameUtil.getMyPlayCardRect(War.getMe().getPlayArea().indexOfCard(getBelongCard()), getBelongCard().getArea().cardSize());
        if (startRect.isValid()){
            if (Objects.equals(getBelongCard().getArea(), War.getMe().getPlayArea())){
                startRect.lClickMoveLClick(GameUtil.RIVAL_HERO_RECT);
                lastRect = GameUtil.RIVAL_HERO_RECT;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean execPointTo(Card card) {
        GameRect endRect;
        if (lastRect == null || !lastRect.isValid()) {
            endRect = getCardRect(getBelongCard());
            if (endRect.isValid()){
                endRect.lClick();
            }
        }else {
            GameRect startRect = getCardRect(getBelongCard());
            endRect = getCardRect(card);
            if (startRect.isValid() && endRect.isValid()){
                startRect.move();
                startRect.move(endRect);
                startRect.lClick();
            }
        }
        return !(lastRect = endRect).isValid();
    }

    @Override
    public CardAction createNewInstance() {
        return new DefaultCardAction();
    }

}
