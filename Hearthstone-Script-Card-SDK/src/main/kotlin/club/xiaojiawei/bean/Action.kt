package club.xiaojiawei.bean

import club.xiaojiawei.status.War
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
    val simulate: Consumer<War>
)

/**
 * 攻击动作
 */
open class AttackAction(
    exec: Consumer<War>,
    simulate: Consumer<War>
) : Action(exec, simulate)

/**
 * 打出动作
 */
open class PlayAction(
    exec: Consumer<War>,
    simulate: Consumer<War>
) : Action(exec, simulate)

private val empty: Consumer<War> = Consumer {}

/**
 * 回合结束动作
 */
object TurnOverAction : Action(empty, empty)

/**
 * 初始动作
 */
object InitAction : Action(empty, empty)



