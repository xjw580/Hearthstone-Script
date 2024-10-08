package club.xiaojiawei.hsscript.status

import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.hsscript.utils.onlyWriteRun
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.value.ChangeListener

/**
 * 脚本暂停状态
 * @author 肖嘉威
 * @date 2023/7/5 15:04
 */
object PauseStatus {

    private val isPauseProperty: ReadOnlyBooleanWrapper = ReadOnlyBooleanWrapper(false)
    //    todo move
//        get() {
//            val booleanProperty = SimpleBooleanProperty(true)
//            booleanProperty.addListener { observable: ObservableValue<out Boolean>?, oldValue: Boolean?, newValue: Boolean ->
//                javafxMainController!!.changeSwitch(newValue)
//                WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.PAUSE, newValue))
//                if (newValue) {
//                    SystemDll.INSTANCE.changeInput(ScriptStaticData.getGameHWND(), false)
//                    SystemDll.INSTANCE.changeWindow(ScriptStaticData.getGameHWND(), false)
//                    SystemUtil.closeAll()
//                    Work.setWorking(false)
//                } else {
//                    if (Work.isDuringWorkDate()) {
//                        core!!.start()
//                    } else {
//                        Work.cannotWorkLog()
//                    }
//                }
//                log.info { "当前处于" + (if (newValue) "停止" else "运行") + "状态" }
//            }
//            return booleanProperty
//        }

    var isPause: Boolean
        get() {
            return isPauseProperty.get()
        }
        set(value) {
            onlyWriteRun {
                isPauseProperty.set(value)
            }
        }

    fun asyncSetPause(isPaused: Boolean) {
        onlyWriteRun {
            EXTRA_THREAD_POOL.submit{
                isPauseProperty.set(isPaused)
            }
        }
    }

    fun addListener(listener: ChangeListener<Boolean>) {
        isPauseProperty.addListener(listener)
    }

    fun removeListener(listener: ChangeListener<Boolean>) {
        isPauseProperty.removeListener(listener)
    }

}
