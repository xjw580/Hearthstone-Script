package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/12/4 11:53
 */
@Getter
@ToString
@AllArgsConstructor
public enum WsResultTypeEnum {
    /**
     * 暂停状态
     */
    PAUSE(100),
    /**
     * 模式
     */
    MODE(101),
    /**
     * 卡组
     */
    DECK(102),
    /**
     * 游戏局数
     */
    GAME_COUNT(103),
    /**
     * 胜率
     */
    WINNING_PERCENTAGE(104),
    /**
     * 工作时间
     */
    WORK_DATE(105),
    /**
     * 所有模式
     */
    MODE_LIST(106),
    /**
     * 游戏时长
     */
    GAME_TIME(107),
    /**
     * 经验
     */
    EXP(108),
    /**
     * 脚本日志
     */
    LOG(200),
    ;
    private final int value;
}
