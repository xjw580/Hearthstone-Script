package club.xiaojiawei

import club.xiaojiawei.bean.Card

/**
 * @author 肖嘉威
 * @date 2025/1/10 15:28
 */
abstract class Action {
    abstract fun exec()
    abstract fun simulate()
}

class AttackAction(val source: Card, val target: Card) : Action() {
    override fun exec() {
        source.action.attack(target)
    }

    override fun simulate() {
//        todo 剧毒等
        source.health -= target.atc
        target.health -= source.atc
    }
}

abstract class PlayAction : Action()

abstract class PointPlayAction(val source: Card, val targets: List<Card>) : PlayAction() {
    override fun exec() {
        source.action.lClick()
        for (target in targets) {
            source.action.pointTo(target, true, isPause = false)
        }
    }
}

abstract class NonPointPlayAction(val source: Card) : PlayAction() {
    override fun exec() {
        source.action.power()
    }
}

object TurnOverAction : Action() {
    override fun exec() {}
    override fun simulate() {}
}

fun main() {
    test(TurnOverAction)
}

fun test(action: Action) {

}

