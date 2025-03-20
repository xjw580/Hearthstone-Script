package club.xiaojiawei.hsscript.status

import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import club.xiaojiawei.hsscript.utils.go
import java.util.concurrent.ArrayBlockingQueue

/**
 * 游戏当前模式（界面）
 * @author 肖嘉威
 * @date 2022/11/25 0:09
 */
object Mode {

    data class ModeStruct(var currMode: ModeEnum? = null, var newMode: ModeEnum? = null)

    private val queue = ArrayBlockingQueue<ModeStruct>(10)

    init {
        go {
            while (true) {
                val (currMode1, newMode) = queue.take()
                currMode1?.modeStrategy?.afterLeave()
                AbstractModeStrategy.cancelAllTask()
                newMode?.modeStrategy?.entering()
            }
        }
    }

    @Volatile
    var currMode: ModeEnum? = null
        set(value) {
            if (value === field) return
            queue.add(ModeStruct(field, value))
            prevMode = field
            field = value
        }

    @Volatile
    var prevMode: ModeEnum? = null

    fun reset() {
        currMode?.let {
            currMode = null
            log.info { "已重置模式状态" }
        }
    }
}
