package club.xiaojiawei.enums;

import lombok.Getter;

/**
 * @author 肖嘉威
 * @date 2023/9/13 14:55
 */
@Getter
public enum RegCommonNameEnum {
    DISPLAY_NAME("DisplayName", "软件名称"),
    DISPLAY_VERSION("DisplayVersion", "版本号"),
    PUBLISHER("Publisher", "出版商"),
    UNINSTALL_STRING("UninstallString", "卸载路径"),
    INSTALL_LOCATION("InstallLocation", "安装路径");

    private final String value;
    private final String comment;

    RegCommonNameEnum(String value, String comment) {
        this.value = value;
        this.comment = comment;
    }
}
