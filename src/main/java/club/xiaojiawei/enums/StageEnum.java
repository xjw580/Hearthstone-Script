package club.xiaojiawei.enums;

import club.xiaojiawei.data.ScriptStaticData;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2023/10/1 10:37
 */
@Getter
@ToString
public enum StageEnum {
    SETTINGS("settings.fxml", "设置", 600, 400),
    DASHBOARD("dashboard.fxml", ScriptStaticData.SCRIPT_NAME, 220, 670),
    STARTUP("startup.fxml", "启动页", 683, 400),
    ;
    private final String fxmlName;
    private final String title;
    private final int width;
    private final int height;

    StageEnum(String fxmlName, String title, int width, int height) {
        this.fxmlName = fxmlName;
        this.title = title;
        this.width = width;
        this.height = height;
    }

}
