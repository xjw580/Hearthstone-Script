package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.WindowUtil
import club.xiaojiawei.hsscriptbase.util.isFalse
import club.xiaojiawei.hsscriptbase.util.isTrue
import javafx.beans.value.ChangeListener

/**
 * @author 肖嘉威
 * @date 2025/4/1 15:08
 */
object WorkingMinimizeService : Service<Boolean>() {
    private val changeListener: ChangeListener<Boolean> by lazy {
        ChangeListener { _, _, working ->
            working
                .isTrue {
                    WindowUtil.hideAllStage()
                }.isFalse {
                    WindowUtil.showStage(WindowEnum.MAIN)
                }
        }
    }

    override fun execStart(): Boolean {
        WorkTimeListener.addChangeListener(changeListener)
        return true
    }

    override fun execStop(): Boolean {
        WorkTimeListener.removeChangeListener(changeListener)
        return true
    }

    override fun execIntelligentStartStop(value: Boolean?): Boolean = (value ?: ConfigUtil.getBoolean(ConfigEnum.WORKING_MINIMIZE))
}
