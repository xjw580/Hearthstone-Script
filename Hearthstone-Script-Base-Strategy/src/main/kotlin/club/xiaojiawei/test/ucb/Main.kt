package club.xiaojiawei.test.ucb

/**
 * @author 肖嘉威
 * @date 2025/1/9 15:56
 */
fun main() {
    val robots = listOf(
        Robot("1", 0.0, 10.0),
        Robot("2", 3.0, 13.0),
        Robot("3", -1.0, 11.0),
        Robot("4", -3.0, 6.0),
        Robot("5", 2.0, 15.0),
    )
    val ucbHandlers = listOf(
        UCBHandler(),
        UCBHandler(),
        UCBHandler(),
        UCBHandler(),
        UCBHandler(),
    )

    for (handler in ucbHandlers) {
        handler.addReward(10.0)
    }
    updateUCB(ucbHandlers)

    var chooseTurn = 1000
    while (chooseTurn > 0) {
        val index = getMaxUCB(ucbHandlers)
        ucbHandlers[index].addReward(robots[index].getValue())
        updateUCB(ucbHandlers)
        chooseTurn--
    }
    val index = getMaxUCB(ucbHandlers)
    for ((i, robot) in robots.withIndex()) {
        println("robot: $robot, handler: ${ucbHandlers[i]}")
    }
    println("result: robot: ${robots[index]}, ucb: ${ucbHandlers[index].ucb}")
}

fun updateUCB(ucbHandlers: List<UCBHandler>) {
    for (ucbHandler in ucbHandlers) {
        ucbHandler.increaseTurn()
        ucbHandler.updateUCB()
    }
}

fun getMaxUCB(ucbHandlers: List<UCBHandler>): Int {
    var i = -1
    var max = Int.MIN_VALUE.toDouble()
    for ((index, handler) in ucbHandlers.withIndex()) {
        if (handler.ucb > max) {
            i = index
            max = handler.ucb
        }
    }
    return i
}