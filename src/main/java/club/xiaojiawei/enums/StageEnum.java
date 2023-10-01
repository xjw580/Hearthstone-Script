package club.xiaojiawei.enums;

import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2023/10/1 10:37
 */
@Getter
@ToString
public enum StageEnum {
    SETTINGS("settings.fxml", "设置"),
    ;
    private final String fxmlName;
    private final String title;

    StageEnum(String fxmlName, String title) {
        this.fxmlName = fxmlName;
        this.title = title;
    }
}
