package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.data.GAME_HWND
import club.xiaojiawei.hsscript.data.GAME_US_NAME
import club.xiaojiawei.hsscript.data.INJECT_UTIL_FILE
import club.xiaojiawei.hsscript.data.LIB_HS_FILE
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.utils.CMDUtil
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import java.io.IOException

/**
 * 启动游戏
 * @author 肖嘉威
 * @date 2023/7/5 14:38
 */
class InjectStarter : AbstractStarter() {

    override fun execStart() {
        val mouseControlMode = ConfigExUtil.getMouseControlMode()
        log.info { "鼠标控制模式：${mouseControlMode.name}" }
        val preventAC = ConfigUtil.getBoolean(ConfigEnum.PREVENT_AC)
        log.info { "阻止游戏反作弊：${preventAC}" }
        if (mouseControlMode === MouseControlModeEnum.MESSAGE || preventAC){
            if (GAME_HWND != null && injectCheck()){
                startNextStarter()
                return
            }
        }
        pause()
    }

    private fun injectCheck(): Boolean {
        val injectFile = SystemUtil.getExeFilePath(INJECT_UTIL_FILE) ?: return false
        val dllFile = SystemUtil.getDllFilePath(LIB_HS_FILE) ?: return false
        return execInject(injectFile.absolutePath, dllFile.absolutePath)
    }

    private fun execInject(injectUtilPath: String, dllPath: String): Boolean {
        try {
            val result = CMDUtil.exec(arrayOf(injectUtilPath, "$GAME_US_NAME.exe", dllPath))
            if (result.contains("completed")) {
                log.info { "注入成功" }
                return true
            } else {
                log.error { "注入失败：${result}" }
                if (!SystemDll.INSTANCE.IsRunAsAdministrator()) {
                    log.error { "请以管理员运行本软件" }
                    SystemUtil.messageError("请以管理员运行本软件")
                }
            }
        } catch (e: IOException) {
            log.error(e) { "注入异常" }
        }
        return false
    }
}
