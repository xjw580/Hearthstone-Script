package club.xiaojiawei.bean;

import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.bean.area.PlayArea;
import club.xiaojiawei.status.War;
import club.xiaojiawei.utils.GameUtil;

import java.util.Objects;

/**
 * @author 肖嘉威
 * @date 2024/9/5 22:42
 */
public class DefaultCard extends Card{

    protected GameRect lastRect;

    @Override
    public boolean power() {
        return power(War.getMe().getPlayArea().cardSize() - 1);
    }

    @NotNull
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
    public boolean power(Card card) {
        GameRect startRect = GameUtil.getMyHandCardRect(War.getMe().getHandArea().indexOfCard(this), getArea().cardSize());
        if (startRect.isValid()){
            if (getArea() instanceof PlayArea){
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
    public boolean power(int index) {
        GameRect startRect = GameUtil.getMyHandCardRect(War.getMe().getHandArea().indexOfCard(this), getArea().cardSize());
        if (startRect.isValid()){
            if (getArea() instanceof PlayArea){
                GameRect endRect = GameUtil.getMyPlayCardRect(index, War.getMe().getPlayArea().cardSize());
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
    public boolean attackMinion(Card card) {
        GameRect startRect = GameUtil.getMyHandCardRect(War.getMe().getPlayArea().indexOfCard(this), getArea().cardSize());
        if (startRect.isValid()){
            if (Objects.equals(getArea(), War.getMe().getPlayArea())){
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
    public boolean attackHero() {
        GameRect startRect = GameUtil.getMyHandCardRect(War.getMe().getPlayArea().indexOfCard(this), getArea().cardSize());
        if (startRect.isValid()){
            if (Objects.equals(getArea(), War.getMe().getPlayArea())){
                startRect.lClickMoveLClick(GameUtil.RIVAL_HERO_RECT);
                lastRect = GameUtil.RIVAL_HERO_RECT;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean pointTo(Card card) {
        GameRect endRect = GameRect.INVALID;
        if (lastRect == null || !lastRect.isValid()) {
            endRect = getCardRect(this);
            if (endRect.isValid()){
                endRect.lClick();
            }
        }else {
            GameRect startRect = getCardRect(this);
            endRect = getCardRect(card);
            if (startRect.isValid() && endRect.isValid()){
                startRect.move();
                startRect.move(endRect);
                startRect.lClick();
            }
        }
        return !(lastRect = endRect).isValid();
    }

}
