package club.xiaojiawei.enums;

import club.xiaojiawei.data.ScriptStaticData;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.ToString;

import static javafx.stage.StageStyle.*;

/**
 * @author 肖嘉威
 * @date 2023/10/1 10:37
 */
@Getter
@ToString
public enum WindowEnum {
    SETTINGS(
            "settings.fxml", ScriptStaticData.SCRIPT_NAME + "-设置",
            600D, 400D, -1, -1,
            true, DECORATED
    ),
    MAIN(
            "main.fxml", ScriptStaticData.SCRIPT_NAME, 220D,
            670D, Screen.getPrimary().getBounds().getWidth() - 215D, (Screen.getPrimary().getBounds().getHeight() - 670D) / 2,
            true, DECORATED
    ),
    STARTUP(
            "startup.fxml", ScriptStaticData.SCRIPT_NAME + "-启动页",
            558D, 400D, -1, -1,
            false, UNDECORATED
    ),
    ;
    private final String fxmlName;
    private final String title;
    private final double width;
    private final double height;
    private final double x;
    private final double y;
    private final boolean alwaysOnTop;
    private final StageStyle initStyle;

    WindowEnum(String fxmlName, String title, double width, double height, double x, double y, boolean alwaysOnTop, StageStyle initStyle) {
        this.fxmlName = fxmlName;
        this.title = title;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.alwaysOnTop = alwaysOnTop;
        this.initStyle = initStyle;
    }
}
