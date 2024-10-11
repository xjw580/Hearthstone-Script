package club.xiaojiawei.hsscript.utils

import java.lang.RuntimeException
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
                throw RuntimeException("build.version file is not found in the classpath.")
            }
            val properties = Properties()
            properties.load(resourceStream)
            VERSION = properties.getProperty("version", VERSION)
        }
    }
}
