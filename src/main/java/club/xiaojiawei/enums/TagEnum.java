package club.xiaojiawei.enums;

/**
 * @author 肖嘉威
 * @date 2022/11/29 14:30
 */
public enum TagEnum {

    MULLIGAN_STATE("MULLIGAN_STATE", "调度阶段"),
    RESOURCES("RESOURCES", "当前玩家水晶"),
    RESOURCES_USED("RESOURCES_USED", "已使用水晶"),
    TEMP_RESOURCES("TEMP_RESOURCES", "当前玩家临时水晶数"),
    STEP("STEP", "步骤"),
    NEXT_STEP("NEXT_STEP", "下一步骤"),
    CURRENT_PLAYER("CURRENT_PLAYER", "当前玩家"),
    ZONE_POSITION("ZONE_POSITION", "区位置"),
    ZONE("ZONE", "区域"),
    HEALTH("HEALTH", "生命值"),
    ATK("ATK", "攻击力"),
    COST("COST", "法力值"),
    FROZEN("FROZEN", "冻结"),
    EXHAUSTED("EXHAUSTED", "疲劳"),
    PLAYSTATE("PLAYSTATE", "游戏状态"),
    FIRST_PLAYER("FIRST_PLAYER", "先手玩家"),
    DAMAGE("DAMAGE", "收到的伤害"),
    TAUNT("TAUNT", "嘲讽"),
    ARMOR("ARMOR", "嘲讽"),
    DIVINE_SHIELD("DIVINE_SHIELD", "圣盾"),
    DEATHRATTLE("DEATHRATTLE", "亡语"),
    POISONOUS("POISONOUS", "剧毒"),
    AURA("AURA", "光环"),
    STEALTH("STEALTH", "潜行"),
    UNKNOWN("UNKNOWN", "未知")
    ;

    private final String value;

    private final String comment;

    TagEnum(String value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public String getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "TagTypeEnum{" +
                "value='" + value + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
