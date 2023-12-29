package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/30 17:56
 */
@Getter
@ToString
@AllArgsConstructor
public enum BlockTypeEnum {

    TRIGGER("触发"),
    PLAY("打出"),
    POWER("卡牌效果结算"),
    ATTACK("攻击"),
    DEATHS("死亡"),
    FATIGUE("疲劳"),
    ACTION("行动"),
    CONTINUOUS("连续"),
    GAME_RESET("游戏复位"),
    INVALID("无效"),
    ;
    private final String comment;

}
