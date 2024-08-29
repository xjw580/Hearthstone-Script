package club.xiaojiawei.strategy.extra;

import club.xiaojiawei.bean.PureCard;
import club.xiaojiawei.bean.Card;
import club.xiaojiawei.enums.CardRaceEnum;

import java.util.List;
import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/5 22:11
 */
public class CalcDeckStrategy extends BaseDeckStrategy{
    /**
     * 计算我方场上法强
     * @return
     */
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
        return Math.max(0, card.getHealth() + card.getArmor() - card.getDamage());
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

    /**
     * 计算指定卡的数量
     * @param cards
     * @param card
     * @return
     */
    protected int calcCardCount(List<Card> cards, PureCard card){
        return calcCardCount(cards, card.cardId());
    }
    /**
     * 计算对方英雄血量
     * @return
     */
    protected int calcRivalHeroBlood(){
        return calcCardBlood(rivalPlayArea.getHero());
    }
    protected int calcMyHeroAtc(){
        if (myPlayArea.getHero().isFrozen() || myPlayArea.getHero().isExhausted()){
            return 0;
        }
        Card hero = myPlayArea.getHero();
        Card weapon = myPlayArea.getWeapon();
        return hero.getAtc() * (weapon != null && weapon.isWindFury() & calcCardBlood(weapon) > 1 ? 2 : 1);
    }
    protected int calcMyHeroBlood(){
        return calcCardBlood(myPlayArea.getHero());
    }
    protected int calcMyPlayAtc(){
        return calcCanMoveAtc(myPlayCards);
    }
    protected int calcMyTotalAtc(){
        return calcMyPlayAtc() + calcMyHeroAtc();
    }
    protected int calcCanMoveAtc(List<Card> cards){
        int atc = 0;
        for (Card card : cards) {
            if (canMove(card)){
                atc += card.getAtc() * (card.isWindFury()? 2 : 1);
            }
        }
        return atc;
    }
    protected int calcRivalTotalAtc(){
        int atc = 0;
        for (Card rivalPlayCard : rivalPlayCards) {
            atc += rivalPlayCard.getAtc() * (rivalPlayCard.isWindFury()? 2 : 1);
        }
        atc += rivalPlayArea.getHero().getAtc() * (rivalPlayArea.getHero().isWindFury()? 2 : 1);
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
     * 获取可用水晶数
     * @return
     */
    protected int calcMyUsableResource(){
        return me.getResources() - me.getResourcesUsed() + me.getTempResources();
    }
}
