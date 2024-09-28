package club.xiaojiawei.starter

import club.xiaojiawei.config.log
import club.xiaojiawei.data.ScriptStaticData
import club.xiaojiawei.dll.SystemDll
import club.xiaojiawei.utils.CMDUtil
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
        val rootPath = System.getProperty("user.dir")
        val injectUtilName = "injectUtil.exe"
        val dllName = "libHS.dll"
        val dllDir = "dll"

        var injectFile = Path.of(rootPath, injectUtilName).toFile()
        val dllFile: File

        if (injectFile.exists()) {
//            打包查找
            dllFile = Path.of(rootPath, ScriptStaticData.LIB_DIR, dllDir, dllName).toFile()
            inject(injectFile.absolutePath, dllFile.absolutePath)
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
                    inject(injectFile.absolutePath, dllFile.absolutePath)
                } else {
                    log.error { "未找到$dllName" }
                }
            } else {
                log.error { "未找到$injectUtilName" }
            }
        }
        SystemDll.INSTANCE.changeWindow(ScriptStaticData.getGameHWND(), true)
        SystemDll.INSTANCE.changeInput(ScriptStaticData.getGameHWND(), true)
        startNextStarter()
    }

    private fun inject(injectUtilPath: String, dllPath: String) {
        try {
            val result = CMDUtil.exec(arrayOf(injectUtilPath, ScriptStaticData.GAME_US_NAME + ".exe", dllPath))
            log.info { "注入dll" + (if (result.contains("completed")) "成功" else "失败") }
        } catch (e: IOException) {
            log.error(e) { "注入dll异常" }
        }
    }
}
