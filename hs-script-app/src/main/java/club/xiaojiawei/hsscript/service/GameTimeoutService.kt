package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.dll.User32ExDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.go
import club.xiaojiawei.hsscriptbase.util.isTrue
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser.SMTO_ABORTIFHUNG
import com.sun.jna.platform.win32.WinUser.SMTO_BLOCK
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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

    private fun timeoutCheck1(timeoutSec: Int) {
        val countDownLatch = CountDownLatch(1)
        go {
            User32.INSTANCE.SendMessage(
                ScriptStatus.gameHWND,
                User32ExDll.WM_NULL,
                WinDef.WPARAM(),
                WinDef.LPARAM(),
            )
            countDownLatch.countDown()
        }
        try {
            val res = countDownLatch.await(timeoutSec.toLong(), TimeUnit.SECONDS)
            if (!res) {
                log.warn { "触发游戏响应超时，超过${timeoutSec}秒，准备重启" }
                Core.restart(true)
                SystemUtil.delay(15_000)
            }
        } catch (e: Exception) {
            log.error(e) { "" }
        }
    }

    private fun timeoutCheck2(timeoutSec: Int) {
        val res =
            User32.INSTANCE.SendMessageTimeout(
                ScriptStatus.gameHWND,
                User32ExDll.WM_NULL,
                WinDef.WPARAM(),
                WinDef.LPARAM(),
                SMTO_ABORTIFHUNG xor SMTO_BLOCK,
                timeoutSec * 1000,
                WinDef.DWORDByReference(),
            )
        if (res.toInt() == 0) {
            log.warn { "触发游戏响应超时，超过${timeoutSec}秒，准备重启" }
            Core.restart(true)
            SystemUtil.delay(15_000)
        }
    }

    override fun execStart(): Boolean {
        thread =
            Thread {
                try {
                    while (thread?.isInterrupted == false) {
                        Thread.sleep(1000)
                        ScriptStatus.gameHWND ?: continue
                        if (!WorkTimeListener.working) continue
                        timeoutCheck1(ConfigUtil.getInt(ConfigEnum.GAME_TIMEOUT))
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

    override fun execIntelligentStartStop(value: Int?): Boolean = (value ?: ConfigUtil.getInt(ConfigEnum.GAME_TIMEOUT)) > 0
}
