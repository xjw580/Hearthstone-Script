package club.xiaojiawei.hsscript.enums

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.data.SCRIPT_NAME
import javafx.stage.Screen
import javafx.stage.StageStyle

/**
 * @author 肖嘉威
 * @date 2023/10/1 10:37
 */

private fun formatTitle(title: String): String {
    return "$SCRIPT_NAME-${title}"
}

enum class WindowEnum(
    val fxmlName: String,
    val title: String = "",
    val width: Double = -1.0,
    val height: Double = -1.0,
    val x: Double = -1.0,
    val y: Double = -1.0,
    val cache: Boolean = true,
    val alwaysOnTop: Boolean = false,
    val initStyle: StageStyle = StageStyle.DECORATED,
) {
    SETTINGS(
        "settings/settings.fxml", formatTitle("设置"),
        width = 650.0, height = 450.0,
    ),
    INIT_SETTINGS(
        "settings/initSettings.fxml"
    ),
    ADVANCED_SETTINGS(
        "settings/advancedSettings.fxml"
    ),
    PLUGIN_SETTINGS(
        "settings/pluginSettings.fxml"
    ),
    STRATEGY_SETTINGS(
        "settings/strategySettings.fxml"
    ),
    WEIGHT_SETTINGS(
        "settings/weightSettings.fxml"
    ),
    DEVELOPER_SETTINGS(
        "settings/developerSettings.fxml"
    ),
    ABOUT(
        "settings/about.fxml"
    ),
    MAIN(
        "main.fxml",
        SCRIPT_NAME,
        220.0,
        670.0,
        Screen.getPrimary().bounds.width - 215.0,
        (Screen.getPrimary().bounds.height - 670.0) / 2,
        alwaysOnTop = true,
    ),
    STARTUP(
        "startup.fxml", formatTitle("启动页"),
        558.0, 400.0
    ),
    VERSION_MSG(
        "versionMsg.fxml", formatTitle("版本说明"),
        width = 550.0,
        cache = false,
    ),
    STATISTICS(
        "statistics.fxml", formatTitle("数据统计"),
        cache = false,
    ),
    GAME_DATA_ANALYSIS(
        "gameDataAnalysis.fxml", formatTitle("游戏数据分析"),
        x = 0.0, y = 0.0,
        cache = false,
        alwaysOnTop = true
    ),
    MEASURE_GAME(
        "measureGame.fxml", formatTitle("游戏控件测量"),
        cache = false, alwaysOnTop = true
    ),
    GAME_MEASURE_MODAL(
        "gameMeasureModal.fxml",
        cache = false, alwaysOnTop = true,
        initStyle = StageStyle.TRANSPARENT,
    ),
    ;

    companion object {
        fun fromString(str: String?): WindowEnum? {
            if (str.isNullOrBlank()) return null
            return try {
                WindowEnum.valueOf(str.uppercase())
            } catch (e: Exception) {
                log.error(e) { }
                null
            }
        }
    }
}
