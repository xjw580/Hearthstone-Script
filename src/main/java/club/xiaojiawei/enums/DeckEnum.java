package club.xiaojiawei.enums;

import club.xiaojiawei.strategy.AbstractDeckStrategy;
import club.xiaojiawei.strategy.deck.EvenNumberShamanDeckStrategy;
import club.xiaojiawei.strategy.deck.FreeDeckStrategy;
import club.xiaojiawei.strategy.deck.SwordfishRogueDeckStrategy;
import club.xiaojiawei.strategy.deck.ZooDeckStrategy;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:16
 */
@ToString
@Getter
public enum DeckEnum {

    ZOO("动物园", DeckTypeEnum.CLASSIC, RunModeEnum.CLASSIC, "AAEDAcn1AgAP+5UEs5YE1ZYE7ZYEgaEErqEEsqEEw6EE06EE7qEEnaIEo6IEu6IEv6IEw6MEAA==", ZooDeckStrategy.class, false),
    FREE("核心骑", DeckTypeEnum.STANDARD, RunModeEnum.STANDARD, "AAECAZ8FAp/UBLaeBg6hnwS1nwSWoASkoATHoATJoATWoASp1ASL7AW0ngbPngbQngbRngbUngYAAA==", FreeDeckStrategy.class, true),
    FREE_CASUAL("核心骑_休闲", DeckTypeEnum.CASUAL, RunModeEnum.CASUAL, "AAECAZ8FAp/UBLaeBg6hnwS1nwSWoASkoATHoATJoATWoASp1ASL7AW0ngbPngbQngbRngbUngYAAA==", FreeDeckStrategy.class, true),
    EVEN_NUMBER_SHAMAN( "偶数萨", DeckTypeEnum.WILD, RunModeEnum.WILD, "AAEBAdL8BQQzzfQCsdkE9PIFDb4GshSU7wKdowPapQP5kQT6tASG1ASq2QS95QTBngbfoQbpoQYAAA==", EvenNumberShamanDeckStrategy.class, true),
    EVEN_NUMBER_SHAMAN_CASUAL( "偶数萨_休闲", DeckTypeEnum.CASUAL, RunModeEnum.CASUAL, "AAEBAdL8BQQzzfQCsdkE9PIFDb4GshSU7wKdowPapQP5kQT6tASG1ASq2QS95QTBngbfoQbpoQYAAA==", EvenNumberShamanDeckStrategy.class, true),
    SWORDFISH_ROGUE("剑鱼贼",DeckTypeEnum.WILD, RunModeEnum.WILD, "AAEBAZurBASRvALl0QKvoATooAUNjALUBe4G+w+gvQLpsAO6tgOqywOKyQSa2wTXowXTsgW/9wUAAA==",SwordfishRogueDeckStrategy .class, false),
    ;
    private final String comment;
    private final DeckTypeEnum deckType;
    private final RunModeEnum runMode;
    private final String deckCode;
    private final Class<? extends AbstractDeckStrategy> abstractDeckStrategyClass;
    @Setter
    private AbstractDeckStrategy abstractDeckStrategy;
    private final boolean enable;

    DeckEnum(String comment, DeckTypeEnum deckType, RunModeEnum runMode, String deckCode, Class<? extends AbstractDeckStrategy> abstractDeckStrategyClass, boolean enable) {
        this.comment = comment;
        this.deckType = deckType;
        this.runMode = runMode;
        this.deckCode = deckCode;
        this.abstractDeckStrategyClass = abstractDeckStrategyClass;
        this.enable = enable;
    }

}
