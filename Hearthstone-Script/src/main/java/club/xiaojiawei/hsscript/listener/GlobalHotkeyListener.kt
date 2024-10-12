package club.xiaojiawei.hsscript.listener

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import com.melloware.jintellitype.HotkeyListener
import com.melloware.jintellitype.JIntellitype

/**
 * 热键监听器
 * @author 肖嘉威
 * @date 2022/12/11 11:23
 */
object GlobalHotkeyListener : HotkeyListener {

    private const val HOT_KEY_EXIT = 111

    private const val HOT_KEY_PAUSE = 222

    init {
        JIntellitype.getInstance().addHotKeyListener(this)
    }

    fun reload(){
        unregister()
        register()
    }

//    todo register()
    fun register() {
        if (JIntellitype.isJIntellitypeSupported()) {
            ConfigExUtil.getExitHotKey()?.let {
                JIntellitype.getInstance()
                    .registerHotKey(HOT_KEY_EXIT, it.modifier, it.keyCode)
            }
            ConfigExUtil.getPauseHotKey()?.let {
                JIntellitype.getInstance()
                    .registerHotKey(HOT_KEY_PAUSE, it.modifier, it.keyCode)
            }
        } else {
            log.warn { "当前系统不支持设置热键" }
        }
    }

    fun unregister() {
        if (JIntellitype.isJIntellitypeSupported()) {
            JIntellitype.getInstance().unregisterHotKey(HOT_KEY_PAUSE)
            JIntellitype.getInstance().unregisterHotKey(HOT_KEY_EXIT)
        }
    }

    /**
     * 快捷键组合键按键事件
     * @param i
     */
    override fun onHotKey(i: Int) {
        when (i) {
            HOT_KEY_EXIT -> {
                unregister()
                log.info { "捕捉到热键，关闭程序" }
                SystemUtil.notice("捕捉到热键，关闭程序")
                SystemUtil.shutdown()
            }

            HOT_KEY_PAUSE -> {
                if (!PauseStatus.isPause) {
                    log.info { "捕捉到热键,停止脚本" }
                } else {
                    log.info { "捕捉到热键,开始脚本" }
                }
            }
        }
    }

}