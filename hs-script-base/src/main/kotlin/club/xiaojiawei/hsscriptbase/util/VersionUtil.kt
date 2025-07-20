package club.xiaojiawei.hsscriptbase.util

import club.xiaojiawei.hsscriptbase.config.log
import java.util.Properties

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/24 9:53
 */
object VersionUtil {

    val VERSION: String by lazy {
        var version = "UNKNOWN"
        try {
            VersionUtil::class.java.getClassLoader().getResourceAsStream("build.version").use { resourceStream ->
                if (resourceStream == null) {
                    log.error { "build.version file is not found in the classpath." }
                } else {
                    val properties = Properties()
                    properties.load(resourceStream)
                    version = properties.getProperty("version", version)
                }
            }
        } catch (e: Exception) {
            log.warn(e) { "无法读取版本号" }
        }
        version
    }

}