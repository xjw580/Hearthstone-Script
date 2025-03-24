package club.xiaojiawei.hsscript.service

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.data.GAME_HWND
import club.xiaojiawei.hsscript.dll.User32ExDll
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.utils.SystemUtil
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser.SMTO_ABORTIFHUNG
import com.sun.jna.platform.win32.WinUser.SMTO_BLOCK

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:21
 */
object GameTimeoutService : Service {

    @Volatile
    private var thread: Thread? = null

    @Synchronized
    override fun start(): Boolean {
        if (isRunning()) {
            log.warn { "GameTimeoutService不能重复启动" }
            return false
        }
        thread = Thread {
            try {
                while (thread?.isInterrupted == false) {
                    Thread.sleep(1000)
                    GAME_HWND ?: continue
                    if (!WorkListener.working) continue

                    val res = User32.INSTANCE.SendMessageTimeout(
                        GAME_HWND,
                        User32ExDll.WM_NULL,
                        WinDef.WPARAM(),
                        WinDef.LPARAM(),
                        SMTO_ABORTIFHUNG xor SMTO_BLOCK,
                        60 * 1000,
                        WinDef.DWORDByReference()
                    )
                    if (res.toInt() != 0) {
                        Core.restart(true)
                        SystemUtil.delay(5000)
                    }
                }
            } catch (e: Exception) {
                log.error(e) { "" }
            }
        }.apply {
            name = "Check Game Timeout Thread"
            start()
        }
        return true
    }

    @Synchronized
    override fun stop(): Boolean {
        thread?.interrupt()
        return true
    }

    @Synchronized
    override fun isRunning(): Boolean {
        return thread?.isAlive == true
    }

    override fun name(): String {
        return "检查游戏超时服务"
    }

}