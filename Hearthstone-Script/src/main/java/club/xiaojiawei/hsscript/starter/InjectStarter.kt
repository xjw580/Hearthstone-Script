package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.consts.DLL_PATH
import club.xiaojiawei.hsscript.consts.GAME_US_NAME
import club.xiaojiawei.hsscript.consts.SCRIPT_NAME
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.CMDUtil
import club.xiaojiawei.hsscript.utils.ConfigUtil
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.*

/**
 * 启动游戏
 * @author 肖嘉威
 * @date 2023/7/5 14:38
 */
object InjectStarter : AbstractStarter() {

    public override fun execStart() {
        val controlMode = ConfigUtil.getBoolean(ConfigEnum.CONTROL_MODE)
        log.info { "控制模式：${controlMode}" }
        if (!controlMode) {
            val rootPath = System.getProperty("user.dir")
            val injectUtilName = "injectUtil.exe"
            val dllName = "libHS.dll"
            val dllDir = "dll"

            var injectFile = Path.of(rootPath, injectUtilName).toFile()
            val dllFile: File

            if (injectFile.exists()) {
//            打包查找
                dllFile = Path.of(DLL_PATH, dllName).toFile()
                if (dllFile.exists()) {
                    if (!inject(injectFile.absolutePath, dllFile.absolutePath)) return
                } else {
                    log.error { dllFile.absolutePath + "不存在" }
                }
            } else {
//            IDE查找
                injectFile = File(
                    Objects.requireNonNull(
                        javaClass.classLoader.getResource(
                            "exe/$injectUtilName"
                        )
                    ).path
                )
                if (injectFile.exists()) {
                    dllFile = File(Objects.requireNonNull(javaClass.classLoader.getResource("$dllDir/$dllName")).path)
                    if (dllFile.exists()) {
                        if (!inject(injectFile.absolutePath, dllFile.absolutePath)) return
                    } else {
                        log.error { "未找到$dllName" }
                    }
                } else {
                    log.error { "未找到$injectUtilName" }
                }
            }
        }
        startNextStarter()
    }

    private fun inject(injectUtilPath: String, dllPath: String): Boolean {
        try {
            val result = CMDUtil.exec(arrayOf(injectUtilPath, "$GAME_US_NAME.exe", dllPath))
            if (result.contains("completed")) {
                log.info { "注入dll成功" }
                return true
            } else {
                log.error { "注入dll失败：${result}" }
                if (!SystemDll.INSTANCE.IsRunAsAdministrator()) {
                    log.error { "请以管理员运行本软件" }
                    SystemDll.INSTANCE.MessageBox_(null, "请以管理员运行本软件", SCRIPT_NAME, 0)
                }
            }
        } catch (e: IOException) {
            log.error(e) { "注入dll异常" }
        }
        PauseStatus.asyncSetPause(true)
        return false
    }
}
