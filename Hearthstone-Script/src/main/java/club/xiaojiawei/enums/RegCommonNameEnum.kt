package club.xiaojiawei.enums;

import lombok.Getter;

/**
 * @author 肖嘉威
 * @date 2023/9/13 14:55
 */
@Getter
public enum RegCommonNameEnum {
    /**
     * 软件名称
     */
    DISPLAY_NAME("DisplayName"),
    /**
     * 版本号
     */
    DISPLAY_VERSION("DisplayVersion"),
    /**
     * 出版商
     */
    PUBLISHER("Publisher"),
    /**
     * 卸载路径
     */
    UNINSTALL_STRING("UninstallString"),
    /**
     * 安装路径
     */
    INSTALL_LOCATION("InstallLocation");

    private final String value;

    RegCommonNameEnum(String value) {
        this.value = value;
    }

}
