package club.xiaojiawei.hsscriptbase.bean

/**
 * @author 肖嘉威
 * @date 2024/9/8 18:36
 */
class WritableThread: ReadableThread {

    constructor(task: Runnable?) : super(task)

    constructor(task: Runnable?, name: String?) : super(task, name)

}