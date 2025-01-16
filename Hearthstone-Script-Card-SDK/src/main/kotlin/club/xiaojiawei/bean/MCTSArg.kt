package club.xiaojiawei.bean

import club.xiaojiawei.status.War
import java.util.function.Function

/**
 * @author 肖嘉威
 * @date 2025/1/15 10:59
 */
data class MCTSArg(
    /**
     * 每回合模拟的时间
     */
    val thinkingTime: Int,
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
     * 评分计算器
     */
    val scoreCalculator: Function<War, Double>
)