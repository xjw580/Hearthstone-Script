package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.utils.MouseUtil

/**
 * @author 肖嘉威
 * @date 2025/4/1 15:20
 */
object PauseStepService : Service<Int>() {

    override fun execStart(): Boolean {
        return true
    }

    override fun execStop(): Boolean {
        return true
    }

    override fun execIntelligentStartStop(value: Int?): Boolean {
        value?.let {
            MouseUtil.mouseMovePauseStep = it
        }
        return true
    }

    override fun execValueChanged(
        oldValue: Int,
        newValue: Int,
    ) {
        MouseUtil.mouseMovePauseStep = newValue
    }
}
