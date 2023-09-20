package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.entity.Card;

/**
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 * @msg 牌库区
 */
public class DeckArea extends Area {

    public DeckArea() {
        super(60);
    }

    @Override
    protected void addZeroCard(Card card) {
        add(card);
    }

}
