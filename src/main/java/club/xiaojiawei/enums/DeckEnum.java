package club.xiaojiawei.enums;

import club.xiaojiawei.strategy.AbstractDeckStrategy;
import club.xiaojiawei.strategy.deck.EvenNumberShamanAbstractDeckStrategy;
import club.xiaojiawei.strategy.deck.FreeAbstractDeckStrategy;
import club.xiaojiawei.strategy.deck.ZooAbstractDeckStrategy;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:16
 */
@ToString
@Getter
public enum DeckEnum {

    ZOO("ZOO", "动物园", DeckTypeEnum.CLASSIC, RunModeEnum.CLASSIC, "AAEDAcn1AgAP+5UEs5YE1ZYE7ZYEgaEErqEEsqEEw6EE06EE7qEEnaIEo6IEu6IEv6IEw6MEAA==", ZooAbstractDeckStrategy.class),
    FREE("FREE", "核心骑", DeckTypeEnum.STANDARD, RunModeEnum.STANDARD, "AAECAZ8FAp/UBLaeBg6hnwS1nwSWoASkoATHoATJoATWoASp1ASL7AW0ngbPngbQngbRngbUngYAAA==", FreeAbstractDeckStrategy.class),
    EVEN_NUMBER_SHAMAN("EVEN_NUMBER_SHAMAN", "偶数萨", DeckTypeEnum.WILD, RunModeEnum.WILD, "AAEBAaoIBM30AuHMA7HZBPTyBQ0zvgaU7wKdowPapQP5kQT6tASywQSG1ASq2QS95QTBngbQngYAAA==", EvenNumberShamanAbstractDeckStrategy.class)
    ;
    private final String value;
    private final String comment;
    private final DeckTypeEnum deckType;
    private final RunModeEnum runMode;
    private final String deckCode;
    private final Class<? extends AbstractDeckStrategy> abstractDeckStrategyClass;
    private AbstractDeckStrategy abstractDeckStrategy;

    DeckEnum(String value, String comment, DeckTypeEnum deckType, RunModeEnum runMode, String deckCode, Class<? extends AbstractDeckStrategy> abstractDeckStrategyClass) {
        this.value = value;
        this.comment = comment;
        this.deckType = deckType;
        this.runMode = runMode;
        this.deckCode = deckCode;
        this.abstractDeckStrategyClass = abstractDeckStrategyClass;
    }

    public void setAbstractDeckStrategy(AbstractDeckStrategy abstractDeckStrategy) {
        this.abstractDeckStrategy = abstractDeckStrategy;
    }
}
