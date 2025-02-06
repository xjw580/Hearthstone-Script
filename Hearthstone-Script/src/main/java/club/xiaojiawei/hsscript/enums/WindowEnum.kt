package club.xiaojiawei.hsscript.enums

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
        "settings.fxml", formatTitle("设置"),
        width = 650.0, height = 450.0,
        alwaysOnTop = true
    ),
    INIT_SETTINGS(
        "initSettings.fxml"
    ),
    ADVANCED_SETTINGS(
        "advancedSettings.fxml"
    ),
    PLUGIN_SETTINGS(
        "pluginSettings.fxml"
    ),
    STRATEGY_SETTINGS(
        "strategySettings.fxml"
    ),
    WEIGHT_SETTINGS(
        "weightSettings.fxml"
    ),
    DEVELOPER_SETTINGS(
        "developerSettings.fxml"
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
        alwaysOnTop = true
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
    ;

}
