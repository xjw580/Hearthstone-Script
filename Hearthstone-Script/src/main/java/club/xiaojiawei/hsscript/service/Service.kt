package club.xiaojiawei.hsscript.service

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:20
 */
abstract class Service<T> {
    private var isRunningInner = false

    open val isRunning: Boolean
        get() = isRunningInner

    fun intelligentStartStop(value: T? = null): Boolean =
        if (execIntelligentStartStop(value)) {
            start()
        } else {
            stop()
        }

    fun start(): Boolean {
        synchronized(this) {
            if (isRunning) return true
            isRunningInner = execStart()
            return isRunningInner
        }
    }

    fun stop(): Boolean {
        synchronized(this) {
            if (!isRunning) return true
            isRunningInner = !execStop()
            return !isRunningInner
        }
    }

    fun valueChanged(
        oldValue: T,
        newValue: T,
    ) {
        synchronized(this) {
            execValueChanged(oldValue, newValue)
        }
    }

    fun restart() {
        synchronized(this) {
            execStop()
            execStart()
        }
    }

    protected abstract fun execStart(): Boolean

    protected abstract fun execStop(): Boolean

    protected abstract fun execIntelligentStartStop(value: T?): Boolean

    protected open fun execValueChanged(
        oldValue: T,
        newValue: T,
    ) {}
}
