package club.xiaojiawei.bean

/**
 * @author 肖嘉威
 * @date 2024/9/8 18:23
 */
open class LogThread : Thread {

    constructor(task: Runnable?) : super(LogRunnable(task))
    constructor(task: Runnable?, name: String?) : super(LogRunnable(task), name)

}