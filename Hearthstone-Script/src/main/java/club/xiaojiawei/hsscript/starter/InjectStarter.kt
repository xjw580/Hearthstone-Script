package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.consts.DLL_PATH
import club.xiaojiawei.hsscript.consts.GAME_HWND
import club.xiaojiawei.hsscript.consts.GAME_US_NAME
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.utils.CMDUtil
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
            dllFile = Path.of(rootPath, DLL_PATH, dllName).toFile()
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
        startNextStarter()
    }

    private fun inject(injectUtilPath: String, dllPath: String) {
        try {
            val result = CMDUtil.exec(arrayOf(injectUtilPath, "$GAME_US_NAME.exe", dllPath))
            if (result.contains("completed")){
                log.info { "注入dll成功" }
            }else{
                log.error { "注入dll失败" }
            }
        } catch (e: IOException) {
            log.error(e) { "注入dll异常" }
        }
    }
}
