package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.Player;
import club.xiaojiawei.enums.HeroTypeEnum;
import club.xiaojiawei.enums.PowerTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static club.xiaojiawei.enums.CardTypeEnum.*;

/**
 * 战场
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
@ToString
@Slf4j
public class PlayArea extends Area {

    @Getter
    @Setter
    private HeroTypeEnum heroType;
    @Getter
    @Setter
    private PowerTypeEnum powerType;
    @Getter
    @Setter
    private Card hero;
    private Card heroHide;
    @Getter
    @Setter
    private Card power;
    private Card powerHide;
    @Getter
    @Setter
    private Card weapon;
    private Card weaponHide;

    public PlayArea(Player player) {
        super(7, player);
    }

    private void addZoneAndLog(String name, Card card){
        addZone(card);
        log.info(getLogText(card, name));
    }

    @Override
    public boolean add(Card card, int pos){
        boolean result = true;
        if (card == null){
            result = false;
        }else if (card.getCardType() == HERO_POWER){
            powerHide = power;
            power = card;
//            TODO 设置powerType
            addZoneAndLog("技能", card);
        }else if (card.getCardType() == HERO){
//            TODO 设置heroType
            heroHide = hero;
            hero = card;
            addZoneAndLog("英雄", card);
        }else if (card.getCardType() == WEAPON){
            weaponHide = weapon;
            weapon = card;
            addZoneAndLog("武器", card);
        }else {
            result =  super.add(card, pos);
        }
        return result;
    }

    @Override
    public Card findByEntityId(String entityId) {
        Card card = super.findByEntityId(entityId);
        if (card == null){
            if (hero != null && Objects.equals(hero.getEntityId(), entityId)){
                card = hero;
            }else if (power != null && Objects.equals(power.getEntityId(), entityId)){
                card = power;
            }else if (weapon != null && Objects.equals(weapon.getEntityId(), entityId)){
                card = weapon;
            }else if (heroHide != null && Objects.equals(heroHide.getEntityId(), entityId)){
                card = heroHide;
            }else if (powerHide != null && Objects.equals(powerHide.getEntityId(), entityId)){
                card = powerHide;
            }else if (weaponHide != null && Objects.equals(weaponHide.getEntityId(), entityId)){
                card = weaponHide;
            }
        }
        return card;
    }


    @Override
    public Card removeByEntityId(String entityId) {
        Card card = super.removeByEntityId(entityId);
        if (card == null){
            if (hero != null && Objects.equals(hero.getEntityId(), entityId)){
                card = hero;
                hero = null;
            }else if (power != null && Objects.equals(power.getEntityId(), entityId)){
                card = power;
                power = null;
            }else if (weapon != null && Objects.equals(weapon.getEntityId(), entityId)){
                card = weapon;
                weapon = null;
            }else if (heroHide != null && Objects.equals(heroHide.getEntityId(), entityId)){
                card = heroHide;
                heroHide = null;
            }else if (powerHide != null && Objects.equals(powerHide.getEntityId(), entityId)){
                card = powerHide;
                powerHide = null;
            }else if (weaponHide != null && Objects.equals(weaponHide.getEntityId(), entityId)){
                card = weaponHide;
                weaponHide = null;
            }
        }
        return card;
    }

}
