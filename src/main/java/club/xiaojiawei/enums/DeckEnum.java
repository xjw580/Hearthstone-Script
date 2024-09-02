package club.xiaojiawei.enums;

import club.xiaojiawei.strategy.AbstractDeckStrategy;
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
