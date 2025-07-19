package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.dll.CSystemDll
import java.io.File
import java.io.IOException

/**
 * @author 肖嘉威
 * @date 2025/4/15 23:27
 */
object InjectUtil {
    fun execInject(
        injectUtilFile: File,
        dllFile: File,
        targetProcessName: String,
    ): Boolean = execInject(injectUtilFile.absolutePath, dllFile.absolutePath, targetProcessName)

    fun execInject(
        injectUtilPath: String,
        dllPath: String,
        targetProcessName: String,
    ): Boolean {
        val dllFile = File(dllPath)
        try {
            if (!dllFile.exists()) {
                log.error { "${dllPath}不存在，注入失败" }
                return false
            }
            val result = CMDUtil.exec(arrayOf(injectUtilPath, targetProcessName, dllPath))
            if (result.output.contains("completed")) {
                log.info { "注入${dllFile.name}成功" }
                return true
            } else {
                log.error { "注入${dllFile.name}失败：$result" }
                if (!CSystemDll.INSTANCE.isRunAsAdministrator()) {
                    log.error { "请以管理员运行本软件" }
                    SystemUtil.messageError("请以管理员运行本软件")
                }
            }
        } catch (e: IOException) {
            log.error(e) { "注入${dllFile.name}异常" }
        }
        return false
    }
}
