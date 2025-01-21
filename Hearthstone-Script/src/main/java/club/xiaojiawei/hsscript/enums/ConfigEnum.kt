package club.xiaojiawei.hsscript.enums

import ch.qos.logback.classic.Level
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.hsscript.bean.HotKey
import club.xiaojiawei.hsscript.bean.WorkDay
import club.xiaojiawei.hsscript.bean.WorkTime
import com.alibaba.fastjson.JSON
import com.melloware.jintellitype.JIntellitype

/**
 * 脚本配置信息
 * @author 肖嘉威
 * @date 2023/7/5 11:26
 */

enum class ConfigEnum(
    val group: String = "",
    val defaultValue: String = "",
) {

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
    WORK_DAY(
        group = "time",
        defaultValue = JSON.toJSONString(
            listOf(
                WorkDay("every", true)
            )
        )
    ),

    /**
     * 工作时间，具体时间段
     */
    WORK_TIME(
        group = "time", defaultValue = JSON.toJSONString(
            listOf(
                WorkTime("00:00", "00:00", true),
            )
        )
    ),

    /**
     * 更新开发版
     */
    UPDATE_DEV(group = "version", defaultValue = "false"),

    /**
     * 自动更新
     */
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
     * 适配畸变模式
     */
    DISTORTION(group = "action", defaultValue = "true"),

    /**
     * 鼠标移动暂停间隔，值越小越慢，最小为1
     */
    PAUSE_STEP(group = "action", defaultValue = "7"),

    /**
     * 控制模式/真实控制鼠标
     */
    CONTROL_MODE(group = "action", defaultValue = "false"),

    /**
     * 置顶游戏窗口
     */
    TOP_GAME_WINDOW(group = "action", defaultValue = CONTROL_MODE.defaultValue),

    /**
     * 选择卡组位
     */
    CHOOSE_DECK_POS(group = "action", defaultValue = "1"),

    /**
     * 套牌插件禁用列表
     */
    DECK_PLUGIN_DISABLED(group = "plugin", defaultValue = JSON.toJSONString(emptyList<String>())),

    /**
     * 卡牌插件禁用列表
     */
    CARD_PLUGIN_DISABLED(group = "plugin", defaultValue = JSON.toJSONString(emptyList<String>())),

    /**
     * 默认套牌(deck id)
     */
    DEFAULT_DECK_STRATEGY(group = "other", defaultValue = ""),

    /**
     * 默认运行模式
     */
    DEFAULT_RUN_MODE(group = "other", defaultValue = RunModeEnum.CASUAL.name),

    /**
     * 战网密码
     */
    PLATFORM_PASSWORD(group = "other", defaultValue = ""),

    /**
     * 游戏日志大小限制/KB，游戏默认10240
     */
    GAME_LOG_LIMIT(group = "other", defaultValue = "51200"),

    /**
     * 最长匹配时间/s（超过重新匹配）
     */
    MATCH_MAXIMUM_TIME(group = "other", defaultValue = "90"),

    /**
     * 最长空闲时间/min（超过重启游戏）
     */
    IDLE_MAXIMUM_TIME(group = "other", defaultValue = "10"),

    /**
     * 运行后最小化软件
     */
    RUNNING_MINIMIZE(group = "other", defaultValue = "false"),

    /**
     * 当前版本
     */
    CURRENT_VERSION(group = "other", defaultValue = "0.0.0-GA"),

    /**
     * 随机事件
     */
    RANDOM_EVENT(group = "other", defaultValue = "true"),

    /**
     * 随机表情
     */
    RANDOM_EMOTION(group = "other", defaultValue = "true"),

    /**
     * 自动投降
     */
    AUTO_SURRENDER(group = "other", defaultValue = "-1"),

    /**
     * 只打人机
     */
    ONLY_ROBOT(group = "other", defaultValue = "false"),

    /**
     * 允许发送windows通知
     */
    SEND_NOTICE(group = "system", defaultValue = "true"),

    /**
     * 退出程序热键
     */
    EXIT_HOT_KEY(group = "system", defaultValue = JSON.toJSONString(HotKey(JIntellitype.MOD_ALT, 'P'.code))),

    /**
     * 暂停程序热键
     */
    PAUSE_HOT_KEY(group = "system", defaultValue = JSON.toJSONString(HotKey(JIntellitype.MOD_CONTROL, 'P'.code))),

    /**
     * 文件日志级别
     */
    FILE_LOG_LEVEL(group = "dev", defaultValue = Level.INFO.levelStr),

    /**
     * 自动打开游戏数据分析页
     */
    AUTO_OPEN_GAME_ANALYSIS(group = "dev", defaultValue = "false"),

    ;
}
