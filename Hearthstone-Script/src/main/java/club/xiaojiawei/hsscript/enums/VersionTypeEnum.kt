package club.xiaojiawei.hsscript.enums

import club.xiaojiawei.hsscript.bean.Release

/**
 * @author 肖嘉威
 * @date 2024/10/9 16:41
 */
enum class VersionTypeEnum(val order: Int, val isPreview: Boolean) {

    GA(10, false),
    PATCH(8, false),
    DEV(4, true),
    BETA(2, true),
    UNKNOWN(0, true),
    ;

    companion object {

        fun getEnum(type: String): VersionTypeEnum {
            return entries.find { it.name.trim().lowercase() == type.trim().lowercase() } ?: UNKNOWN
        }

        fun getEnum(release: Release): VersionTypeEnum {
            val indexOfLast = release.tagName.indexOfLast { it == '-' }
            val type = release.tagName.substring(indexOfLast + 1)
            return getEnum(type)
        }

    }

}