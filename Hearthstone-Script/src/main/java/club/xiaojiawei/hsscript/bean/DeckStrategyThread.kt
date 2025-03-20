package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.bean.LThread

/**
 * @author 肖嘉威
 * @date 2024/10/15 21:36
 */

fun isDeckStrategyThread():Boolean{
    return Thread.currentThread() is DeckStrategyThread
}

open class DeckStrategyThread: LThread{
    constructor(task: Runnable?) : super(task, true)
    constructor(task: Runnable?, name: String?) : super(task, name, true)
}