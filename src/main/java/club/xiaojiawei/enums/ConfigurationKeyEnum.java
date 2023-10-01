package club.xiaojiawei.enums;

import club.xiaojiawei.config.ConfigurationConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2023/7/5 11:26
 * @msg 脚本配置信息里的key，需要配置初始值的在{@link ConfigurationConfig}里处理
 */
@Getter
@ToString
@AllArgsConstructor
public enum ConfigurationKeyEnum {
    /**
     * 自动打开web页
     */
    AUTO_OPEN_KEY("autoOpen"),
    /**
     * 是否执行策略
     */
    STRATEGY_KEY("strategy"),
    /**
     * 游戏路径
     */
    GAME_PATH_KEY("gamePath"),
    /**
     * 战网路径
     */
    PLATFORM_PATH_KEY("platformPath"),
    /**
     * 模式，狂野、标准等
     */
    RUN_MODE_KEY("runMode"),
    /**
     * 套牌，偶数萨，动物园等
     */
    DECK_KEY("deck"),
    /**
     * 工作日标记
     */
    WORK_DAY_FLAG_KEY("workDayFlag"),
    /**
     * 工作时间标记
     */
    WORK_TIME_FLAG_KEY("workTimeFlag"),
    /**
     * 工作时间，具体时间段
     */
    WORK_TIME_KEY("workTime"),
    /**
     * WEB访问密码
     */
    VERIFY_PASSWORD("verifyPassword"),
    /**
     * WEB启用密码
     */
    ENABLE_VERIFY("enableVerify"),
    /**
     * 更新开发版
     */
    UPDATE_DEV("updateDev"),
    ;
    private final String key;

}
