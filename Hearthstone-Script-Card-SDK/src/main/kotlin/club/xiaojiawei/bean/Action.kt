package club.xiaojiawei.bean

import java.util.function.Consumer

/**
 * @author 肖嘉威
 * @date 2025/1/10 15:28
 */
abstract class Action(

    /**
     * 真正执行
     */
    val exec: Consumer<War>,
    /**
     * 模拟执行
     */
    val simulate: Consumer<War>,

    val creator: Card?,

    val recalculate: Boolean,
)

/**
 * 攻击动作
 */
open class AttackAction(
    exec: Consumer<War>,
    simulate: Consumer<War>,
    creator: Card? = null,
    recalculate: Boolean = false,
) : Action(exec, simulate, creator, recalculate)

/**
 * 打出动作
 */
open class PlayAction(
    exec: Consumer<War>,
    simulate: Consumer<War>,
    creator: Card? = null,
    recalculate: Boolean = false,
) : Action(exec, simulate, creator, recalculate)

/**
 * 使用动作，如地标和技能的使用
 */
open class PowerAction(
    exec: Consumer<War>,
    simulate: Consumer<War>,
    creator: Card? = null,
    recalculate: Boolean = false,
) : Action(exec, simulate, creator, recalculate)

private val empty: Consumer<War> = Consumer {}

open class EmptyAction(
    exec: Consumer<War>,
    simulate: Consumer<War>,
) : Action(exec, simulate, null, false)

/**
 * 回合结束动作
 */
object TurnOverAction : EmptyAction(empty, empty)

/**
 * 初始动作
 */
object InitAction : EmptyAction(empty, empty)



