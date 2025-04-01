package club.xiaojiawei.hsscript.enums

import ch.qos.logback.classic.Level
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.hsscript.bean.HotKey
import club.xiaojiawei.hsscript.bean.WorkDay
import club.xiaojiawei.hsscript.bean.WorkTime
import club.xiaojiawei.hsscript.data.GAME_HWND
import club.xiaojiawei.hsscript.service.*
import com.alibaba.fastjson.JSON
import com.melloware.jintellitype.JIntellitype

/**
 * 脚本配置信息
 * @author 肖嘉威
 * @date 2023/7/5 11:26
 */

const val INIT_CONFIG_GROUP = "init"

const val PLUGIN_CONFIG_GROUP = "plugin"
const val OTHER_CONFIG_GROUP = "other"

const val STRATEGY_CONFIG_GROUP = "strategy"

const val BEHAVIOR_CONFIG_GROUP = "behavior"
const val SYSTEM_CONFIG_GROUP = "system"
//const val SERVICE_CONFIG_GROUP = "service"
const val VERSION_CONFIG_GROUP = "version"

const val DEV_CONFIG_GROUP = "version"

enum class ConfigEnum(
    val group: String = "",
    val defaultValue: String = "",
    val service: Service<*>? = null,
) {

    /**
     * 游戏路径
     */
    GAME_PATH(group = INIT_CONFIG_GROUP, defaultValue = ""),

    /**
     * 战网路径
     */
    PLATFORM_PATH(group = INIT_CONFIG_GROUP, defaultValue = ""),

    /**
     * 选择卡组位
     */
    CHOOSE_DECK_POS(group = INIT_CONFIG_GROUP, defaultValue = "1"),

    /**
     * 工作日标记
     */
    WORK_DAY(
        group = OTHER_CONFIG_GROUP,
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
        group = OTHER_CONFIG_GROUP, defaultValue = JSON.toJSONString(
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
    STRATEGY(group = DEV_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 启用鼠标
     */
    ENABLE_MOUSE(group = DEV_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 动作间隔/ms
     */
    MOUSE_ACTION_INTERVAL(group = STRATEGY_CONFIG_GROUP, defaultValue = "3500"),

    /**
     * 适配畸变模式
     */
    DISTORTION(group = STRATEGY_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 鼠标移动暂停间隔，值越小越慢，最小为1
     */
    PAUSE_STEP(group = STRATEGY_CONFIG_GROUP, defaultValue = "7"),

    /**
     * 工作时最小化软件
     */
    WORKING_MINIMIZE(group = BEHAVIOR_CONFIG_GROUP, defaultValue = false.toString(), service = WorkingMinimizeService),

    /**
     * 鼠标控制模式
     */
    MOUSE_CONTROL_MODE(group = BEHAVIOR_CONFIG_GROUP, defaultValue = MouseControlModeEnum.MESSAGE.name),

    /**
     * 置顶游戏窗口
     */
    TOP_GAME_WINDOW(group = BEHAVIOR_CONFIG_GROUP, defaultValue = MOUSE_CONTROL_MODE.defaultValue, service = TopGameWindowService),

    /**
     * 阻止游戏的反作弊
     */
    PREVENT_AC(group = BEHAVIOR_CONFIG_GROUP, defaultValue = false.toString()),

    /**
     * 限制鼠标范围
     */
    LIMIT_MOUSE_RANGE(group = BEHAVIOR_CONFIG_GROUP, defaultValue = true.toString(), ),

    /**
     * 游戏窗口不透明度(0~255)
     */
    GAME_WINDOW_OPACITY(group = BEHAVIOR_CONFIG_GROUP, defaultValue = "255", service = GameWindowOpacityService),

    /**
     * 套牌插件禁用列表
     */
    DECK_PLUGIN_DISABLED(group = PLUGIN_CONFIG_GROUP, defaultValue = JSON.toJSONString(emptyList<String>())),

    /**
     * 卡牌插件禁用列表
     */
    CARD_PLUGIN_DISABLED(group = PLUGIN_CONFIG_GROUP, defaultValue = JSON.toJSONString(emptyList<String>())),

    /**
     * 默认套牌策略(deck id)
     */
    DEFAULT_DECK_STRATEGY(group = OTHER_CONFIG_GROUP, defaultValue = ""),

    /**
     * 默认运行模式
     */
    DEFAULT_RUN_MODE(group = OTHER_CONFIG_GROUP, defaultValue = RunModeEnum.CASUAL.name),

    /**
     * 战网密码
     */
    PLATFORM_PASSWORD(group = INIT_CONFIG_GROUP, defaultValue = ""),

    /**
     * 游戏日志大小限制/KB，游戏默认10240
     */
    GAME_LOG_LIMIT(group = STRATEGY_CONFIG_GROUP, defaultValue = "51200"),

    /**
     * 最长匹配时间/s（超过重新匹配）
     */
    MATCH_MAXIMUM_TIME(group = STRATEGY_CONFIG_GROUP, defaultValue = "90"),

    /**
     * 最长空闲时间/min（超过重启游戏）
     */
    IDLE_MAXIMUM_TIME(group = STRATEGY_CONFIG_GROUP, defaultValue = "10"),

    /**
     * 当前版本
     */
    CURRENT_VERSION(group = OTHER_CONFIG_GROUP, defaultValue = "0.0.0-GA"),

    /**
     * 随机事件
     */
    RANDOM_EVENT(group = STRATEGY_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 随机表情
     */
    RANDOM_EMOTION(group = STRATEGY_CONFIG_GROUP, defaultValue = true.toString()),

    /**
     * 自动投降
     */
    AUTO_SURRENDER(group = STRATEGY_CONFIG_GROUP, defaultValue = "-1"),

    /**
     * 只打人机
     */
    ONLY_ROBOT(group = STRATEGY_CONFIG_GROUP, defaultValue = false.toString()),

    /**
     * 检查游戏响应超时
     */
    GAME_TIMEOUT(
        group = BEHAVIOR_CONFIG_GROUP,
        defaultValue = true.toString(),
        service = GameTimeoutService
    ),

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
     * 自动关闭显示器
     */
    AUTO_OFF_SCREEN(group = SYSTEM_CONFIG_GROUP, defaultValue = false.toString()),

    /**
     * 自动睡眠
     */
    AUTO_SLEEP(group = SYSTEM_CONFIG_GROUP, defaultValue = false.toString()),

    /**
     * 自动唤醒
     */
    AUTO_WAKE(group = SYSTEM_CONFIG_GROUP, defaultValue = false.toString()),

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