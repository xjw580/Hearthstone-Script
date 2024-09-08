package club.xiaojiawei.bean

/**
 * @author 肖嘉威
 * @date 2024/9/8 18:35
 */
open class ReadableThread : LogThread {

    constructor(task: Runnable?) : super(task)

    constructor(task: Runnable?, name: String?) : super(task, name)

}