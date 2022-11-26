package club.xiaojiawei.hearthstone.status;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 肖嘉威
 * @date 2022/11/25 20:57
 * 对局状态
 */
@SuppressWarnings("all")
public record War(Player me, Player rival) implements Serializable {




    @Data
    public static class Player implements Serializable{

        private String playerId;

        private HandArea handArea;

        private PlayArea playArea;

        private BuffArea buffArea;

        private byte maxResources = 10;

        private byte currentResources;

        private byte availableResources;
    }

    /**
     * 手牌区
     */
    @Data
    public static class HandArea implements Serializable{

        private List<Card> cards;

        public byte maxCard = 10;

    }

    /**
     * 牌库区
     */
    @Data
    public static class DeckArea implements Serializable{


    }

    /**
     * 战场区
     */
    @Data
    public static class PlayArea implements Serializable{

        private List<Card> cards;

        public static final byte maxCard = 7;

    }

    /**
     * 墓地区
     */
    @Data
    public static class GraveyardArea implements Serializable{

    }

    /**
     * 奥秘区
     */
    @Data
    public static class SecretArea implements Serializable{

    }

    /**
     * 除外区
     */
    @Data
    public static class SetasideArea implements Serializable{
        private String entityId;
    }

    /**
     * 光环区
     */
    @Data
    public static class BuffArea implements Serializable{

        private byte spellDamage;

        private byte spellCost;

        private byte battleShoutCost;

    }

    /**
     * 英雄
     */
    @Data
    public static class Hero implements Serializable{
        private String cardId;

        private byte health;

        private byte armor;
    }

    /**
     * 技能
     */
    @Data
    public static class Power implements Serializable{

        private String cardId;

        private byte cost;

        private byte directDamage;

        private byte indirectDamage;

    }


    /**
     * 卡牌
     */
    @Data
    public static class Card implements Serializable{

        private String cardId;

        private String entityId;

        private byte cost;

        private byte atc;

        private byte health;

        private byte lastCost;

        private byte lastAtc;

        private byte lastHealth;

    }

    /**
     * 随从
     */
    @Data
    public static class Minion extends Card implements Serializable{

        private byte directDamage;

        private byte indirectDamage;

        private byte atcCount;

        private byte isBattleShout;

        private List<Race> race;

    }

    /**
     * 种族
     */
    public static enum Race implements Serializable{

        ;

    }

    /**
     * 法术
     */
    @Data
    public static class Spell extends Card implements Serializable{

        private byte directDamage;

        private byte indirectDamage;

    }

    /**
     * 武器
     */
    @Data
    public static class Weapon extends Card implements Serializable{

        private byte atcCount;
    }

}
