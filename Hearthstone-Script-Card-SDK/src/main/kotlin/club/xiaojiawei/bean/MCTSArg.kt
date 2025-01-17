package club.xiaojiawei.bean

import club.xiaojiawei.status.War
import java.util.function.Function

/**
 * @author 肖嘉威
 * @date 2025/1/15 10:59
 */
data class MCTSArg(
    /**
     * 总模拟的时间
     */
    val thinkingSecTime: Int,
    /**
     * 模拟的回合数
     */
    val turnCount: Int,
    /**
     * 回合因子：小于1时表示越后面的回合的影响越小，反之越大
     */
    val turnFactor: Double,
    /**
     * 每回合模拟的次数
     */
    val countPerTurn: Int,
    /**
     * 评分计算器，(输入游戏状态，返回游戏状态的评分，一个好的评分系统应该至少考虑战场和手牌)
     */
    val scoreCalculator: Function<War, Double>,
    /**
     * 开启多线程计算
     */
    val enableMultiThread: Boolean,

    )