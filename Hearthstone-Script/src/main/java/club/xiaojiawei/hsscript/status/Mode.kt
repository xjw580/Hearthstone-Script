package club.xiaojiawei.hsscript.status

import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import club.xiaojiawei.hsscript.utils.go
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * 游戏当前模式（界面）
 * @author 肖嘉威
 * @date 2022/11/25 0:09
 */
object Mode {

    data class ModeStruct(var currMode: ModeEnum? = null, var newMode: ModeEnum? = null)

    private val modeQueue = ArrayBlockingQueue<ModeStruct>(5)

    private var nextModeTimeoutTask: Future<*>? = null

    init {
        WorkTimeListener.addChangeListener { _, oldValue, newValue ->
            if (!newValue) {
                stopTask()
            }
        }
        go {
            while (true) {
                val (currMode1, newMode) = modeQueue.take()
                AbstractModeStrategy.cancelAllTask()
                go {
                    currMode1?.modeStrategy?.afterLeave()
                    AbstractModeStrategy.cancelAllTask()
                    newMode?.modeStrategy?.entering()
                }
            }
        }
    }

    private fun stopTask() {
        nextModeTimeoutTask?.let {
            it.cancel(true)
            nextModeTimeoutTask = null
        }
    }

    @Volatile
    var nextMode: ModeEnum? = null
        set(value) {
            if (value == field) return
            stopTask()
            field = value
            if (value == null) return
            log.info { "准备进入【${value.comment}】" }
            nextModeTimeoutTask = EXTRA_THREAD_POOL.schedule({
                if (currMode != value) {
                    log.warn { "日志长时间未打印已进入${value.comment}，默认已经进入" }
                    currMode = value
                }
            }, 5, TimeUnit.SECONDS)
        }

    @Volatile
    var currMode: ModeEnum? = null
        set(value) {
            if (value === field) return
            stopTask()
            modeQueue.add(ModeStruct(field, value))
            prevMode = field
            field = value
        }

    @Volatile
    var prevMode: ModeEnum? = null

    fun reset() {
        currMode?.let {
            currMode = null
            nextMode = null
            prevMode = null
            log.info { "已重置模式状态" }
        }
    }
}
