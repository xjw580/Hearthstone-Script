package club.xiaojiawei.hsscript.enums

import ch.qos.logback.classic.Level
import club.xiaojiawei.hsscriptbase.enums.RunModeEnum
import club.xiaojiawei.hsscript.bean.HotKey
import club.xiaojiawei.hsscript.bean.WorkTime
import club.xiaojiawei.hsscript.bean.WorkTimeRule
import club.xiaojiawei.hsscript.bean.WorkTimeRuleSet
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
const val TIME_CONFIG_GROUP = "time"

const val STRATEGY_CONFIG_GROUP = "strategy"

const val BEHAVIOR_CONFIG_GROUP = "behavior"
const val SYSTEM_CONFIG_GROUP = "system"
const val VERSION_CONFIG_GROUP = "version"

const val DEV_CONFIG_GROUP = "dev"

const val WEIGHT_CONFIG_GROUP = "weight"

private const val WORK_TIME_RULE_PRESETS_ONE = "presets-one"

private const val WORK_TIME_RULE_PRESETS_EMPTY = ""

private const val WORK_TIME_RULE_PRESETS_TWO = "presets-two"

private val operations by lazy {
    setOf(
        OperateEnum.CLOSE_GAME,
        OperateEnum.CLOSE_PLATFORM,
    )
}

private const val FALSE_STR = false.toString()

private const val TRUE_STR = true.toString()

