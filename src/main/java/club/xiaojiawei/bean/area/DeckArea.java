package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.entity.Card;

/**
 * 牌库区
 * @author 肖嘉威
 * @date 2022/11/27 15:02
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
