package club.xiaojiawei.hsscriptbase.bean

/**
 * @author 肖嘉威
 * @date 2024/9/8 18:23
 */
open class LThread : Thread {

    constructor(task: Runnable?, ignoreInterrupted: Boolean = false) : super(LRunnable(ignoreInterrupted, task))
    constructor(task: Runnable?, name: String?, ignoreInterrupted: Boolean = false) : super(
        LRunnable(
            ignoreInterrupted,
            task
        ), name
    )

}