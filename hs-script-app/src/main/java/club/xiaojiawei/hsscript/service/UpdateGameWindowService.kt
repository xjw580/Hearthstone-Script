package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.dll.User32ExDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscriptbase.util.isTrue
import com.sun.jna.platform.win32.User32
import javafx.beans.value.ChangeListener

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:21
 */
object UpdateGameWindowService : Service<Boolean>() {
    override val isRunning: Boolean
        get() {
            return thread?.isAlive == true
        }

    private var thread: Thread? = null

    private val workingChangeListener: ChangeListener<Boolean> by lazy {
        ChangeListener { _, _, working ->
            CSystemDll.INSTANCE.limitWindowResize(
                ScriptStatus.gameHWND,
                working && !ConfigUtil.getBoolean(ConfigEnum.UPDATE_GAME_WINDOW)
            )
        }
    }

    override fun execStart(): Boolean {
        WorkTimeListener.addChangeListener(workingChangeListener)
        CSystemDll.INSTANCE.limitWindowResize(ScriptStatus.gameHWND, false)
        thread =
            Thread({
                while (thread?.isInterrupted == false) {
                    try {
                        Thread.sleep(1000)
                        if (WorkTimeListener.working) {
                            val hwnd = ScriptStatus.gameHWND
                            if (User32.INSTANCE.IsWindow(hwnd) && !User32ExDll.INSTANCE.IsIconic(hwnd)) {
                                GameUtil.updateGameRect(hwnd)
                            }
                        }
                    } catch (e: Exception) {
                        if (e !is InterruptedException) {
                            log.error(e) { "" }
                        }
                    }
                }
            }, "Update GameWindow Thread").apply {
                start()
            }
        return true
    }

    override fun execStop(): Boolean {
        WorkTimeListener.removeChangeListener(workingChangeListener)
        CSystemDll.INSTANCE.limitWindowResize(ScriptStatus.gameHWND, WorkTimeListener.working)
        thread?.let {
            it.isAlive.isTrue {
                it.interrupt()
            }
            thread = null
        }
        return true
    }

    override fun execIntelligentStartStop(value: Boolean?): Boolean =
        ConfigUtil.getBoolean(ConfigEnum.UPDATE_GAME_WINDOW)
}
