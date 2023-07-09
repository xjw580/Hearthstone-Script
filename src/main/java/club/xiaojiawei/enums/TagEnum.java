package club.xiaojiawei.enums;

import club.xiaojiawei.entity.ExtraEntity;
import club.xiaojiawei.entity.TagChangeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.RandomAccessFile;

/**
 * @author 肖嘉威
 * @date 2022/11/29 14:30
 */
@Getter
@ToString
@AllArgsConstructor
public enum TagEnum {
    /**
     * 调度标签，在AbstractPhaseStrategy里使用
     */
    MULLIGAN_STATE("MULLIGAN_STATE", "调度阶段"),
    STEP("STEP", "步骤"),
    NEXT_STEP("NEXT_STEP", "下一步骤"),
    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    /**
     * 游戏标签
     * 还需在此添加{@link club.xiaojiawei.utils.PowerLogUtil}
     */
    RESOURCES("RESOURCES", "当前玩家水晶"),
    RESOURCES_USED("RESOURCES_USED", "已使用水晶"),
    TEMP_RESOURCES("TEMP_RESOURCES", "当前玩家临时水晶数"),
    CURRENT_PLAYER("CURRENT_PLAYER", "当前玩家"),
    ZONE_POSITION("ZONE_POSITION", "区位置"),
    ZONE("ZONE", "区域"),
    PLAYSTATE("PLAYSTATE", "游戏状态"),
    FIRST_PLAYER("FIRST_PLAYER", "先手玩家"),
    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    /**
     * 卡牌属性标签
     * 需要在{@link club.xiaojiawei.utils.PowerLogUtil#parseExtraEntity(String, RandomAccessFile)}和{@link club.xiaojiawei.entity.Card}和{@link club.xiaojiawei.entity.Card#extraEntityToCard(ExtraEntity)}里添加
     * 如果值会改变则还需要在此添加{@link club.xiaojiawei.utils.PowerLogUtil#dealTagChange(TagChangeEntity)}
     */
    HEALTH("HEALTH", "生命值"),
    ATK("ATK", "攻击力"),
    COST("COST", "法力值"),
    FROZEN("FROZEN", "冻结"),
    EXHAUSTED("EXHAUSTED", "疲劳"),
    DAMAGE("DAMAGE", "受到的伤害"),
    TAUNT("TAUNT", "嘲讽"),
    ARMOR("ARMOR", "护甲"),
    DIVINE_SHIELD("DIVINE_SHIELD", "圣盾"),
    DEATHRATTLE("DEATHRATTLE", "亡语"),
    POISONOUS("POISONOUS", "剧毒"),
    AURA("AURA", "光环"),
    STEALTH("STEALTH", "潜行"),
    WINDFURY("WINDFURY", "风怒"),
    BATTLECRY("BATTLECRY", "战吼"),
    ADJACENT_BUFF("ADJACENT_BUFF", "相邻增益"),
    CANT_BE_TARGETED_BY_SPELLS("CANT_BE_TARGETED_BY_SPELLS", "不能被法术指向"),
    CANT_BE_TARGETED_BY_HERO_POWERS("CANT_BE_TARGETED_BY_HERO_POWERS", "不能被英雄技能指向"),
    SPAWN_TIME_COUNT("SPAWN_TIME_COUNT", "休眠"),
    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    UNKNOWN("UNKNOWN", "未知")
    ;

    private final String value;

    private final String comment;

}
