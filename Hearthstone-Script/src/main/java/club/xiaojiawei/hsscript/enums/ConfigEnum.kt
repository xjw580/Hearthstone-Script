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

const val PATH_CONFIG_GROUP = "path"
const val TIME_CONFIG_GROUP = "time"
const val VERSION_CONFIG_GROUP = "version"
const val ACTION_CONFIG_GROUP = "action"
const val PLUGIN_CONFIG_GROUP = "plugin"
const val OTHER_CONFIG_GROUP = "other"
const val SYSTEM_CONFIG_GROUP = "system"
const val DEV_CONFIG_GROUP = "dev"

enum class ConfigEnum(
    val group: String = "",
    val defaultValue: String = "",
) {

    /**
     * 游戏路径
     */
    GAME_PATH(group = PATH_CONFIG_GROUP, defaultValue = ""),

    /**
     * 战网路径
     */
    PLATFORM_PATH(group = PATH_CONFIG_GROUP, defaultValue = ""),

    /**
     * 工作日标记
     */
    WORK_DAY(
        group = TIME_CONFIG_GROUP,
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
        group = TIME_CONFIG_GROUP, defaultValue = JSON.toJSONString(
            listOf(
                WorkTime("00:00", "00:00", true),
            )
        )
    ),

    /**
     * 更新源
     */
    UPDATE_SOURCE(group = VERSION_CONFIG_GROUP, defaultValue = "Gitee"),

    /**
     * 更新开发版
     */
    UPDATE_DEV(group = VERSION_CONFIG_GROUP, defaultValue = false.toString()),

    /**
     * 自动更新
     */
    AUTO_UPDATE(group = VERSION_CONFIG_GROUP, defaultValue = false.toString()),

    /**
     * 是否执行策略
     */
    STRATEGY(group = ACTION_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 动作间隔/ms
     */
    MOUSE_ACTION_INTERVAL(group = ACTION_CONFIG_GROUP, defaultValue = "3500"),

    /**
     * 适配畸变模式
     */
    DISTORTION(group = ACTION_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 鼠标移动暂停间隔，值越小越慢，最小为1
     */
    PAUSE_STEP(group = ACTION_CONFIG_GROUP, defaultValue = "7"),

    /**
     * 鼠标控制模式
     */
    MOUSE_CONTROL_MODE(group = ACTION_CONFIG_GROUP, defaultValue = MouseControlModeEnum.MESSAGE.name),

    /**
     * 置顶游戏窗口
     */
    TOP_GAME_WINDOW(group = ACTION_CONFIG_GROUP, defaultValue = MOUSE_CONTROL_MODE.defaultValue),

    /**
     * 选择卡组位
     */
    CHOOSE_DECK_POS(group = ACTION_CONFIG_GROUP, defaultValue = "1"),

    /**
     * 套牌插件禁用列表
     */
    DECK_PLUGIN_DISABLED(group = PLUGIN_CONFIG_GROUP, defaultValue = JSON.toJSONString(emptyList<String>())),

    /**
     * 卡牌插件禁用列表
     */
    CARD_PLUGIN_DISABLED(group = PLUGIN_CONFIG_GROUP, defaultValue = JSON.toJSONString(emptyList<String>())),

    /**
     * 默认套牌(deck id)
     */
    DEFAULT_DECK_STRATEGY(group = OTHER_CONFIG_GROUP, defaultValue = ""),

    /**
     * 默认运行模式
     */
    DEFAULT_RUN_MODE(group = OTHER_CONFIG_GROUP, defaultValue = RunModeEnum.CASUAL.name),

    /**
     * 战网密码
     */
    PLATFORM_PASSWORD(group = OTHER_CONFIG_GROUP, defaultValue = ""),

    /**
     * 游戏日志大小限制/KB，游戏默认10240
     */
    GAME_LOG_LIMIT(group = OTHER_CONFIG_GROUP, defaultValue = "51200"),

    /**
     * 最长匹配时间/s（超过重新匹配）
     */
    MATCH_MAXIMUM_TIME(group = OTHER_CONFIG_GROUP, defaultValue = "90"),

    /**
     * 最长空闲时间/min（超过重启游戏）
     */
    IDLE_MAXIMUM_TIME(group = OTHER_CONFIG_GROUP, defaultValue = "10"),

    /**
     * 运行后最小化软件
     */
    RUNNING_MINIMIZE(group = OTHER_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 当前版本
     */
    CURRENT_VERSION(group = OTHER_CONFIG_GROUP, defaultValue = "0.0.0-GA"),

    /**
     * 随机事件
     */
    RANDOM_EVENT(group = OTHER_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 随机表情
     */
    RANDOM_EMOTION(group = OTHER_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 自动投降
     */
    AUTO_SURRENDER(group = OTHER_CONFIG_GROUP, defaultValue = "-1"),

    /**
     * 只打人机
     */
    ONLY_ROBOT(group = OTHER_CONFIG_GROUP, defaultValue = false.toString()),

    /**
     * 允许发送windows通知
     */
    SEND_NOTICE(group = SYSTEM_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 使用系统代理
     */
    USE_PROXY(group = SYSTEM_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 退出程序热键
     */
    EXIT_HOT_KEY(group = SYSTEM_CONFIG_GROUP, defaultValue = JSON.toJSONString(HotKey(JIntellitype.MOD_ALT, 'P'.code))),

    /**
     * 暂停程序热键
     */
    PAUSE_HOT_KEY(
        group = SYSTEM_CONFIG_GROUP,
        defaultValue = JSON.toJSONString(HotKey(JIntellitype.MOD_CONTROL, 'P'.code))
    ),

    /**
     * 文件日志级别
     */
    FILE_LOG_LEVEL(group = DEV_CONFIG_GROUP, defaultValue = Level.INFO.levelStr),

    /**
     * 自动打开游戏数据分析页
     */
    AUTO_OPEN_GAME_ANALYSIS(group = DEV_CONFIG_GROUP, defaultValue = false.toString()),

    ;
}
