package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.config.log
import java.util.*

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/24 9:53
 */
object VersionUtil {

    val VERSION: String by lazy {
        var version = "UNKNOWN"
        VersionUtil::class.java.getClassLoader().getResourceAsStream("build.version").use { resourceStream ->
            if (resourceStream == null) {
                log.error { "build.version file is not found in the classpath." }
            } else {
                val properties = Properties()
                properties.load(resourceStream)
                version = properties.getProperty("version", version)
            }
        }
        version
    }

}
