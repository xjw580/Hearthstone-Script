package club.xiaojiawei.enums

import club.xiaojiawei.data.ScriptStaticData
import javafx.stage.Screen
import javafx.stage.StageStyle
import lombok.Getter
import lombok.ToString

/**
 * @author 肖嘉威
 * @date 2023/10/1 10:37
 */
enum class WindowEnum(
    val fxmlName: String,
    val title: String,
    val width: Double,
    val height: Double,
    val x: Double,
    val y: Double,
    val alwaysOnTop: Boolean,
    val alwaysCreateNew: Boolean,
    val initStyle: StageStyle
) {
    SETTINGS(
        "settings.fxml", ScriptStaticData.SCRIPT_NAME + "-设置",
        600.0, 400.0, -1.0, -1.0,
        true, false, StageStyle.DECORATED
    ),
    MAIN(
        "main.fxml",
        ScriptStaticData.SCRIPT_NAME,
        220.0,
        670.0,
        Screen.getPrimary().bounds.width - 215.0,
        (Screen.getPrimary().bounds.height - 670.0) / 2,
        true,
        false,
        StageStyle.DECORATED
    ),
    STARTUP(
        "startup.fxml", ScriptStaticData.SCRIPT_NAME + "-启动页",
        558.0, 400.0, -1.0, -1.0,
        false, false, StageStyle.UNDECORATED
    ),
    ;

}
