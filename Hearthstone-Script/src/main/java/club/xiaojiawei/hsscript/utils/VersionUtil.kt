package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.config.log
import java.util.Properties

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/24 9:53
 */
object VersionUtil {

    var VERSION: String = "UNKNOWN"

    init {
        VersionUtil::class.java.getClassLoader().getResourceAsStream("build.version").use { resourceStream ->
            if (resourceStream == null) {
                log.error { "build.version file is not found in the classpath." }
            } else {
                val properties = Properties()
                properties.load(resourceStream)
                VERSION = properties.getProperty("version", VERSION)
            }
        }
    }
}
