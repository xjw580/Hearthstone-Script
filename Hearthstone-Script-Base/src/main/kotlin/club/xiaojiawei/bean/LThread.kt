package club.xiaojiawei.bean

/**
 * @author 肖嘉威
 * @date 2024/9/8 18:23
 */
open class LThread : Thread {

    constructor(task: Runnable?) : super(LRunnable(task))
    constructor(task: Runnable?, name: String?) : super(LRunnable(task), name)

}