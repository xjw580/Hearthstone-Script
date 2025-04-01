package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.WindowUtil
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue
import javafx.beans.value.ChangeListener

/**
 * @author 肖嘉威
 * @date 2025/4/1 15:08
 */
object WorkingMinimizeService : Service<Boolean>() {

    private val changeListener: ChangeListener<Boolean> by lazy {
        ChangeListener { _, _, working ->
            working.isTrue {
                WindowUtil.hideAllStage()
            }.isFalse {
                WindowUtil.showStage(WindowEnum.MAIN)
            }
        }
    }

    override fun execStart(): Boolean {
        if (ConfigUtil.getBoolean(ConfigEnum.WORKING_MINIMIZE)) {
            WorkListener.workingProperty.addListener(changeListener)
            return true
        }
        return false
    }

    override fun execStop(): Boolean {
        WorkListener.workingProperty.removeListener(changeListener)
        return true
    }

}