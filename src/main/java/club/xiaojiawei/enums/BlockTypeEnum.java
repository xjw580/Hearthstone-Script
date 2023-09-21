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

    TRIGGER("TRIGGER", "触发"),
    PLAY("PLAY", "打出"),
    POWER("POWER", "卡牌效果结算"),
    ATTACK("ATTACK", "攻击"),
    DEATHS("DEATHS", "死亡"),
    FATIGUE("FATIGUE", "疲劳"),
    ACTION("ACTION", "行动"),
    CONTINUOUS("CONTINUOUS", "连续"),
    GAME_RESET("GAME_RESET", "游戏复位"),
    INVALID("INVALID", "无效"),
    ;
    private final String value;
    private final String comment;

}
