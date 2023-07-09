package club.xiaojiawei.entity;

import club.xiaojiawei.entity.area.*;
import club.xiaojiawei.enums.ZoneEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;

import java.nio.charset.StandardCharsets;

/**
 * @author 肖嘉威
 * @date 2022/11/27 15:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class Player extends Entity{

    private String playerId;

    private String gameId;

    private final HandArea handArea;

    private final PlayArea playArea;

    private final SecretArea secretArea;

    private final GraveyardArea graveyardArea;

    private final DeckArea deckArea;

    private final SetasideArea setasideArea;

    private final RemovedfromgameArea removedfromgameArea;

    private volatile int maxResources = 10;

    private volatile int resources;

    private volatile int usedResources;

    private volatile int tempResources;

    private volatile int usedTempResources;

    public Player() {
        this.handArea = new HandArea();
        this.playArea = new PlayArea();
        this.secretArea = new SecretArea();
        this.graveyardArea = new GraveyardArea();
        this.deckArea = new DeckArea();
        this.setasideArea = new SetasideArea();
        this.removedfromgameArea = new RemovedfromgameArea();
    }

    @SneakyThrows
    public void setGameId(String gameId) {
        this.gameId = new String(gameId.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    public void clear(){
        usedResources = 0;
        tempResources = 0;
        usedTempResources = 0;
    }

    public Area getArea(ZoneEnum zoneEnum){
        return switch (zoneEnum){
            case DECK -> deckArea;
            case HAND -> handArea;
            case PLAY -> playArea;
            case SETASIDE -> setasideArea;
            case SECRET -> secretArea;
            case GRAVEYARD -> graveyardArea;
            case REMOVEDFROMGAME -> removedfromgameArea;
        };
    }
}
