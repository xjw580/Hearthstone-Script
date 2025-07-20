package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.hsscriptbase.bean.LThread

/**
 * @author 肖嘉威
 * @date 2024/10/15 21:36
 */

fun isDeckStrategyThread():Boolean{
    return Thread.currentThread() is DeckStrategyThread
}

fun isOutCardThread():Boolean{
    return Thread.currentThread() is OutCardThread
}

fun isDiscoverCardThread():Boolean{
    return Thread.currentThread() is DiscoverCardThread
}

fun isChangeCardThread():Boolean{
    return Thread.currentThread() is ChangeCardThread
}

open class DeckStrategyThread: LThread{
    constructor(task: Runnable?) : super(task, true)
    constructor(task: Runnable?, name: String?) : super(task, name, true)
}

open class OutCardThread(task: Runnable?) : DeckStrategyThread(task, "OutCard Thread")

open class ChangeCardThread(task: Runnable?) : DeckStrategyThread(task, "Change Card Thread")

open class DiscoverCardThread(task: Runnable?) : DeckStrategyThread(task, "Discover Card Thread")

