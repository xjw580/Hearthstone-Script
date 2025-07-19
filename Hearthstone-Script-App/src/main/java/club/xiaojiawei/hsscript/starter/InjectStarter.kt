package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.consts.GAME_US_NAME
import club.xiaojiawei.hsscript.consts.INJECT_UTIL_FILE
import club.xiaojiawei.hsscript.consts.LIB_HS_FILE
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.*

/**
 * 启动游戏
 * @author 肖嘉威
 * @date 2023/7/5 14:38
 */
class InjectStarter : AbstractStarter() {
    override fun execStart() {
        val mouseControlMode = ConfigExUtil.getMouseControlMode()
        val acHook = ConfigUtil.getBoolean(ConfigEnum.PREVENT_AC)
        val mouseHook = mouseControlMode === MouseControlModeEnum.MESSAGE
        val limitMouseRange = ConfigUtil.getBoolean(ConfigEnum.LIMIT_MOUSE_RANGE)

        log.info { "鼠标控制模式：${mouseControlMode.name}" }
        log.info { "阻止游戏反作弊：$acHook" }
        if (mouseHook ||
            acHook ||
            limitMouseRange
        ) {
            if (ScriptStatus.gameHWND == null || !injectCheck()) {
                pause()
                return
            }
            val delay = 200L
            val maxRetry = 10_000 / delay
            var retryI = 0
            go {
                while (!CSystemDll.INSTANCE.isConnected() && retryI++ < maxRetry) {
                    Thread.sleep(delay)
                }
                if (mouseHook) {
                    CSystemDll.INSTANCE.mouseHook(true)
                }
                if (limitMouseRange) {
                    CSystemDll.INSTANCE.limitMouseRange(true)
                }
            }
        } else {
            log.info { "无需注入" }
        }
        startNextStarter()
    }

    private fun injectCheck(): Boolean {
        val injectFile = SystemUtil.getExeFilePath(INJECT_UTIL_FILE) ?: return false
        val dllFile = SystemUtil.getDllFilePath(LIB_HS_FILE) ?: return false
        return InjectUtil.execInject(injectFile.absolutePath, dllFile.absolutePath, "$GAME_US_NAME.exe")
    }
}
