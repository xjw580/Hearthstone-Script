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
    PAUSE(100, "暂停状态"),
    MODE(101, "模式"),
    DECK(102, "卡组"),
    GAME_COUNT(103, "游戏局数"),
    WINNING_PERCENTAGE(104, "胜率"),
    WORK_DATE(105, "工作时间"),
    MODE_LIST(106, "所有模式"),
    GAME_TIME(107, "游戏时长"),
    EXP(108, "经验"),
    LOG(200, "脚本日志"),
    ;
    private final int value;
    private final String comment;
}
