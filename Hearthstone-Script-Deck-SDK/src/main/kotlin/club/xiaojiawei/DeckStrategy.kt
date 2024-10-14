package club.xiaojiawei

import club.xiaojiawei.bean.Card
import club.xiaojiawei.enums.RunModeEnum
import java.util.*

/**
 * @author 肖嘉威
 * @date 2024/9/9 0:33
 */
abstract class DeckStrategy {

    var pluginId: String = ""

    val runModes: Array<RunModeEnum> by lazy { getRunMode() }

    /**
     * 是否需要投降，改为true时软件会在适合的时机投降
     */
    var needSurrender = false

    /**
     * 每局游戏开始时调用此方法
     */
    open fun reset(){
        needSurrender = false
    }

    /**
     * 卡组名，将会显示在界面中
     * @return 非空
     */
    abstract fun name(): String

    /**
     * 卡组运行的模式
     * @return 返回非null非空且不包含null,推荐每次返回的数组对象是一样的
     */
    protected abstract fun getRunMode(): Array<RunModeEnum>

    /**
     * 卡组代码
     * @return 非空
     */
    abstract fun deckCode(): String

    /**
     * 卡组唯一标识
     * @return 非空
     */
    abstract fun id(): String

    /**
     * 执行换牌策略
     * @param cards 需要换掉的牌直接从集合中删除
     */
    abstract fun executeChangeCard(cards: HashSet<Card>)

    /**
     * 执行出牌策略
     */
    abstract fun executeOutCard()

    /**
     * 执行发现选牌
     * @param cards 发现的牌
     * @return 返回范围 [0,数组长度)
     */
    abstract fun executeDiscoverChooseCard(vararg cards: Card): Int

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as DeckStrategy
        return id() == that.id()
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id())
    }
}