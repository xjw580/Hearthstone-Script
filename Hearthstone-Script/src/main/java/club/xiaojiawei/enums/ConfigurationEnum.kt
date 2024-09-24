package club.xiaojiawei.enums

/**
 * 脚本配置信息，在[ConfigurationConfig]里写入
 * @author 肖嘉威
 * @date 2023/7/5 11:26
 */
enum class ConfigurationEnum(
    val key: String,
    val defaultValue: String
) {
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
     * 默认套牌(deck id)
     */
    DEFAULT_DECK_STRATEGY("defaultDeckStrategy", ""),

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
     * 动作间隔/ms
     */
    MOUSE_ACTION_INTERVAL("mouseActionInterval", "3500"),

    /**
     * 鼠标移动间隔/ms
     */
    MOUSE_MOVE_INTERVAL("mouseMoveInterval", "4"),

    /**
     * 套牌插件禁用列表
     */
    DECK_PLUGIN_DISABLED("deckPluginDisabled", ""),

    /**
     * 卡牌插件禁用列表
     */
    CARD_PLUGIN_DISABLED("cardPluginDisabled", ""),
    ;

}
