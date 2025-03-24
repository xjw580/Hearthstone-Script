package club.xiaojiawei.hsscript.service

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:20
 */
interface Service {

    fun start():Boolean

    fun stop():Boolean

    fun isRunning(): Boolean

    fun name(): String

    fun restart() {
        stop()
        start()
    }

}