enum class ConfigEnum(
    val group: String = "",
    private var defaultValueInitializer: (() -> String)? = null,
    val service: Service<*>? = null,
    val isEnable: Boolean = true,
) {
    /**
     * 游戏安装路径
     */
    GAME_PATH(group = INIT_CONFIG_GROUP, defaultValueInitializer = { "" }),

    /**
     * 战网程序路径
     */
    PLATFORM_PATH(group = INIT_CONFIG_GROUP, defaultValueInitializer = { "" }),

    /**
     * 选择卡组位
     */
    CHOOSE_DECK_POS(group = INIT_CONFIG_GROUP, defaultValueInitializer = { "[1]" }),

    /**
     * 工作时间规则
     */
    WORK_TIME_RULE_SET(
        group = TIME_CONFIG_GROUP,
        defaultValueInitializer = {
            JSON.toJSONString(
                listOf(
                    WorkTimeRuleSet(
                        "预设1",
                        listOf(
                            WorkTimeRule(
                                WorkTime("00:00", "23:59"),
                                operations.toSet(),
                                true,
                            ),
                            WorkTimeRule(
                                WorkTime("00:00", "06:30"),
                                operations.toSet(),
                                false,
                            ),
                            WorkTimeRule(
                                WorkTime("12:30", "13:50"),
                                operations.toSet(),
                                false,
                            ),
                            WorkTimeRule(
                                WorkTime("20:00", "23:59"),
                                operations.toSet(),
                                false,
                            ),
                        ),
                        WORK_TIME_RULE_PRESETS_ONE,
                    ),
                    WorkTimeRuleSet(
                        "预设2",
                        listOf(
                            WorkTimeRule(
                                WorkTime("00:00", "23:59"),
                                operations.toSet(),
                                true,
                            ),
                            WorkTimeRule(
                                WorkTime("00:00", "08:00"),
                                operations.toSet(),
                                false,
                            ),
                            WorkTimeRule(
                                WorkTime("18:00", "23:59"),
                                operations.toSet(),
                                false,
                            ),
                        ),
                        WORK_TIME_RULE_PRESETS_TWO,
                    ),
                    WorkTimeRuleSet(
                        "空",
                        emptyList(),
                        WORK_TIME_RULE_PRESETS_EMPTY,
                    ),
                ),
            )
        },
    ),

    WORK_TIME_SETTING(
        group = TIME_CONFIG_GROUP,
        defaultValueInitializer =
            {
                JSON.toJSONString(
                    arrayOf(
                        WORK_TIME_RULE_PRESETS_ONE,
                        WORK_TIME_RULE_PRESETS_ONE,
                        WORK_TIME_RULE_PRESETS_ONE,
                        WORK_TIME_RULE_PRESETS_ONE,
                        WORK_TIME_RULE_PRESETS_ONE,
                        WORK_TIME_RULE_PRESETS_ONE,
                        WORK_TIME_RULE_PRESETS_ONE,
                    ),
                )
            },
    ),

    /**
     * 更新源
     */
    UPDATE_SOURCE(group = VERSION_CONFIG_GROUP, defaultValueInitializer = { "Gitee" }),

    /**
     * 更新开发版
     */
    UPDATE_DEV(group = VERSION_CONFIG_GROUP, defaultValueInitializer = { FALSE_STR }),

    /**
     * 自动更新
     */
    AUTO_UPDATE(group = VERSION_CONFIG_GROUP, defaultValueInitializer = { FALSE_STR }),

    /**
     * 是否执行策略
     */
    STRATEGY(group = DEV_CONFIG_GROUP, defaultValueInitializer = { TRUE_STR }),

    /**
     * 启用鼠标
     */
    ENABLE_MOUSE(group = DEV_CONFIG_GROUP, defaultValueInitializer = { TRUE_STR }),

    /**
     * 工作时最小化软件
     */
    WORKING_MINIMIZE(
        group = BEHAVIOR_CONFIG_GROUP,
        defaultValueInitializer = { FALSE_STR },
        service = WorkingMinimizeService
    ),

    /**
     * 鼠标控制模式
     */
    MOUSE_CONTROL_MODE(group = BEHAVIOR_CONFIG_GROUP, defaultValueInitializer = { MouseControlModeEnum.MESSAGE.name }),

    /**
     * 置顶游戏窗口
     */
    TOP_GAME_WINDOW(
        group = BEHAVIOR_CONFIG_GROUP,
        defaultValueInitializer = { MOUSE_CONTROL_MODE.defaultValue },
        service = TopGameWindowService,
    ),

    /**
     * 阻止游戏的反作弊
     */
    PREVENT_AC(group = BEHAVIOR_CONFIG_GROUP, defaultValueInitializer = { FALSE_STR }, service = PreventACService),

    /**
     * 限制鼠标范围
     */
    LIMIT_MOUSE_RANGE(
        group = BEHAVIOR_CONFIG_GROUP,
        defaultValueInitializer = { FALSE_STR },
        service = LimitMouseRangeService
    ),

    /**
     * 游戏日志大小限制/KB，游戏默认10240
     */
    GAME_LOG_LIMIT(group = BEHAVIOR_CONFIG_GROUP, defaultValueInitializer = { "51200" }, service = GameLogLimitService),

    /**
     * 游戏窗口不透明度(0~255)
     */
    GAME_WINDOW_OPACITY(
        group = BEHAVIOR_CONFIG_GROUP,
        defaultValueInitializer = { "255" },
        service = GameWindowOpacityService
    ),

    /**
     * 战网平台窗口不透明度(0~255)
     */
    PLATFORM_WINDOW_OPACITY(
        group = BEHAVIOR_CONFIG_GROUP,
        defaultValueInitializer = { "255" },
        service = PlatformWindowOpacityService,
    ),

    /**
     * 游戏窗口缩小倍数
     */
    GAME_WINDOW_REDUCTION_FACTOR(
        group = BEHAVIOR_CONFIG_GROUP,
        defaultValueInitializer = { "0" },
        service = GameWindowReductionFactorService,
    ),

    /**
     * 战网窗窗口缩小倍数
     */
    PLATFORM_WINDOW_REDUCTION_FACTOR(
        group = BEHAVIOR_CONFIG_GROUP,
        defaultValueInitializer = { "0" },
        service = PlatformWindowReductionFactorService,
    ),

    /**
     * 更新游戏窗口信息
     */
    UPDATE_GAME_WINDOW(
        group = BEHAVIOR_CONFIG_GROUP,
        defaultValueInitializer = { TRUE_STR },
        service = UpdateGameWindowService,
    ),

    /**
     * 检查游戏响应超时(s)
     */
    GAME_TIMEOUT(
        group = BEHAVIOR_CONFIG_GROUP,
        defaultValueInitializer = { "60" },
        service = GameTimeoutService,
    ),

    /**
     * 最长匹配时间/s（超过重新匹配）
     */
    MATCH_MAXIMUM_TIME(group = BEHAVIOR_CONFIG_GROUP, defaultValueInitializer = { "90" }),

    /**
     * 最长空闲时间/min（超过重启游戏）
     */
    IDLE_MAXIMUM_TIME(group = BEHAVIOR_CONFIG_GROUP, defaultValueInitializer = { "10" }),

    /**
     * 套牌插件禁用列表
     */
    DECK_PLUGIN_DISABLED(
        group = PLUGIN_CONFIG_GROUP,
        defaultValueInitializer = { JSON.toJSONString(emptyList<String>()) }),

    /**
     * 卡牌插件禁用列表
     */
    CARD_PLUGIN_DISABLED(
        group = PLUGIN_CONFIG_GROUP,
        defaultValueInitializer = { JSON.toJSONString(emptyList<String>()) }),

    /**
     * 默认套牌策略(deck id)
     */
    DEFAULT_DECK_STRATEGY(
        group = OTHER_CONFIG_GROUP,
        defaultValueInitializer = { "e71234fa-1-radical-deck-97e9-1f4e126cd33b" }),

    /**
     * 默认运行模式
     */
    DEFAULT_RUN_MODE(group = OTHER_CONFIG_GROUP, defaultValueInitializer = { RunModeEnum.STANDARD.name }),

    /**
     * 战网密码
     */
    PLATFORM_PASSWORD(group = INIT_CONFIG_GROUP, defaultValueInitializer = { "" }),

    /**
     * 操作间隔/ms
     */
    MOUSE_ACTION_INTERVAL(
        group = STRATEGY_CONFIG_GROUP,
        defaultValueInitializer = { "3500" },
        service = MouseActionIntervalService
    ),

    /**
     * 适配畸变模式
     */
    DISTORTION(group = STRATEGY_CONFIG_GROUP, defaultValueInitializer = { TRUE_STR }),

    /**
     * 鼠标移动暂停间隔，值越小越慢，最小为1
     */
    PAUSE_STEP(group = STRATEGY_CONFIG_GROUP, defaultValueInitializer = { "7" }, service = PauseStepService),

    /**
     * 随机事件
     */
    RANDOM_EVENT(group = STRATEGY_CONFIG_GROUP, defaultValueInitializer = { FALSE_STR }),

    /**
     * 随机表情
     */
    RANDOM_EMOTION(group = STRATEGY_CONFIG_GROUP, defaultValueInitializer = { FALSE_STR }),

    /**
     * 超过指定回合投降
     */
    OVER_TURN_SURRENDER(group = STRATEGY_CONFIG_GROUP, defaultValueInitializer = { "-1" }),

    /**
     * 被斩杀投降
     */
    KILLED_SURRENDER(group = STRATEGY_CONFIG_GROUP, defaultValueInitializer = { FALSE_STR }),

    /**
     * 只打人机
     */
    ONLY_ROBOT(group = STRATEGY_CONFIG_GROUP, defaultValueInitializer = { FALSE_STR }),

    /**
     * 游戏对局超时投降(s)
     */
    WAR_TIMEOUT_SURRENDER(
        group = STRATEGY_CONFIG_GROUP,
        defaultValueInitializer = { "-1" },
        service = WarTimeoutSurrenderService,
    ),

    /**
     * 允许发送windows通知
     */
    SEND_NOTICE(group = SYSTEM_CONFIG_GROUP, defaultValueInitializer = { TRUE_STR }),

    /**
     * 使用系统代理
     */
    USE_PROXY(group = SYSTEM_CONFIG_GROUP, defaultValueInitializer = { TRUE_STR }),

    /**
     * 退出程序热键
     */
    EXIT_HOT_KEY(
        group = SYSTEM_CONFIG_GROUP,
        defaultValueInitializer = { JSON.toJSONString(HotKey(JIntellitype.MOD_ALT, 'P'.code)) }
    ),

    /**
     * 暂停程序热键
     */
    PAUSE_HOT_KEY(
        group = SYSTEM_CONFIG_GROUP,
        defaultValueInitializer = { JSON.toJSONString(HotKey(JIntellitype.MOD_CONTROL, 'P'.code)) },
    ),

    /**
     * 文件日志级别
     */
    FILE_LOG_LEVEL(group = DEV_CONFIG_GROUP, defaultValueInitializer = { Level.INFO.levelStr }),

    /**
     * 自动打开游戏数据分析页
     */
    AUTO_OPEN_GAME_ANALYSIS(group = DEV_CONFIG_GROUP, defaultValueInitializer = { FALSE_STR }),

    /**
     * 显示游戏控件位置
     */
    DISPLAY_GAME_RECT_POS(
        group = DEV_CONFIG_GROUP,
        defaultValueInitializer = { FALSE_STR },
        service = DisplayGameRectPosService,
        isEnable = false,
    ),

    /**
     * 启用换牌权重
     */
    ENABLE_CHANGE_WEIGHT(
        group = WEIGHT_CONFIG_GROUP,
        defaultValueInitializer = { FALSE_STR },
        service = null,
    ),

    /**
     * 当前版本
     */
    CURRENT_VERSION(group = OTHER_CONFIG_GROUP, defaultValueInitializer = { "0.0.0-GA" }),

    ;

    val defaultValue: String by lazy {
        val res = defaultValueInitializer?.invoke() ?: ""
        defaultValueInitializer = null
        res
    }
}
