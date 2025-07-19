package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.status.ScriptStatus

/**
 * @author 肖嘉威
 * @date 2025/4/1 15:20
 */
object GameLogLimitService : Service<Int>() {

    override fun execStart(): Boolean {
        return true
    }

    override fun execStop(): Boolean {
        return true
    }

    override fun execIntelligentStartStop(value: Int?): Boolean {
        value?.let {
            ScriptStatus.reloadLogSize(it)
        }
        return true
    }

    override fun execValueChanged(
        oldValue: Int,
        newValue: Int,
    ) {
        ScriptStatus.reloadLogSize(newValue)
    }
}
