package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.bean.Player
import club.xiaojiawei.hsscript.enums.BlockTypeEnum
import java.util.*

/**
 * @author 肖嘉威
 * @date 2024/12/10 19:44
 */
class PlayerBehavior(val player: Player) {

    val behaviors: MutableList<Behavior> = LinkedList()

    var robotPlayerProbability: Double = 0.0

    var isCalcGameId = false

    fun calcRobotPlayerProbability() {
        if (!isCalcGameId) {
            isCalcGameId = true
            val gameId = player.gameId
            if (gameId.contains("之")) {
                robotPlayerProbability += 0.3
            }
        }
        val atcBehavior = mutableListOf<Behavior>()
        val playBehavior = mutableListOf<Behavior>()
        val avgInterval = mutableListOf<Long>()

        var lastBehavior: Behavior? = null
        for (behavior in behaviors) {
            if (behavior.blockType == BlockTypeEnum.ATTACK) {
                atcBehavior.add(behavior)
            } else if (behavior.blockType == BlockTypeEnum.PLAY) {
                playBehavior.add(behavior)
            }
            lastBehavior?.let {
                avgInterval.add(behavior.millis - it.millis)
            }
            lastBehavior = behavior
        }

    }
}