package club.xiaojiawei;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.enums.RunModeEnum;

import java.util.HashSet;
import java.util.Objects;

/**
 * @author 肖嘉威
 * @date 2024/9/7 13:36
 */
public abstract class DeckStrategy {


    public final RunModeEnum[] runMode = getRunMode();

    /**
     * 卡组名，将会显示在界面中
     * @return 非空
     */
    abstract public String name();

    /**
     * 卡组运行的模式
     * @return 返回非null非空且不包含null,推荐每次返回的数组对象是一样的
     */
    abstract protected RunModeEnum[] getRunMode();

    /**
     * 卡组代码
     * @return 可空
     */
    abstract public String deckCode();

    /**
     * 卡组唯一标识
     * @return 非空，长度必需为36
     */
    abstract public String id();


    /**
     * 执行换牌策略
     * @param cards 需要换掉的牌直接从集合中删除
     */
    abstract public void executeChangeCard(HashSet<Card> cards);

    /**
     * 执行出牌策略
     */
    abstract public void executeOutCard();

    /**
     * 执行发现选牌
     * @param cards 发现的牌
     * @return 返回范围 [0,数组长度)
     */
    abstract public int executeDiscoverChooseCard(Card...cards);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeckStrategy that = (DeckStrategy) o;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
