package club.xiaojiawei.entity.area;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.Player;
import club.xiaojiawei.enums.HeroTypeEnum;
import club.xiaojiawei.enums.PowerTypeEnum;
import club.xiaojiawei.status.War;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
@Getter
@Setter
@ToString
@Slf4j
public class PlayArea extends Area {

    private HeroTypeEnum heroProfession;

    private PowerTypeEnum heroPower;

    private Card hero;
    private Card power;
    private Card weapon;

    @Override
    public boolean add(Card card, int pos){
        if (card == null){
            return false;
        }else if (card.getCardType() == null){
            return super.add(card, pos);
        }
        switch (card.getCardType()){
            case HERO_POWER -> {
                if (this == War.getPlayer1().getPlayArea()){
                    War.getPlayer1().getSetasideArea().add(power);
                }else {
                    War.getPlayer2().getSetasideArea().add(power);
                }
                Player player = War.testArea(this);
                log.info("向玩家" + (player == War.getPlayer1()? 1 : 2) + " - " + player.getGameId() + "的" + this.getClass().getSimpleName() + " 的Power 添加卡牌， entityId:" + card.getEntityId());
                power = card;
                addZone(card);
                return true;
            }
            case HERO -> {
                if (this == War.getPlayer1().getPlayArea()){
                    War.getPlayer1().getSetasideArea().add(power);
                }else {
                    War.getPlayer2().getSetasideArea().add(power);
                }
                Player player = War.testArea(this);
                log.info("向玩家" + (player == War.getPlayer1()? 1 : 2) + " - " + player.getGameId() + "的" + this.getClass().getSimpleName() + " 的Hero 添加卡牌， entityId:" + card.getEntityId());
                hero = card;
                addZone(card);
                return true;
            }
            default -> {
                return super.add(card, pos);
            }
        }
    }

    @Override
    public Card getByCardId(String cardId) {
        Card byCardId = super.getByCardId(cardId);
        if (byCardId != null){
            return byCardId;
        }
        if (Objects.equals(hero.getCardId(), cardId)){
            return hero;
        }else if(Objects.equals(power.getCardId(), cardId)){
            return power;
        }
        return null;
    }

    @Override
    public Card getByEntityId(String entityId) {
        Card card = super.getByEntityId(entityId);
        if (card != null){
            return card;
        }
        if (Objects.equals(hero.getEntityId(), entityId)){
            return hero;
        }else if (Objects.equals(power.getEntityId(), entityId)){
            return power;
        }
        return null;
    }

    @Override
    public Card removeByCardId(String cardId) {
        Card byCardId = super.removeByCardId(cardId);
        if (byCardId != null){
            return byCardId;
        }
        Card temp;
        if (Objects.equals(hero.getCardId(), cardId)){
            temp = hero;
            hero = null;
            return temp;
        }else if(Objects.equals(power.getCardId(), cardId)){
            temp = power;
            power = null;
            return temp;
        }
        return null;
    }

    @Override
    public Card removeByEntityId(String entityId) {
        Card card = super.removeByEntityId(entityId);
        if (card != null){
            return card;
        }
        Card temp;
        if (Objects.equals(hero.getEntityId(), entityId)){
            temp = hero;
            hero = null;
            return temp;
        }else if (Objects.equals(power.getEntityId(), entityId)){
            temp = power;
            power = null;
            return temp;
        }
        return null;
    }

    public PlayArea() {
        super(7);
    }

}
