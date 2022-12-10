package club.xiaojiawei.hearthstone.entity.area;

import club.xiaojiawei.hearthstone.entity.Card;

/**
 * @author 肖嘉威
 * @date 2022/12/3 21:37
 */
public class RemovedfromgameArea extends Area{
    public RemovedfromgameArea() {
        super(Integer.MAX_VALUE);
    }

    @Override
    public void putZeroAreaCard(Card card) {
        add(card);
    }
}
