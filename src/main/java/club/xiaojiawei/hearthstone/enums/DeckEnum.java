package club.xiaojiawei.hearthstone.enums;

import club.xiaojiawei.hearthstone.strategy.AbstractDeckStrategy;
import club.xiaojiawei.hearthstone.strategy.deck.FreeAbstractDeckStrategy;
import club.xiaojiawei.hearthstone.strategy.deck.ZooAbstractDeckStrategy;

import java.util.function.Supplier;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:16
 */
public enum DeckEnum {

    ZOO("ZOO", DeckTypeEnum.CLASSIC, "动物园", ZooAbstractDeckStrategy::new),
    FREE("FREE", DeckTypeEnum.GENERAL, "免费", FreeAbstractDeckStrategy::new)
    ;
    private final String value;
    private final DeckTypeEnum deckType;
    private final String comment;
    private final Supplier<AbstractDeckStrategy> strategySupplier;

    DeckEnum(String value, DeckTypeEnum deckType, String comment, Supplier<AbstractDeckStrategy> strategySupplier) {
        this.value = value;
        this.deckType = deckType;
        this.comment = comment;
        this.strategySupplier = strategySupplier;
    }

    public String getValue() {
        return value;
    }
    public DeckTypeEnum getDeckType() {
        return deckType;
    }

    public String getComment() {
        return comment;
    }

    public Supplier<AbstractDeckStrategy> getStrategySupplier() {
        return strategySupplier;
    }

    @Override
    public String toString() {
        return "DeckEnum{" +
                "gameType=" + deckType +
                ", comment='" + comment + '\'' +
                ", strategySupplier=" + strategySupplier +
                '}';
    }
}
