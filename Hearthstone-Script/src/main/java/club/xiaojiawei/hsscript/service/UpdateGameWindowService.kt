package club.xiaojiawei.hsscript.service

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.dll.User32ExDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue
import com.sun.jna.WString

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

    override fun execStart(): Boolean {
        CSystemDll.INSTANCE.changeWindow(ScriptStatus.gameHWND, false)
        thread = Thread({
            while (true) {
                try {
                    Thread.sleep(1000)
                    ScriptStatus.gameHWND?.let {
                        User32ExDll.INSTANCE.IsIconic(it).isFalse {
                            GameUtil.updateGameRect()
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
        thread?.let {
            it.isAlive.isTrue {
                it.interrupt()
            }
            thread = null
        }
        CSystemDll.INSTANCE.changeWindow(ScriptStatus.gameHWND, true)
        return true
    }

    override fun execIntelligentStartStop(value: Boolean?): Boolean {
        return ConfigUtil.getBoolean(ConfigEnum.UPDATE_GAME_WINDOW)
    }

}