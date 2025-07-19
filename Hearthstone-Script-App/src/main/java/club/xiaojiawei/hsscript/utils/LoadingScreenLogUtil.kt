package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.consts.GAME_MODE_LOG_NAME
import java.io.*

/**
 * @author 肖嘉威
 * @date 2025/4/17 22:07
 */
object LoadingScreenLogUtil {

    fun formatLogFile(logDir: String, renew: Boolean): File? {
        val sourceFile = File("$logDir\\${GAME_MODE_LOG_NAME}")
        var res: File? = null
        if (sourceFile.exists()) {
            val newFile = File("$logDir\\renew_${GAME_MODE_LOG_NAME}")
            runCatching {
                BufferedReader(FileReader(sourceFile)).use { reader ->
                    BufferedWriter(FileWriter(newFile)).use { writer ->
                        reader.lineSequence()
                            .filter { line ->
                                line.contains("prevMode") || line.contains("currMode") ||
                                        line.contains("nextMode") || line.contains("OnDestroy")
                            }
                            .forEach { writer.write("$it\n") }
                    }
                }
            }.onFailure {
                log.error { it }
            }.onSuccess {
                if (renew) {
                    res = newFile
                } else {
                    newFile.renameTo(sourceFile)
                    res = sourceFile
                }
            }
        } else {
            log.error { "${sourceFile}不存在" }
        }
        return res
    }

}