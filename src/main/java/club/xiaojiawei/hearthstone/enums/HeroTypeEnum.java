package club.xiaojiawei.hearthstone.enums;

/**
 * @author 肖嘉威
 * @date 2022/11/28 20:11
 */
public enum HeroTypeEnum {

    WARRIOR("战士"),
    MAGE("法师"),
    WARLOCK("术士"),
    PRIEST("牧师"),
    ROGUE("盗贼"),
    SHAMAN("萨满"),
    PALADIN("圣骑士"),
    HUNTER("猎人"),
    DRUID("德鲁伊"),
    DEMON_HUNTER("恶魔猎手"),
    DEATH_KNIGHT("死亡骑士")
    ;

    private final String comment;

    HeroTypeEnum(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "ProfessionEnum{" +
                "comment='" + comment + '\'' +
                '}';
    }

    public String getComment() {

        return comment;
    }
}
