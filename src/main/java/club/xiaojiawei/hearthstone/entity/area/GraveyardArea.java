package club.xiaojiawei.hearthstone.entity.area;

import club.xiaojiawei.hearthstone.entity.Card;

/**
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
public class GraveyardArea extends Area {


    public GraveyardArea() {
        super(Integer.MAX_VALUE);
    }


    @Override
    public void putZeroAreaCard(Card card) {
        add(card);
    }

}
