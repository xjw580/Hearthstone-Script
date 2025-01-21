package club.xiaojiawei.hsscript.enums

import club.xiaojiawei.hsscript.data.GameRationConst
import club.xiaojiawei.hsscript.data.SCRIPT_NAME
import javafx.stage.Screen
import javafx.stage.StageStyle

/**
 * @author 肖嘉威
 * @date 2023/10/1 10:37
 */
enum class WindowEnum(
    val fxmlName: String,
    val title: String = "",
    val width: Double = -1.0,
    val height: Double = -1.0,
    val x: Double = -1.0,
    val y: Double = -1.0,
    val alwaysOnTop: Boolean = false,
    val initStyle: StageStyle = StageStyle.DECORATED,
) {
    SETTINGS(
        "settings.fxml", "$SCRIPT_NAME-设置",
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
        true,
    ),
    STARTUP(
        "startup.fxml", "$SCRIPT_NAME-启动页",
        558.0, 400.0
    ),
    VERSION_MSG(
        "versionMsg.fxml", "版本说明",
        width = 550.0,
        alwaysOnTop = true
    ),
    GAME_DATA_ANALYSIS(
        "gameDataAnalysis.fxml", "游戏数据分析",
        alwaysOnTop = true
    ),
    ;
}
