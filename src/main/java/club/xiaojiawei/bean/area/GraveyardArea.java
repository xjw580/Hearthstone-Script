package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.entity.Card;

/**
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 * @msg 墓地
 */
public class GraveyardArea extends Area {

    public GraveyardArea() {
        super(Integer.MAX_VALUE);
    }

    @Override
    protected void addZeroCard(Card card) {
        add(card);
    }

}
