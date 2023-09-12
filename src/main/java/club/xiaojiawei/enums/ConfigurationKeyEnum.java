package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2023/7/5 11:26
 * @msg 脚本配置信息里的key
 */
@Getter
@ToString
@AllArgsConstructor
public enum ConfigurationKeyEnum {
    AUTO_OPEN_KEY("autoOpen"),
    GAME_PATH_KEY("gamePath"),
    PLATFORM_PATH_KEY("platformPath"),
    RUN_MODE_KEY("runMode"),
    DECK_KEY("deck"),
    WORK_DAY_FLAG_KEY("workDayFlag"),
    WORK_TIME_FLAG_KEY("workTimeFlag"),
    WORK_TIME_KEY("workTime")
    ;
    private final String key;

}
