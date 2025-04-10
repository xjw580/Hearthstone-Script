package club.xiaojiawei.hsscript.service

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.dll.User32ExDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.util.isTrue
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser.SMTO_ABORTIFHUNG
import com.sun.jna.platform.win32.WinUser.SMTO_BLOCK

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:21
 */
object GameTimeoutService : Service<Int>() {

    override val isRunning: Boolean
        get() {
            return thread?.isAlive == true
        }

    private var thread: Thread? = null


    override fun execStart(): Boolean {
        thread = Thread {
            try {
                while (thread?.isInterrupted == false) {
                    Thread.sleep(1000)
                    ScriptStatus.gameHWND ?: continue
                    if (!WorkListener.working) continue
                    val timeoutSec = ConfigUtil.getInt(ConfigEnum.GAME_TIMEOUT)
                    val res = User32.INSTANCE.SendMessageTimeout(
                        ScriptStatus.gameHWND,
                        User32ExDll.WM_NULL,
                        WinDef.WPARAM(),
                        WinDef.LPARAM(),
                        SMTO_ABORTIFHUNG xor SMTO_BLOCK,
                        timeoutSec * 1000,
                        WinDef.DWORDByReference()
                    )
                    if (res.toInt() == 0) {
                        log.warn { "触发游戏响应超时，超过${timeoutSec}秒，准备重启" }
                        Core.restart(true)
                        SystemUtil.delay(5000)
                    }
                }
            } catch (e: Exception) {
                if (e !is InterruptedException) {
                    log.error(e) { "" }
                }
            }
        }.apply {
            name = "GameTimeout Thread"
            start()
        }
        return true
    }

    override fun execStop(): Boolean {
        thread?.let {
            it.isAlive.isTrue {
                it.interrupt()
            }
            thread = null
        }
        return true
    }

    override fun execIntelligentStartStop(value: Int?): Boolean {
        return (value ?: ConfigUtil.getInt(ConfigEnum.GAME_TIMEOUT)) > 0
    }

}