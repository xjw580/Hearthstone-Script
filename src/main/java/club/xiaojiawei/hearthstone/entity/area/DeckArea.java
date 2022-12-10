package club.xiaojiawei.hearthstone.entity.area;

import club.xiaojiawei.hearthstone.entity.Card;

/**
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
public class DeckArea extends Area {

    public DeckArea() {
        super(60);
    }

    @Override
    public void putZeroAreaCard(Card card) {
        add(card);
    }

}
