package club.xiaojiawei.enums;

import club.xiaojiawei.config.ConfigurationConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 脚本配置信息，在{@link ConfigurationConfig}里写入
 * @author 肖嘉威
 * @date 2023/7/5 11:26
 */
@Getter
@ToString
@AllArgsConstructor
public enum ConfigurationEnum {
    /**
     * 自动打开web页
     */
    AUTO_OPEN_WEB("autoOpenWeb", "false"),
    /**
     * 是否执行策略
     */
    STRATEGY("strategy", "true"),
    /**
     * 游戏路径
     */
    GAME_PATH("gamePath", ""),
    /**
     * 战网路径
     */
    PLATFORM_PATH("platformPath", ""),
    /**
     * 模式，狂野、标准等
     */
    RUN_MODE("runMode", "WILD"),
    /**
     * 套牌，偶数萨，动物园等
     */
    DECK("deck", "EVEN_NUMBER_SHAMAN"),
    /**
     * 工作日标记
     */
    WORK_DAY_FLAG("workDayFlag", "true,false,false,false,false,false,false,false"),
    /**
     * 工作时间标记
     */
    WORK_TIME_FLAG("workTimeFlag", "true,false,false"),
    /**
     * 工作时间，具体时间段
     */
    WORK_TIME("workTime", "00:00-00:00,null,null"),
    /**
     * WEB访问密码
     */
    VERIFY_PASSWORD("verifyPassword", ""),
    /**
     * WEB启用密码
     */
    ENABLE_VERIFY("enableVerify", "false"),
    /**
     * 更新开发版
     */
    UPDATE_DEV("updateDev", "false"),
    /**
     * 战网密码
     */
    PLATFORM_PASSWORD("platformPassword", ""),
    AUTO_UPDATE("autoUpdate", "false"),
    /**
     * 鼠标操作完后回到原位置
     */
    STATIC_CURSOR("staticCursor", "false"),
    /**
     * 允许发送windows通知
     */
    SEND_NOTICE("sendNotice", "true"),
    /**
     * 鼠标操作完一套流程的停顿时间
     */
    MOUSE_ACTION_INTERVAL("mouseActionInterval", "3500"),
    MOUSE_MOVE_INTERVAL("mouseMoveInterval", "13"),
    ;
    private final String key;
    private final String defaultValue;

}
