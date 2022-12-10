package club.xiaojiawei.hearthstone.entity.area;

import club.xiaojiawei.hearthstone.entity.Card;

/**
 * @author 肖嘉威
 * @date 2022/11/30 14:36
 */
public class SetasideArea extends Area{
    public SetasideArea() {
        super(Integer.MAX_VALUE);
    }

    @Override
    public void putZeroAreaCard(Card card) {
        add(card);
    }

}
