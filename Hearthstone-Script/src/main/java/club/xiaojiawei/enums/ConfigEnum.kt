package club.xiaojiawei.enums

/**
 * 脚本配置信息，在[ConfigurationConfig]里写入
 * @author 肖嘉威
 * @date 2023/7/5 11:26
 */
enum class ConfigEnum(
    val group: String = "",
    val defaultValue: String = "",
) {
    /**
     * 自动打开web页
     */
    AUTO_OPEN_WEB(group = "web", defaultValue = "false"),

    /**
     * WEB访问密码
     */
    VERIFY_PASSWORD(group = "web", defaultValue = ""),

    /**
     * WEB启用密码
     */
    ENABLE_VERIFY(group = "web", defaultValue = "false"),

    /**
     * 游戏路径
     */
    GAME_PATH(group = "path", defaultValue = ""),

    /**
     * 战网路径
     */
    PLATFORM_PATH(group = "path", defaultValue = ""),

    /**
     * 工作日标记
     */
    WORK_DAY_FLAG(group = "time", defaultValue = "true,false,false,false,false,false,false,false"),

    /**
     * 工作时间标记
     */
    WORK_TIME_FLAG(group = "time", defaultValue = "true,false,false"),

    /**
     * 工作时间，具体时间段
     */
    WORK_TIME(group = "time", defaultValue = "00:00-00:00,null,null"),

    /**
     * 更新开发版
     */
    UPDATE_DEV(group = "version", defaultValue = "false"),

    AUTO_UPDATE(group = "version", defaultValue = "false"),

    /**
     * 是否执行策略
     */
    STRATEGY(group = "action", defaultValue = "true"),

    /**
     * 动作间隔/ms
     */
    MOUSE_ACTION_INTERVAL(group = "action", defaultValue = "3500"),

    /**
     * 鼠标移动间隔/ms
     */
    MOUSE_MOVE_INTERVAL(group = "action", defaultValue = "4"),

    /**
     * 套牌插件禁用列表
     */
    DECK_PLUGIN_DISABLED(group = "plugin"),

    /**
     * 卡牌插件禁用列表
     */
    CARD_PLUGIN_DISABLED(group = "plugin"),

    /**
     * 默认套牌(deck id)
     */
    DEFAULT_DECK_STRATEGY(group = "other", defaultValue = ""),

    /**
     * 战网密码
     */
    PLATFORM_PASSWORD(group = "other"),

    /**
     * 允许发送windows通知
     */
    SEND_NOTICE(group = "system", defaultValue = "true"),
    ;
}
