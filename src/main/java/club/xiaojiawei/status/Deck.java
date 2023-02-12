package club.xiaojiawei.status;

import club.xiaojiawei.enums.DeckEnum;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:11
 * 当前套牌
 */
public class Deck {

    private static DeckEnum deck;

    public static DeckEnum getCurrentDeck(){
        return deck;
    }

    public static void setDeck(DeckEnum deck){
        Deck.deck = deck;
    }

}
